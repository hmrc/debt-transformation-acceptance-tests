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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.sol.{SolCalculation, SolCalculationSummaryResponse, SolDuty, SolMultipleDebtsRequest}
import uk.gov.hmrc.test.api.requests.StatementOfLiabilityRequests
import uk.gov.hmrc.test.api.scalatest.builders.{InterestForecastingBuilder, StatementOfLiabilityBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext

trait StatementOfLiabilityStepHelpers { this: Matchers =>

  // ^a request is made to get response from sol hello world endpoint$
//  def aRequestIsMadeToGetResponseFromSolHelloWorldEndpoint(context: StatementOfLiabilityContext): Unit = {
//    val response = StatementOfLiabilityRequests.getStatementLiabilityHelloWorld(context.request)
//
//    context.status = response.status
//    context.responseBody = response.body
//    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
//  }
//
//  // ^a request is made to an invalid sol endpoint$
//  def aRequestIsMadeToAnInvalidSolEndpoint(context: StatementOfLiabilityContext): Unit = {
//    val response = StatementOfLiabilityRequests.getStatementLiabilityHelloWorld(context.request)
//
//    context.status = response.status
//    context.responseBody = response.body
//    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
//  }

  // ^the sol hello world response body should be (.*)$
  def theSolHelloWorldResponseBodyShouldBe(context: StatementOfLiabilityContext, p1: String): Unit = {
    // Migration hint: legacy StatementOfLiabilityContext usage, response assertion
    // message: String =>
    // val response: StandaloneWSResponse = StatementOfLiabilityContext.get("response")
    // val responseBody                   = Json.parse(response.body).as[HelloWorld]
    // responseBody.message should be(message)
    // TODO: Implement typed helper for this step.
  }

  // ^the sol response code should be {int}$
  def theSolResponseCodeShouldBeInt(context: StatementOfLiabilityContext): Unit = {
    // int
    // TODO: Implement typed helper for this step.
  }

  // ^a request to sol with no debt items provided$
  def aRequestToSolWithNoDebtItemsProvided(context: StatementOfLiabilityContext): Unit = {
    // Migration hint: legacy StatementOfLiabilityContext usage
    // StatementOfLiabilityContext.set(
    // "debtDetails",
    // "{" + "\"solType\":\"UI\"," +
    // "\"customerUniqueRef\":\"XZ0000100351724\"," +
    // TODO: Implement typed helper for this step.
  }

  // ^debt details$
  def debtDetails(
    context: StatementOfLiabilityContext,
    input: InterestForecastingBuilder.InterestTypeRequestBodyInput
  ): Unit = {
    // TODO: Wire input into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.InterestTypeRequestBodyInput
  }

  // ^statement of liability multiple debt requests$
  def statementOfLiabilityMultipleDebtRequests(
    context: StatementOfLiabilityContext,
    request: SolMultipleDebtsRequest
  ): Unit =
    context.request = Some(request)


  // ^a debt statement of liability is requested$
  def aDebtStatementOfLiabilityIsRequested(context: StatementOfLiabilityContext): Unit = {
    val response = StatementOfLiabilityBuilder.getStatementOfLiability(context.request)

    val jsonResponseBody = response.body[JsValue]
    context.status = response.status
    context.responseBody = Some(jsonResponseBody.as[SolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  // ^service returns debt statement of liability data$
  def serviceReturnsDebtStatementOfLiabilityData(
    context: StatementOfLiabilityContext,
    expectedResponse: SolCalculationSummaryResponse
  ): Unit = {
    // val response: StandaloneWSResponse = StatementOfLiabilityContext.get("response")
    // response.status should be(200)
    // val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse]
    // locally {
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'input' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against input.

    val actualResponseBody = context.responseBody

    context.status     shouldBe 200
    println(s"actualResponseBody : " + actualResponseBody)
    println(s"expectedResponse : " + Some(expectedResponse))

    actualResponseBody shouldBe Some(expectedResponse)
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) sol debt summary will contain$
  def theSolDebtSummaryWillContain(context: StatementOfLiabilityContext, index: Int, input: SolCalculation): Unit = {
    // val response: StandaloneWSResponse = StatementOfLiabilityContext.get("response")
    // response.status should be(200)
    // val debt: SolCalculation = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(index - 1)
    // debt.debtId                        shouldBe asMapTransposed.get("debtId").toString
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'input' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against input.
  }

  // ^the ([0-9])(?:st|nd|rd|th) sol debt summary will contain duties$
  def theSolDebtSummaryWillContainDuties(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    inputs: Seq[SolDuty]
  ): Unit = {
    // val response: StandaloneWSResponse = StatementOfLiabilityContext.get("response")
    // asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
    // val responseBody = Json
    // .parse(response.body)
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'inputs' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against inputs.
  }

  // ^the {int}(st|nd|rd|th) customer statement of liability contains duty values as$
  def theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context: StatementOfLiabilityContext, p1: String): Unit = {
    // int
    // TODO: Implement typed helper for this step.
  }

  // ^the {int}(st|nd|rd|th) customer statement of liability contains debt values as$
  def theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context: StatementOfLiabilityContext, p1: String): Unit = {
    // int
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) multiple statement of liability duties summary will contain$
  def theMultipleStatementOfLiabilityDutiesSummaryWillContain(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    inputs: Seq[SolDuty]
  ): Unit = {
    // val asMapTransposed: Iterable[util.Map[Nothing, Nothing]] =
    // val response: StandaloneWSResponse                        = StatementOfLiabilityContext.get("response")
    // asMapTransposed.zipWithIndex.foreach { case (duty, _) =>
    // val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(debtIndex - 1)
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'inputs' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against inputs.
  }

  // ^the sol service will respond with (.*)$
  def theSolServiceWillRespondWith(context: StatementOfLiabilityContext, expectedMessage: String): Unit = {
    // Migration hint: legacy StatementOfLiabilityContext usage
    // val response: StandaloneWSResponse = StatementOfLiabilityContext.get("response")
    // response.body shouldBe expectedMessage
    // TODO: Implement typed helper for this step.
  }

}
