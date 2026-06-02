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
import uk.gov.hmrc.test.api.models.ifs.FCDebtCalculationRequest
import uk.gov.hmrc.test.api.models.{FCCalculationWindow, FCDebtCalculation, FCDebtCalculationsSummary}
import uk.gov.hmrc.test.api.scalatest.builders.{FieldCollectionsBuilder, InterestForecastingBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FieldCollectionsContext, InterestForecastingContext}

// TODO: Validate that InterestForecastingContext is the correct context for helpers migrated from FCInterestForecastingSteps.scala.
trait FCInterestForecastingStepHelpers { this: Matchers =>

  // ^a fc debt collection$
  def aFcDebtCalculation(context: FieldCollectionsContext, request: FCDebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  // ^fc debt item with cotax charge interest$
  def fcDebtItemWithCotaxChargeInterest(context: InterestForecastingContext): Unit = {
    // createFcCotaxChargeInterestRequest(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the debt item has fc payment history$
  def theDebtItemHasFcPaymentHistory(
    context: InterestForecastingContext,
    inputs: Seq[FieldCollectionsBuilder.FCPaymentHistoryInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using FieldCollectionsBuilder.
    // Suggested type: FieldCollectionsBuilder.FCPaymentHistoryInput
  }

  // ^the fc debt item has no payment history$
  def theFcDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // fcCustomerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the fc ifs service$
  def theDebtItemIsSentToTheFcIfsService(context: FieldCollectionsContext): Unit = {
    val requestJson                    = Json.stringify(Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context"))))
    val response: StandaloneWSResponse = FieldCollectionsBuilder.getDebtCalculation(context, requestJson)
    context.response = response

    val jsonResponseBody = response.body[JsValue]
    context.ifsResponseBody = Some(jsonResponseBody.as[FCDebtCalculationsSummary])
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    println("\n==== RESPONSE BODY ====")
    println(jsonResponseBody)
  }

  // ^the fc ifs service will return a total debts summary of$
  def theFcIfsServiceWillReturnATotalDebtsSummaryOf(
    context: FieldCollectionsContext,
    expectedResponse: FCDebtCalculationsSummary
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

      withClue("interestDueCallTotal") {
        responseBody.interestDueCallTotal shouldBe expectedResponse.interestDueCallTotal
      }

      withClue("totalAmountIntTotal") {
        responseBody.totalAmountIntTotal shouldBe expectedResponse.totalAmountIntTotal
      }

      withClue("amountOnIntDueTotal") {
        responseBody.amountOnIntDueTotal shouldBe expectedResponse.amountOnIntDueTotal
      }
    }
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) fc debt summary will contain$
  def theFcDebtSummaryWillContain(
    context: FieldCollectionsContext,
    index: Int,
    expectedResponse: FCDebtCalculation
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val FCDebtCalculations = responseBody.debtCalculations(index - 1)

    withClue("FCDebtCalculation") {
      withClue("debtItemChargeId") {
        FCDebtCalculations.debtItemChargeId shouldBe expectedResponse.debtItemChargeId
      }

      withClue("interestDueDailyAccrual") {
        FCDebtCalculations.interestDueDailyAccrual shouldBe expectedResponse.interestDueDailyAccrual
      }

      withClue("interestDueDutyTotal") {
        FCDebtCalculations.interestDueDutyTotal shouldBe expectedResponse.interestDueDutyTotal
      }

      withClue("amountOnIntDueDuty") {
        FCDebtCalculations.amountOnIntDueDuty shouldBe expectedResponse.amountOnIntDueDuty
      }

      withClue("totalAmountIntDuty") {
        FCDebtCalculations.totalAmountIntDuty shouldBe expectedResponse.totalAmountIntDuty
      }

      withClue("unpaidAmountDuty") {
        FCDebtCalculations.unpaidAmountDuty shouldBe expectedResponse.unpaidAmountDuty
      }
    }
  }

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will have calculation windows$
  def theFcDebtSummaryWillHaveCalculationWindows(
    context: FieldCollectionsContext,
    summaryIndex: Int,
    inputs: List[FCCalculationWindow]
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    inputs.zipWithIndex.foreach { case (expectedResponse, index) =>
      val actual = responseBody
        .debtCalculations(summaryIndex - 1)
        .calculationWindows(index)

      withClue("FCDebtCalculationsSummary") {
        withClue("periodFrom") {
          actual.periodFrom shouldBe expectedResponse.periodFrom
        }

        withClue("periodTo") {
          actual.periodTo shouldBe expectedResponse.periodTo
        }

        withClue("numberOfDays") {
          actual.numberOfDays shouldBe expectedResponse.numberOfDays
        }

        withClue("interestRate") {
          actual.interestRate shouldBe expectedResponse.interestRate
        }

        withClue("interestDueDailyAccrual") {
          actual.interestDueDailyAccrual shouldBe expectedResponse.interestDueDailyAccrual
        }

        withClue("interestDueWindow") {
          actual.interestDueWindow shouldBe expectedResponse.interestDueWindow
        }

        withClue("amountOnIntDueWindow") {
          actual.amountOnIntDueWindow shouldBe expectedResponse.amountOnIntDueWindow
        }

        withClue("unpaidAmountWindow") {
          actual.unpaidAmountWindow shouldBe expectedResponse.unpaidAmountWindow
        }

        // only assert suppressionApplied fields if they are present in input
        expectedResponse.suppressionApplied.foreach { expectedSuppression =>
          if (expectedSuppression.reason.nonEmpty) {
            withClue("reason") {
              actual.suppressionApplied.head.reason shouldBe expectedSuppression.reason
            }
          }

          if (expectedSuppression.description.nonEmpty) {
            withClue("description") {
              actual.suppressionApplied.head.description shouldBe expectedSuppression.description
            }
          }

          if (expectedSuppression.code.nonEmpty) {
            withClue("code") {
              actual.suppressionApplied.head.code shouldBe expectedSuppression.code
            }
          }
        }
      }
    }
  }

  // ^the fc customer has breathing spaces applied$
  def theFcCustomerHasBreathingSpacesApplied(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the fc debt item$
  def noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context: InterestForecastingContext): Unit = {
    // noFCBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the fc customer has post codes$
  def theFcCustomerHasPostCodes(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.CustomerPostCodesInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.CustomerPostCodesInput
  }

  // ^add charge interest cotax$
  def addChargeInterestCotax(context: InterestForecastingContext): Unit = {
    // addChargedInterestCotax(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the fc customer has no post codes$
  def theFcCustomerHasNoPostCodes(context: InterestForecastingContext): Unit = {
    // noFCCustomerPostCodes()
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will not have any calculation windows$
  def theFcDebtSummaryWillNotHaveAnyCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Unit =
    getFCCountOfCalculationWindows(context, summaryIndex) shouldBe 0

  // ^the fc ifs service will respond with (.*)$
  def theFcIfsServiceWillRespondWith(context: InterestForecastingContext, expectedMessage: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage, response assertion
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.body   should include(expectedMessage)
    // response.status should be(400)
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will have ([0-9]) calculation windows$
  def theFcDebtSummaryWillHaveCalculationWindows2(
    context: InterestForecastingContext,
    summaryIndex: Int,
    numberOfWindows: Int
  ): Unit = {
    // getFCCountOfCalculationWindows(summaryIndex) shouldBe numberOfWindows
    // TODO: Implement typed helper for this step.
  }

  def getFCCountOfCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Int = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))
    responseBody.debtCalculations(summaryIndex - 1).calculationWindows.size
  }

}
