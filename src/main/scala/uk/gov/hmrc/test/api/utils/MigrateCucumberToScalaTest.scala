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

object MigrateCucumberToScalaTest {

  // Usage:
  // sbt "runMain uk.gov.hmrc.test.api.utils.MigrateCucumberToScalaTest"

  // ---------------------------------------------------------------------------
  // Config (adjust per repo)
  // ---------------------------------------------------------------------------

//   1) Requests → Builders
//  sbt "runMain uk.gov.hmrc.test.api.utils.RequestsToBuildersConverter \
//    src/test/scala/uk/gov/hmrc/test/api/requests \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/builders \
//    --models-root src/test/scala/uk/gov/hmrc/test/api/models \
//  --context-root src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context \
//  --recurse"
//   2) StepDefs → StepHelpers
//  sbt "runMain uk.gov.hmrc.test.api.utils.StepDefToScalaFunctionConverter \
//    src/test/scala/uk/gov/hmrc/test/api/cucumber/stepdefs \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/helpers \
//    --builders-root src/test/scala/uk/gov/hmrc/test/api/scalatest/builders \
//  --models-root src/test/scala/uk/gov/hmrc/test/api/models \
//  --context-root src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context \
//  --recurse"
//   3) Context Scaffolder (WITH DEFAULT CONTEXT)
//
//   Use this when helpers can’t confidently match a context
//  sbt "runMain uk.gov.hmrc.test.api.utils.ContextScaffolder \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/builders \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/helpers \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context \
//    --default-context FCStatementOfLiabilityContext \
//  --recurse"
//
//   Change FCStatementOfLiabilityContext to whatever makes sense per repo.
//
//     4) Feature → ScalaTest Specs
//    sbt "runMain uk.gov.hmrc.test.api.utils.FeatureToScalaSpecConverter \
//    src/test/resources/features \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/specs \
//    --helpers-root src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/helpers \
//  --context-root src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context \
//  --builders-root src/test/scala/uk/gov/hmrc/test/api/scalatest/builders \
//  --recurse"
//   5) Tags + Runner Generator
//    sbt "runMain uk.gov.hmrc.test.api.utils.TagsObjectGenerator \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/specs \
//    src/test/scala/uk/gov/hmrc/test/api/scalatest/tags \
//  --recurse"

  private val featuresRoot = s"src/test/resources/features"
  private val specsRoot    = "src/test/scala/uk/gov/hmrc/test/api/scalatest/specs"

  private val tagsOut   = "src/test/scala/uk/gov/hmrc/test/api/scalatest/tags"
  private val runnerOut = "src/test/scala/uk/gov/hmrc/test/api/scalatest/runner"

  private val cucumberRunnerRoot =
    "src/test/scala/uk/gov/hmrc/test/api/cucumber/runner"

  private val stepdefsRoot =
    "src/test/scala/uk/gov/hmrc/test/api/cucumber/stepdefs"

  private val requestsRoot =
    "src/test/scala/uk/gov/hmrc/test/api/requests"

  private val modelsRoot =
    "src/test/scala/uk/gov/hmrc/test/api/models"

  private val buildersRoot =
    "src/test/scala/uk/gov/hmrc/test/api/scalatest/builders"

  private val helpersRoot =
    "src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/helpers"

  private val contextRoot =
    "src/test/scala/uk/gov/hmrc/test/api/scalatest/steps/context"

  // ---------------------------------------------------------------------------
  // Optional fallback context (ONLY used when no match is found)
  // ---------------------------------------------------------------------------

  private val defaultContext: Option[String] =
    None
  // Example:
  // None
  // Some("CommonContext")
  // Some("uk.gov.hmrc.test.api.scalatest.steps.context.CommonContext")

  // ---------------------------------------------------------------------------
  // Pipeline
  // ---------------------------------------------------------------------------

  def main(args: Array[String]): Unit = {

    println("1) Converting legacy Requests to typed ScalaTest Builders")
    RequestsToBuildersConverter.main(
      Array(
        requestsRoot,
        buildersRoot,
        "--models-root",
        modelsRoot,
        "--context-root",
        contextRoot,
        "--recurse"
      )
    )

    println("2) Converting Cucumber step definitions to ScalaTest StepHelpers")
    StepDefToScalaFunctionConverter.main(
      Array(
        stepdefsRoot,
        helpersRoot,
        "--builders-root",
        buildersRoot,
        "--models-root",
        modelsRoot,
        "--context-root",
        contextRoot,
        "--recurse"
      )
    )

    println("3) Scaffolding and aligning Context files from Builders")

    val contextArgs =
      Seq(
        buildersRoot,
        helpersRoot,
        contextRoot,
        "--recurse"
      ) ++ defaultContext.map(dc => Seq("--default-context", dc)).getOrElse(Seq.empty)

    ContextScaffolder.main(contextArgs.toArray)

    println("4) Converting .feature files to ScalaTest FeatureSpecs")
    FeatureToScalaSpecConverter.main(
      Array(
        featuresRoot,
        specsRoot,
        "--helpers-root",
        helpersRoot,
        "--context-root",
        contextRoot,
        "--builders-root",
        buildersRoot,
        "--recurse"
      )
    )

    println("5) Generating ScalaTest Tags and Cucumber-mirrored Runners")
    TagsObjectGenerator.main(
      Array(
        specsRoot,
        tagsOut,
        runnerOut,
        "--cucumber-runner-root",
        cucumberRunnerRoot,
        "--recurse"
      )
    )

    println("✓ Cucumber → ScalaTest migration pipeline completed.")
  }
}
