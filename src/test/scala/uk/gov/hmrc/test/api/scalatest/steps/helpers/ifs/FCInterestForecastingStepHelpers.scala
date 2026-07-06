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
import uk.gov.hmrc.test.api.models.FCDebtCalculationsSummary
import uk.gov.hmrc.test.api.models.ifs.FCDebtCalculationRequest
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsBuilder
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsBuilder.{FCCalculationWindowExpected, FCDebtCalculationExpected, FCDebtCalculationsSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsContext

trait FCInterestForecastingStepHelpers { this: Matchers =>

  def aFcDebtCalculation(context: FieldCollectionsContext, request: FCDebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

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

  def theFcIfsServiceWillReturnATotalDebtsSummaryOf(
    context: FieldCollectionsContext,
    expectedResponse: FCDebtCalculationsSummaryExpected
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    withClue("FCDebtCalculationsSummary") {

      expectedResponse.dateOfCalculation.foreach { e =>
        withClue("dateOfCalculation") {
          responseBody.dateOfCalculation shouldBe Some(e)
        }
      }

      expectedResponse.combinedDailyAccrual.foreach { e =>
        withClue("combinedDailyAccrual") {
          responseBody.combinedDailyAccrual shouldBe e
        }
      }

      expectedResponse.unpaidAmountTotal.foreach { e =>
        withClue("unpaidAmountTotal") {
          responseBody.unpaidAmountTotal shouldBe e
        }
      }

      expectedResponse.interestDueCallTotal.foreach { e =>
        withClue("interestDueCallTotal") {
          responseBody.interestDueCallTotal shouldBe e
        }
      }

      expectedResponse.totalAmountIntTotal.foreach { e =>
        withClue("totalAmountIntTotal") {
          responseBody.totalAmountIntTotal shouldBe e
        }
      }

      expectedResponse.amountOnIntDueTotal.foreach { e =>
        withClue("amountOnIntDueTotal") {
          responseBody.amountOnIntDueTotal shouldBe e
        }
      }
    }
  }

  def theFcDebtSummaryWillContain(
    context: FieldCollectionsContext,
    index: Int,
    expectedResponse: FCDebtCalculationExpected
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val FCDebtCalculations = responseBody.debtCalculations(index - 1)

    withClue("FCDebtCalculation") {

      expectedResponse.debtItemChargeId.foreach { e =>
        withClue("debtItemChargeId") {
          FCDebtCalculations.debtItemChargeId shouldBe e
        }
      }

      expectedResponse.interestDueDailyAccrual.foreach { e =>
        withClue("interestDueDailyAccrual") {
          FCDebtCalculations.interestDueDailyAccrual shouldBe e
        }
      }

      expectedResponse.interestDueDutyTotal.foreach { e =>
        withClue("interestDueDutyTotal") {
          FCDebtCalculations.interestDueDutyTotal shouldBe e
        }
      }

      expectedResponse.amountOnIntDueDuty.foreach { e =>
        withClue("amountOnIntDueDuty") {
          FCDebtCalculations.amountOnIntDueDuty shouldBe e
        }
      }

      expectedResponse.totalAmountIntDuty.foreach { e =>
        withClue("totalAmountIntDuty") {
          FCDebtCalculations.totalAmountIntDuty shouldBe e
        }
      }

      expectedResponse.unpaidAmountDuty.foreach { e =>
        withClue("unpaidAmountDuty") {
          FCDebtCalculations.unpaidAmountDuty shouldBe e
        }
      }
    }
  }

  def theFcDebtSummaryWillHaveCalculationWindows(
    context: FieldCollectionsContext,
    summaryIndex: Int,
    inputs: List[FCCalculationWindowExpected]
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    inputs.zipWithIndex.foreach { case (expectedResponse, index) =>
      val actual = responseBody
        .debtCalculations(summaryIndex - 1)
        .calculationWindows(index)

      withClue("FCDebtCalculationsSummary") {

        expectedResponse.periodFrom.foreach { e =>
          withClue("periodFrom") {
            actual.periodFrom shouldBe e
          }
        }

        expectedResponse.periodTo.foreach { e =>
          withClue("periodTo") {
            actual.periodTo shouldBe e
          }
        }

        expectedResponse.numberOfDays.foreach { e =>
          withClue("numberOfDays") {
            actual.numberOfDays shouldBe e
          }
        }

        expectedResponse.interestRate.foreach { e =>
          withClue("interestRate") {
            actual.interestRate shouldBe e
          }
        }

        expectedResponse.interestDueDailyAccrual.foreach { e =>
          withClue("interestDueDailyAccrual") {
            actual.interestDueDailyAccrual shouldBe e
          }
        }

        expectedResponse.interestDueWindow.foreach { e =>
          withClue("interestDueWindow") {
            actual.interestDueWindow shouldBe e
          }
        }

        expectedResponse.amountOnIntDueWindow.foreach { e =>
          withClue("amountOnIntDueWindow") {
            actual.amountOnIntDueWindow shouldBe e
          }
        }

        expectedResponse.unpaidAmountWindow.foreach { e =>
          withClue("unpaidAmountWindow") {
            actual.unpaidAmountWindow shouldBe e
          }
        }

        expectedResponse.suppressionApplied.foreach { expectedSuppression =>
          expectedSuppression.reason.foreach { e =>
            withClue("reason") {
              actual.suppressionApplied.head.reason shouldBe e
            }
          }

          expectedSuppression.description.foreach { e =>
            withClue("description") {
              actual.suppressionApplied.head.description shouldBe e
            }
          }

          expectedSuppression.code.foreach { e =>
            withClue("code") {
              actual.suppressionApplied.head.code shouldBe e
            }
          }
        }
      }
    }
  }

  def theFcDebtSummaryWillNotHaveAnyCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Unit =
    getFCCountOfCalculationWindows(context, summaryIndex) shouldBe 0

  def getFCCountOfCalculationWindows(context: FieldCollectionsContext, summaryIndex: Int): Int = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))
    responseBody
      .debtCalculations(summaryIndex - 1)
      .calculationWindows
      .size
  }

}
