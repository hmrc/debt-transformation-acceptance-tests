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
import uk.gov.hmrc.test.api.models.ifs.DebtCalculationRequest
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext

trait InterestForecastingStepHelpers { this: Matchers =>

  def aDebtCalculationIsCreated(context: InterestForecastingContext, request: DebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  def theDebtItemIsSentToTheIfsService(context: InterestForecastingContext): Unit = {
    val requestJson                    = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = InterestForecastingBuilder.getDebtCalculation(requestJson)
    context.response = response

    val jsonResponseBody = response.body[JsValue]
    context.ifsResponseBody = Some(jsonResponseBody.as[DebtCalculationsSummary])
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    println("\n==== RESPONSE BODY ====")
    println(jsonResponseBody)
  }

  def theDebtItemIsSentToTheIfsServiceAndFails(
    context: InterestForecastingContext
  ): Unit = {
    val requestJson = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response    = InterestForecastingBuilder.getDebtCalculation(requestJson)

    context.response = response
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap
  }

  def theDebtInterestTypeRequestIsSentToTheIfsService(context: InterestForecastingContext): Unit = {
    val requestJson                    = Json.toJson(context.ditRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = InterestForecastingBuilder.getDebtInterestTypeRequestBody(requestJson)
    context.response = response

    val jsonResponseBody = response.body[JsValue]
    context.ditResponseBody = Some(jsonResponseBody.as[DebtInterestTypeResponse])
    context.status = response.status

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    println("\n==== RESPONSE BODY ====")
    println(jsonResponseBody)
  }

  def theIfsServiceWillReturnATotalDebtsSummaryOf(
    context: InterestForecastingContext,
    expectedResponse: DebtCalculationsSummary
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    withClue("combinedDailyAccrual") {
      responseBody.combinedDailyAccrual shouldBe expectedResponse.combinedDailyAccrual
    }

    withClue("interestDueCallTotal") {
      responseBody.interestDueCallTotal shouldBe expectedResponse.interestDueCallTotal
    }

    withClue("amountIntTotal") {
      responseBody.amountIntTotal shouldBe expectedResponse.amountIntTotal
    }

    withClue("amountOnIntDueTotal") {
      responseBody.amountOnIntDueTotal shouldBe expectedResponse.amountOnIntDueTotal
    }

    withClue("unpaidAmountTotal") {
      responseBody.unpaidAmountTotal shouldBe expectedResponse.unpaidAmountTotal
    }

  }

  def theDebtSummaryWillContain(
    context: InterestForecastingContext,
    index: Int,
    expectedResponse: DebtCalculation
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val debtCalculations = responseBody.debtCalculations(index - 1)

    withClue("DebtCalculations") {
      withClue("debtItemChargeId") {
        debtCalculations.debtItemChargeId shouldBe expectedResponse.debtItemChargeId
      }

      withClue("debtID") {
        debtCalculations.debtID shouldBe expectedResponse.debtID
      }

      withClue("interestBearing") {
        debtCalculations.interestBearing shouldBe expectedResponse.interestBearing
      }

      withClue("numberOfChargeableDays") {
        debtCalculations.numberOfChargeableDays shouldBe expectedResponse.numberOfChargeableDays
      }

      withClue("interestDueDailyAccrual") {
        debtCalculations.interestDueDailyAccrual shouldBe expectedResponse.interestDueDailyAccrual
      }

      withClue("interestDueDutyTotal") {
        debtCalculations.interestDueDutyTotal shouldBe expectedResponse.interestDueDutyTotal
      }

      withClue("amountOnIntDueDuty") {
        debtCalculations.amountOnIntDueDuty shouldBe expectedResponse.amountOnIntDueDuty
      }

      withClue("totalAmountIntDuty") {
        debtCalculations.totalAmountIntDuty shouldBe expectedResponse.totalAmountIntDuty
      }

      withClue("totalAmountIntDuty") {
        debtCalculations.unpaidAmountDuty shouldBe expectedResponse.unpaidAmountDuty
      }

      withClue("totalAmountIntDuty") {
        debtCalculations.unpaidAmountDuty shouldBe expectedResponse.unpaidAmountDuty
      }
    }
  }

  def theDebtSummaryWillHaveCalculationWindows(
    context: InterestForecastingContext,
    summaryIndex: Int,
    inputs: List[CalculationWindow]
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    inputs.zipWithIndex.foreach { case (expectedResponse, index) =>
      val actual = responseBody
        .debtCalculations(summaryIndex - 1)
        .calculationWindows(index)

      withClue("CalculationWindows") {
        withClue("periodFrom: ") {
          actual.periodFrom shouldBe expectedResponse.periodFrom
        }

        withClue("periodTo: ") {
          actual.periodTo shouldBe expectedResponse.periodTo
        }

        withClue("numberOfDays: ") {
          actual.numberOfDays shouldBe expectedResponse.numberOfDays
        }

        withClue("interestRate: ") {
          actual.interestRate shouldBe expectedResponse.interestRate
        }

        withClue("interestDueDailyAccrual: ") {
          actual.interestDueDailyAccrual shouldBe expectedResponse.interestDueDailyAccrual
        }

        withClue("interestDueWindow: ") {
          actual.interestDueWindow shouldBe expectedResponse.interestDueWindow
        }

        withClue("amountOnIntDueWindow: ") {
          actual.amountOnIntDueWindow shouldBe expectedResponse.amountOnIntDueWindow
        }

        withClue("unpaidAmountWindow: ") {
          actual.unpaidAmountWindow shouldBe expectedResponse.unpaidAmountWindow
        }

        withClue("breathingSpaceApplied: ") {
          actual.breathingSpaceApplied shouldBe expectedResponse.breathingSpaceApplied
        }

        // only assert suppressionApplied fields if they are present in input
        expectedResponse.suppressionApplied.foreach { expectedSuppression =>
          if (expectedSuppression.reason.nonEmpty) {
            withClue("reason: ") {
              actual.suppressionApplied.head.reason shouldBe expectedSuppression.reason
            }
          }

          if (expectedSuppression.description.nonEmpty) {
            withClue("description: ") {
              actual.suppressionApplied.head.description shouldBe expectedSuppression.description
            }
          }

          if (expectedSuppression.code.nonEmpty) {
            withClue("code: ") {
              actual.suppressionApplied.head.code shouldBe expectedSuppression.code
            }
          }
        }
      }
    }
  }

  def theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(
    context: InterestForecastingContext,
    summaryIndex: Int,
    windowIndex: Int,
    expectedResponse: SuppressionsApplied
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val calculationWindows = responseBody
      .debtCalculations(summaryIndex - 1)
      .calculationWindows

    if (calculationWindows.isDefinedAt(windowIndex - 1)) {
      val suppressions = calculationWindows(windowIndex - 1).suppressionsApplied
        .getOrElse(List.empty)

      suppressions.foreach { suppression =>
        withClue("SuppressionsApplied") {
          withClue("dateFrom") {
            suppression.dateFrom shouldBe expectedResponse.dateFrom
          }

          withClue("dateTo") {
            suppression.dateTo shouldBe expectedResponse.dateTo
          }

          withClue("reason") {
            suppression.reason shouldBe expectedResponse.reason
          }

          withClue("reasonDesc") {
            suppression.reasonDesc shouldBe expectedResponse.reasonDesc
          }

          withClue("postcode") {
            suppression.postcode shouldBe expectedResponse.postcode
          }

          withClue("mainTrans") {
            suppression.mainTrans shouldBe expectedResponse.mainTrans
          }

          withClue("subTrans") {
            suppression.subTrans shouldBe expectedResponse.subTrans
          }

          withClue("periodEnd") {
            suppression.periodEnd shouldBe expectedResponse.periodEnd
          }
        }
      }
    }
  }
  def theDebtSummaryWillNotHaveAnyCalculationWindows(context: InterestForecastingContext, summaryIndex: Int): Unit = {
    val response: StandaloneWSResponse = context.response
    response.status should be(200)

    Json
      .parse(response.body)
      .as[DebtCalculationsSummary]
      .debtCalculations(summaryIndex - 1)
      .calculationWindows
      .size shouldBe 0
  }

  def aDebtInterestTypeItem(context: InterestForecastingContext, debtInterestType: Seq[DebtInterestTypeRequest]): Unit =
    context.ditRequest = Some(debtInterestType)

  def theDebtInterestTypeResponseSummaryWillContain(
    context: InterestForecastingContext,
    index: Int,
    expectedResponse: DebtInterestType
  ): Unit = {
    val response: StandaloneWSResponse = context.response
    response.status should be(200)

    val responseBody: DebtInterestType = Json.parse(response.body).as[DebtInterestTypeResponse].debts(index - 1)

    locally {
      withClue("interestBearing") {
        responseBody.interestBearing shouldBe expectedResponse.interestBearing
      }
    }
    locally {

      withClue(s"mainTrans") {
        responseBody.mainTrans shouldBe expectedResponse.mainTrans
      }
    }

    locally {
      withClue("subTrans") {
        responseBody.subTrans shouldBe expectedResponse.subTrans
      }
    }

    locally {
      withClue("useChargeReference") {
        responseBody.useChargeReference shouldBe expectedResponse.useChargeReference
      }
    }
  }

}
