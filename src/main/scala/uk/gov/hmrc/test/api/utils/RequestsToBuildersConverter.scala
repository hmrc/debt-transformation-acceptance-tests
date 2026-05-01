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

/** Typed-first converter for legacy request objects.
  *
  * Goals:
  *   - Generate ScalaTest builder objects from legacy request classes.
  *   - Use legacy DataTable methods only for inference.
  *   - Do NOT emit Cucumber/DataTable/Map adapter code.
  *   - Prefer existing model case classes where constructor calls are detectable.
  *   - Preserve safe HTTP client methods that return StandaloneWSResponse.
  *
  * Usage: RequestsToBuildersConverter <requests_root> <builders_root> --models-root <models_root> --context-root
  * <context_root> [--recurse]
  */
object RequestsToBuildersConverter {

  // ---------------------------------------------------------------------------
  // Regexes
  // ---------------------------------------------------------------------------

  private val packageRe: Regex =
    """(?m)^\s*package\s+([^\n]+)\s*$""".r

  private val objectNameRe: Regex =
    """\bobject\s+([A-Za-z0-9_]+)\b""".r

  private val dtMethodHeaderRe: Regex =
    """\bdef\s+([A-Za-z0-9_]+)\s*\(([^)]*DataTable[^)]*)\)\s*:\s*Unit\s*=\s*\{""".r

  private val clientMethodHeaderRe: Regex =
    """\bdef\s+([A-Za-z0-9_]+)\s*\(([^)]*)\)\s*:\s*StandaloneWSResponse\s*=\s*\{""".r

  private val methodHeaderRe: Regex =
    """\bdef\s+([A-Za-z0-9_]+)\s*\(([^)]*)\)\s*(?::\s*([A-Za-z0-9_\.\[\]]+))?\s*=\s*\{""".r

  private val valAssignRe: Regex =
    """^\s*val\s+([A-Za-z_][A-Za-z0-9_]*)\s*(?::\s*([^=\n]+?))?\s*=\s*(.+?)\s*$""".r

  private val caseClassRe: Regex =
    """(?s)\b(?:final\s+)?case\s+class\s+([A-Za-z0-9_]+)\s*\((.*?)\)\s*""".r

  private val getKeyRe: Regex =
    """\.get\(\s*"([^"]+)"\s*\)""".r

  private val containsKeyRe: Regex =
    """(?:\.contains|\.containsKey)\(\s*"([^"]+)"\s*\)""".r

  private val replacePlaceholderRe: Regex =
    """"<REPLACE_([^">]+)>"""".r

  private val scenarioContextSetRe: Regex =
    """ScenarioContext\.\w+\(\s*"([^"]+)"\s*,\s*(.*?)\s*\)""".r

  private val scenarioContextGetRe: Regex =
    """ScenarioContext\.\w+\(\s*"([^"]+)"\s*\)""".r

  private val wsClientCallRe: Regex =
    """\bWsClient\.\w+\s*\(""".r

  private val constructorStartRe: Regex =
    """\b([A-Z][A-Za-z0-9_]*)\s*\(""".r

  // ---------------------------------------------------------------------------
  // Models
  // ---------------------------------------------------------------------------

  private case class Config(
    requestsRoot: File,
    buildersRoot: File,
    modelsRoot: File,
    contextRoot: File,
    recurse: Boolean
  )

  private case class KeyInfo(name: String, tpe: String)
  private case class ClientMethod(name: String, params: String, body: String)
  private case class ContextField(name: String, tpe: String, exact: Boolean = false)
  private case class ModelField(name: String, tpe: String, defaultOpt: Option[String])
  private case class ModelDef(name: String, pkg: String, fields: Seq[ModelField])
  private case class ConstructorCall(modelName: String, args: Seq[String])
  private case class RenderedExpr(expr: String, risky: Boolean, note: Option[String] = None)

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
    val kids  = Option(dir.listFiles()).toSeq.flatten
    val dirs  = kids.filter(_.isDirectory)
    val files = kids.filter(f => f.isFile && f.getName.endsWith(".scala"))
    if (recurse) files ++ dirs.flatMap(d => listScalaFiles(d, recurse = true)) else files
  }

  private def basePkgFromPath(root: File): String = {
    val abs    = root.getCanonicalPath.replace('\\', '/')
    val marker = "/src/test/scala/"
    val idx    = abs.indexOf(marker)
    val tail   = if (idx >= 0) abs.substring(idx + marker.length) else abs
    tail.split('/').filter(_.nonEmpty).mkString(".")
  }

  // ---------------------------------------------------------------------------
  // Parsing helpers
  // ---------------------------------------------------------------------------

  private def stripRequestsSuffix(name: String): String =
    name.stripSuffix("Requests").stripSuffix("Request")

  private def toPascal(s: String): String = {
    val cleaned = s.replaceAll("[^A-Za-z0-9]+", " ").trim
    val parts   =
      if (cleaned.isEmpty) Seq.empty
      else cleaned.split("\\s+").toSeq.flatMap(_.split("(?<=[a-z0-9])(?=[A-Z])")).filter(_.nonEmpty)

    if (parts.isEmpty) s else parts.map(p => p.head.toUpper + p.tail).mkString
  }

  private def toCamel(s: String): String = {
    val p = toPascal(s)
    if (p.isEmpty) p else p.head.toLower + p.tail
  }

  private def methodStem(name: String): String =
    toPascal(name.replaceFirst("^(add|create|set|get|put|post|build|make|update|prepare|generate)", ""))

  private def normalizeType(tpe: String): String =
    tpe.trim.replaceAll("\\s+", " ").stripSuffix(",").stripSuffix(";")

  private def isPrimitive(tpe: String): Boolean =
    Set("String", "Int", "Long", "Boolean", "Double", "BigDecimal", "LocalDate").contains(normalizeType(tpe))

  private def isOption(tpe: String): Boolean = {
    val t = normalizeType(tpe)
    t.startsWith("Option[") && t.endsWith("]")
  }

  private def optionInner(tpe: String): Option[String] = {
    val t = normalizeType(tpe)
    if (isOption(t)) Some(t.substring(7, t.length - 1).trim) else None
  }

  private def splitTopLevelArgs(s: String): Seq[String] = {
    val out      = scala.collection.mutable.ListBuffer.empty[String]
    val cur      = new StringBuilder
    var paren    = 0
    var bracket  = 0
    var brace    = 0
    var inString = false
    var i        = 0

    while (i < s.length) {
      val ch = s.charAt(i)
      if (ch == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
        inString = !inString
        cur.append(ch)
      } else if (!inString) {
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
      } else cur.append(ch)
      i += 1
    }

    if (cur.nonEmpty) out += cur.toString.trim
    out.toList.filter(_.nonEmpty)
  }

  private def findMatchingBrace(src: String, openIndex: Int): Int = {
    var i        = openIndex + 1
    var depth    = 1
    var inString = false
    while (i < src.length) {
      val ch = src.charAt(i)
      if (ch == '"' && (i == 0 || src.charAt(i - 1) != '\\')) inString = !inString
      if (!inString) {
        ch match {
          case '{' => depth += 1
          case '}' =>
            depth -= 1
            if (depth == 0) return i
          case _   =>
        }
      }
      i += 1
    }
    -1
  }

  private def sliceMethodBody(src: String, headerStartIdx: Int): Option[(Int, Int)] = {
    val open = src.indexOf('{', headerStartIdx)
    if (open < 0) None
    else {
      val close = findMatchingBrace(src, open)
      if (close < 0) None else Some((open + 1, close))
    }
  }

  private def sliceParenBlock(src: String, openIdx: Int): Option[(Int, Int)] = {
    if (openIdx < 0 || openIdx >= src.length || src.charAt(openIdx) != '(') return None
    var i        = openIdx + 1
    var depth    = 1
    var inString = false
    while (i < src.length) {
      val ch = src.charAt(i)
      if (ch == '"' && (i == 0 || src.charAt(i - 1) != '\\')) inString = !inString
      if (!inString) {
        ch match {
          case '(' => depth += 1
          case ')' =>
            depth -= 1
            if (depth == 0) return Some((openIdx, i))
          case _   =>
        }
      }
      i += 1
    }
    None
  }

  private def objectBody(src: String): Option[String] =
    objectNameRe.findFirstMatchIn(src).flatMap { m =>
      val open = src.indexOf('{', m.end)
      if (open < 0) None
      else {
        val close = findMatchingBrace(src, open)
        if (close < 0) None else Some(src.substring(open + 1, close))
      }
    }

  private def removeMethodBodies(src: String): String = {
    val matches = methodHeaderRe.findAllMatchIn(src).toList
    if (matches.isEmpty) src
    else {
      val ranges = matches.flatMap { m =>
        sliceMethodBody(src, m.start).map { case (_, close) => m.start -> (close + 1) }
      }
      val sb     = new StringBuilder
      var last   = 0
      ranges.sortBy(_._1).foreach { case (start, end) =>
        if (start >= last) {
          sb.append(src.substring(last, start))
          last = end
        }
      }
      sb.append(src.substring(last))
      sb.toString
    }
  }

  // ---------------------------------------------------------------------------
  // Model parsing
  // ---------------------------------------------------------------------------

  private def parseModelDefs(modelsRoot: File): Map[String, Seq[ModelDef]] = {
    if (!modelsRoot.exists() || !modelsRoot.isDirectory) return Map.empty

    listScalaFiles(modelsRoot, recurse = true)
      .flatMap { f =>
        val src = readFile(f)
        val pkg = packageRe.findFirstMatchIn(src).map(_.group(1).trim).getOrElse("")

        caseClassRe.findAllMatchIn(src).flatMap { m =>
          val name      = m.group(1)
          val rawParams = m.group(2)

          val fields = splitTopLevelArgs(rawParams).flatMap { p =>
            val noComment = p.replaceAll("""//.*$""", "").trim
            val parts     = noComment.split(":", 2).map(_.trim)
            if (parts.length != 2) None
            else {
              val fieldName  = parts(0).stripPrefix("val ").stripPrefix("var ").trim
              val rhs        = parts(1)
              val eqSplit    = rhs.split("=", 2).map(_.trim)
              val tpe        = normalizeType(eqSplit(0))
              val defaultOpt = if (eqSplit.length == 2) Some(eqSplit(1)) else None
              if (fieldName.matches("[A-Za-z_][A-Za-z0-9_]*")) Some(ModelField(fieldName, tpe, defaultOpt))
              else None
            }
          }

          Some(ModelDef(name, pkg, fields))
        }
      }
      .groupBy(_.name)
  }

  private def importForType(typeName: String, modelDefsByName: Map[String, Seq[ModelDef]]): Option[String] =
    modelDefsByName.get(typeName).collect { case Seq(md) => s"import ${md.pkg}.${md.name}" }

  // ---------------------------------------------------------------------------
  // Context generation
  // ---------------------------------------------------------------------------

  private def findContextFile(contextRoot: File, stem: String): Option[File] = {
    def loop(d: File): Option[File] = {
      val kids = Option(d.listFiles()).getOrElse(Array.empty)
      kids
        .find(f => f.isFile && f.getName == s"${stem}Context.scala")
        .orElse(kids.filter(_.isDirectory).to(LazyList).flatMap(loop).headOption)
    }
    if (!contextRoot.exists()) None else loop(contextRoot)
  }

  private def ensureContextExists(contextRoot: File, contextPkg: String, stem: String): File =
    findContextFile(contextRoot, stem).getOrElse {
      if (!contextRoot.exists()) contextRoot.mkdirs()
      val out     = new File(contextRoot, s"${stem}Context.scala")
      val content =
        s"""package $contextPkg
           |
           |// Minimal per-scenario context; extend fields as migration progresses.
           |final case class ${stem}Context(
           |  var request: String = "",
           |  var responseBody: String = "",
           |  var status: Int = 0,
           |  var headers: Map[String, String] = Map.empty,
           |)
           |""".stripMargin
      writeFile(out, content)
      out
    }

  private def baseDefault(tpe: String): String = normalizeType(tpe) match {
    case "String"                           => "\"\""
    case "Int"                              => "0"
    case "Long"                             => "0L"
    case "Boolean"                          => "false"
    case "Double"                           => "0.0"
    case "BigDecimal"                       => "BigDecimal(0)"
    case "LocalDate"                        => "java.time.LocalDate.now()"
    case other if other.startsWith("Seq[")  => s"Seq.empty[${other.stripPrefix("Seq[").stripSuffix("]")}]"
    case other if other.startsWith("List[") => s"List.empty[${other.stripPrefix("List[").stripSuffix("]")}]"
    case other                              => s"null.asInstanceOf[$other]"
  }

  private def contextTypeAndDefault(tpeRaw: String, exact: Boolean): (String, String) = {
    val tpe = normalizeType(tpeRaw)
    if (exact) {
      if (isOption(tpe)) (tpe, "None")
      else (tpe, baseDefault(tpe))
    } else {
      if (isOption(tpe)) (tpe, "None")
      else if (tpe.startsWith("Seq[") || tpe.startsWith("List[")) (tpe, baseDefault(tpe))
      else if (isPrimitive(tpe)) (tpe, baseDefault(tpe))
      else (s"Option[$tpe]", "None")
    }
  }

  private def upsertContextFields(
    contextRoot: File,
    contextPkg: String,
    stem: String,
    fields: Iterable[ContextField]
  ): Unit = {
    val contextFile = ensureContextExists(contextRoot, contextPkg, stem)
    val src         = readFile(contextFile)
    val ccRe        = ("""(?s)\bfinal\s+case\s+class\s+""" + Regex.quote(stem) + """Context\s*\((.*?)\)\s*""").r
    val mOpt        = ccRe.findFirstMatchIn(src)
    if (mOpt.isEmpty) return

    val params    = mOpt.get.group(1)
    val existing  = """\bvar\s+([A-Za-z_][A-Za-z0-9_]*)\s*:""".r.findAllMatchIn(params).map(_.group(1)).toSet
    val additions = fields.toSeq.distinctBy(_.name).filterNot(f => existing.contains(f.name))

    if (additions.isEmpty) return

    val additionText = additions
      .map { f =>
        val (tpe, dflt) = contextTypeAndDefault(f.tpe, f.exact)
        s"  var ${f.name}: $tpe = $dflt"
      }
      .mkString(",\n")

    val paramsTrimmed = params.replaceFirst("""\s+$""", "")
    val join          = if (paramsTrimmed.trim.isEmpty) "" else if (paramsTrimmed.trim.endsWith(",")) "\n" else ",\n"
    val newParams     = paramsTrimmed + join + additionText
    val updated       = src.substring(0, mOpt.get.start(1)) + newParams + src.substring(mOpt.get.end(1))
    writeFile(contextFile, updated)
  }

  // ---------------------------------------------------------------------------
  // Legacy method inference
  // ---------------------------------------------------------------------------

  private def inferTypeFromKey(key: String): String = {
    val k = key.toLowerCase
    if (k.startsWith("is") || k.contains("flag") || k.contains("indicator") || k.startsWith("use")) "Boolean"
    else if (k.contains("amount") || k.contains("balance") || k.contains("interest")) "BigDecimal"
    else if (k.contains("duration") || k.contains("count") || k.endsWith("number")) "Int"
    else if (k.endsWith("date") || k.endsWith("day")) "String"
    else "String"
  }

  private def isValidScalaIdentifier(name: String): Boolean = {
    val reserved = Set(
      "abstract",
      "case",
      "catch",
      "class",
      "def",
      "do",
      "else",
      "extends",
      "false",
      "final",
      "finally",
      "for",
      "forSome",
      "if",
      "implicit",
      "import",
      "lazy",
      "match",
      "new",
      "null",
      "object",
      "override",
      "package",
      "private",
      "protected",
      "return",
      "sealed",
      "super",
      "this",
      "throw",
      "trait",
      "try",
      "true",
      "type",
      "val",
      "var",
      "while",
      "with",
      "yield"
    )
    name.matches("[A-Za-z_][A-Za-z0-9_]*") && !reserved.contains(name)
  }

  private def shouldIgnoreRawKey(raw: String): Boolean = {
    val t = raw.trim
    t.isEmpty ||
    t == "-" ||
    t.contains(" ") ||
    t.contains("<") ||
    t.contains(">") ||
    t.contains("/") ||
    t.contains("\\") ||
    t.contains(":") ||
    t.contains(",") ||
    t.contains(".")
  }

  private def extractKeysFromDataTableBody(body: String): Seq[KeyInfo] = {
    val rawKeys =
      getKeyRe.findAllMatchIn(body).map(_.group(1)).toSet ++
        containsKeyRe.findAllMatchIn(body).map(_.group(1)).toSet ++
        replacePlaceholderRe.findAllMatchIn(body).map(_.group(1)).toSet

    rawKeys.toSeq
      .filterNot(shouldIgnoreRawKey)
      .map(k => toCamel(k))
      .filter(isValidScalaIdentifier)
      .distinct
      .sorted
      .map(k => KeyInfo(k, inferTypeFromKey(k)))
  }

  private def classifyTemplateMethod(body: String): Boolean =
    body.contains("TestData.loadedFiles") ||
      body.contains("getBodyAsString") ||
      body.contains("replaceAll") ||
      body.contains("ScenarioContext")

  private def extractConstructorCalls(body: String, knownModels: Set[String]): Seq[ConstructorCall] =
    constructorStartRe.findAllMatchIn(body).toSeq.flatMap { m =>
      val modelName = m.group(1)
      if (!knownModels.contains(modelName)) None
      else {
        val openIdx = body.indexOf('(', m.start(1) + modelName.length)
        sliceParenBlock(body, openIdx).map { case (from, to) =>
          ConstructorCall(modelName, splitTopLevelArgs(body.substring(from + 1, to)))
        }
      }
    }

  private def previewLines(body: String, maxLines: Int = 8): Seq[String] =
    body.linesIterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .filterNot(_.startsWith("//"))
      .take(maxLines)
      .toSeq

  private def emitInputCaseClass(methName: String, keys: Seq[KeyInfo]): (String, String) = {
    val clsName = methodStem(methName) match {
      case "" => toPascal(methName) + "Input"
      case s  => s + "Input"
    }

    val fields =
      if (keys.isEmpty) "    // No stable input keys inferred from legacy method."
      else keys.map(ki => s"    ${ki.name}: Option[${ki.tpe}] = None").mkString(",\n")

    val code =
      s"""  final case class $clsName(
         |$fields
         |  )
         |
         |""".stripMargin

    (clsName, code)
  }

  // ---------------------------------------------------------------------------
  // Rendering model builders
  // ---------------------------------------------------------------------------

  private def renderArgExpr(
    field: ModelField,
    inputClassName: String,
    keys: Seq[KeyInfo],
    localArgs: Seq[String]
  ): RenderedExpr = {
    val inputFields = keys.map(_.name).toSet
    val fieldName   = field.name
    val targetType  = normalizeType(field.tpe)
    val fieldInput  = toCamel(fieldName)

    if (inputFields.contains(fieldInput)) {
      val raw = s"in.$fieldInput"
      optionInner(targetType) match {
        case Some(inner) => RenderedExpr(raw, risky = false)
        case None        =>
          val expr =
            targetType match {
              case "String"     => s"$raw.getOrElse(\"\")"
              case "Int"        => s"$raw.getOrElse(0)"
              case "Long"       => s"$raw.getOrElse(0L)"
              case "Boolean"    => s"$raw.getOrElse(false)"
              case "Double"     => s"$raw.map(_.toDouble).getOrElse(0.0)"
              case "BigDecimal" => s"$raw.getOrElse(BigDecimal(0))"
              case _            =>
                field.defaultOpt.getOrElse(s"/* TODO: supply $fieldName: $targetType */ null.asInstanceOf[$targetType]")
            }
          RenderedExpr(
            expr,
            risky = expr.contains("TODO"),
            if (expr.contains("TODO")) Some(s"Unable to safely infer $fieldName") else None
          )
      }
    } else {
      field.defaultOpt match {
        case Some(default)                            => RenderedExpr(default, risky = false)
        case None if optionInner(targetType).nonEmpty => RenderedExpr("None", risky = false)
        case None                                     =>
          RenderedExpr(
            s"/* TODO: supply $fieldName: $targetType */ ${baseDefault(targetType)}",
            risky = true,
            Some(s"Missing required model field $fieldName")
          )
      }
    }
  }

  private def emitModelBuilderMethods(
    inputClassName: String,
    keys: Seq[KeyInfo],
    constructors: Seq[ConstructorCall],
    modelDefsByName: Map[String, Seq[ModelDef]]
  ): (String, Set[String]) = {
    if (constructors.isEmpty) return "" -> Set.empty

    val sb      = new StringBuilder
    val imports = scala.collection.mutable.Set.empty[String]

    constructors.distinctBy(_.modelName).foreach { ctor =>
      modelDefsByName.get(ctor.modelName) match {
        case Some(Seq(model)) =>
          imports += s"import ${model.pkg}.${model.name}"

          val rendered: Seq[(ModelField, RenderedExpr, String)] =
            model.fields.map { field =>
              val r    = renderArgExpr(field, inputClassName, keys, ctor.args)
              val line = s"      ${field.name} = ${r.expr}"
              (field, r, line)
            }

          val methodName = "to" + model.name
          val hasRisky   = rendered.exists { case (_, renderedExpr, _) =>
            renderedExpr.risky
          }

          sb.append(
            s"""  // Best-effort typed model builder from legacy constructor usage.
               |  def $methodName(in: $inputClassName): ${model.name} = {
               |""".stripMargin
          )

          if (hasRisky) {
            sb.append("    // TODO: review inferred defaults below before relying on this method.\n")
          }

          sb.append(s"    ${model.name}(\n")
          sb.append(rendered.map { case (_, _, line) => line }.mkString(",\n"))
          sb.append("\n    )\n")
          sb.append("  }\n\n")

          sb.append(
            s"""  def ${methodName}Seq(inputs: Seq[$inputClassName]): Seq[${model.name}] =
               |    inputs.map($methodName)
               |
               |""".stripMargin
          )

        case Some(_) =>
          sb.append(
            s"""  // TODO: Multiple model classes named ${ctor.modelName} were found under models-root.
               |  // Import and build the intended model manually for $inputClassName.
               |
               |""".stripMargin
          )

        case None =>
          sb.append(
            s"""  // TODO: Constructor ${ctor.modelName}(...) was detected, but no matching model case class was found.
               |
               |""".stripMargin
          )
      }
    }

    sb.toString -> imports.toSet
  }

  // ---------------------------------------------------------------------------
  // Safe HTTP client methods
  // ---------------------------------------------------------------------------

  private def extractClientMethods(src: String): Seq[ClientMethod] =
    clientMethodHeaderRe.findAllMatchIn(src).toSeq.flatMap { m =>
      val name   = m.group(1)
      val params = m.group(2).trim
      sliceMethodBody(src, m.start).flatMap { case (from, to) =>
        val body = src.substring(from, to)
        if (wsClientCallRe.findFirstIn(body).nonEmpty && !params.contains("DataTable")) {
          Some(ClientMethod(name, params, body))
        } else None
      }
    }

  private def inferTypeFromExpr(expr: String): String = {
    val e = expr.trim
    if (e.matches("""-?\d+""")) "Int"
    else if (e.matches("""-?\d+[lL]""")) "Long"
    else if (e.matches("""-?\d+\.\d+""")) "Double"
    else if (e.equalsIgnoreCase("true") || e.equalsIgnoreCase("false")) "Boolean"
    else if (e.contains("BigDecimal(")) "BigDecimal"
    else "String"
  }

  private def rewriteScenarioContext(body: String, contextName: String): (String, Seq[ContextField]) = {
    val fields = scala.collection.mutable.ListBuffer.empty[ContextField]

    val afterSet = scenarioContextSetRe.replaceAllIn(
      body,
      m => {
        val key   = m.group(1)
        val expr  = m.group(2).trim
        val field = toCamel(key)
        fields += ContextField(field, inferTypeFromExpr(expr))
        s"$contextName.$field = $expr"
      }
    )

    val afterGet = scenarioContextGetRe.replaceAllIn(
      afterSet,
      m => {
        val key   = m.group(1)
        val field = toCamel(key)
        s"$contextName.$field"
      }
    )

    afterGet -> fields.toSeq
  }

  private def emitClientMethods(
    methods: Seq[ClientMethod],
    stem: String,
    contextRoot: File,
    contextPkg: String
  ): (String, Seq[ContextField]) = {
    if (methods.isEmpty) return "" -> Seq.empty

    val contextType = s"${stem}Context"
    val contextName = "context"
    val allFields   = scala.collection.mutable.ListBuffer.empty[ContextField]

    val code = methods
      .map { m =>
        val originalParams          = m.params.split(",").toSeq.map(_.trim).filter(_.nonEmpty)
        val finalParams             = (s"$contextName: $contextType" +: originalParams).mkString(", ")
        val (rewrittenBody, fields) = rewriteScenarioContext(m.body, contextName)
        allFields ++= fields

        s"""  def ${m.name}($finalParams): StandaloneWSResponse = {
         |${indent(rewrittenBody.trim, 4)}
         |  }
         |""".stripMargin
      }
      .mkString("\n\n")

    (
      s"""  // -----------------------------------------------------------------------
         |  // HTTP client methods lifted from legacy Requests with typed context access.
         |  // -----------------------------------------------------------------------
         |
         |$code
         |
         |""".stripMargin,
      allFields.toSeq
    )
  }

  // ---------------------------------------------------------------------------
  // True top-level val lifting
  // ---------------------------------------------------------------------------

  private def extractSafeTopLevelVals(src: String): Seq[String] = {
    val body           = objectBody(src).getOrElse("")
    val withoutMethods = removeMethodBodies(body)

    withoutMethods.linesIterator
      .flatMap { line =>
        val trimmed = line.trim
        valAssignRe.findFirstMatchIn(trimmed).flatMap { m =>
          val expr = m.group(3).trim

          val unsafe =
            expr.contains("dataTable") ||
              expr.contains("DataTable") ||
              expr.contains("asMap") ||
              expr.contains("asMaps") ||
              expr.contains("asScala") ||
              expr.contains("ScenarioContext") ||
              expr.contains("getBodyAsString") ||
              expr.endsWith("(") ||
              expr == "Map(" ||
              expr.contains("WsClient.")

          if (unsafe) None else Some(trimmed)
        }
      }
      .toSeq
      .distinct
  }

  private def indent(s: String, spaces: Int): String = {
    val pad = " " * spaces
    s.linesIterator.map(line => if (line.trim.isEmpty) "" else pad + line).mkString("\n")
  }

  // ---------------------------------------------------------------------------
  // Main conversion
  // ---------------------------------------------------------------------------

  private def convertOne(
    f: File,
    cfg: Config,
    builderPkg: String,
    contextPkg: String,
    modelDefsByName: Map[String, Seq[ModelDef]]
  ): Unit = {
    val src         = readFile(f)
    val stem        = objectNameRe
      .findFirstMatchIn(src)
      .map(m => stripRequestsSuffix(m.group(1)))
      .getOrElse(f.getName.stripSuffix(".scala"))
    val builderName = stem + "Builder"
    val contextName = stem + "Context"

    val out = new File(cfg.buildersRoot, builderName + ".scala")

    ensureContextExists(cfg.contextRoot, contextPkg, stem)

    val knownModelNames = modelDefsByName.keySet

    val imports = scala.collection.mutable.Set(
      "import play.api.libs.json.Json",
      "import play.api.libs.ws.StandaloneWSResponse",
      "import uk.gov.hmrc.test.api.client.WsClient",
      s"import $contextPkg.$contextName",
      "import uk.gov.hmrc.test.api.utils.BaseRequests",
      "import uk.gov.hmrc.test.api.utils.RandomValues",
      "import uk.gov.hmrc.test.api.utils.TestData"
    )

    val sb = new StringBuilder

    sb.append(s"package $builderPkg\n\n")

    val body          = new StringBuilder
    val contextFields = scala.collection.mutable.ListBuffer.empty[ContextField]

    val safeTopLevelVals = extractSafeTopLevelVals(src)
    if (safeTopLevelVals.nonEmpty) {
      body.append("  // Safe top-level values preserved from legacy Requests.\n")
      safeTopLevelVals.foreach(v => body.append("  ").append(v).append("\n"))
      body.append("\n")
    }

    val dtMethods = dtMethodHeaderRe.findAllMatchIn(src).toSeq

    dtMethods.foreach { mh =>
      val methName = mh.group(1)
      sliceMethodBody(src, mh.start).foreach { case (from, to) =>
        val methodBody                    = src.substring(from, to)
        val keys                          = extractKeysFromDataTableBody(methodBody)
        val (inputClassName, inputCode)   = emitInputCaseClass(methName, keys)
        val constructors                  = extractConstructorCalls(methodBody, knownModelNames)
        val (modelBuilders, modelImports) = emitModelBuilderMethods(inputClassName, keys, constructors, modelDefsByName)
        imports ++= modelImports

        body.append("  // -----------------------------------------------------------------------\n")
        body.append(s"  // Typed input generated from legacy method: $methName(DataTable)\n")
        body.append("  // Legacy DataTable code is inference-only and is not emitted.\n")
        body.append("  // -----------------------------------------------------------------------\n")
        body.append(inputCode)

        if (modelBuilders.nonEmpty) {
          body.append(modelBuilders)
        } else if (classifyTemplateMethod(methodBody)) {
          body.append("  // -----------------------------------------------------------------------\n")
          body.append(s"  // Legacy method '$methName' looked like template/string-body setup.\n")
          body.append("  // Add a typed builder method here if this step is still needed by ScalaTest specs.\n")
          body.append("  // Legacy preview:\n")
          previewLines(methodBody).foreach(l => body.append("  //   ").append(l).append("\n"))
          body.append("  // -----------------------------------------------------------------------\n\n")
        }
      }
    }

    if (src.contains("getBodyAsString") || src.contains("TestData.loadedFiles")) {
      body.append(
        """  def getBodyAsString(variant: String): String =
          |    TestData.loadedFiles(variant)
          |
          |""".stripMargin
      )
    }

    val clientMethods                     = extractClientMethods(src)
    val (clientCode, clientContextFields) = emitClientMethods(clientMethods, stem, cfg.contextRoot, contextPkg)
    contextFields ++= clientContextFields
    body.append(clientCode)

    upsertContextFields(cfg.contextRoot, contextPkg, stem, contextFields.toSeq)

    val sortedImports =
      imports.toSeq
        .filterNot(_.contains("io.cucumber"))
        .filterNot(_.contains("cucumber.api"))
        .filterNot(_.contains("CollectionConverters"))
        .distinct
        .sorted

    sortedImports.foreach(i => sb.append(i).append("\n"))
    sb.append("\n")
    sb.append(s"object $builderName extends BaseRequests with RandomValues {\n\n")
    sb.append(body.toString)
    sb.append("}\n")

    val generated = sb.toString

    // Final safety guard: generated executable code must not contain Cucumber/DataTable references.
    val unsafeGenerated =
      generated.linesIterator
        .filterNot(_.trim.startsWith("//"))
        .exists(line =>
          line.contains("DataTable") ||
            line.contains("dataTable") ||
            line.contains(".asMap") ||
            line.contains(".asMaps") ||
            line.contains(".asScala") ||
            line.contains("io.cucumber") ||
            line.contains("cucumber.api")
        )

    if (unsafeGenerated) {
      val guarded =
        generated +
          "\n// ERROR: converter safety guard detected legacy DataTable/Cucumber code in executable output.\n"
      writeFile(out, guarded)
      println(s"⚠ Generated with safety warning: ${out.getPath}")
    } else {
      writeFile(out, generated)
      println(s"✓ Generated ${out.getPath}")
    }
  }

  private def parseArgs(args: Array[String]): Config = {
    if (args.length < 2) {
      println(
        """Usage:
          |  RequestsToBuildersConverter <requests_root> <builders_root>
          |    --models-root <models_root>
          |    --context-root <context_root>
          |    [--recurse]
          |""".stripMargin
      )
      System.exit(1)
    }

    val requestsRoot = new File(args(0))
    val buildersRoot = new File(args(1))

    var modelsRoot: Option[File]  = None
    var contextRoot: Option[File] = None
    var recurse                   = false

    var i = 2
    while (i < args.length)
      args(i) match {
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
          println(s"ERROR: Unknown or incomplete argument: $other")
          System.exit(2)
      }

    if (!requestsRoot.exists() || !requestsRoot.isDirectory) {
      println(s"ERROR: requests root not found or not a directory: ${requestsRoot.getPath}")
      System.exit(2)
    }

    val mr = modelsRoot.getOrElse {
      println("ERROR: --models-root is required")
      System.exit(2)
      new File(".")
    }

    val cr = contextRoot.getOrElse {
      println("ERROR: --context-root is required")
      System.exit(2)
      new File(".")
    }

    if (!mr.exists() || !mr.isDirectory) {
      println(s"ERROR: models root not found or not a directory: ${mr.getPath}")
      System.exit(2)
    }

    Config(requestsRoot, buildersRoot, mr, cr, recurse)
  }

  def main(args: Array[String]): Unit = {
    val cfg = parseArgs(args)

    if (!cfg.buildersRoot.exists()) cfg.buildersRoot.mkdirs()
    if (!cfg.contextRoot.exists()) cfg.contextRoot.mkdirs()

    val builderPkg      = basePkgFromPath(cfg.buildersRoot)
    val contextPkg      = basePkgFromPath(cfg.contextRoot)
    val modelDefsByName = parseModelDefs(cfg.modelsRoot)

    val files = listScalaFiles(cfg.requestsRoot, cfg.recurse)
    if (files.isEmpty) {
      println(s"⚠ No Scala files found in ${cfg.requestsRoot.getPath}")
      return
    }

    println(s"Model case classes discovered: ${modelDefsByName.values.map(_.size).sum}")
    files.foreach(f => convertOne(f, cfg, builderPkg, contextPkg, modelDefsByName))
    println("Done.")
  }
}
