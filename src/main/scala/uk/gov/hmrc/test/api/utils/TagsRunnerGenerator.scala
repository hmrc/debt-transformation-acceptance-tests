/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.test.api.utils

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

object TagsRunnerGenerator {

  private case class Config(
    specsRoot: File,
    tagsOut: File,
    runnerOut: File,
    cucumberRunnerRoot: File,
    recurse: Boolean
  )

  private case class SpecInfo(file: File, className: String, pkg: String, relativeParts: Seq[String]) {
    def fqcn: String = s"$pkg.$className"
  }

  private case class CucumberRunnerInfo(file: File, className: String, featureSubPaths: Seq[String])

  private val scenarioCallRe: Regex = """(?s)\bScenario\s*\((.*?)\)""".r
  private val lineCommentRe: Regex  = """(?m)//.*$""".r
  private val blockCommentRe: Regex = """(?s)/\*.*?\*/""".r
  private val tripleStrRe: Regex    = """(?s)\"\"\".*?\"\"\"""".r
  private val singleStrRe: Regex    = """"(?:\\.|[^"\\])*"""".r
  private val tagTokenRe: Regex     = """\b([A-Za-z][A-Za-z0-9_]*)\b""".r
  private val packageRe: Regex      = """(?m)^\s*package\s+([^\n]+)\s*$""".r
  private val classRe: Regex        = """\bclass\s+([A-Za-z0-9_]+)\b""".r
  private val featuresBlockRe: Regex = """(?s)features\s*=\s*Array\s*\((.*?)\)""".r
  private val quotedStringRe: Regex = """"([^"\\]*(?:\\.[^"\\]*)*)"""".r

  private def readFile(f: File): String =
    Using.resource(Source.fromFile(f, "UTF-8"))(_.mkString)

  private def writeFile(f: File, s: String): Unit = {
    Option(f.getParentFile).foreach(_.mkdirs())
    Using.resource(new PrintWriter(f, "UTF-8"))(_.write(s))
  }

  private def listFiles(dir: File, pred: File => Boolean, recurse: Boolean): Seq[File] = {
    val kids = Option(dir.listFiles()).toSeq.flatten
    val (dirs, files) = kids.partition(_.isDirectory)
    val here = files.filter(pred)
    if (recurse) here ++ dirs.flatMap(d => listFiles(d, pred, recurse = true)) else here
  }

  private def basePkgFromPath(root: File): String = {
    val abs = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx = abs.indexOf(marker)
    val tail = if (idx >= 0) abs.substring(idx + marker.length) else abs
    tail.split('/').filter(_.nonEmpty).mkString(".")
  }

  private def relativeParts(root: File, file: File): Seq[String] = {
    val rootPath = root.getCanonicalFile.toPath
    val parent = file.getCanonicalFile.getParentFile.toPath
    val rel = rootPath.relativize(parent).toString.replace('\\', '/')
    if (rel == "" || rel == ".") Seq.empty else rel.split('/').filter(_.nonEmpty).toSeq
  }

  private def normalizeTag(raw: String): String = {
    val cleaned = raw.replaceAll("[^A-Za-z0-9_]", "_")
    val prefixed = if (cleaned.headOption.exists(_.isDigit)) s"T_$cleaned" else cleaned
    if (prefixed.forall(_.isLetter)) prefixed.head.toUpper + prefixed.tail.toLowerCase
    else prefixed.toUpperCase
  }

  private def looksLikeNonTag(id: String): Boolean = {
    val lower = id.toLowerCase
    Set(
      "context", "none", "some", "true", "false", "feature", "scenario",
      "given", "when", "then", "and", "ignore", "pending"
    ).contains(lower)
  }

  private def tagsFromSpecFile(f: File): Set[String] = {
    val src0 = readFile(f)
    val src1 = lineCommentRe.replaceAllIn(src0, "")
    val src2 = blockCommentRe.replaceAllIn(src1, "")
    val tags = scala.collection.mutable.Set.empty[String]

    scenarioCallRe.findAllMatchIn(src2).foreach { m =>
      val inside = m.group(1)
      val withoutStrings = singleStrRe.replaceAllIn(tripleStrRe.replaceAllIn(inside, ""), "")
      val parts = withoutStrings.split(",").map(_.trim).drop(1)
      parts.foreach { p =>
        tagTokenRe.findAllMatchIn(p).foreach { mm =>
          val token = mm.group(1)
          if (!looksLikeNonTag(token)) tags += normalizeTag(token)
        }
      }
    }

    tags.toSet
  }

  private def discoverSpecs(specsRoot: File, recurse: Boolean): Seq[SpecInfo] =
    listFiles(specsRoot, f => f.isFile && f.getName.endsWith("FeatureSpec.scala"), recurse).flatMap { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse {
        val base = basePkgFromPath(specsRoot)
        val rel = relativeParts(specsRoot, f)
        (base +: rel).mkString(".")
      }
      val cls = classRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))
      Some(SpecInfo(f, cls, pkg, relativeParts(specsRoot, f)))
    }.sortBy(_.fqcn)

  private def emitTags(tagsPkg: String, tags: Seq[String]): String = {
    val body = tags.distinct.sorted.map { tag =>
      s"object $tag extends Tag(\"$tag\")"
    }.mkString("\n")

    s"""package $tagsPkg
       |
       |import org.scalatest.Tag
       |
       |// Auto-generated by TagsRunnerGenerator. Re-run after spec tags change.
       |
       |$body
       |""".stripMargin
  }

  private def tagsOutFile(tagsOut: File): File =
    if (tagsOut.getName.endsWith(".scala")) tagsOut else new File(tagsOut, "Tags.scala")

  private def generateTags(cfg: Config, specs: Seq[SpecInfo]): Unit = {
    val tagsFile = tagsOutFile(cfg.tagsOut)
    val tagsPkg = basePkgFromPath(tagsFile.getParentFile)
    val tags = specs.flatMap(s => tagsFromSpecFile(s.file)).distinct.sorted
    writeFile(tagsFile, emitTags(tagsPkg, tags))
    println(s"✓ Wrote tags: ${tagsFile.getPath}")
  }

  private def parseCucumberRunner(f: File): Option[CucumberRunnerInfo] = {
    val src = readFile(f)
    val className = classRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))

    val featureStrings = featuresBlockRe.findFirstMatchIn(src).toSeq.flatMap { m =>
      quotedStringRe.findAllMatchIn(m.group(1)).map(_.group(1)).toSeq
    }

    val joinedFeatureStrings =
      if (featureStrings.nonEmpty) Seq(featureStrings.mkString)
      else quotedStringRe.findAllMatchIn(src).map(_.group(1)).toSeq

    val featureSubPaths = joinedFeatureStrings
      .filter(_.contains("resources/features"))
      .map { raw =>
        val normalized = raw.replace("+", "").replaceAll("\\s+", "").replace('\\', '/')
        val marker = "resources/features/"
        val idx = normalized.indexOf(marker)
        if (idx >= 0) normalized.substring(idx + marker.length).stripPrefix("/").stripSuffix("/") else ""
      }
      .filter(_.nonEmpty)
      .distinct

    Some(CucumberRunnerInfo(f, className, featureSubPaths))
  }

  private def discoverCucumberRunners(root: File, recurse: Boolean): Seq[CucumberRunnerInfo] = {
    if (!root.exists()) Seq.empty
    else listFiles(root, f => f.isFile && f.getName.endsWith(".scala"), recurse).flatMap(parseCucumberRunner).sortBy(_.className)
  }

  private def specsForFeatureSubPath(specs: Seq[SpecInfo], featureSubPath: String): Seq[SpecInfo] = {
    val wanted = featureSubPath.split('/').filter(_.nonEmpty).map(_.toLowerCase).toSeq
    if (wanted.isEmpty) Seq.empty
    else specs.filter { s =>
      val rel = s.relativeParts.map(_.toLowerCase)
      rel.take(wanted.length) == wanted || rel.contains(wanted.last)
    }
  }

  private def emitRunner(runnerPkg: String, runnerName: String, specs: Seq[SpecInfo], sourceFeaturePaths: Seq[String]): String = {
    val header =
      s"""package $runnerPkg
         |
         |import org.scalatest.Suites
         |""".stripMargin

    val comment =
      if (sourceFeaturePaths.nonEmpty)
        sourceFeaturePaths.map(p => s"// Mirrored from Cucumber feature path: src/test/resources/features/$p").mkString("\n")
      else
        "// TODO: No feature path could be inferred from the original Cucumber runner. Validate this runner manually."

    if (specs.isEmpty) {
      s"""$header
         |
         |$comment
         |// TODO: No generated FeatureSpec classes matched this runner. Validate spec package/location after migration.
         |class $runnerName extends Suites()
         |""".stripMargin
    } else {
      val suiteArgs = specs.map(s => s"      new ${s.fqcn}").mkString(",\n")
      s"""$header
         |
         |$comment
         |class $runnerName
         |    extends Suites(
         |$suiteArgs
         |    )
         |""".stripMargin
    }
  }

  private def generateMirroredRunners(cfg: Config, specs: Seq[SpecInfo]): Unit = {
    val runnerPkg = basePkgFromPath(cfg.runnerOut)
    val cucumberRunners = discoverCucumberRunners(cfg.cucumberRunnerRoot, cfg.recurse)

    if (cucumberRunners.isEmpty) {
      println(s"⚠ No Cucumber runners found in ${cfg.cucumberRunnerRoot.getPath}")
      return
    }

    cucumberRunners.foreach { cr =>
      val matched = cr.featureSubPaths.flatMap(p => specsForFeatureSubPath(specs, p)).distinct.sortBy(_.fqcn)
      val out = new File(cfg.runnerOut, s"${cr.className}.scala")
      writeFile(out, emitRunner(runnerPkg, cr.className, matched, cr.featureSubPaths))
      println(s"✓ Wrote mirrored runner: ${out.getPath}")
    }
  }

  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 3) {
      println(
        "Usage: TagsRunnerGenerator <specs_root> <tags_out> <runner_out> " +
          "--cucumber-runner-root <cucumber_runner_root> [--recurse]"
      )
      System.exit(1)
    }

    val specsRoot = new File(args(0))
    val tagsOut = new File(args(1))
    val runnerOut = new File(args(2))
    var cucumberRunnerRoot: Option[File] = None
    var recurse = false

    var i = 3
    while (i < args.length) {
      args(i) match {
        case "--cucumber-runner-root" if i + 1 < args.length =>
          cucumberRunnerRoot = Some(new File(args(i + 1)))
          i += 2
        case "--recurse" =>
          recurse = true
          i += 1
        case other =>
          println(s"Unknown or incomplete argument: $other")
          System.exit(1)
      }
    }

    if (!specsRoot.exists() || !specsRoot.isDirectory) {
      println(s"ERROR: specs root not found or not a directory: ${specsRoot.getPath}")
      System.exit(2)
    }

    val runnerRoot = cucumberRunnerRoot.getOrElse {
      println("ERROR: --cucumber-runner-root is required")
      System.exit(2)
      new File(".")
    }

    Config(specsRoot, tagsOut, runnerOut, runnerRoot, recurse)
  }

  def main(args: Array[String]): Unit = {
    val cfg = parseArgs(args)
    val specs = discoverSpecs(cfg.specsRoot, cfg.recurse)

    if (specs.isEmpty) {
      println(s"⚠ No *FeatureSpec.scala files found in ${cfg.specsRoot.getPath}")
    }

    generateTags(cfg, specs)
    generateMirroredRunners(cfg, specs)
    println("Done.")
  }
}
