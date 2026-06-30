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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.ifs.FCVATDebtCalculationRequest
import uk.gov.hmrc.test.api.models.{FCVATDebtCalculation, FCVATDebtCalculationsSummary}
import uk.gov.hmrc.test.api.scalatest.builders.{FieldCollectionsVATBuilder, InterestForecastingBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FieldCollectionsVATContext, InterestForecastingContext}

// TODO: Validate that InterestForecastingContext is the correct context for helpers migrated from FCVATInterestForecastingSteps.scala.
trait FCVATInterestForecastingStepHelpers { this: Matchers =>

  // ^a fc vat debt calculation$
  def aFcVatDebtCalculation(context: FieldCollectionsVATContext, request: FCVATDebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  // ^the fc vat debt item has payment history$
  def theFcVatDebtItemHasPaymentHistory(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.PaymentHistoryInput
  }

  // ^the fc vat debt item has no payment history$
  def theFcVatDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // fcVatCustomerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the fc vat ifs service$
  def theDebtItemIsSentToTheFcVatIfsService(context: FieldCollectionsVATContext): Unit = {
    val requestJson                    = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = FieldCollectionsVATBuilder.getDebtCalculation(requestJson)
    context.response = response
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    if (response.status == 200) {
      val jsonResponseBody = response.body[JsValue]
      context.ifsResponseBody = Some(jsonResponseBody.as[FCVATDebtCalculationsSummary])

      println("\n==== RESPONSE BODY ====")
      println(jsonResponseBody)
    } else {
      println("\n==== ERROR RESPONSE BODY ====")
      println(response.body)
    }
  }

  // ^the fc vat ifs service wilL return a total debts summary of$
  def theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(
    context: FieldCollectionsVATContext,
    expectedResponse: FCVATDebtCalculationsSummary
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    withClue("FCDebtCalculationsSummary") {
      withClue("dateOfCalculation") {
        responseBody.dateOfCalculation shouldBe expectedResponse.dateOfCalculation
      }

      withClue("combinedDailyAccrual") {
        responseBody.combinedDailyAccrual shouldBe expectedResponse.combinedDailyAccrual
      }

      withClue("unpaidAmountTotal") {
        responseBody.unpaidAmountTotal shouldBe expectedResponse.unpaidAmountTotal
      }
    }
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) fc vat debt summary will contain$
  def theFcVatDebtSummaryWillContain(
    context: FieldCollectionsVATContext,
    index: Int,
    expectedResponse: FCVATDebtCalculation
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val FCVATDebtCalculations = responseBody.debtCalculations(index - 1)

    withClue("FCDebtCalculation") {
      withClue("debtItemChargeId") {
        FCVATDebtCalculations.debtItemChargeId shouldBe expectedResponse.debtItemChargeId
      }

      withClue("interestDueDailyAccrual") {
        FCVATDebtCalculations.interestDueDailyAccrual shouldBe expectedResponse.interestDueDailyAccrual
      }

      withClue("interestRate") {
        FCVATDebtCalculations.interestRate shouldBe expectedResponse.interestRate
      }
    }
  }

  // ^the fc vat customer has breathing spaces applied$
  def theFcVatCustomerHasBreathingSpacesApplied(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the fc vat customer$
  def noBreathingSpacesHaveBeenAppliedToTheFcVatCustomer(context: InterestForecastingContext): Unit = {
    // noFCVatBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the fc vat ifs service will respond with (.*)$
  def theFcVatIfsServiceWillRespondWith(context: FieldCollectionsVATContext, expectedMessage: String): Unit = {
    val response = Option(context.response).getOrElse(fail("Missing response in context"))

    withClue("response body should include expected message") {
      response.body should include(expectedMessage)
    }

    withClue("response status") {
      context.status shouldBe 400
    }
  }

}
