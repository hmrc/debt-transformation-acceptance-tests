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
import uk.gov.hmrc.test.api.models.ifs.InstalmentCalculationRequest
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.scalatest.builders.IFSInstalmentCalculationBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

trait IFSInstalmentCalculationStepHelpers {
  this: Matchers =>

  var quoteDateString                  = "2022-03-13"
  val formatter: DateTimeFormatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val quoteDate: LocalDate             = LocalDate.parse(quoteDateString, formatter)
  val instalmentPaymentDate: LocalDate = quoteDate.plusDays(1)

  // ^debt instalment calculation with details$
  def instalmentCalculationDetails(
    context: IFSInstalmentCalculationContext,
    request: InstalmentCalculationRequest
  ): Unit =
    context.ifsRequest = Some(request)

  // ^debt instalment calculation with 129 details$
  def debtInstalmentCalculationWith129Details(context: IFSInstalmentCalculationContext): Unit = {
    // createInstalmentCalculationRequestBody(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the instalment calculation has debt item charges$
  def theInstalmentCalculationHasDebtItemCharges(context: IFSInstalmentCalculationContext): Unit = {
    // addDebtItemChargesToInstalmentCalculation(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the instalment calculation has postcode (.*) with postcode date a year in the past$
  def theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(
    context: IFSInstalmentCalculationContext,
    postCode: String
  ): Unit = {
    // addPostCodeToInstalmentCalculation(postCode, LocalDate.now().minusYears(1).toString)
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation has no postcodes$
  def theInstalmentCalculationHasNoPostcodes(context: IFSInstalmentCalculationContext): Unit = {
    // addEmptyPostCodeArrayToInstalmentCalculation()
    // TODO: Implement typed helper for this step.
  }

  // ^debt plan details with initial payment$
  def debtPlanDetailsWithInitialPayment(
    context: IFSInstalmentCalculationContext,
    inputs: Seq[IFSInstalmentCalculationBuilder.InitialPaymentInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using IFSInstalmentCalculationBuilder.
    // Suggested type: IFSInstalmentCalculationBuilder.InitialPaymentInput
  }

  // ^no initial payment for the debt item charge$
  def noInitialPaymentForTheDebtItemCharge(context: IFSInstalmentCalculationContext): Unit = {
    // noInitialPayment()
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation detail(s) is sent to the ifs service$
  def theInstalmentCalculationDetailIsSentToTheIfsService(context: IFSInstalmentCalculationContext): Unit = {
    val requestJson                    = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = IFSInstalmentCalculationBuilder.getInstalmentCalculation(requestJson)
    context.response = response

    val jsonResponseBody = response.body[JsValue]
    context.ifsResponseBody = Some(jsonResponseBody.as[InstalmentCalculationSummaryResponse])
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    println("\n==== RESPONSE BODY ====")
    println(jsonResponseBody)
  }

  // ^the instalment calculation is sent to the ifs service with query parameters$
  def theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(
    context: IFSInstalmentCalculationContext,
    combineLastInstalments: String
  ): Unit = {
    val requestJson = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))

    val response: StandaloneWSResponse =
      IFSInstalmentCalculationBuilder.getInstalmentCalculationWithQueryParams(requestJson, combineLastInstalments)

    context.response = response

    val jsonResponseBody = response.body[JsValue]
    context.ifsResponseBody = Some(jsonResponseBody.as[InstalmentCalculationSummaryResponse])
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    println("\n==== RESPONSE BODY ====")
    println(jsonResponseBody)
  }

  // ^add initial payment for the debt item charge$
  def addInitialPaymentForTheDebtItemCharge(
    context: IFSInstalmentCalculationContext,
    inputs: Seq[IFSInstalmentCalculationBuilder.InitialPaymentInput]
  ): Unit = {
    // TODO: Wire inputs into context or request JSON using IFSInstalmentCalculationBuilder.
    // Suggested type: IFSInstalmentCalculationBuilder.InitialPaymentInput
  }

  // ^ifs service returns weekly payment frequency instalment calculation plan$
  def ifsServiceReturnsWeeklyPaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs returns payment frequency summary$
  def ifsReturnsPaymentFrequencySummary(
    context: IFSInstalmentCalculationContext,
    input: InstalmentCalculationSummaryResponse
  ): Unit = {
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("paymentPlan")
    // response.status should be(200)
    // val paymentPlanSummary = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // paymentPlanSummary.numberOfInstalments.toString shouldBe (asMapTransposed
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'input' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against input.
  }

  // ^ifs service returns an interest bearing payment instalment plan$
  def ifsServiceReturnsAnInterestBearingPaymentInstalmentPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns an non-interest bearing payment instalment plan$
  def ifsServiceReturnsAnNonInterestBearingPaymentInstalmentPlan(context: IFSInstalmentCalculationContext): Unit = {
    val response: StandaloneWSResponse = context.response
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      218,
      1423,
      1423 + 218,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusDays(1), 10000, 90000, 6, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusDays(2), 10000, 80000, 5, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusDays(3), 10000, 70000, 4, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusDays(4), 10000, 60000, 4, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusDays(5), 10000, 50000, 3, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusDays(6), 10000, 40000, 2, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusDays(7), 10000, 30000, 2, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusDays(8), 10000, 20000, 1, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusDays(9), 10000, 10000, 0, 100000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusDays(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  // ^ifs service returns single payment frequency instalment calculation plan$
  def ifsServiceReturnsSinglePaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns 2-Weekly frequency instalment calculation plan$
  def ifsServiceReturns2WeeklyFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns monthly payment frequency instalment plan with (.*) instalments$
  def ifsServiceReturnsMonthlyPaymentFrequencyInstalmentPlanWithInstalments(
    context: IFSInstalmentCalculationContext,
    noOfInstalments: Int
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val responseBody = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // responseBody.numberOfInstalments shouldBe noOfInstalments
    // TODO: Implement typed helper for this step.
  }

  // ^the IFS request should return status (.*)$
  def theIfsRequestShouldReturnStatus(context: IFSInstalmentCalculationContext, status: Int): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe status
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) instalment should have an interest accrued of (.*)$
  def theInstalmentShouldHaveAnInterestAccruedOf(
    context: IFSInstalmentCalculationContext,
    index: Int,
    interestAccrued: Int
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // responseBody.instalments(index - 1).instalmentInterestAccrued shouldBe interestAccrued
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns monthly payment frequency instalment calculation plan$
  def ifsServiceReturnsMonthlyPaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns 4-Weekly frequency instalment calculation plan$
  def ifsServiceReturns4WeeklyFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns Quarterly payment frequency instalment calculation plan$
  def ifsServiceReturnsQuarterlyPaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns 6Monthly payment frequency instalment calculation plan$
  def ifsServiceReturns6monthlyPaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns Annually payment frequency instalment calculation plan$
  def ifsServiceReturnsAnnuallyPaymentFrequencyInstalmentCalculationPlan(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId       = "debtId"
    // val responseBody = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns monthly instalment calculation plan with initial payment$
  def ifsServiceReturnsMonthlyInstalmentCalculationPlanWithInitialPayment(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^IFS response contains expected values$
  def ifsResponseContainsExpectedValues(
    context: IFSInstalmentCalculationContext,
    expectedResponse: InstalmentCalculationSummaryResponse
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    withClue("InstalmentCalculationSummaryResponse") {
      withClue("dateOfCalculation") {
        responseBody.dateOfCalculation shouldBe expectedResponse.dateOfCalculation
      }

      withClue("numberOfInstalments") {
        responseBody.numberOfInstalments shouldBe expectedResponse.numberOfInstalments
      }

      withClue("planInterest") {
        responseBody.planInterest shouldBe expectedResponse.planInterest
      }

      withClue("interestAccrued") {
        responseBody.interestAccrued shouldBe expectedResponse.interestAccrued
      }

      withClue("totalInterest") {
        responseBody.totalInterest shouldBe expectedResponse.totalInterest
      }

      withClue("duration") {
        responseBody.duration shouldBe expectedResponse.duration
      }

      ifsResponseContainsExpectedValues(context, responseBody.instalments)
    }
  }

  def ifsResponseContainsExpectedValues(
    context: IFSInstalmentCalculationContext,
    expectedResponse: Seq[InstalmentResponse]
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    expectedResponse.foreach { expectedInstalment =>
      val responseIndex: Int = expectedInstalment.instalmentNumber - 1

      val actualInstalment = responseBody.instalments
        .lift(responseIndex)
        .getOrElse(
          fail(s"Missing instalment at index [$responseIndex] in response")
        )

      withClue("Instalments") {
        withClue("debtId") {
          actualInstalment.debtId shouldBe expectedInstalment.debtId
        }

        withClue("instalmentNumber") {
          actualInstalment.instalmentNumber shouldBe expectedInstalment.instalmentNumber
        }

        withClue("dueDate") {
          actualInstalment.dueDate shouldBe expectedInstalment.dueDate
        }

        withClue("amountDue") {
          actualInstalment.amountDue shouldBe expectedInstalment.amountDue
        }

        withClue("instalmentBalance") {
          actualInstalment.instalmentBalance shouldBe expectedInstalment.instalmentBalance
        }

        withClue("instalmentInterestAccrued") {
          actualInstalment.instalmentInterestAccrued shouldBe expectedInstalment.instalmentInterestAccrued
        }

        withClue("expectedPayment") {
          actualInstalment.expectedPayment shouldBe expectedInstalment.expectedPayment
        }

        withClue("intRate") {
          actualInstalment.intRate shouldBe expectedInstalment.intRate
        }
      }
    }
  }

  // ^ifs service returns weekly frequency instalment calculation plan with initial payment$
  def ifsServiceReturnsWeeklyFrequencyInstalmentCalculationPlanWithInitialPayment(
    context: IFSInstalmentCalculationContext
  ): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val instalmentPaymentDate     = quoteDate.plusDays(129)
    // val debtId                    = "debtId"
    // TODO: Implement typed helper for this step.
  }

}
