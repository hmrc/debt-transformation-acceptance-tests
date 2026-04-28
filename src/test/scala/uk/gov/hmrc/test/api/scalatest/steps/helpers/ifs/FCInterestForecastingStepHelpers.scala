package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import org.scalatest.matchers.should.Matchers

import uk.gov.hmrc.test.api.scalatest.builders.FCStatementOfLiabilityBuilder
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsBuilder
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder

// TODO: Validate that InterestForecastingContext is the correct context for helpers migrated from FCInterestForecastingSteps.scala.
trait FCInterestForecastingStepHelpers { this: Matchers =>

  // ^a fc debt item$
  def aFcDebtItem(context: InterestForecastingContext): Unit = {
    // createInterestFocastingRequestBodyFC(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^fc debt item with cotax charge interest$
  def fcDebtItemWithCotaxChargeInterest(context: InterestForecastingContext): Unit = {
    // createFcCotaxChargeInterestRequest(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the debt item has fc payment history$
  def theDebtItemHasFcPaymentHistory(context: InterestForecastingContext, inputs: Seq[FieldCollectionsBuilder.FCPaymentHistoryInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using FieldCollectionsBuilder.
    // Suggested type: FieldCollectionsBuilder.FCPaymentHistoryInput
  }

  // ^the fc debt item has no payment history$
  def theFcDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // fcCustomerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the fc ifs service$
  def theDebtItemIsSentToTheFcIfsService(context: InterestForecastingContext, p1: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // val request  = InterestForecastingContext.get("fcDebtItem").toString
    // println(s"IFS REQUEST --> $request")
    // val response = getDebtCalculation(request)
    // println(s"IFS RESPONSE --> ${response.body}")
    // TODO: Implement typed helper for this step.
  }

  // ^the fc ifs service wilL return a total debts summary of$
  def theFcIfsServiceWillReturnATotalDebtsSummaryOf(context: InterestForecastingContext): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // val responseBody = Json.parse(response.body).as[FCDebtCalculationsSummary]
    // locally {
    // val fieldName = "combinedDailyAccrual"
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) fc debt summary will contain$
  def theFcDebtSummaryWillContain(context: InterestForecastingContext, index: Int, input: FCStatementOfLiabilityBuilder.FCDebtsInput): Unit = {
    // TODO: Wire input into context or request JSON using FCStatementOfLiabilityBuilder.
    // Suggested type: FCStatementOfLiabilityBuilder.FCDebtsInput
  }

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will have calculation windows$
  def theFcDebtSummaryWillHaveCalculationWindows(context: InterestForecastingContext, summaryIndex: Int, inputs: Seq[FCStatementOfLiabilityBuilder.FCDebtsInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using FCStatementOfLiabilityBuilder.
    // Suggested type: FCStatementOfLiabilityBuilder.FCDebtsInput
  }

  // ^the fc customer has breathing spaces applied$
  def theFcCustomerHasBreathingSpacesApplied(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the fc debt item$
  def noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context: InterestForecastingContext): Unit = {
    // noFCBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the fc customer has post codes$
  def theFcCustomerHasPostCodes(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.CustomerPostCodesInput]): Unit = {
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
  def theFcDebtSummaryWillNotHaveAnyCalculationWindows(context: InterestForecastingContext, summaryIndex: Int): Unit = {
    // getFCCountOfCalculationWindows(summaryIndex) shouldBe 0
    // TODO: Implement typed helper for this step.
  }

  // ^the fc ifs service will respond with (.*)$
  def theFcIfsServiceWillRespondWith(context: InterestForecastingContext, expectedMessage: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage, response assertion
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.body   should include(expectedMessage)
    // response.status should be(400)
    // TODO: Implement typed helper for this step.
  }

  // ^the ([0-9])(?:st|nd|rd|th) fc debt summary will have ([0-9]) calculation windows$
  def theFcDebtSummaryWillHaveCalculationWindows2(context: InterestForecastingContext, summaryIndex: Int, numberOfWindows: Int): Unit = {
    // getFCCountOfCalculationWindows(summaryIndex) shouldBe numberOfWindows
    // TODO: Implement typed helper for this step.
  }

}
