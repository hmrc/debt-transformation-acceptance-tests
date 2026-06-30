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

  // ^a debt calculation$
  def aDebtCalculationIsCreated(context: InterestForecastingContext, request: DebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  // ^no debt item$
  def noDebtItem(context: InterestForecastingContext): Unit = {
    // createInterestForcastingRequestWithNoDebtItems()
    // TODO: Implement typed helper for this step.
  }

  // ^a rule has been updated$
  def aRuleHasBeenUpdated(
    context: InterestForecastingContext,
    input: InterestForecastingBuilder.InterestTypeRequestBodyInput
  ): Unit = {
    // TODO: Wire input into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.InterestTypeRequestBodyInput
  }

  // ^the current set of rules$
  def theCurrentSetOfRules(context: InterestForecastingContext): Unit = {
    // Migration hint: response assertion
    // val responseGEtRules = getAllRules
    // val collection        = Json.parse(responseGEtRules.body).as[GetRulesResponse]
    // val existingProdRules = collection.rules.find(_.version == 1)
    // existingProdRules match {
    // TODO: Implement typed helper for this step.
  }

  // ^(.*) debt items$
  def debtItems(context: InterestForecastingContext, numberItems: Int): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // var debtItems: String = null
    // var n                 = 0
    // while (n < numberItems) {
    // val debtItem = getBodyAsString("debtItem")
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item has payment history$
  def theDebtItemHasPaymentHistory(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.PaymentHistoryInput
  }

  // ^the debt item has no payment history$
  def theDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // customerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the ifs service$
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

  // ^the debt interest type request is sent to the ifs service$
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

  // ^the ifs service will return a total debts summary of$
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

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) debt summary will contain$
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

  // ^the ifs service will respond with (.*)$
  def theIfsServiceWillRespondWith(context: InterestForecastingContext, expectedMessage: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage, response assertion
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.body   should include(expectedMessage)
    // response.status should be(400)
    // TODO: Implement typed helper for this step.
  }

  // ^the ifs service will respond with$
  def theIfsServiceWillRespondWith2(context: InterestForecastingContext): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // val errorResponse                  = Json.parse(response.body).as[Errors]
    // locally {
    // val fieldName = "statusCode"
    // Inferred legacy table keys: response
    // TODO: Assertion step with a table, but no matching generated builder input or existing model was found.
    // Add a typed expected-response parameter and compare it against context.responseBody.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt summary will have calculation windows$
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

  // ^the ([0-9])(?:st|nd|rd|th) debt summary will have suppression applied calculation windows$
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

  // ^Ifs service returns response code (.*)$
  def ifsServiceReturnsResponseCode(context: InterestForecastingContext, expectedCode: Int): Unit = {
    // Migration hint: legacy InterestForecastingContext usage, response assertion
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.status should be(expectedCode)
    // TODO: Implement typed helper for this step.
  }

  // ^Ifs service returns error message (.*)$
  def ifsServiceReturnsErrorMessage(context: InterestForecastingContext, expectedMessage: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // val responseBody                   = response.body.stripMargin
    // print("response message*****************************" + responseBody)
    // responseBody should be(expectedMessage)
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item has breathing spaces applied$
  def theDebtItemHasBreathingSpacesApplied(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the debt item$
  def noBreathingSpacesHaveBeenAppliedToTheDebtItem(context: InterestForecastingContext): Unit = {
    // noBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the customer has post codes$
  def theCustomerHasPostCodes(
    context: InterestForecastingContext,
    inputs: Seq[InterestForecastingBuilder.CustomerPostCodesInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.CustomerPostCodesInput
  }

  // ^no post codes have been provided for the customer$
  def noPostCodesHaveBeenProvidedForTheCustomer(context: InterestForecastingContext): Unit = {
    // noCustomerPostCodes()
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt summary will not have any calculation windows$
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

  // ^a debt interest type item$
  def aDebtInterestTypeItem(context: InterestForecastingContext, debtInterestType: Seq[DebtInterestTypeRequest]): Unit =
    context.ditRequest = Some(debtInterestType)

  // ^the ([0-9])(?:st|nd|rd|th) debt interest type response summary will contain$
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

  // ^the ([0-9])(?:st|nd|rd|th) debt applied suppression summary contains values as$
  def theDebtAppliedSuppressionSummaryContainsValuesAs(
    context: InterestForecastingContext,
    summaryIndex: Int,
    inputs: Seq[SuppressionApplied]
  ): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // asMapTransposed.asScala.zipWithIndex.foreach { case (window, index) =>
    // val maybeSuppression = for {
    // debt        <-
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'inputs' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against inputs.
  }

}
