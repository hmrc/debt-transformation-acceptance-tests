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

/**
  * Converts .feature files into ScalaTest FeatureSpecs.
  *
  * Opinionated ScalaTest migration defaults:
  *   - mirrors feature subfolders into the output specs root
  *   - does not emit Cucumber/DataTable imports or runtime table maps
  *   - parses step tables and uses them only to generate typed constructor scaffolding
  *   - discovers generated helpers, contexts, and builders from normal ScalaTest folders
  *   - emits helper calls where safe
  *   - emits concise TODO comments where a helper/type cannot be inferred confidently
  *
  * Usage:
  *   FeatureToScalaSpecConverter <features_root> <specs_root>
  *     --helpers-root <helpers_root>
  *     --context-root <context_root>
  *     --builders-root <builders_root>
  *     [--recurse]
  */
object FeatureToScalaSpecConverter {

  // ---------------------------------------------------------------------------
  // Regexes
  // ---------------------------------------------------------------------------
  private val featurePattern: Regex         = """(?i)^\s*Feature:\s*(.*)""".r
  private val scenarioPattern: Regex        = """(?i)^\s*Scenario:\s*(.*)""".r
  private val scenarioOutlinePattern: Regex = """(?i)^\s*Scenario Outline:\s*(.*)""".r
  private val stepPattern: Regex            = """(?i)^\s*(Given|When|Then|And|But)\s+(.*)""".r
  private val examplesPattern: Regex        = """(?i)^\s*Examples:\s*""".r
  private val tableRowPattern: Regex        = """^\s*\|\s*(.+?)\s*\|\s*$""".r
  private val tagsLinePattern: Regex        = """^\s*@(.+?)\s*$""".r

  private val packageRe: Regex              = """(?m)^\s*package\s+([^\n]+)\s*$""".r
  private val traitRe: Regex                = """\btrait\s+([A-Za-z_][A-Za-z0-9_]*)\b""".r
  private val classRe: Regex                = """\bclass\s+([A-Za-z_][A-Za-z0-9_]*)\b""".r
  private val objectRe: Regex               = """\bobject\s+([A-Za-z_][A-Za-z0-9_]*)\b""".r
  private val methodHeaderRe: Regex         = """(?m)^\s*def\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)\s*:?.*?=\s*\{""".r
  private val caseClassRe: Regex            = """(?s)\b(?:final\s+)?case\s+class\s+([A-Za-z_][A-Za-z0-9_]*)\s*\((.*?)\)""".r

  // ---------------------------------------------------------------------------
  // Small models
  // ---------------------------------------------------------------------------
  private case class Config(
    featuresRoot: File,
    specsRoot: File,
    helpersRoot: File,
    contextRoot: File,
    buildersRoot: File,
    recurse: Boolean
  )

  private case class StepData(
    keyword: String,
    text: String,
    table: Seq[Map[String, String]] = Seq.empty
  )

  private case class ScenarioData(
    title: String,
    tags: Seq[String],
    steps: Seq[StepData]
  )

  private case class HelperMethod(
    helperName: String,
    methodName: String,
    params: Seq[(String, String)],
    pkg: String,
    file: File
  )

  private case class HelperDef(
    name: String,
    stem: String,
    pkg: String,
    file: File,
    methods: Seq[HelperMethod]
  )

  private case class ContextDef(
    name: String,
    stem: String,
    pkg: String,
    file: File,
    relativeDir: String
  )

  private case class BuilderInput(
    builderName: String,
    inputName: String,
    fqType: String,
    fields: Seq[(String, String)],
    pkg: String,
    file: File
  )

  private case class BuilderDef(
    name: String,
    stem: String,
    pkg: String,
    file: File,
    relativeDir: String,
    inputs: Seq[BuilderInput]
  )

  private case class Discovered(
    helpers: Seq[HelperDef],
    contexts: Seq[ContextDef],
    builders: Seq[BuilderDef],
    inputsBySimpleName: Map[String, Seq[BuilderInput]]
  )

  // ---------------------------------------------------------------------------
  // IO helpers
  // ---------------------------------------------------------------------------
  private def readFile(f: File): String =
    Using.resource(Source.fromFile(f, "UTF-8"))(_.mkString)

  private def writeFile(f: File, content: String): Unit = {
    if (!f.getParentFile.exists()) f.getParentFile.mkdirs()
    Using.resource(new PrintWriter(f, "UTF-8"))(_.write(content))
  }

  private def listFiles(dir: File, pred: File => Boolean, recurse: Boolean): Seq[File] = {
    val kids          = Option(dir.listFiles()).toSeq.flatten
    val (dirs, files) = kids.partition(_.isDirectory)
    val here          = files.filter(pred)
    if (recurse) here ++ dirs.flatMap(d => listFiles(d, pred, recurse = true)) else here
  }

  private def basePkgFromPath(root: File): String = {
    val abs = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx = abs.indexOf(marker)
    val tail = if (idx >= 0) abs.substring(idx + marker.length) else abs
    tail.split('/').filter(_.nonEmpty).mkString(".")
  }

  private def relativeParent(root: File, file: File): String = {
    val rootPath = root.getCanonicalFile.toPath
    val parent   = file.getCanonicalFile.getParentFile.toPath
    val rel      = try rootPath.relativize(parent).toString.replace('\\', '/') catch { case _: Throwable => "" }
    if (rel == ".") "" else rel
  }

  private def packageFromRelative(basePkg: String, relDir: String): String = {
    val suffix = relDir.split('/').filter(_.nonEmpty).mkString(".")
    if (suffix.isEmpty) basePkg else s"$basePkg.$suffix"
  }

  // ---------------------------------------------------------------------------
  // Naming helpers
  // ---------------------------------------------------------------------------
  private def toPascal(s: String): String = {
    val cleaned = s.replaceAll("[^A-Za-z0-9]", " ")
    val parts = cleaned
      .split("[^A-Za-z0-9]+")
      .toList
      .flatMap(_.split("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])"))
      .filter(_.nonEmpty)
    if (parts.isEmpty) "Generated" else parts.map(p => p.head.toUpper + p.tail).mkString
  }

  private def toCamel(s: String): String = {
    val p = toPascal(s)
    if (p.isEmpty) p else p.head.toLower + p.tail
  }

  private def normalizeAlnum(s: String): String =
    s.toLowerCase.replaceAll("[^a-z0-9]+", "")

  private def cleanStepText(raw: String): String =
    raw.trim
      .replaceAll("\"([^\"]+)\"", "$1")
      .replaceAll("'([^']+)'", "$1")
      .replaceAll("\\s+", " ")
      .trim

  private def stripRegexFragmentsForNaming(stepText: String): String =
    stepText
      .replaceAll("""\(\?:[^)]*\)""", " ")
      .replaceAll("""\([^)]*\)""", " ")
      .replaceAll("""[\^$"':]""", " ")
      .replaceAll("""[^\p{Alnum}\s]""", " ")
      .replaceAll("""\s+""", " ")
      .trim

  private def stepTextToMethodName(stepText: String): String = {
    val cleaned = stripRegexFragmentsForNaming(stepText)
    val words = if (cleaned.isEmpty) Array("step") else cleaned.split("\\s+").filter(_.nonEmpty)
    val lower = words.map(_.toLowerCase)
    val head = lower.headOption.getOrElse("step")
    val tail = lower.drop(1).map(w => w.head.toUpper + w.tail).mkString
    head + tail
  }

  private def normalizeTag(raw: String): String = {
    val step1 = raw.replaceAll("[^A-Za-z0-9_]", "_")
    val step2 = if (step1.headOption.exists(_.isDigit)) "T_" + step1 else step1
    if (step2.forall(_.isLetter)) step2.head.toUpper + step2.tail.toLowerCase else step2.toUpperCase
  }

  private def escape(s: String): String =
    s.replace("\\", "\\\\").replace("\"", "\\\"")

  // ---------------------------------------------------------------------------
  // Type/constructor helpers
  // ---------------------------------------------------------------------------
  private def splitTopLevelArgs(s: String): Seq[String] = {
    val out = scala.collection.mutable.ListBuffer.empty[String]
    val cur = new StringBuilder
    var paren = 0
    var bracket = 0
    var brace = 0
    var inString = false
    var escaped = false

    s.foreach { ch =>
      if (inString) {
        cur.append(ch)
        if (escaped) escaped = false
        else if (ch == '\\') escaped = true
        else if (ch == '"') inString = false
      } else {
        ch match {
          case '"' => inString = true; cur.append(ch)
          case '(' => paren += 1; cur.append(ch)
          case ')' => paren -= 1; cur.append(ch)
          case '[' => bracket += 1; cur.append(ch)
          case ']' => bracket -= 1; cur.append(ch)
          case '{' => brace += 1; cur.append(ch)
          case '}' => brace -= 1; cur.append(ch)
          case ',' if paren == 0 && bracket == 0 && brace == 0 =>
            out += cur.toString.trim
            cur.clear()
          case _ => cur.append(ch)
        }
      }
    }
    if (cur.nonEmpty) out += cur.toString.trim
    out.toSeq.filter(_.nonEmpty)
  }

  private def parseParamList(params: String): Seq[(String, String)] =
    splitTopLevelArgs(params).flatMap { raw =>
      val cleaned = raw.trim
      val parts = cleaned.split(":", 2).map(_.trim)
      if (parts.length == 2) Some(parts(0) -> parts(1)) else None
    }

  private def parseCaseClassFields(raw: String): Seq[(String, String)] =
    splitTopLevelArgs(raw).flatMap { p =>
      val cleaned = p.replaceAll("//.*$", "").trim
      val withoutDefault = splitTopLevelArgs(cleaned).headOption.getOrElse(cleaned).split("=", 2).head.trim
      val parts = withoutDefault.split(":", 2).map(_.trim)
      if (parts.length == 2) {
        val name = parts(0).stripPrefix("val ").stripPrefix("var ").trim
        Some(name -> parts(1))
      } else None
    }

  private def optionInner(tpe: String): Option[String] = {
    val t = tpe.trim
    if (t.startsWith("Option[") && t.endsWith("]")) Some(t.substring(7, t.length - 1).trim) else None
  }

  private def seqInner(tpe: String): Option[String] = {
    val t = tpe.trim
    if (t.startsWith("Seq[") && t.endsWith("]")) Some(t.substring(4, t.length - 1).trim)
    else if (t.startsWith("List[") && t.endsWith("]")) Some(t.substring(5, t.length - 1).trim)
    else None
  }

  private def simpleTypeName(tpe: String): String = {
    val noPkg = tpe.trim.split('.').lastOption.getOrElse(tpe.trim)
    noPkg.replaceAll("[^A-Za-z0-9_]", "")
  }

  private def fieldCoreType(tpe: String): String =
    optionInner(tpe).getOrElse(tpe.trim)

  private def literalFor(value: String, tpeRaw: String): String = {
    val tpe = fieldCoreType(tpeRaw)
    val v = value.trim
    tpe match {
      case "String" => s"\"${escape(v)}\""
      case "Int" => if (v.matches("-?\\d+")) v else s"/* TODO invalid Int */ 0"
      case "Long" => if (v.matches("-?\\d+")) s"${v}L" else s"/* TODO invalid Long */ 0L"
      case "Double" => if (v.matches("-?\\d+(\\.\\d+)?")) v else s"/* TODO invalid Double */ 0.0"
      case "Boolean" => if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) v.toLowerCase else s"/* TODO invalid Boolean */ false"
      case "BigDecimal" => s"BigDecimal(\"${escape(v)}\")"
      case "LocalDate" | "java.time.LocalDate" => s"java.time.LocalDate.parse(\"${escape(v)}\")"
      case other if other.nonEmpty && other != "String" =>
        s"/* TODO convert \"${escape(v)}\" to $other */ null.asInstanceOf[$other]"
      case _ => s"\"${escape(v)}\""
    }
  }

  private def optionLiteralFor(value: String, tpeRaw: String): String =
    if (value.trim.isEmpty) "None" else s"Some(${literalFor(value, tpeRaw)})"

  // ---------------------------------------------------------------------------
  // Discovery
  // ---------------------------------------------------------------------------
  private def discoverHelpers(root: File, recurse: Boolean): Seq[HelperDef] = {
    val files = listFiles(root, f => f.isFile && f.getName.endsWith("StepHelpers.scala"), recurse)
    files.map { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(basePkgFromPath(root))
      val name = traitRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))
      val stem = name.stripSuffix("StepHelpers")
      val methods = methodHeaderRe.findAllMatchIn(src).map { m =>
        HelperMethod(
          helperName = name,
          methodName = m.group(1),
          params = parseParamList(m.group(2)),
          pkg = pkg,
          file = f
        )
      }.toSeq
      HelperDef(name, stem, pkg, f, methods)
    }
  }

  private def discoverContexts(root: File, recurse: Boolean): Seq[ContextDef] = {
    val base = basePkgFromPath(root)
    listFiles(root, f => f.isFile && f.getName.endsWith("Context.scala"), recurse).map { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(packageFromRelative(base, relativeParent(root, f)))
      val name = classRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))
      ContextDef(name, name.stripSuffix("Context"), pkg, f, relativeParent(root, f))
    }
  }

  private def discoverBuilders(root: File, recurse: Boolean): Seq[BuilderDef] = {
    val base = basePkgFromPath(root)
    listFiles(root, f => f.isFile && f.getName.endsWith("Builder.scala"), recurse).map { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(packageFromRelative(base, relativeParent(root, f)))
      val name = objectRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(f.getName.stripSuffix(".scala"))
      val inputs = caseClassRe.findAllMatchIn(src).map { m =>
        val inputName = m.group(1)
        BuilderInput(
          builderName = name,
          inputName = inputName,
          fqType = s"$name.$inputName",
          fields = parseCaseClassFields(m.group(2)),
          pkg = pkg,
          file = f
        )
      }.toSeq
      BuilderDef(name, name.stripSuffix("Builder"), pkg, f, relativeParent(root, f), inputs)
    }
  }

  private def discover(cfg: Config): Discovered = {
    val helpers = discoverHelpers(cfg.helpersRoot, cfg.recurse)
    val contexts = discoverContexts(cfg.contextRoot, cfg.recurse)
    val builders = discoverBuilders(cfg.buildersRoot, cfg.recurse)
    val inputsBySimple = builders.flatMap(_.inputs).groupBy(_.inputName)
    Discovered(helpers, contexts, builders, inputsBySimple)
  }

  // ---------------------------------------------------------------------------
  // Feature parsing
  // ---------------------------------------------------------------------------
  private def parseTableLine(line: String): Option[Seq[String]] =
    tableRowPattern.findFirstMatchIn(line).map { m =>
      m.group(1).split("\\|").toSeq.map(_.trim)
    }

  private def rowsToMaps(rows: Seq[Seq[String]]): Seq[Map[String, String]] =
    rows match {
      case header +: values => values.map(row => header.zipAll(row, "", "").toMap.filter(_._1.nonEmpty))
      case _ => Seq.empty
    }

  private def substitutePlaceholders(text: String, headers: Seq[String], values: Seq[String]): String = {
    var result = text
    headers.zip(values).foreach { case (h, v) => result = result.replace(s"<$h>", v) }
    result
  }

  private def substituteTable(table: Seq[Map[String, String]], headers: Seq[String], values: Seq[String]): Seq[Map[String, String]] =
    table.map(_.map { case (k, v) => k -> substitutePlaceholders(v, headers, values) })

  private def parseFeature(file: File): (String, Seq[ScenarioData]) = {
    val lines = Source.fromFile(file, "UTF-8").getLines().toList

    var featureTitle = ""
    val scenarios = scala.collection.mutable.ArrayBuffer.empty[ScenarioData]

    var currentScenarioTitle: Option[String] = None
    var currentTags = Vector.empty[String]
    var currentSteps = Vector.empty[StepData]
    var pendingStepIndex: Option[Int] = None

    var inExamplesBlock = false
    var outlineHeaders: Seq[String] = Nil
    var outlineExamples: Seq[Seq[String]] = Nil

    def flushScenario(): Unit = {
      currentScenarioTitle.foreach { title =>
        if (outlineHeaders.nonEmpty && outlineExamples.nonEmpty) {
          outlineExamples.foreach { row =>
            val expandedSteps = currentSteps.map { step =>
              step.copy(
                text = substitutePlaceholders(step.text, outlineHeaders, row),
                table = substituteTable(step.table, outlineHeaders, row)
              )
            }
            val suffix = outlineHeaders.zip(row).map { case (h, v) => s"$h=$v" }.mkString(", ")
            scenarios += ScenarioData(s"$title [$suffix]", currentTags, expandedSteps)
          }
        } else {
          scenarios += ScenarioData(title, currentTags, currentSteps)
        }
      }

      currentScenarioTitle = None
      currentTags = Vector.empty
      currentSteps = Vector.empty
      pendingStepIndex = None
      inExamplesBlock = false
      outlineHeaders = Nil
      outlineExamples = Nil
    }

    lines.foreach {
      case featurePattern(title) =>
        featureTitle = title.trim

      case tagsLinePattern(tagsRaw) =>
        val tags = tagsRaw.split("[ @]+").filter(_.nonEmpty).map(normalizeTag).toVector
        currentTags = currentTags ++ tags

      case scenarioPattern(title) =>
        flushScenario()
        currentScenarioTitle = Some(title.trim)

      case scenarioOutlinePattern(title) =>
        flushScenario()
        currentScenarioTitle = Some(title.trim)

      case examplesPattern() =>
        inExamplesBlock = true
        pendingStepIndex = None

      case line if inExamplesBlock && parseTableLine(line).nonEmpty =>
        val cells = parseTableLine(line).get
        if (outlineHeaders.isEmpty) outlineHeaders = cells else outlineExamples = outlineExamples :+ cells

      case stepPattern(keyword, text) =>
        inExamplesBlock = false
        val step = StepData(keyword.capitalize, cleanStepText(text))
        currentSteps = currentSteps :+ step
        pendingStepIndex = Some(currentSteps.size - 1)

      case line if !inExamplesBlock && pendingStepIndex.nonEmpty && parseTableLine(line).nonEmpty =>
        // Accumulate contiguous step table rows. Convert each time for simplicity.
        val idx = pendingStepIndex.get
        val existingAsRows = if (currentSteps(idx).table.isEmpty) Vector.empty[Seq[String]] else Vector.empty[Seq[String]]
        // We cannot reconstruct original table rows from maps, so keep a side-channel by parsing in a small local loop below.
        // Simpler approach: append to a synthetic row buffer encoded temporarily in notes is overkill.
        // Instead, parse tables in a second pass below would be ideal; here, handle common case by storing raw table rows in the map.
        // This branch is intentionally replaced by the custom parser below; kept unreachable by parseFeatureFull.
        ()

      case _ => ()
    }

    // Re-parse with table support because streaming above cannot easily accumulate normal tables without side state.
    parseFeatureFull(lines, featureTitle)
  }

  private def parseFeatureFull(lines: List[String], featureTitle0: String): (String, Seq[ScenarioData]) = {
    var featureTitle = featureTitle0
    val scenarios = scala.collection.mutable.ArrayBuffer.empty[ScenarioData]
    var currentScenarioTitle: Option[String] = None
    var currentTags = Vector.empty[String]
    var currentSteps = Vector.empty[StepData]
    var inExamplesBlock = false
    var outlineHeaders: Seq[String] = Nil
    var outlineExamples: Seq[Seq[String]] = Nil

    def flushScenario(): Unit = {
      currentScenarioTitle.foreach { title =>
        if (outlineHeaders.nonEmpty && outlineExamples.nonEmpty) {
          outlineExamples.foreach { row =>
            val expandedSteps = currentSteps.map { step =>
              step.copy(
                text = substitutePlaceholders(step.text, outlineHeaders, row),
                table = substituteTable(step.table, outlineHeaders, row)
              )
            }
            val suffix = outlineHeaders.zip(row).map { case (h, v) => s"$h=$v" }.mkString(", ")
            scenarios += ScenarioData(s"$title [$suffix]", currentTags, expandedSteps)
          }
        } else scenarios += ScenarioData(title, currentTags, currentSteps)
      }
      currentScenarioTitle = None
      currentTags = Vector.empty
      currentSteps = Vector.empty
      inExamplesBlock = false
      outlineHeaders = Nil
      outlineExamples = Nil
    }

    var i = 0
    while (i < lines.length) {
      lines(i) match {
        case featurePattern(title) =>
          featureTitle = title.trim
          i += 1

        case tagsLinePattern(tagsRaw) =>
          currentTags = currentTags ++ tagsRaw.split("[ @]+").filter(_.nonEmpty).map(normalizeTag)
          i += 1

        case scenarioPattern(title) =>
          flushScenario()
          currentScenarioTitle = Some(title.trim)
          i += 1

        case scenarioOutlinePattern(title) =>
          flushScenario()
          currentScenarioTitle = Some(title.trim)
          i += 1

        case examplesPattern() =>
          inExamplesBlock = true
          i += 1
          val rows = scala.collection.mutable.ArrayBuffer.empty[Seq[String]]
          while (i < lines.length && parseTableLine(lines(i)).nonEmpty) {
            rows += parseTableLine(lines(i)).get
            i += 1
          }
          rows.headOption.foreach(h => outlineHeaders = h)
          if (rows.size > 1) outlineExamples = rows.tail.toSeq

        case stepPattern(keyword, text) =>
          inExamplesBlock = false
          val stepKeyword = keyword.capitalize
          val stepText = cleanStepText(text)
          i += 1
          val tableRows = scala.collection.mutable.ArrayBuffer.empty[Seq[String]]
          while (i < lines.length && parseTableLine(lines(i)).nonEmpty) {
            tableRows += parseTableLine(lines(i)).get
            i += 1
          }
          currentSteps = currentSteps :+ StepData(stepKeyword, stepText, rowsToMaps(tableRows.toSeq))

        case _ => i += 1
      }
    }

    flushScenario()
    (featureTitle, scenarios.toSeq)
  }

  // ---------------------------------------------------------------------------
  // Matching
  // ---------------------------------------------------------------------------
  private def bestContextForSpec(featureStem: String, relDir: String, discovered: Discovered): Option[ContextDef] = {
    val candidates = discovered.contexts
    def score(c: ContextDef): Int = {
      val stemScore =
        if (normalizeAlnum(featureStem).startsWith(normalizeAlnum(c.stem)) || normalizeAlnum(c.stem).startsWith(normalizeAlnum(featureStem))) 50
        else if (normalizeAlnum(featureStem).contains(normalizeAlnum(c.stem)) || normalizeAlnum(c.stem).contains(normalizeAlnum(featureStem))) 30
        else 0
      val dirScore = if (c.relativeDir == relDir) 30 else if (c.relativeDir.nonEmpty && relDir.startsWith(c.relativeDir)) 15 else 0
      stemScore + dirScore
    }
    candidates.map(c => c -> score(c)).filter(_._2 > 0).sortBy(-_._2).headOption.map(_._1)
      .orElse(candidates.find(_.relativeDir == relDir))
      .orElse(candidates.headOption)
  }

  private def helperScoreForSpec(h: HelperDef, featureStem: String, relDir: String, helpersRoot: File): Int = {
    val hRel = relativeParent(helpersRoot, h.file)
    val stemScore =
      if (normalizeAlnum(featureStem).startsWith(normalizeAlnum(h.stem)) || normalizeAlnum(h.stem).startsWith(normalizeAlnum(featureStem))) 50
      else if (normalizeAlnum(featureStem).contains(normalizeAlnum(h.stem)) || normalizeAlnum(h.stem).contains(normalizeAlnum(featureStem))) 30
      else 0
    val dirScore = if (hRel == relDir) 30 else if (hRel.nonEmpty && relDir.startsWith(hRel)) 15 else 0
    stemScore + dirScore
  }

  private def bestHelpersForSpec(featureStem: String, relDir: String, discovered: Discovered, helpersRoot: File): Seq[HelperDef] = {
    val scored = discovered.helpers.map(h => h -> helperScoreForSpec(h, featureStem, relDir, helpersRoot)).filter(_._2 > 0).sortBy(-_._2)
    if (scored.nonEmpty) scored.take(3).map(_._1)
    else discovered.helpers.filter(h => relativeParent(helpersRoot, h.file) == relDir).take(3)
  }

  private def methodForStep(step: StepData, helpers: Seq[HelperDef]): Option[HelperMethod] = {
    val expectedName = stepTextToMethodName(step.text)
    val all = helpers.flatMap(_.methods)
    all.find(_.methodName == expectedName).orElse {
      val nStep = normalizeAlnum(expectedName)
      all.map(m => m -> similarity(nStep, normalizeAlnum(m.methodName))).sortBy(-_._2).headOption.collect {
        case (m, score) if score >= 0.72 => m
      }
    }
  }

  private def similarity(a: String, b: String): Double = {
    if (a.isEmpty && b.isEmpty) 1.0
    else if (a == b) 1.0
    else {
      val A = trigrams(a); val B = trigrams(b)
      if (A.isEmpty || B.isEmpty) 0.0 else (A intersect B).size.toDouble / (A union B).size.toDouble
    }
  }

  private def trigrams(s: String): Set[String] =
    if (s.length < 3) Set(s) else (0 to s.length - 3).map(i => s.substring(i, i + 3)).toSet

  private def nonContextParams(method: HelperMethod): Seq[(String, String)] =
    method.params.filterNot { case (_, tpe) => tpe.trim.endsWith("Context") || tpe.trim.contains("Context") }

  private def builderInputForParam(tpe: String, discovered: Discovered): Option[(BuilderInput, Boolean)] = {
    val seq = seqInner(tpe)
    val raw = seq.getOrElse(tpe.trim)
    val simple = simpleTypeName(raw)
    val input = discovered.inputsBySimpleName.get(simple).flatMap(_.headOption)
    input.map(_ -> seq.nonEmpty)
  }

  // ---------------------------------------------------------------------------
  // Emission helpers
  // ---------------------------------------------------------------------------
  private def emitInputInstance(input: BuilderInput, row: Map[String, String], indent: String): String = {
    val fieldLines = input.fields.map { case (name, tpe) =>
      val value = row.getOrElse(name, "")
      s"$indent  $name = ${optionLiteralFor(value, tpe)}"
    }
    val missingColumns = row.keySet.diff(input.fields.map(_._1).toSet).toSeq.sorted
    val missingComment =
      if (missingColumns.nonEmpty)
        missingColumns.map(c => s"$indent  // TODO: Column '$c' did not match a field on ${input.inputName}").mkString("\n") + "\n"
      else ""
    s"${input.builderName}.${input.inputName}(\n" + missingComment + fieldLines.mkString(",\n") + s"\n$indent)"
  }

  private def emitHelperCall(
    step: StepData,
    method: HelperMethod,
    discovered: Discovered,
    imports: scala.collection.mutable.Set[String],
    indent: String
  ): String = {
    val params = nonContextParams(method)
    if (params.isEmpty) {
      s"${indent}${method.methodName}(context)"
    } else if (params.size == 1) {
      val (_, tpe) = params.head
      builderInputForParam(tpe, discovered) match {
        case Some((input, isSeq)) if step.table.nonEmpty && isSeq =>
          imports += s"${input.pkg}.${input.builderName}"
          val instances = step.table.map(row => emitInputInstance(input, row, indent + "    ")).mkString(s",\n")
          s"${indent}${method.methodName}(\n${indent}  context,\n${indent}  Seq(\n$instances\n${indent}  )\n$indent)"

        case Some((input, isSeq)) if step.table.nonEmpty && !isSeq =>
          imports += s"${input.pkg}.${input.builderName}"
          val row = step.table.head
          s"${indent}${method.methodName}(\n${indent}  context,\n${indent}  ${emitInputInstance(input, row, indent + "  ")}\n$indent)"

        case Some((input, isSeq)) if step.table.isEmpty =>
          imports += s"${input.pkg}.${input.builderName}"
          val shape =
            if (isSeq)
              s"Seq(${input.builderName}.${input.inputName}(/* TODO */))"
            else
              s"${input.builderName}.${input.inputName}(/* TODO */)"
          s"${indent}// TODO: Provide typed input for this helper.\n${indent}// ${method.methodName}(context, $shape)"

        case None if step.table.nonEmpty =>
          s"${indent}// TODO: Step table was parsed, but the helper parameter type '$tpe' could not be matched to a generated builder input.\n${indent}// Validate the helper signature and call ${method.methodName}(context, ...)"

        case None =>
          s"${indent}// TODO: Helper '${method.methodName}' requires parameter type '$tpe'. Add the appropriate typed value.\n${indent}// ${method.methodName}(context, /* TODO */)"
      }
    } else {
      s"${indent}// TODO: Helper '${method.methodName}' has multiple non-context parameters: ${params.map { case (n, t) => s"$n: $t" }.mkString(", ")}.\n${indent}// ${method.methodName}(context, /* TODO */)"
    }
  }

  private def emitSpec(inputFile: File, cfg: Config, discovered: Discovered): Unit = {
    val (featureTitleRaw, scenarios) = parseFeature(inputFile)
    val featureTitle = if (featureTitleRaw.nonEmpty) featureTitleRaw else inputFile.getName.stripSuffix(".feature")
    val featureStem = toPascal(inputFile.getName.stripSuffix(".feature"))
    val specName = s"${featureStem}FeatureSpec"
    val relDir = relativeParent(cfg.featuresRoot, inputFile)
    val specPkg = packageFromRelative(basePkgFromPath(cfg.specsRoot), relDir)
    val outDir = new File(cfg.specsRoot, relDir)
    val outFile = new File(outDir, specName + ".scala")

    val context = bestContextForSpec(featureStem, relDir, discovered)
    val helpers = bestHelpersForSpec(featureStem, relDir, discovered, cfg.helpersRoot)

    val imports = scala.collection.mutable.LinkedHashSet.empty[String]
    imports += "org.scalatest.GivenWhenThen"
    imports += "org.scalatest.featurespec.FixtureAnyFeatureSpec"
    imports += "org.scalatest.matchers.should.Matchers"
    context.foreach(c => imports += s"${c.pkg}.${c.name}")
    helpers.foreach(h => imports += s"${h.pkg}.${h.name}")
    imports += "uk.gov.hmrc.test.api.scalatest.tags._"

    val contextName = context.map(_.name).getOrElse(s"${featureStem}Context")
    if (context.isEmpty) {
      imports += s"uk.gov.hmrc.test.api.scalatest.steps.context.$contextName"
    }

    val body = new StringBuilder
    body.append(s"package $specPkg\n\n")
    val scenarioBlocks = new StringBuilder

    scenarios.foreach { sc =>
      val tagArgs = if (sc.tags.nonEmpty) sc.tags.mkString(", ", ", ", "") else ""
      scenarioBlocks.append(s"    Scenario(\"${escape(sc.title)}\"$tagArgs) { context =>\n")
      sc.steps.foreach { step =>
        scenarioBlocks.append(s"      ${step.keyword}(\"${escape(step.text)}\")\n")
        methodForStep(step, helpers) match {
          case Some(method) =>
            scenarioBlocks.append(emitHelperCall(step, method, discovered, imports, "      ")).append("\n")
          case None =>
            scenarioBlocks.append(s"      // TODO: No matching helper method found for this step. Validate and call the correct helper.\n")
            if (step.table.nonEmpty) scenarioBlocks.append(s"      // TODO: This step had a feature table; convert the values into typed builder/model inputs.\n")
        }
        scenarioBlocks.append("\n")
      }
      scenarioBlocks.append("    }\n")
    }

    imports.toSeq.sorted.foreach(i => body.append(s"import $i\n"))
    body.append("\n")
    body.append(s"class $specName\n")
    body.append("  extends FixtureAnyFeatureSpec\n")
    body.append("    with GivenWhenThen\n")
    body.append("    with Matchers")
    helpers.foreach(h => body.append(s"\n    with ${h.name}"))
    body.append(" {\n\n")
    body.append(s"  override type FixtureParam = $contextName\n\n")
    body.append("  override def withFixture(test: OneArgTest) = {\n")
    body.append(s"    val context = $contextName()\n")
    body.append("    try test(context)\n")
    body.append("    finally ()\n")
    body.append("  }\n\n")
    body.append(s"  Feature(\"${escape(featureTitle)}\") {\n\n")
    body.append(scenarioBlocks.toString)
    body.append("  }\n")
    body.append("}\n")

    writeFile(outFile, body.toString)
    println(s"✓ Created ${outFile.getPath}")
  }

  // ---------------------------------------------------------------------------
  // CLI
  // ---------------------------------------------------------------------------
  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 2) {
      println("Usage: FeatureToScalaSpecConverter <features_root> <specs_root> --helpers-root <helpers_root> --context-root <context_root> --builders-root <builders_root> [--recurse]")
      System.exit(1)
    }

    val featuresRoot = new File(args(0))
    val specsRoot = new File(args(1))
    var helpersRoot: Option[File] = None
    var contextRoot: Option[File] = None
    var buildersRoot: Option[File] = None
    var recurse = false

    var i = 2
    while (i < args.length) {
      args(i) match {
        case "--helpers-root" if i + 1 < args.length => helpersRoot = Some(new File(args(i + 1))); i += 2
        case "--context-root" if i + 1 < args.length => contextRoot = Some(new File(args(i + 1))); i += 2
        case "--builders-root" if i + 1 < args.length => buildersRoot = Some(new File(args(i + 1))); i += 2
        case "--recurse" => recurse = true; i += 1
        case other =>
          println(s"ERROR: Unknown or incomplete argument: $other")
          System.exit(1)
      }
    }

    val cfg = Config(
      featuresRoot = featuresRoot,
      specsRoot = specsRoot,
      helpersRoot = helpersRoot.getOrElse(new File("src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/helpers")),
      contextRoot = contextRoot.getOrElse(new File("src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context")),
      buildersRoot = buildersRoot.getOrElse(new File("src/test/scala/uk/gov/hmrc/test/api/scalatest/builders")),
      recurse = recurse
    )

    Seq(cfg.featuresRoot -> "features_root", cfg.helpersRoot -> "helpers_root", cfg.contextRoot -> "context_root", cfg.buildersRoot -> "builders_root").foreach {
      case (f, label) if !f.exists() || !f.isDirectory =>
        println(s"ERROR: $label '${f.getPath}' not found or not a directory")
        System.exit(2)
      case _ => ()
    }

    if (!cfg.specsRoot.exists()) cfg.specsRoot.mkdirs()
    cfg
  }

  def main(args: Array[String]): Unit = {
    val cfg = parseArgs(args)
    val discovered = discover(cfg)

    println(s"Discovered ${discovered.helpers.size} helper file(s), ${discovered.contexts.size} context file(s), ${discovered.builders.size} builder file(s).")

    val features = listFiles(cfg.featuresRoot, f => f.isFile && f.getName.endsWith(".feature"), cfg.recurse)
    if (features.isEmpty) {
      println(s"⚠️ No .feature files found in ${cfg.featuresRoot.getPath}")
      return
    }

    features.foreach(f => emitSpec(f, cfg, discovered))
    println("Done.")
  }
}
