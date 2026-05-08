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

/** Builder-aligned context tidier and helper-context normaliser.
  *
  * Usage: ContextScaffolder <builders_root> <helpers_root> <context_root> [--recurse] [--default-context
  * <ContextNameOrFqcn>]
  *
  * Behaviour:
  *   1. Ensures every *Builder.scala has a matching *Context.scala.
  *   2. Mirrors builder subpackages under the context root.
  *   3. Does not create contexts from helpers.
  *   4. Updates helper imports/context types to the matching builder context where possible.
  *   5. If no builder-context match is found and --default-context is supplied, uses that context and adds a validation
  *      comment above the helper trait.
  */
object ContextScaffolder {

  private final case class Config(
    buildersRoot: File,
    helpersRoot: File,
    contextRoot: File,
    recurse: Boolean,
    defaultContext: Option[String]
  )

  private final case class ContextRef(
    stem: String,
    fqcn: String,
    file: File,
    relativeDir: String
  )

  private final case class HelperRef(
    stem: String,
    file: File,
    relativeDir: String
  )

  // ---------------------------------------------------------------------------
  // IO
  // ---------------------------------------------------------------------------

  private def readFile(f: File): String =
    Using.resource(Source.fromFile(f, "UTF-8"))(_.mkString)

  private def writeFile(f: File, content: String): Unit = {
    if (!f.getParentFile.exists()) f.getParentFile.mkdirs()
    Using.resource(new PrintWriter(f, "UTF-8"))(_.write(content))
  }

  private def listScalaFiles(dir: File, recurse: Boolean): Seq[File] = {
    val children = Option(dir.listFiles()).getOrElse(Array.empty).toSeq
    val files    = children.filter(f => f.isFile && f.getName.endsWith(".scala"))
    val dirs     = children.filter(_.isDirectory)

    if (recurse) files ++ dirs.flatMap(d => listScalaFiles(d, recurse = true))
    else files
  }

  private def basePkgFromPath(root: File): String = {
    val abs    = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx    = abs.indexOf(marker)
    val tail   = if (idx >= 0) abs.substring(idx + marker.length) else abs
    tail.split('/').filter(_.nonEmpty).mkString(".")
  }

  private def relativeParent(root: File, file: File): String = {
    val rootPath   = root.getCanonicalFile.toPath
    val parentPath = file.getParentFile.getCanonicalFile.toPath
    val rel        = rootPath.relativize(parentPath).toString.replace('\\', '/')
    if (rel == ".") "" else rel
  }

  private def appendPkg(basePkg: String, relativeDir: String): String = {
    val suffix = relativeDir.split('/').filter(_.nonEmpty).mkString(".")
    if (suffix.isEmpty) basePkg else s"$basePkg.$suffix"
  }

  // ---------------------------------------------------------------------------
  // Naming
  // ---------------------------------------------------------------------------

  private def builderStem(file: File): String =
    file.getName.stripSuffix(".scala").stripSuffix("Builder")

  private def helperStem(file: File): String =
    file.getName
      .stripSuffix(".scala")
      .stripSuffix("StepHelpers")
      .stripSuffix("StepHelper")
      .stripSuffix("Helpers")
      .stripSuffix("Helper")

  private def normalize(s: String): String =
    s.toLowerCase.replaceAll("[^a-z0-9]", "")

  private def tokenise(s: String): Seq[String] =
    s.replaceAll("([A-Z])", " $1")
      .replaceAll("[^A-Za-z0-9]+", " ")
      .trim
      .split("\\s+")
      .filter(_.nonEmpty)
      .map(_.toLowerCase)
      .toSeq

  private def longestPrefixMatch(helper: HelperRef, contexts: Seq[ContextRef]): Option[ContextRef] = {
    val helperNorm = normalize(helper.stem)

    contexts
      .filter { c =>
        val contextNorm = normalize(c.stem)
        helperNorm.startsWith(contextNorm) || contextNorm.startsWith(helperNorm)
      }
      .sortBy(c => -normalize(c.stem).length)
      .headOption
  }

  private def packageAwareMatch(helper: HelperRef, contexts: Seq[ContextRef]): Option[ContextRef] = {
    val samePackage = contexts.filter(_.relativeDir == helper.relativeDir)

    longestPrefixMatch(helper, samePackage)
      .orElse(longestPrefixMatch(helper, contexts))
      .orElse {
        val helperTokens = tokenise(helper.stem).toSet
        val scored       = contexts
          .map { c =>
            val contextTokens = tokenise(c.stem).toSet
            val score         =
              if (contextTokens.isEmpty) 0.0
              else (helperTokens intersect contextTokens).size.toDouble / contextTokens.size
            c -> score
          }
          .sortBy(-_._2)

        scored.headOption.collect { case (c, score) if score >= 0.6 => c }
      }
  }

  // ---------------------------------------------------------------------------
  // Context creation/tidying
  // ---------------------------------------------------------------------------

  private def defaultContextSource(pkg: String, className: String): String =
    s"""package $pkg
       |
       |final case class $className(
       |  var request: String = "",
       |  var responseBody: String = "",
       |  var status: Int = 0,
       |  var headers: Map[String, String] = Map.empty,
       |)
       |""".stripMargin

  private def ensureDefaultFields(contextFile: File): Unit = {
    val src = readFile(contextFile)

    val defaults = Seq(
      "request"      -> "var request: String = \"\"",
      "responseBody" -> "var responseBody: String = \"\"",
      "status"       -> "var status: Int = 0",
      "headers"      -> "var headers: Map[String, String] = Map.empty"
    )

    val missing = defaults.collect {
      case (name, line) if !src.matches(s"(?s).*\\bvar\\s+$name\\s*:.*") => s"  $line"
    }

    if (missing.isEmpty) return

    val classParamsRe: Regex = """(?s)(final\s+case\s+class\s+[A-Za-z0-9_]+\s*\()(.*?)(\)\s*)""".r
    classParamsRe.findFirstMatchIn(src) match {
      case Some(m) =>
        val existingRaw   = m.group(2).replaceFirst("\\s+$", "")
        val join          = if (existingRaw.trim.isEmpty) "" else if (existingRaw.trim.endsWith(",")) "\n" else ",\n"
        val updatedParams = existingRaw + join + missing.mkString(",\n")
        val updated       = src.substring(0, m.start(2)) + updatedParams + src.substring(m.end(2))
        writeFile(contextFile, updated)
        println(s"• Tidied default fields in ${contextFile.getPath}")

      case None =>
        println(s"⚠️ Could not tidy ${contextFile.getPath}: no final case class found")
    }
  }

  private def ensureContextsFromBuilders(cfg: Config): Seq[ContextRef] = {
    val contextBasePkg = basePkgFromPath(cfg.contextRoot)
    val builders       = listScalaFiles(cfg.buildersRoot, cfg.recurse)
      .filter(f => f.getName.endsWith("Builder.scala"))

    builders.map { builderFile =>
      val stem       = builderStem(builderFile)
      val relDir     = relativeParent(cfg.buildersRoot, builderFile)
      val outDir     = if (relDir.isEmpty) cfg.contextRoot else new File(cfg.contextRoot, relDir)
      val contextPkg = appendPkg(contextBasePkg, relDir)
      val className  = s"${stem}Context"
      val outFile    = new File(outDir, s"$className.scala")

      if (!outFile.exists()) {
        writeFile(outFile, defaultContextSource(contextPkg, className))
        println(s"✓ Created ${outFile.getPath}")
      } else {
        ensureDefaultFields(outFile)
        println(s"• Using existing ${outFile.getPath}")
      }

      ContextRef(stem, s"$contextPkg.$className", outFile, relDir)
    }
  }

  private def discoverExistingContexts(contextRoot: File, recurse: Boolean): Seq[ContextRef] = {
    val contextBasePkg = basePkgFromPath(contextRoot)
    listScalaFiles(contextRoot, recurse)
      .filter(_.getName.endsWith("Context.scala"))
      .map { f =>
        val relDir = relativeParent(contextRoot, f)
        val stem   = f.getName.stripSuffix(".scala").stripSuffix("Context")
        val pkg    = appendPkg(contextBasePkg, relDir)
        ContextRef(stem, s"$pkg.${stem}Context", f, relDir)
      }
  }

  // ---------------------------------------------------------------------------
  // Helper patching
  // ---------------------------------------------------------------------------

  private val packageRe: Regex        = """(?m)^\s*package\s+([^\n]+)\s*$""".r
  private val contextImportRe: Regex  =
    """(?m)^\s*import\s+uk\.gov\.hmrc\.test\.api\.[^\n]*?\.steps\.context(?:\.[A-Za-z0-9_]+)*\.[A-Za-z0-9_]+Context\s*$""".r
  private val anyContextTypeRe: Regex = """\b[A-Za-z0-9_]+Context\b""".r
  private val traitRe: Regex          = """(?m)^\s*trait\s+[A-Za-z0-9_]+\b""".r
  private val classRe: Regex          = """(?m)^\s*(?:final\s+)?class\s+[A-Za-z0-9_]+\b""".r

  private def importInsertIndex(src: String): Int =
    packageRe.findFirstMatchIn(src).map(_.end).getOrElse(0)

  private def defaultContextRef(raw: String, contextRoot: File, recurse: Boolean): Option[ContextRef] = {
    val existing       = discoverExistingContexts(contextRoot, recurse)
    val contextBasePkg = basePkgFromPath(contextRoot)

    val trimmed     = raw.trim.stripSuffix(".scala")
    val simple      = trimmed.split('.').last.stripSuffix("Context")
    val wantedClass = s"${simple}Context"

    if (trimmed.contains(".")) {
      Some(ContextRef(simple, trimmed, new File(""), ""))
    } else {
      existing.find(_.fqcn.endsWith(s".$wantedClass")).orElse {
        // Best-effort fallback to root context package if the file does not exist yet.
        Some(ContextRef(simple, s"$contextBasePkg.$wantedClass", new File(contextRoot, s"$wantedClass.scala"), ""))
      }
    }
  }

  private def validationComment(contextName: String): String =
    s"// TODO: Validate context choice. No matching builder context was found for this helper, so it defaults to $contextName. Check this is the correct context for the migrated ScalaTest scenario."

  private def hasValidationComment(src: String, contextName: String): Boolean =
    src.contains(validationComment(contextName)) || src.contains(
      "TODO: Validate context choice. No matching builder context was found"
    )

  private def addValidationComment(src: String, contextName: String): String = {
    if (hasValidationComment(src, contextName)) return src
    val comment = validationComment(contextName) + "\n"
    traitRe.findFirstMatchIn(src).orElse(classRe.findFirstMatchIn(src)) match {
      case Some(m) => src.substring(0, m.start) + comment + src.substring(m.start)
      case None    => src + "\n" + comment
    }
  }

  private def updateHelperContext(src: String, contextRef: ContextRef, addComment: Boolean): String = {
    val simpleContext = contextRef.fqcn.split('.').last
    val importLine    = s"import ${contextRef.fqcn}"

    val withoutOldContextImports = contextImportRe.replaceAllIn(src, "")

    val insertAt   = importInsertIndex(withoutOldContextImports)
    val withImport =
      if (withoutOldContextImports.contains(importLine)) withoutOldContextImports
      else
        withoutOldContextImports.substring(0, insertAt) + "\n" + importLine + withoutOldContextImports.substring(
          insertAt
        )

    val withContextType = anyContextTypeRe.replaceAllIn(withImport, simpleContext)

    if (addComment) addValidationComment(withContextType, simpleContext)
    else withContextType
  }

  private def patchHelpers(cfg: Config, contexts: Seq[ContextRef]): Unit = {
    val defaultRef = cfg.defaultContext.flatMap(defaultContextRef(_, cfg.contextRoot, cfg.recurse))

    val helpers = listScalaFiles(cfg.helpersRoot, cfg.recurse)
      .filter(f => f.getName.endsWith("StepHelpers.scala") || f.getName.endsWith("StepHelper.scala"))

    helpers.foreach { helperFile =>
      val helper  = HelperRef(helperStem(helperFile), helperFile, relativeParent(cfg.helpersRoot, helperFile))
      val matched = packageAwareMatch(helper, contexts)
      val chosen  = matched.orElse(defaultRef)

      chosen match {
        case Some(contextRef) =>
          val addComment = matched.isEmpty && defaultRef.nonEmpty
          val original   = readFile(helperFile)
          val updated    = updateHelperContext(original, contextRef, addComment)

          if (updated != original) {
            writeFile(helperFile, updated)
            val mode = if (addComment) "defaulted" else "matched"
            println(s"• Updated helper context ($mode): ${helperFile.getPath} -> ${contextRef.fqcn}")
          }

        case None =>
          println(
            s"⚠️ No context match for helper ${helperFile.getPath}. Supply --default-context <ContextName> to patch it."
          )
      }
    }
  }

  // ---------------------------------------------------------------------------
  // CLI
  // ---------------------------------------------------------------------------

  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 3) {
      println(
        "Usage: ContextScaffolder <builders_root> <helpers_root> <context_root> [--recurse] [--default-context <ContextNameOrFqcn>]"
      )
      System.exit(1)
    }

    val buildersRoot = new File(args(0))
    val helpersRoot  = new File(args(1))
    val contextRoot  = new File(args(2))

    var recurse                        = false
    var defaultContext: Option[String] = None

    var i = 3
    while (i < args.length)
      args(i) match {
        case "--recurse" =>
          recurse = true
          i += 1

        case "--default-context" if i + 1 < args.length =>
          defaultContext = Some(args(i + 1))
          i += 2

        case other =>
          println(s"ERROR: Unknown or incomplete argument: $other")
          System.exit(1)
      }

    Seq(
      "builders" -> buildersRoot,
      "helpers"  -> helpersRoot,
      "context"  -> contextRoot
    ).foreach { case (label, dir) =>
      if (!dir.exists() || !dir.isDirectory) {
        println(s"ERROR: $label folder ${dir.getPath} not found or not a directory")
        System.exit(2)
      }
    }

    Config(buildersRoot, helpersRoot, contextRoot, recurse, defaultContext)
  }

  def main(args: Array[String]): Unit = {
    val cfg = parseArgs(args)

    val builderContexts = ensureContextsFromBuilders(cfg)
    val allContexts     = (builderContexts ++ discoverExistingContexts(cfg.contextRoot, cfg.recurse))
      .groupBy(_.fqcn)
      .values
      .map(_.head)
      .toSeq

    patchHelpers(cfg, allContexts)

    println("Done.")
  }
}
