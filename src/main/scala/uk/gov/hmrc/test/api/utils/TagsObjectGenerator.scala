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

/** Generates ScalaTest Tag objects from generated FeatureSpec files.
  *
  * Usage: TagsObjectGenerator <specs_root> <tags_out> [--recurse]
  *
  * Examples: TagsObjectGenerator \ src/test/scala/uk/gov/hmrc/test/api/scalatest/specs \
  * src/test/scala/uk/gov/hmrc/test/api/scalatest/tags \ --recurse
  *
  * Notes:
  *   - Does not generate runner classes.
  *   - Extracts tags from Scenario("...", TAG_1, TAG_2) calls.
  *   - Uses a balanced parser so scenario titles containing parentheses do not break tag extraction.
  *   - Normalises WIP/wip to WIP and DTD-123/DTD_123 to DTD_123.
  */
object TagsObjectGenerator {

  private case class Config(
    specsRoot: File,
    tagsOut: File,
    recurse: Boolean
  )

  private case class SpecInfo(file: File, className: String, pkg: String, relativeParts: Seq[String]) {
    def fqcn: String = s"$pkg.$className"
  }

  private val lineCommentRe: Regex  = """(?m)//.*$""".r
  private val blockCommentRe: Regex = """(?s)/\*.*?\*/""".r
  private val tripleStrRe: Regex    = """(?s)\"\"\".*?\"\"\"""".r
  private val singleStrRe: Regex    = """"(?:\\.|[^"\\])*"""".r
  private val tagTokenRe: Regex     = """\b([A-Za-z][A-Za-z0-9_]*)\b""".r
  private val packageRe: Regex      = """(?m)^\s*package\s+([^\n]+)\s*$""".r
  private val classRe: Regex        = """\bclass\s+([A-Za-z0-9_]+)\b""".r

  private def readFile(f: File): String =
    Using.resource(Source.fromFile(f, "UTF-8"))(_.mkString)

  private def writeFile(f: File, s: String): Unit = {
    Option(f.getParentFile).foreach(_.mkdirs())
    Using.resource(new PrintWriter(f, "UTF-8"))(_.write(s))
  }

  private def listFiles(dir: File, pred: File => Boolean, recurse: Boolean): Seq[File] = {
    val kids          = Option(dir.listFiles()).toSeq.flatten
    val (dirs, files) = kids.partition(_.isDirectory)
    val here          = files.filter(pred)
    if (recurse) here ++ dirs.flatMap(d => listFiles(d, pred, recurse = true)) else here
  }

  private def normalisePackageSegment(seg: String): String = {
    val cleaned = seg.replaceAll("[^A-Za-z0-9_]", "")
    val safe    = cleaned.replaceAll("^[^A-Za-z_]+", "")
    if (safe.nonEmpty) safe else "pkg"
  }

  private def basePkgFromPath(root: File): String = {
    val abs    = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx    = abs.indexOf(marker)
    val tail   = if (idx >= 0) abs.substring(idx + marker.length) else abs
    tail.split('/').filter(_.nonEmpty).map(normalisePackageSegment).mkString(".")
  }

  private def relativeParts(root: File, file: File): Seq[String] = {
    val rootPath = root.getCanonicalFile.toPath
    val parent   = file.getCanonicalFile.getParentFile.toPath
    val rel      = rootPath.relativize(parent).toString.replace('\\', '/')
    if (rel == "" || rel == ".") Seq.empty else rel.split('/').filter(_.nonEmpty).toSeq
  }

  private def normalizeTag(raw: String): String = {
    val cleaned0 = raw.trim
      .replaceAll("[^A-Za-z0-9_]", "_")
      .replaceAll("_+", "_")
      .stripPrefix("_")
      .stripSuffix("_")

    val cleaned = if (cleaned0.headOption.exists(_.isDigit)) s"T_$cleaned0" else cleaned0

    cleaned.toUpperCase match {
      case "WIP"                                               => "WIP"
      case dtd if dtd.matches("DTD_[0-9]+")                    => dtd
      case upper if upper.matches("[A-Z]+_[0-9]+")             => upper
      case _ if cleaned.forall(_.isLetter) && cleaned.nonEmpty =>
        cleaned.head.toUpper + cleaned.tail.toLowerCase
      case _                                                   => cleaned.toUpperCase
    }
  }

  private def looksLikeNonTag(id: String): Boolean = {
    val lower = id.toLowerCase
    Set(
      "context",
      "none",
      "some",
      "true",
      "false",
      "feature",
      "scenario",
      "given",
      "when",
      "then",
      "and",
      "but",
      "ignore",
      "pending",
      "step"
    ).contains(lower)
  }

  private def splitTopLevelArgs(s: String): Seq[String] = {
    val out = scala.collection.mutable.ListBuffer.empty[String]
    val cur = new StringBuilder

    var paren          = 0
    var bracket        = 0
    var brace          = 0
    var inString       = false
    var inTripleString = false
    var escaped        = false
    var i              = 0

    while (i < s.length)
      if (!inString && i + 2 < s.length && s.substring(i, i + 3) == "\"\"\"") {
        inTripleString = !inTripleString
        cur.append("\"\"\"")
        i += 3
      } else {
        val ch = s.charAt(i)

        if (!inTripleString && inString) {
          cur.append(ch)
          if (escaped) escaped = false
          else if (ch == '\\') escaped = true
          else if (ch == '"') inString = false
          i += 1
        } else if (!inTripleString && ch == '"') {
          inString = true
          cur.append(ch)
          i += 1
        } else if (!inString && !inTripleString) {
          ch match {
            case '('                                             => paren += 1; cur.append(ch)
            case ')'                                             => paren -= 1; cur.append(ch)
            case '['                                             => bracket += 1; cur.append(ch)
            case ']'                                             => bracket -= 1; cur.append(ch)
            case '{'                                             => brace += 1; cur.append(ch)
            case '}'                                             => brace -= 1; cur.append(ch)
            case ',' if paren == 0 && bracket == 0 && brace == 0 =>
              out += cur.toString.trim
              cur.clear()
            case _                                               => cur.append(ch)
          }
          i += 1
        } else {
          cur.append(ch)
          i += 1
        }
      }

    if (cur.nonEmpty) out += cur.toString.trim
    out.toSeq.filter(_.nonEmpty)
  }

  private def scenarioArgumentBlocks(src: String): Seq[String] = {
    val out = scala.collection.mutable.ListBuffer.empty[String]
    var i   = 0

    while (i < src.length) {
      val idx = src.indexOf("Scenario", i)

      if (idx < 0) i = src.length
      else {
        val beforeOk     = idx == 0 || !src.charAt(idx - 1).isLetterOrDigit
        val afterKeyword = idx + "Scenario".length
        val afterOk      = afterKeyword >= src.length || !src.charAt(afterKeyword).isLetterOrDigit

        if (!beforeOk || !afterOk) {
          i = afterKeyword
        } else {
          var open = afterKeyword
          while (open < src.length && src.charAt(open).isWhitespace) open += 1

          if (open >= src.length || src.charAt(open) != '(') {
            i = afterKeyword
          } else {
            var j              = open + 1
            var depth          = 1
            var inString       = false
            var inTripleString = false
            var escaped        = false

            while (j < src.length && depth > 0)
              if (!inString && j + 2 < src.length && src.substring(j, j + 3) == "\"\"\"") {
                inTripleString = !inTripleString
                j += 3
              } else {
                val ch = src.charAt(j)

                if (!inTripleString && inString) {
                  if (escaped) escaped = false
                  else if (ch == '\\') escaped = true
                  else if (ch == '"') inString = false
                } else if (!inTripleString && ch == '"') {
                  inString = true
                } else if (!inString && !inTripleString) {
                  ch match {
                    case '(' => depth += 1
                    case ')' => depth -= 1
                    case _   =>
                  }
                }

                j += 1
              }

            if (depth == 0) {
              out += src.substring(open + 1, j - 1)
              i = j
            } else {
              i = open + 1
            }
          }
        }
      }
    }

    out.toSeq
  }

  private def tagsFromSpecFile(f: File): Set[String] = {
    val src0 = readFile(f)
    val src1 = lineCommentRe.replaceAllIn(src0, "")
    val src2 = blockCommentRe.replaceAllIn(src1, "")
    val tags = scala.collection.mutable.Set.empty[String]

    scenarioArgumentBlocks(src2).foreach { inside =>
      val parts = splitTopLevelArgs(inside).drop(1)

      parts.foreach { p =>
        val withoutStrings = singleStrRe.replaceAllIn(tripleStrRe.replaceAllIn(p, ""), "")
        tagTokenRe.findAllMatchIn(withoutStrings).foreach { mm =>
          val token      = mm.group(1)
          val normalized = normalizeTag(token)
          if (normalized.nonEmpty && !looksLikeNonTag(normalized)) tags += normalized
        }
      }
    }

    tags.toSet
  }

  private def discoverSpecs(specsRoot: File, recurse: Boolean): Seq[SpecInfo] =
    listFiles(specsRoot, f => f.isFile && f.getName.endsWith("FeatureSpec.scala"), recurse)
      .flatMap { f =>
        val src = readFile(f)
        val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse {
          val base = basePkgFromPath(specsRoot)
          val rel  = relativeParts(specsRoot, f).map(normalisePackageSegment)
          (base +: rel).mkString(".")
        }
        val cls = classRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))
        Some(SpecInfo(f, cls, pkg, relativeParts(specsRoot, f)))
      }
      .sortBy(_.fqcn)

  private def emitTags(tagsPkg: String, tags: Seq[String]): String = {
    val body = tags.distinct.sorted
      .map { tag =>
        s"object $tag extends Tag(\"$tag\")"
      }
      .mkString("\n")

    s"""package $tagsPkg
       |
       |import org.scalatest.Tag
       |
       |// Auto-generated by TagsObjectGenerator. Re-run after spec tags change.
       |
       |$body
       |""".stripMargin
  }

  private def tagsOutFile(tagsOut: File): File =
    if (tagsOut.getName.endsWith(".scala")) tagsOut else new File(tagsOut, "Tags.scala")

  private def generateTags(cfg: Config, specs: Seq[SpecInfo]): Unit = {
    val tagsFile = tagsOutFile(cfg.tagsOut)
    val tagsPkg  = basePkgFromPath(tagsFile.getParentFile)
    val tags     = specs.flatMap(s => tagsFromSpecFile(s.file)).distinct.sorted
    writeFile(tagsFile, emitTags(tagsPkg, tags))
    println(s"✓ Wrote tags: ${tagsFile.getPath}")
    println(s"• Generated ${tags.size} tag object(s)")
  }

  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 2) {
      println("Usage: TagsObjectGenerator <specs_root> <tags_out> [--recurse]")
      System.exit(1)
    }

    val specsRoot = new File(args(0))
    val tagsOut   = new File(args(1))
    var recurse   = false

    var i = 2
    while (i < args.length)
      args(i) match {
        case "--recurse" =>
          recurse = true
          i += 1
        case other       =>
          println(s"Unknown argument: $other")
          System.exit(1)
      }

    if (!specsRoot.exists() || !specsRoot.isDirectory) {
      println(s"ERROR: specs root not found or not a directory: ${specsRoot.getPath}")
      System.exit(2)
    }

    Config(specsRoot, tagsOut, recurse)
  }

  def main(args: Array[String]): Unit = {
    val cfg   = parseArgs(args)
    val specs = discoverSpecs(cfg.specsRoot, cfg.recurse)

    if (specs.isEmpty) {
      println(s"⚠ No *FeatureSpec.scala files found in ${cfg.specsRoot.getPath}")
    }

    generateTags(cfg, specs)
    println("Done.")
  }
}
