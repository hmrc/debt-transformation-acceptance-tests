package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.scalatest.builders.IFSInstalmentCalculationBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext

trait IFSInstalmentCalculationStepHelpers { this: Matchers =>

  // ^debt instalment calculation with details$
  def debtInstalmentCalculationWithDetails(context: IFSInstalmentCalculationContext): Unit = {
    // createInstalmentCalculationRequestBody(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

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
  def theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context: IFSInstalmentCalculationContext, postCode: String): Unit = {
    // addPostCodeToInstalmentCalculation(postCode, LocalDate.now().minusYears(1).toString)
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation has no postcodes$
  def theInstalmentCalculationHasNoPostcodes(context: IFSInstalmentCalculationContext): Unit = {
    // addEmptyPostCodeArrayToInstalmentCalculation()
    // TODO: Implement typed helper for this step.
  }

  // ^debt plan details with initial payment$
  def debtPlanDetailsWithInitialPayment(context: IFSInstalmentCalculationContext, inputs: Seq[IFSInstalmentCalculationBuilder.InitialPaymentInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using IFSInstalmentCalculationBuilder.
    // Suggested type: IFSInstalmentCalculationBuilder.InitialPaymentInput
  }

  // ^no initial payment for the debt item charge$
  def noInitialPaymentForTheDebtItemCharge(context: IFSInstalmentCalculationContext): Unit = {
    // noInitialPayment()
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation detail(s) is sent to the ifs service$
  def theInstalmentCalculationDetailIsSentToTheIfsService(context: IFSInstalmentCalculationContext, p1: String): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage
    // val request  = IFSInstalmentCalculationContext.get("paymentPlan").toString
    // println(s"IFS REQUST --> $request")
    // val response = getInstalmentCalculation(request)
    // println(s"RESP --> ${response.body}")
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation is sent to the ifs service with query parameters$
  def theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context: IFSInstalmentCalculationContext, input: IFSInstalmentCalculationBuilder.DebtItemChargesToInstalmentCalculationInput): Unit = {
    // TODO: Wire input into context or request JSON using IFSInstalmentCalculationBuilder.
    // Suggested type: IFSInstalmentCalculationBuilder.DebtItemChargesToInstalmentCalculationInput
  }

  // ^add initial payment for the debt item charge$
  def addInitialPaymentForTheDebtItemCharge(context: IFSInstalmentCalculationContext, inputs: Seq[IFSInstalmentCalculationBuilder.InitialPaymentInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using IFSInstalmentCalculationBuilder.
    // Suggested type: IFSInstalmentCalculationBuilder.InitialPaymentInput
  }

  // ^ifs service returns weekly payment frequency instalment calculation plan$
  def ifsServiceReturnsWeeklyPaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs returns payment frequency summary$
  def ifsReturnsPaymentFrequencySummary(context: IFSInstalmentCalculationContext, input: InstalmentCalculationSummaryResponse): Unit = {
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
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns single payment frequency instalment calculation plan$
  def ifsServiceReturnsSinglePaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
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
  def ifsServiceReturnsMonthlyPaymentFrequencyInstalmentPlanWithInstalments(context: IFSInstalmentCalculationContext, noOfInstalments: Int): Unit = {
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
  def theInstalmentShouldHaveAnInterestAccruedOf(context: IFSInstalmentCalculationContext, index: Int, interestAccrued: Int): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // responseBody.instalments(index - 1).instalmentInterestAccrued shouldBe interestAccrued
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns monthly payment frequency instalment calculation plan$
  def ifsServiceReturnsMonthlyPaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
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
  def ifsServiceReturnsQuarterlyPaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns 6Monthly payment frequency instalment calculation plan$
  def ifsServiceReturns6monthlyPaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns Annually payment frequency instalment calculation plan$
  def ifsServiceReturnsAnnuallyPaymentFrequencyInstalmentCalculationPlan(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId       = "debtId"
    // val responseBody = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^ifs service returns monthly instalment calculation plan with initial payment$
  def ifsServiceReturnsMonthlyInstalmentCalculationPlanWithInitialPayment(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val debtId                    = "debtId"
    // val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    // TODO: Implement typed helper for this step.
  }

  // ^the instalment calculation summary contains values$
  def theInstalmentCalculationSummaryContainsValues(context: IFSInstalmentCalculationContext): Unit = {
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // response.status.shouldBe(200)
    // locally {
    // Inferred legacy table keys: response
    // TODO: Assertion step with a table, but no matching generated builder input or existing model was found.
    // Add a typed expected-response parameter and compare it against context.responseBody.
  }

  // ^IFS response contains expected values$
  def ifsResponseContainsExpectedValues(context: IFSInstalmentCalculationContext, inputs: Seq[InstalmentResponse]): Unit = {
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    // response.status.shouldBe(200)
    // map.zipWithIndex.foreach { case (expectedInstalment, _) =>
    // TODO: Assertion step. Check models and builders to use to compare against.
    // Compare 'inputs' against the actual parsed response from context.responseBody.
    // Suggested approach:
    //   context.status shouldBe 200
    //   val actualResponse = Json.parse(context.responseBody).as[/* TODO response model */]
    //   // Assert the relevant element/field against inputs.
  }

  // ^ifs service returns weekly frequency instalment calculation plan with initial payment$
  def ifsServiceReturnsWeeklyFrequencyInstalmentCalculationPlanWithInitialPayment(context: IFSInstalmentCalculationContext): Unit = {
    // Migration hint: legacy IFSInstalmentCalculationContext usage, response assertion
    // val response: StandaloneWSResponse = IFSInstalmentCalculationContext.get("response")
    // response.status shouldBe 200
    // val instalmentPaymentDate     = quoteDate.plusDays(129)
    // val debtId                    = "debtId"
    // TODO: Implement typed helper for this step.
  }

}
