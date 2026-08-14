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

import org.scalactic.source.Position
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.*
import uk.gov.hmrc.test.api.models.ifs.DebtCalculationRequest
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.*
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext

trait InterestForecastingStepHelpers extends OptionValues { this: Matchers =>

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
    expectedResponse: DebtCalculationsSummaryExpected
  )(implicit pos: Position): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    expectedResponse.combinedDailyAccrual.foreach { v =>
      withClue("combinedDailyAccrual: ") {
        responseBody.combinedDailyAccrual shouldBe v
      }
    }

    expectedResponse.interestDueCallTotal.foreach { v =>
      withClue("interestDueCallTotal: ") {
        responseBody.interestDueCallTotal shouldBe v
      }
    }

    expectedResponse.amountIntTotal.foreach { v =>
      withClue("amountIntTotal: ") {
        responseBody.amountIntTotal shouldBe v
      }
    }

    expectedResponse.amountOnIntDueTotal.foreach { v =>
      withClue("amountOnIntDueTotal: ") {
        responseBody.amountOnIntDueTotal shouldBe v
      }
    }

    expectedResponse.unpaidAmountTotal.foreach { v =>
      withClue("unpaidAmountTotal: ") {
        responseBody.unpaidAmountTotal shouldBe v
      }
    }
  }

  def theDebtSummaryWillContain(
    context: InterestForecastingContext,
    index: Int,
    expectedResponse: DebtCalculationExpected
  )(implicit pos: Position): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val debtCalculations = responseBody.debtCalculations
      .lift(index - 1)
      .getOrElse(fail(s"Missing debtCalculation at index [${index - 1}] in response"))

    withClue(s"debtCalculations[${index - 1}]") {

      expectedResponse.debtItemChargeId.foreach { v =>
        withClue("debtItemChargeId: ") {
          debtCalculations.debtItemChargeId shouldBe Some(v)
        }
      }

      expectedResponse.debtID.foreach { v =>
        withClue("debtID: ") {
          debtCalculations.debtID shouldBe Some(v)
        }
      }

      expectedResponse.interestBearing.foreach { v =>
        withClue("interestBearing: ") {
          debtCalculations.interestBearing shouldBe v
        }
      }

      expectedResponse.numberOfChargeableDays.foreach { v =>
        withClue("numberOfChargeableDays: ") {
          debtCalculations.numberOfChargeableDays shouldBe v
        }
      }

      expectedResponse.interestDueDailyAccrual.foreach { v =>
        withClue("interestDueDailyAccrual: ") {
          debtCalculations.interestDueDailyAccrual shouldBe v
        }
      }

      expectedResponse.interestDueDutyTotal.foreach { v =>
        withClue("interestDueDutyTotal: ") {
          debtCalculations.interestDueDutyTotal shouldBe v
        }
      }

      expectedResponse.amountOnIntDueDuty.foreach { v =>
        withClue("amountOnIntDueDuty: ") {
          debtCalculations.amountOnIntDueDuty shouldBe v
        }
      }

      expectedResponse.totalAmountIntDuty.foreach { v =>
        withClue("totalAmountIntDuty: ") {
          debtCalculations.totalAmountIntDuty shouldBe v
        }
      }

      expectedResponse.unpaidAmountDuty.foreach { v =>
        withClue("unpaidAmountDuty: ") {
          debtCalculations.unpaidAmountDuty shouldBe v
        }
      }

      expectedResponse.interestOnlyIndicator.foreach { v =>
        withClue("interestOnlyIndicator: ") {
          debtCalculations.interestOnlyIndicator shouldBe v
        }
      }
    }
  }

  def theIfsServiceWillRespondWith(context: InterestForecastingContext, expectedMessage: String): Unit = {
    val response: StandaloneWSResponse = context.response

    Json.stringify(response.body) should include(expectedMessage)
    response.status               should be(400)
  }

  def theDebtSummaryWillHaveCalculationWindows(
    context: InterestForecastingContext,
    summaryIndex: Int,
    inputs: List[CalculationWindowExpected]
  )(implicit pos: Position): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    inputs.zipWithIndex.foreach { case (window, index) =>
      val actual = responseBody.debtCalculations
        .lift(summaryIndex - 1)
        .getOrElse(fail(s"Missing debtCalculation at index [${summaryIndex - 1}] in response"))
        .calculationWindows
        .lift(index)
        .getOrElse(fail(s"Missing calculationWindow at index [$index] in response"))

      window.periodFrom.foreach { v =>
        withClue("periodFrom: ") {
          actual.periodFrom.toString shouldBe v.toString
        }
      }

      window.periodTo.foreach { v =>
        withClue("periodTo: ") {
          actual.periodTo.toString shouldBe v.toString
        }
      }

      window.numberOfDays.foreach { v =>
        withClue("numberOfDays: ") {
          actual.numberOfDays.toString shouldBe v.toString
        }
      }

      window.interestRate.foreach { v =>
        withClue("interestRate: ") {
          actual.interestRate.toString shouldBe v.toString
        }
      }

      window.interestDueDailyAccrual.foreach { v =>
        withClue("interestDueDailyAccrual: ") {
          actual.interestDueDailyAccrual.toString shouldBe v.toString
        }
      }

      window.interestDueWindow.foreach { v =>
        withClue("interestDueWindow: ") {
          actual.interestDueWindow.toString shouldBe v.toString
        }
      }

      window.unpaidAmountWindow.foreach { v =>
        withClue("unpaidAmountWindow: ") {
          actual.unpaidAmountWindow.toString shouldBe v.toString
        }
      }

      window.amountOnIntDueWindow.foreach { v =>
        withClue("amountOnIntDueWindow: ") {
          actual.amountOnIntDueWindow.toString shouldBe v.toString
        }
      }

      window.suppressionApplied.foreach { suppression =>
        suppression.reason.filter(_.nonEmpty).foreach { v =>
          withClue("reason: ") {
            actual.suppressionApplied.head.reason shouldBe v
          }
        }

        suppression.description.filter(_.nonEmpty).foreach { v =>
          withClue("description: ") {
            actual.suppressionApplied.head.description shouldBe v
          }
        }

        suppression.code.filter(_.nonEmpty).foreach { v =>
          withClue("code: ") {
            actual.suppressionApplied.head.code shouldBe v
          }
        }
      }

      window.breathingSpaceApplied.foreach { v =>
        withClue("breathingSpaceApplied: ") {
          actual.breathingSpaceApplied.toString shouldBe v.toString
        }
      }
    }
  }

  def theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(
    context: InterestForecastingContext,
    summaryIndex: Int,
    windowIndex: Int,
    expectedResponse: SuppressionsAppliedExpected
  )(implicit pos: Position): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val calculationWindows = responseBody.debtCalculations
      .lift(summaryIndex - 1)
      .getOrElse(fail(s"Missing debtCalculation at index [${summaryIndex - 1}] in response"))
      .calculationWindows

    val suppressions = calculationWindows
      .lift(windowIndex - 1)
      .flatMap(_.suppressionsApplied)
      .getOrElse(List.empty)

    suppressions.foreach { suppression =>
      withClue(s"suppressionsApplied[${windowIndex - 1}]") {

        expectedResponse.dateFrom.foreach { v =>
          withClue("dateFrom: ") {
            suppression.dateFrom shouldBe v
          }
        }

        expectedResponse.dateTo.foreach { v =>
          withClue("dateTo: ") {
            suppression.dateTo shouldBe Some(v)
          }
        }

        expectedResponse.reason.foreach { v =>
          withClue("reason: ") {
            suppression.reason shouldBe v
          }
        }

        expectedResponse.reasonDesc.foreach { v =>
          withClue("reasonDesc: ") {
            suppression.reasonDesc shouldBe v
          }
        }

        expectedResponse.postcode.foreach { v =>
          withClue("postcode: ") {
            suppression.postcode shouldBe Some(v)
          }
        }

        expectedResponse.mainTrans.foreach { v =>
          withClue("mainTrans: ") {
            suppression.mainTrans.value shouldBe v
          }
        }

        expectedResponse.subTrans.foreach { v =>
          withClue("subTrans: ") {
            suppression.subTrans.value shouldBe v
          }
        }

        expectedResponse.periodEnd.foreach { v =>
          withClue("periodEnd: ") {
            suppression.periodEnd.value shouldBe v
          }
        }
      }
    }
  }

  def theDebtSummaryWillNotHaveAnyCalculationWindows(context: InterestForecastingContext, summaryIndex: Int)(implicit
    pos: Position
  ): Unit = {
    val response = context.response
    response.status should be(200)

    val responseBody = context.ifsResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    withClue(s"debtCalculations[${summaryIndex - 1}] should have no calculation windows: ") {
      responseBody.debtCalculations
        .lift(summaryIndex - 1)
        .getOrElse(fail(s"Missing debtCalculation at index [${summaryIndex - 1}] in response"))
        .calculationWindows
        .size shouldBe 0
    }
  }

  def aDebtInterestTypeItem(context: InterestForecastingContext, debtInterestType: Seq[DebtInterestTypeRequest]): Unit =
    context.ditRequest = Some(debtInterestType)

  def theDebtInterestTypeResponseSummaryWillContain(
    context: InterestForecastingContext,
    index: Int,
    expectedResponse: DebtInterestTypeExpected
  )(implicit pos: Position): Unit = {
    val response = context.response
    response.status should be(200)

    val responseBody = Json
      .parse(response.body)
      .as[DebtInterestTypeResponse]
      .debts
      .lift(index - 1)
      .getOrElse(fail(s"Missing debt at index [${index - 1}] in response"))

    withClue(s"debts[${index - 1}]") {

      expectedResponse.interestBearing.foreach { v =>
        withClue("interestBearing: ") {
          responseBody.interestBearing shouldBe v
        }
      }

      expectedResponse.mainTrans.foreach { v =>
        withClue("mainTrans: ") {
          responseBody.mainTrans shouldBe v
        }
      }

      expectedResponse.subTrans.foreach { v =>
        withClue("subTrans: ") {
          responseBody.subTrans shouldBe v
        }
      }

      expectedResponse.useChargeReference.foreach { v =>
        withClue("useChargeReference: ") {
          responseBody.useChargeReference shouldBe v
        }
      }
    }
  }

}
