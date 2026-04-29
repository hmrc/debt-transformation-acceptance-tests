package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext

// TODO: Validate that InterestForecastingContext is the correct context for helpers migrated from FCVATInterestForecastingSteps.scala.
trait FCVATInterestForecastingStepHelpers { this: Matchers =>

  // ^a fc vat debt item$
  def aFcVatDebtItem(context: InterestForecastingContext): Unit = {
    // createInterestForecastingRequestBodyFCVAT(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the fc vat debt item has payment history$
  def theFcVatDebtItemHasPaymentHistory(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.PaymentHistoryInput
  }

  // ^the fc vat debt item has no payment history$
  def theFcVatDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // fcVatCustomerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the fc vat ifs service$
  def theDebtItemIsSentToTheFcVatIfsService(context: InterestForecastingContext, p1: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // addFcVatDebtItemRequest()
    // val request  = InterestForecastingContext.get("fcVatDebtItem").toString
    // println(s"IFS REQUEST --> $request")
    // val response = getDebtCalculation(request)
    // TODO: Implement typed helper for this step.
  }

  // ^the fc vat ifs service wilL return a total debts summary of$
  def theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context: InterestForecastingContext): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // val toDaysDate                     = LocalDate.now().toString
    // val responseBody = Json.parse(response.body).as[FCVATDebtCalculationsSummary]
    // responseBody.dateOfCalculation.toString shouldBe toDaysDate
    // Inferred legacy table keys: response
    // TODO: Assertion step with a table, but no matching generated builder input or existing model was found.
    // Add a typed expected-response parameter and compare it against context.responseBody.
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) fc vat debt summary will contain$
  def theFcVatDebtSummaryWillContain(context: InterestForecastingContext, index: Int): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.status should be(200)
    // val responseBody =
    // Json
    // Inferred legacy table keys: response
    // TODO: Assertion step with a table, but no matching generated builder input or existing model was found.
    // Add a typed expected-response parameter and compare it against context.responseBody.
  }

  // ^the fc vat customer has breathing spaces applied$
  def theFcVatCustomerHasBreathingSpacesApplied(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the fc vat customer$
  def noBreathingSpacesHaveBeenAppliedToTheFcVatCustomer(context: InterestForecastingContext): Unit = {
    // noFCVatBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the fc vat ifs service will respond with (.*)$
  def theFcVatIfsServiceWillRespondWith(context: InterestForecastingContext, expectedMessage: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage, response assertion
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.body   should include(expectedMessage)
    // response.status should be(400)
    // TODO: Implement typed helper for this step.
  }

}
