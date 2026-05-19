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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.{DebtSubmission, SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.models.sol.SolCalculationSummaryResponse
import uk.gov.hmrc.test.api.requests.SuppressionRulesRequests.updateSuppressionData
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, StatementOfLiabilityContext, SuppressionRulesContext, UpdatedDebtContext}
import uk.gov.hmrc.test.api.utils.ScenarioContext
import uk.gov.hmrc.test.api.utils.ScenarioContext.{set, setSuppression}

// TODO: Validate that FCStatementOfLiabilityContext is the correct context for helpers migrated from SuppresionStepDefs.scala.
trait SuppresionStepHelpers { this: Matchers =>

  // ^suppression configuration data is created$
  def suppressionConfigurationDataIsCreated(context: SuppressionRulesContext, request: SuppressionInformation): Unit = {
    // addSuppressionCriteria(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
    println("SuppressionInformation from Spec ********* : " + request)
    var suppressionInfo        = List[SuppressionInformation]()
    suppressionInfo ::= request
    val suppressionRequest     = SuppressionRequest(suppressions = suppressionInfo)
    val suppressionRequestJson = Json.toJson(suppressionRequest)
    setSuppression("suppressionsData", suppressionInfo)
    set("suppressionsJson", Json.stringify(suppressionRequestJson))
    println(s"suppression request json body ************************ $suppressionRequestJson")

//    println("SuppressionInformation from Spec ********* : " + request)
//    context.request = Some(request)
//    println(s"suppression request json body context.request ************************ ${context.request}")
  }

  // ^suppression configuration is sent to ifs service$
  def suppressionConfigurationIsSentToIfsService(context: SuppressionRulesContext): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // val requestJson = ScenarioContext.get[String]("suppressionsJson")
    // println(s"suppression data REQUEST ---------> $requestJson")
    // val response    = updateSuppressionData(requestJson)
    // withClue(s"Incorrect status with body : ${response.body}\n\n") {
    // TODO: Implement typed helper for this step.
    val requestJson    = ScenarioContext.get[String]("suppressionsJson")
    println(s"suppression data REQUEST ---------> $requestJson")
    val updateResponse = updateSuppressionData(requestJson)
    withClue(s"Incorrect status with body : ${updateResponse.body}\n\n") {
      updateResponse.status should be(200)
    }

    println(s"suppression data RESPONSE ---------> ${updateResponse.body}")

//    val response = SuppressionRulesBuilder.getStatementOfLiability(context.request)

//    val jsonResponseBody = response.body[JsValue]
//    println(s"actualResponseBody : " + jsonResponseBody)
//    context.status = response.status
//    context.responseBody = Some(jsonResponseBody.as[SolCalculationSummaryResponse])
//    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }

  }

  // ^a request is sent to ifs service to get suppression$
  def aRequestIsSentToIfsServiceToGetSuppression(context: FCStatementOfLiabilityContext): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // ()
    // val response = getSuppressionData()
    // response.status should be(200)
    // ScenarioContext.set("response", response)
    // TODO: Implement typed helper for this step.
  }

  // ^debt details$
  def debtDetails(
    context: UpdatedDebtContext,
    request: DebtSubmission
  ): Unit = {
    // TODO: Wire input into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.InterestTypeRequestBodyInput
  }

}
