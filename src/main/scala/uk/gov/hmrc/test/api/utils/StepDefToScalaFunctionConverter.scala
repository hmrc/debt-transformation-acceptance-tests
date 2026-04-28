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
import scala.collection.mutable
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

/**
 * Converts legacy Cucumber step definitions into typed ScalaTest helper traits.
 *
 * Opinionated defaults:
 *   - generated helper files contain no Cucumber/DataTable imports or parameters
 *   - generated helper files contain no List[Map[String, String]] parameters
 *   - generated builder inputs are preferred over raw model case classes
 *   - existing model case classes are used when they match table fields cleanly
 *   - existing context fields are used where return types match
 *   - package names are derived from output paths
 *   - with --recurse, sub-packages under stepdefs_root are mirrored under helpers_root
 *
 * Usage:
 *   StepDefToScalaFunctionConverter <stepdefs_root> <helpers_root>
 *     --builders-root <builders_root>
 *     --models-root <models_root>
 *     --context-root <context_root>
 *     [--recurse]
 */
object StepDefToScalaFunctionConverter {

  // ---------------------------------------------------------------------------
  // Regexes
  // ---------------------------------------------------------------------------

  private val headerPattern: Regex =
    """(?s)(Given|When|Then|And)\s*\((.+?)\)\s*\{""".r

  private val classOrObjectNameRe: Regex =
    """\b(?:class|object)\s+([A-Za-z0-9_]+)\b""".r

  private val paramArrowRegex: Regex =
    """(?s)^\s*\(([^)]*)\)\s*=>\s*""".r

  private val datatableHintsRe: Regex =
    """(?s)\bDataTable\b|\.asMaps\s*\(|\.asMap\s*\(|transpose\(\)\.asMap\s*\(""".r

  private val singleRowTableRe: Regex =
    """(?s)\.asMap\s*\(|transpose\(\)\.asMap\s*\(""".r

  private val packageRe: Regex =
    """(?m)^\s*package\s+([^\n]+)\s*$""".r

  private val requestCallRe: Regex =
    """\b([A-Za-z0-9_]+Requests)\.([A-Za-z0-9_]+)\s*\(""".r

  private val requestObjectRefRe: Regex =
    """\b([A-Za-z0-9_]+Requests)\b""".r

  private val scenarioContextRefRe: Regex =
    """\bScenarioContext\b""".r

  private val jsonParseRe: Regex =
    """\bJson\.parse\(""".r

  private val responseStatusRe: Regex =
    """\bresponse\.status\s+should""".r

  private val responseCaptureRe: Regex =
    """(?s)\bval\s+response\s*=\s*([A-Za-z0-9_]+Requests)\.([A-Za-z0-9_]+)\s*\((.*?)\)""".r

  private val tableGetRe: Regex =
    """\.get\(\s*"([^"]+)"\s*\)""".r

  private val replacePlaceholderRe: Regex =
    """<REPLACE_([A-Za-z0-9_]+)>""".r

  private val builderObjectRe: Regex =
    """\bobject\s+([A-Za-z0-9_]+Builder)\b""".r

  private val inputCaseClassRe: Regex =
    """(?s)\bfinal\s+case\s+class\s+([A-Za-z0-9_]+Input)\s*\((.*?)\)""".r

  private val caseClassRe: Regex =
    """(?s)\b(?:final\s+)?case\s+class\s+([A-Za-z0-9_]+)\s*\((.*?)\)""".r

  private val toModelRe: Regex =
    """\bdef\s+(to[A-Za-z0-9_]+)\s*\(\s*in\s*:\s*([A-Za-z0-9_]+Input)\s*,\s*context\s*:[^)]+\)\s*:\s*([A-Za-z0-9_\.]+)""".r

  private val toModelSeqRe: Regex =
    """\bdef\s+(to[A-Za-z0-9_]+Seq)\s*\(\s*inputs\s*:\s*Seq\[([A-Za-z0-9_]+Input)\]\s*,\s*context\s*:[^)]+\)\s*:\s*Seq\[([A-Za-z0-9_\.]+)\]""".r

  private val contextClassRe: Regex =
    """(?s)\bfinal\s+case\s+class\s+([A-Za-z0-9_]+Context)\s*\((.*?)\)""".r

  private val fieldParamRe: Regex =
    """(?s)^\s*(?:val\s+|var\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*:\s*([^=,]+?)(?:\s*=\s*(.*))?$""".r

  // ---------------------------------------------------------------------------
  // Internal models
  // ---------------------------------------------------------------------------

  private case class Config(
                             stepdefsRoot: File,
                             helpersRoot: File,
                             buildersRoot: File,
                             modelsRoot: File,
                             contextRoot: File,
                             recurse: Boolean
                           )

  private case class LambdaParam(name: String, tpe: String)

  private case class StepBlock(keyword: String, rawStepText: String, body: String)

  private case class FieldDef(name: String, tpe: String, defaultValue: Option[String] = None) {
    def normalizedName: String = normalize(name)
    def simpleType: String = simpleTypeName(tpe)
    def isSeq: Boolean = tpe.trim.startsWith("Seq[") || tpe.trim.startsWith("List[")
    def isOption: Boolean = tpe.trim.startsWith("Option[")
  }

  private case class ModelClass(name: String, pkg: String, fields: Seq[FieldDef]) {
    def fqcn: String = s"$pkg.$name"
  }

  private case class ContextInfo(name: String, pkg: String, fields: Seq[FieldDef]) {
    def fqcn: String = s"$pkg.$name"
  }

  private case class BuilderInput(
                                   builderObject: String,
                                   builderPackage: String,
                                   inputClass: String,
                                   fields: Seq[FieldDef],
                                   toModelMethod: Option[String],
                                   toModelSeqMethod: Option[String],
                                   modelType: Option[String]
                                 ) {
    def builderFqcn: String = s"$builderPackage.$builderObject"
    def inputType: String = s"$builderObject.$inputClass"
    def inputBaseName: String = inputClass.stripSuffix("Input")
    def modelSimpleName: Option[String] = modelType.map(simpleTypeName)
  }

  private sealed trait TypedCandidate {
    def fieldNames: Set[String]
    def displayName: String
  }

  private case class BuilderCandidate(input: BuilderInput) extends TypedCandidate {
    override def fieldNames: Set[String] = input.fields.map(_.normalizedName).toSet
    override def displayName: String = s"${input.builderObject}.${input.inputClass}"
  }

  private case class ExistingModelCandidate(model: ModelClass) extends TypedCandidate {
    override def fieldNames: Set[String] = model.fields.map(_.normalizedName).toSet
    override def displayName: String = model.name
  }

  private case class ChosenContext(context: ContextInfo, matchedByStem: Boolean)

  // ---------------------------------------------------------------------------
  // IO / path helpers
  // ---------------------------------------------------------------------------

  private def readFile(f: File): String =
    Using.resource(Source.fromFile(f, "UTF-8"))(_.mkString)

  private def writeFile(f: File, content: String): Unit = {
    if (!f.getParentFile.exists()) f.getParentFile.mkdirs()
    Using.resource(new PrintWriter(f, "UTF-8"))(_.write(content))
  }

  private def listScalaFiles(dir: File, recurse: Boolean): Seq[File] = {
    val children = Option(dir.listFiles()).getOrElse(Array.empty)
    val (dirs, files) = children.partition(_.isDirectory)
    val here = files.filter(f => f.isFile && f.getName.endsWith(".scala")).toSeq
    if (recurse) here ++ dirs.flatMap(d => listScalaFiles(d, recurse = true)) else here
  }

  private def basePkgFromPath(root: File, fallback: String): String = {
    val abs = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx = abs.indexOf(marker)

    if (idx < 0) fallback
    else {
      val tail = abs.substring(idx + marker.length)
      val pkg = tail.split('/').filter(_.nonEmpty).map(normalisePackageSegment).mkString(".")
      if (pkg.nonEmpty) pkg else fallback
    }
  }

  private def normalisePackageSegment(seg: String): String = {
    val cleaned = seg.replaceAll("[^A-Za-z0-9_]", "")
    val safe = cleaned.replaceAll("^[^A-Za-z_]+", "")
    if (safe.nonEmpty) safe else "pkg"
  }

  private def relativeParentDir(inputRoot: File, inputFile: File): String = {
    val root = inputRoot.getCanonicalFile.toPath
    val parent = inputFile.getParentFile.getCanonicalFile.toPath
    val rel = root.relativize(parent).toString.replace('\\', '/')
    if (rel == ".") "" else rel
  }

  private def actualOutputDir(inputRoot: File, inputFile: File, outputRoot: File): File = {
    val rel = relativeParentDir(inputRoot, inputFile)
    if (rel.isEmpty) outputRoot else new File(outputRoot, rel.split('/').map(normalisePackageSegment).mkString(File.separator))
  }

  private def splitTopLevelArgs(s: String): Seq[String] = {
    val out = mutable.ListBuffer.empty[String]
    val cur = new StringBuilder

    var paren = 0
    var bracket = 0
    var brace = 0
    var inString = false
    var inTripleString = false
    var i = 0

    while (i < s.length) {
      if (!inString && i + 2 < s.length && s.substring(i, i + 3) == "\"\"\"") {
        inTripleString = !inTripleString
        cur.append("\"\"\"")
        i += 3
      } else {
        val ch = s.charAt(i)

        if (!inTripleString && ch == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
          inString = !inString
          cur.append(ch)
        } else if (!inString && !inTripleString) {
          ch match {
            case '(' =>
              paren += 1
              cur.append(ch)
            case ')' =>
              paren -= 1
              cur.append(ch)
            case '[' =>
              bracket += 1
              cur.append(ch)
            case ']' =>
              bracket -= 1
              cur.append(ch)
            case '{' =>
              brace += 1
              cur.append(ch)
            case '}' =>
              brace -= 1
              cur.append(ch)
            case ',' if paren == 0 && bracket == 0 && brace == 0 =>
              out += cur.toString.trim
              cur.clear()
            case _ =>
              cur.append(ch)
          }
        } else {
          cur.append(ch)
        }

        i += 1
      }
    }

    if (cur.nonEmpty) out += cur.toString.trim
    out.toSeq.filter(_.nonEmpty)
  }

  // ---------------------------------------------------------------------------
  // Parsing helpers
  // ---------------------------------------------------------------------------

  private def unquote(raw: String): String = {
    val t = raw.trim
    if (t.startsWith("\"\"\"") && t.endsWith("\"\"\"")) t.substring(3, t.length - 3)
    else if (t.startsWith("\"") && t.endsWith("\"")) t.substring(1, t.length - 1)
    else t
  }

  private def findMatchingBrace(src: String, openIndex: Int): Int = {
    if (openIndex < 0 || openIndex >= src.length || src.charAt(openIndex) != '{') return -1

    var i = openIndex + 1
    var depth = 1
    var inString = false
    var inTripleString = false

    while (i < src.length) {
      if (!inString && i + 2 < src.length && src.substring(i, i + 3) == "\"\"\"") {
        inTripleString = !inTripleString
        i += 3
      } else if (!inTripleString && src.charAt(i) == '"' && (i == 0 || src.charAt(i - 1) != '\\')) {
        inString = !inString
        i += 1
      } else if (!inString && !inTripleString) {
        src.charAt(i) match {
          case '{' =>
            depth += 1
          case '}' =>
            depth -= 1
            if (depth == 0) return i
          case _ =>
        }
        i += 1
      } else {
        i += 1
      }
    }

    -1
  }

  private def extractStepBlocks(src: String): Seq[StepBlock] =
    headerPattern.findAllMatchIn(src).toSeq.flatMap { m =>
      val openBrace = src.indexOf('{', m.start)
      val closeBrace = findMatchingBrace(src, openBrace)

      if (closeBrace < 0) None
      else Some(StepBlock(m.group(1), unquote(m.group(2)), src.substring(openBrace + 1, closeBrace)))
    }

  private def lambdaParamsInBody(body: String): Option[String] =
    paramArrowRegex.findFirstMatchIn(body).map(_.group(1).trim)

  private def stripLambdaPrefix(body: String): String =
    paramArrowRegex.replaceFirstIn(body.trim, "").trim

  private def parseLambdaParams(raw: String): Seq[LambdaParam] = {
    if (raw.trim.isEmpty) Seq.empty
    else {
      raw.split(",").toSeq.flatMap { part =>
        val bits = part.trim.split(":", 2).map(_.trim)
        if (bits.length == 2 && bits(0).nonEmpty && bits(1).nonEmpty) Some(LambdaParam(bits(0), bits(1)))
        else None
      }
    }
  }

  private def isDataTableType(tpe: String): Boolean =
    tpe.toLowerCase.contains("datatable")

  private def hasLegacyTable(body: String, params: Seq[LambdaParam]): Boolean =
    params.exists(p => isDataTableType(p.tpe)) || datatableHintsRe.findFirstIn(body).nonEmpty

  private def isSingleRowLegacyTable(body: String): Boolean =
    singleRowTableRe.findFirstIn(body).nonEmpty

  private def extractLegacyTableKeys(body: String): Set[String] = {
    val fromGet = tableGetRe.findAllMatchIn(body).map(_.group(1)).filter(isUsefulKey)
    val fromReplace = replacePlaceholderRe.findAllMatchIn(body).map(_.group(1)).filter(isUsefulKey)
    (fromGet ++ fromReplace).map(k => normalize(k)).toSet
  }

  private def isUsefulKey(k: String): Boolean =
    k.nonEmpty && k.exists(_.isLetterOrDigit) && !Set("-", "", " ").contains(k.trim)

  private def parseFields(raw: String): Seq[FieldDef] =
    splitTopLevelArgs(raw).flatMap { param =>
      val cleaned = param.replaceAll("(?m)//.*$", "").trim.stripSuffix(",")

      fieldParamRe.findFirstMatchIn(cleaned).map { m =>
        FieldDef(
          name = m.group(1).trim,
          tpe = m.group(2).trim,
          defaultValue = Option(m.group(3)).map(_.trim).filter(_.nonEmpty)
        )
      }
    }

  // ---------------------------------------------------------------------------
  // Naming
  // ---------------------------------------------------------------------------

  private def classStemFromSource(src: String, fileName: String): String = {
    val fileStem = fileName.stripSuffix(".scala")
    val rawName = classOrObjectNameRe.findFirstMatchIn(src).map(_.group(1)).getOrElse(fileStem)

    val stem = rawName
      .stripSuffix("StepDefinitions")
      .stripSuffix("StepDefinition")
      .stripSuffix("StepDefs")
      .stripSuffix("StepDef")
      .stripSuffix("Steps")
      .stripSuffix("Step")

    if (stem.nonEmpty) stem else fileStem
  }

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

  private def scalaTypeForGeneratedParam(tpe: String): String = {
    val n = tpe.trim

    n match {
      case "Int" | "Long" | "Double" | "Float" | "Boolean" | "String" => n
      case other if other.endsWith(".Int")     => "Int"
      case other if other.endsWith(".Long")    => "Long"
      case other if other.endsWith(".Double")  => "Double"
      case other if other.endsWith(".Float")   => "Float"
      case other if other.endsWith(".Boolean") => "Boolean"
      case other if other.endsWith(".String")  => "String"
      case _                                   => "String"
    }
  }

  private def countCaptureGroups(pattern: String): Int =
    """(?<!\\)\((?!\?:)""".r.findAllMatchIn(pattern).length

  private def buildCapturedParams(stepText: String, groupCount: Int, lambdaParams: Seq[LambdaParam]): Seq[(String, String)] = {
    val nonTableParams = lambdaParams.filterNot(p => isDataTableType(p.tpe))

    if (nonTableParams.nonEmpty) nonTableParams.map(p => p.name -> scalaTypeForGeneratedParam(p.tpe))
    else if (groupCount <= 0) Seq.empty
    else {
      val lower = stepText.toLowerCase

      if (groupCount == 1 && lower.contains("payment plan")) Seq("paymentPlanIndex" -> "Int")
      else if (groupCount == 1 && lower.contains("month")) Seq("month" -> "String")
      else if (groupCount == 1 && lower.contains("date")) Seq("dateValue" -> "String")
      else if (groupCount == 1 && lower.contains("amount")) Seq("amount" -> "String")
      else (1 to groupCount).map(i => s"p$i" -> "String")
    }
  }

  private def normalize(s: String): String =
    s.toLowerCase.replaceAll("[^a-z0-9]+", "")

  private def tokens(s: String): Set[String] =
    s.replaceAll("([A-Z])", " $1")
      .replaceAll("[^A-Za-z0-9]+", " ")
      .trim
      .split("\\s+")
      .filter(_.nonEmpty)
      .map(_.toLowerCase)
      .toSet

  private def simpleTypeName(tpe: String): String = {
    val trimmed = tpe.trim

    val noOption =
      if (trimmed.startsWith("Option[") && trimmed.endsWith("]")) trimmed.substring(7, trimmed.length - 1)
      else if (trimmed.startsWith("Seq[") && trimmed.endsWith("]")) trimmed.substring(4, trimmed.length - 1)
      else if (trimmed.startsWith("List[") && trimmed.endsWith("]")) trimmed.substring(5, trimmed.length - 1)
      else trimmed

    noOption.split('.').last.trim
  }

  // ---------------------------------------------------------------------------
  // Discovery
  // ---------------------------------------------------------------------------

  private def discoverModels(root: File): Seq[ModelClass] = {
    if (!root.exists() || !root.isDirectory) return Seq.empty

    listScalaFiles(root, recurse = true).flatMap { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(basePkgFromPath(f.getParentFile, ""))

      caseClassRe.findAllMatchIn(src).map { m =>
        ModelClass(m.group(1), pkg, parseFields(m.group(2)))
      }
    }
  }

  private def discoverContexts(root: File): Seq[ContextInfo] = {
    if (!root.exists() || !root.isDirectory) return Seq.empty

    listScalaFiles(root, recurse = true).flatMap { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(basePkgFromPath(f.getParentFile, ""))

      contextClassRe.findAllMatchIn(src).map { m =>
        ContextInfo(m.group(1), pkg, parseFields(m.group(2)))
      }
    }
  }

  private def discoverBuilders(root: File): Seq[BuilderInput] = {
    if (!root.exists() || !root.isDirectory) return Seq.empty

    listScalaFiles(root, recurse = true).flatMap { f =>
      val src = readFile(f)
      val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse(basePkgFromPath(f.getParentFile, ""))
      val builderObjectOpt = builderObjectRe.findFirstMatchIn(src).map(_.group(1))

      builderObjectOpt.toSeq.flatMap { builderObject =>
        val inputClasses = inputCaseClassRe.findAllMatchIn(src).map { m =>
          m.group(1) -> parseFields(m.group(2))
        }.toSeq.distinct

        val toModelByInput = toModelRe.findAllMatchIn(src).map { m =>
          m.group(2) -> (m.group(1), simpleTypeName(m.group(3)))
        }.toMap

        val toModelSeqByInput = toModelSeqRe.findAllMatchIn(src).map { m =>
          m.group(2) -> (m.group(1), simpleTypeName(m.group(3)))
        }.toMap

        inputClasses.map { case (inputClass, fields) =>
          val modelFromOne = toModelByInput.get(inputClass).map(_._2)
          val modelFromSeq = toModelSeqByInput.get(inputClass).map(_._2)

          BuilderInput(
            builderObject = builderObject,
            builderPackage = pkg,
            inputClass = inputClass,
            fields = fields,
            toModelMethod = toModelByInput.get(inputClass).map(_._1),
            toModelSeqMethod = toModelSeqByInput.get(inputClass).map(_._1),
            modelType = modelFromOne.orElse(modelFromSeq)
          )
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Matching
  // ---------------------------------------------------------------------------

  private def fieldOverlapScore(tableKeys: Set[String], candidateFields: Set[String]): Double = {
    if (tableKeys.isEmpty || candidateFields.isEmpty) 0.0
    else {
      val matched = tableKeys.intersect(candidateFields).size.toDouble
      val precision = matched / tableKeys.size.toDouble
      val recall = matched / candidateFields.size.toDouble
      Math.max(precision, recall) * 0.8 + Math.min(precision, recall) * 0.2
    }
  }

  private def textScore(stepText: String, names: Seq[String]): Double = {
    val stepNorm = normalize(stepText)
    val stepTokens = tokens(stepText)

    val scores = names.filter(_.nonEmpty).map { n =>
      val norm = normalize(n)

      val contains =
        if (norm.nonEmpty && stepNorm.contains(norm)) 1.0
        else if (stepNorm.nonEmpty && norm.contains(stepNorm)) 0.65
        else 0.0

      val ntokens = tokens(n)

      val overlap =
        if (ntokens.isEmpty) 0.0
        else (stepTokens intersect ntokens).size.toDouble / ntokens.size.toDouble

      Math.max(contains, overlap)
    }

    if (scores.nonEmpty) scores.max else 0.0
  }

  private def builderCandidateScore(stepText: String, tableKeys: Set[String], input: BuilderInput): Double = {
    val f = fieldOverlapScore(tableKeys, input.fields.map(_.normalizedName).toSet)
    val t = textScore(stepText, Seq(input.inputClass.stripSuffix("Input"), input.builderObject.stripSuffix("Builder")) ++ input.modelType.toSeq)

    // Prefer builder inputs over raw models with a small boost.
    (f * 0.75) + (t * 0.25) + 0.05
  }

  private def modelCandidateScore(stepText: String, tableKeys: Set[String], model: ModelClass): Double = {
    val f = fieldOverlapScore(tableKeys, model.fields.map(_.normalizedName).toSet)
    val t = textScore(stepText, Seq(model.name))
    (f * 0.8) + (t * 0.2)
  }

  private def bestTypedCandidate(
                                  stepText: String,
                                  tableKeys: Set[String],
                                  builders: Seq[BuilderInput],
                                  models: Seq[ModelClass]
                                ): Option[TypedCandidate] = {
    val builderScores = builders.map(b => BuilderCandidate(b) -> builderCandidateScore(stepText, tableKeys, b))
    val modelScores = models.map(m => ExistingModelCandidate(m) -> modelCandidateScore(stepText, tableKeys, m))

    val scored = (builderScores ++ modelScores).sortBy(-_._2)

    scored.headOption.collect {
      case (candidate, score) if score >= 0.28 => candidate
    }
  }

  private def chooseContext(stem: String, outDir: File, contexts: Seq[ContextInfo]): Option[ChosenContext] = {
    val targetName = s"${stem}Context"

    contexts.find(_.name == targetName).map(c => ChosenContext(c, matchedByStem = true)).orElse {
      val outPkg = basePkgFromPath(outDir, "")
      val expectedContextPkg = outPkg.replace(".steps.helpers", ".steps.context")
      val samePackage = contexts.filter(c => c.pkg == expectedContextPkg)

      samePackage.headOption
        .map(c => ChosenContext(c, matchedByStem = false))
        .orElse(contexts.headOption.map(c => ChosenContext(c, matchedByStem = false)))
    }
  }

  private def contextFieldForReturnType(context: ContextInfo, returnType: String, preferSeq: Boolean): Option[FieldDef] = {
    val simple = simpleTypeName(returnType)

    context.fields.find { f =>
      f.simpleType == simple && (!preferSeq || f.isSeq)
    }.orElse {
      context.fields.find(_.simpleType == simple)
    }
  }

  private def contextFieldNameForModel(modelType: String): String = {
    val base = simpleTypeName(modelType).replaceAll("Request$", "").replaceAll("Response$", "")
    val field = base.headOption.map(_.toLower).getOrElse('x').toString + base.drop(1)
    if (field.endsWith("Charge")) field + "s" else field
  }

  private def isLikelyRequestModel(modelType: String): Boolean = {
    val s = simpleTypeName(modelType).toLowerCase
    s.endsWith("request") || s.contains("request")
  }

  private def canJsonSerialize(tpe: String): Boolean =
    isLikelyRequestModel(tpe)

  // ---------------------------------------------------------------------------
  // Method-body emitters
  // ---------------------------------------------------------------------------

  private def emitSendRequestBody(body: String): Option[(String, String)] = {
    responseCaptureRe.findFirstMatchIn(body).map { m =>
      val requestObject = m.group(1)
      val method = m.group(2)
      val args = m.group(3).trim

      val finalArgs =
        if (args.isEmpty) "context.reqJson"
        else if (args.contains("reqJson") || args.contains("json")) "context.reqJson"
        else "context.reqJson"

      val emitted =
        s"""    val response = $requestObject.$method($finalArgs)
           |
           |    context.status = response.status
           |    context.respBody = response.body
           |    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
           |""".stripMargin

      requestObject -> emitted
    }
  }

  private def emitBuilderBackedBody(input: BuilderInput, isSingleRow: Boolean, context: ContextInfo): (String, Boolean) = {
    input.modelType match {
      case Some(modelType) if isLikelyRequestModel(modelType) && isSingleRow && input.toModelMethod.nonEmpty =>
        val body =
          s"""    val req = ${input.builderObject}.${input.toModelMethod.get}(input, context)
             |    context.reqJson = Json.stringify(Json.toJson(req))
             |""".stripMargin

        body -> true

      case Some(modelType) if !isSingleRow && input.toModelSeqMethod.nonEmpty =>
        val field = contextFieldForReturnType(context, modelType, preferSeq = true)
          .map(_.name)
          .getOrElse(contextFieldNameForModel(modelType))

        val body =
          s"""    context.$field = ${input.builderObject}.${input.toModelSeqMethod.get}(inputs, context)
             |""".stripMargin

        body -> false

      case Some(modelType) if isSingleRow && input.toModelMethod.nonEmpty =>
        val field = contextFieldForReturnType(context, modelType, preferSeq = false)
          .map(_.name)
          .getOrElse(contextFieldNameForModel(modelType))

        val body =
          s"""    context.$field = ${input.builderObject}.${input.toModelMethod.get}(input, context)
             |""".stripMargin

        body -> false

      case _ =>
        val paramName = if (isSingleRow) "input" else "inputs"

        val body =
          s"""    // TODO: Wire $paramName into context or request JSON using ${input.builderObject}.
             |    // Suggested type: ${input.builderObject}.${input.inputClass}
             |""".stripMargin

        body -> false
    }
  }

  private def emitModelBackedBody(model: ModelClass, isSingleRow: Boolean, context: ContextInfo): (String, Boolean) = {
    if (canJsonSerialize(model.name) && isSingleRow) {
      val body =
        s"""    context.reqJson = Json.stringify(Json.toJson(input))
           |""".stripMargin

      body -> true
    } else if (!isSingleRow) {
      val field = contextFieldForReturnType(context, model.name, preferSeq = true)
        .map(_.name)
        .getOrElse(contextFieldNameForModel(model.name))

      val body =
        s"""    context.$field = inputs
           |""".stripMargin

      body -> false
    } else {
      val field = contextFieldForReturnType(context, model.name, preferSeq = false)
        .map(_.name)
        .getOrElse(contextFieldNameForModel(model.name))

      val body =
        s"""    context.$field = input
           |""".stripMargin

      body -> false
    }
  }

  private def bodyPreview(body: String, maxLines: Int = 4): Seq[String] =
    body
      .lines
      .map(_.trim)
      .filter(_.nonEmpty)
      .filterNot(_.startsWith("//"))
      .filterNot(_.startsWith("/*"))
      .filterNot(_.startsWith("*/"))
      .filterNot(_.matches("""^\([^)]*\)\s*=>\s*$"""))
      .filterNot(line => datatableHintsRe.findFirstIn(line).nonEmpty)
      .take(maxLines)
      .toSeq

  private def escapeComment(s: String): String =
    s.replace("*/", "* /").replace("\n", " ").replace("\r", " ")

  // ---------------------------------------------------------------------------
  // Conversion
  // ---------------------------------------------------------------------------

  private def convertOneFile(
                              inputRoot: File,
                              inputFile: File,
                              outputRoot: File,
                              builders: Seq[BuilderInput],
                              models: Seq[ModelClass],
                              contexts: Seq[ContextInfo]
                            ): Unit = {
    val src = readFile(inputFile)
    val stepBlocks = extractStepBlocks(src)

    if (stepBlocks.isEmpty) {
      println(s"• Skipping ${inputFile.getPath}: no Cucumber step headers detected")
      return
    }

    val stem = classStemFromSource(src, inputFile.getName)
    val helperTrait = s"${stem}StepHelpers"
    val outDir = actualOutputDir(inputRoot, inputFile, outputRoot)
    val pkg = basePkgFromPath(outDir, "uk.gov.hmrc.test.api.scalatest.steps.helpers")
    val outFile = new File(outDir, helperTrait + ".scala")

    val chosenContext: ChosenContext =
      chooseContext(stem, outDir, contexts).getOrElse {
        val fallbackPkg = pkg.replace(".steps.helpers", ".steps.context")

        ChosenContext(
          context = ContextInfo(s"${stem}Context", fallbackPkg, Seq.empty),
          matchedByStem = false
        )
      }

    val contextType = chosenContext.context.name

    val usedBuilderObjects = mutable.LinkedHashSet.empty[String]
    val usedModelImports = mutable.LinkedHashSet.empty[String]
    val usedRequestObjects = mutable.LinkedHashSet.empty[String]
    val methodNamesSeen = mutable.Map.empty[String, Int]
    val renderedMethods = new StringBuilder
    var needsJsonImport = false

    stepBlocks.foreach { step =>
      val stepText = step.rawStepText.trim
      val body = stripLambdaPrefix(step.body)
      val lambdaParams = lambdaParamsInBody(step.body).map(parseLambdaParams).getOrElse(Seq.empty)

      val hasTable = hasLegacyTable(body, lambdaParams)
      val isSingleRow = isSingleRowLegacyTable(body)
      val tableKeys = extractLegacyTableKeys(body)
      val groupCount = countCaptureGroups(stepText)
      val capturedParams = buildCapturedParams(stepText, groupCount, lambdaParams)

      val baseMethodName = stepTextToMethodName(stepText)
      val occurrence = methodNamesSeen.getOrElse(baseMethodName, 0)
      methodNamesSeen(baseMethodName) = occurrence + 1

      val methodName =
        if (occurrence == 0) baseMethodName
        else s"${baseMethodName}${occurrence + 1}"

      val candidate =
        if (hasTable) bestTypedCandidate(stepText, tableKeys, builders, models)
        else None

      val typedDataParams = candidate.toSeq.map {
        case BuilderCandidate(bi) =>
          usedBuilderObjects += bi.builderFqcn
          if (isSingleRow) s"input: ${bi.inputType}" else s"inputs: Seq[${bi.inputType}]"

        case ExistingModelCandidate(model) =>
          usedModelImports += model.fqcn
          if (isSingleRow) s"input: ${model.name}" else s"inputs: Seq[${model.name}]"
      }

      val params =
        (Seq(s"context: $contextType") ++ capturedParams.map { case (n, t) => s"$n: $t" } ++ typedDataParams)
          .mkString(", ")

      val methodBody =
        if (hasTable) {
          candidate match {
            case Some(BuilderCandidate(builderInput)) =>
              val (emitted, json) = emitBuilderBackedBody(builderInput, isSingleRow, chosenContext.context)
              if (json) needsJsonImport = true
              emitted

            case Some(ExistingModelCandidate(model)) =>
              val (emitted, json) = emitModelBackedBody(model, isSingleRow, chosenContext.context)
              if (json) needsJsonImport = true
              emitted

            case None =>
              val preview = bodyPreview(body)

              val previewBlock =
                if (preview.nonEmpty) preview.map(l => s"    // ${escapeComment(l)}").mkString("\n") + "\n"
                else ""

              val keysLine =
                if (tableKeys.nonEmpty) s"    // Inferred legacy table keys: ${tableKeys.toSeq.sorted.mkString(", ")}\n"
                else ""

              s"""$previewBlock$keysLine    // TODO: No matching generated builder input or existing model was found.
                 |    // Add a typed parameter and wire it into context or request JSON.
                 |""".stripMargin
          }
        } else {
          emitSendRequestBody(body) match {
            case Some((requestObject, emitted)) =>
              usedRequestObjects += requestObject
              emitted

            case None =>
              val hints = Seq(
                if (scenarioContextRefRe.findFirstIn(body).nonEmpty) Some("legacy ScenarioContext usage") else None,
                if (jsonParseRe.findFirstIn(body).nonEmpty || responseStatusRe.findFirstIn(body).nonEmpty) Some("response assertion") else None,
                requestObjectRefRe.findFirstMatchIn(body).map(m => s"request object reference: ${m.group(1)}")
              ).flatten

              val hintLine =
                if (hints.nonEmpty) s"    // Migration hint: ${hints.mkString(", ")}\n"
                else ""

              val preview = bodyPreview(body)

              val previewBlock =
                if (preview.nonEmpty) preview.map(l => s"    // ${escapeComment(l)}").mkString("\n") + "\n"
                else ""

              s"""$hintLine$previewBlock    // TODO: Implement typed helper for this step.
                 |""".stripMargin
          }
        }

      renderedMethods ++= s"  // ^$stepText$$\n"
      renderedMethods ++= s"  def $methodName($params): Unit = {\n"
      renderedMethods ++= methodBody
      renderedMethods ++= "  }\n\n"
    }

    val imports = mutable.LinkedHashSet.empty[String]

    imports += "org.scalatest.matchers.should.Matchers"
    if (needsJsonImport) imports += "play.api.libs.json.Json"

    imports += chosenContext.context.fqcn

    usedBuilderObjects.toSeq.sorted.foreach(i => imports += i)
    usedModelImports.toSeq.sorted.foreach(i => imports += i)
    usedRequestObjects.toSeq.sorted.foreach(ro => imports += s"uk.gov.hmrc.test.api.requests.$ro")

    val validationComment =
      if (!chosenContext.matchedByStem)
        s"// TODO: Validate that ${chosenContext.context.name} is the correct context for helpers migrated from ${inputFile.getName}.\n"
      else ""

    val content = new StringBuilder

    content ++= s"package $pkg\n\n"
    imports.foreach(i => content ++= s"import $i\n")
    content ++= "\n"
    content ++= validationComment
    content ++= s"trait $helperTrait { this: Matchers =>\n\n"
    content ++= renderedMethods.toString
    content ++= "}\n"

    writeFile(outFile, content.toString)
    println(s"✓ Generated ${outFile.getPath} with ${stepBlocks.size} method(s)")
  }

  // ---------------------------------------------------------------------------
  // CLI
  // ---------------------------------------------------------------------------

  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 2) {
      println(
        "Usage: StepDefToScalaFunctionConverter <stepdefs_root> <helpers_root> " +
          "--builders-root <builders_root> --models-root <models_root> --context-root <context_root> [--recurse]"
      )
      System.exit(1)
    }

    val stepdefsRoot = new File(args(0))
    val helpersRoot = new File(args(1))

    var buildersRoot: Option[File] = None
    var modelsRoot: Option[File] = None
    var contextRoot: Option[File] = None
    var recurse = false

    var i = 2

    while (i < args.length) {
      args(i) match {
        case "--builders-root" if i + 1 < args.length =>
          buildersRoot = Some(new File(args(i + 1)))
          i += 2

        case "--models-root" if i + 1 < args.length =>
          modelsRoot = Some(new File(args(i + 1)))
          i += 2

        case "--context-root" if i + 1 < args.length =>
          contextRoot = Some(new File(args(i + 1)))
          i += 2

        case "--recurse" =>
          recurse = true
          i += 1

        case other =>
          println(s"ERROR: unknown or incomplete argument: $other")
          System.exit(1)
      }
    }

    if (buildersRoot.isEmpty) {
      println("ERROR: --builders-root is required")
      System.exit(1)
    }

    if (modelsRoot.isEmpty) {
      println("ERROR: --models-root is required")
      System.exit(1)
    }

    if (contextRoot.isEmpty) {
      println("ERROR: --context-root is required")
      System.exit(1)
    }

    Config(stepdefsRoot, helpersRoot, buildersRoot.get, modelsRoot.get, contextRoot.get, recurse)
  }

  def main(args: Array[String]): Unit = {
    val cfg = parseArgs(args)

    if (!cfg.stepdefsRoot.exists() || !cfg.stepdefsRoot.isDirectory) {
      println(s"ERROR: stepdefs root ${cfg.stepdefsRoot.getPath} not found or not a directory")
      System.exit(2)
    }

    if (!cfg.buildersRoot.exists() || !cfg.buildersRoot.isDirectory) {
      println(s"ERROR: builders root ${cfg.buildersRoot.getPath} not found or not a directory")
      System.exit(2)
    }

    if (!cfg.modelsRoot.exists() || !cfg.modelsRoot.isDirectory) {
      println(s"ERROR: models root ${cfg.modelsRoot.getPath} not found or not a directory")
      System.exit(2)
    }

    if (!cfg.contextRoot.exists() || !cfg.contextRoot.isDirectory) {
      println(s"ERROR: context root ${cfg.contextRoot.getPath} not found or not a directory")
      System.exit(2)
    }

    if (!cfg.helpersRoot.exists()) cfg.helpersRoot.mkdirs()

    val builders = discoverBuilders(cfg.buildersRoot)
    val models = discoverModels(cfg.modelsRoot)
    val contexts = discoverContexts(cfg.contextRoot)

    println(s"• Discovered ${builders.size} generated builder input(s) from ${cfg.buildersRoot.getPath}")
    println(s"• Discovered ${models.size} model case class(es) from ${cfg.modelsRoot.getPath}")
    println(s"• Discovered ${contexts.size} context case class(es) from ${cfg.contextRoot.getPath}")

    val files = listScalaFiles(cfg.stepdefsRoot, cfg.recurse)

    if (files.isEmpty) {
      println(s"⚠️ No .scala stepdef files found in ${cfg.stepdefsRoot.getPath}")
    }

    files.foreach { f =>
      val src = readFile(f)

      if (headerPattern.findFirstIn(src).nonEmpty) {
        convertOneFile(cfg.stepdefsRoot, f, cfg.helpersRoot, builders, models, contexts)
      } else {
        println(s"• Skipping ${f.getPath}: no Cucumber step headers detected")
      }
    }

    println("Done.")
  }
}