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
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsContext

// TODO: Validate that InterestForecastingContext is the correct context for helpers migrated from FCInterestForecastingSteps.scala.
trait FCInterestForecastingStepHelpers { this: Matchers =>

  // ^a fc debt collection$
  def aFcDebtCalculation(context: FieldCollectionsContext, request: FCDebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  // ^the debt item(s) is sent to the fc ifs service$
  def theDebtItemIsSentToTheFcIfsService(context: FieldCollectionsContext): Unit = {
    val requestJson                    = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = FieldCollectionsBuilder.getDebtCalculation(requestJson)
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

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will not have any calculation windows$
  def theFcDebtSummaryWillNotHaveAnyCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Unit =
    getFCCountOfCalculationWindows(context, summaryIndex) shouldBe 0

  def getFCCountOfCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Int = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))
    responseBody.debtCalculations(summaryIndex - 1).calculationWindows.size
  }

}
