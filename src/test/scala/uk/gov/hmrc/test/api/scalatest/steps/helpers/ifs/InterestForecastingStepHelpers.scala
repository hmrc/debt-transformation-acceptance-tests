package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import org.scalatest.matchers.should.Matchers

import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder

trait InterestForecastingStepHelpers { this: Matchers =>

  // ^a debt item$
  def aDebtItem(context: InterestForecastingContext): Unit = {
    // createInterestFocastingRequestBody(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^no debt item$
  def noDebtItem(context: InterestForecastingContext): Unit = {
    // createInterestForcastingRequestWithNoDebtItems()
    // TODO: Implement typed helper for this step.
  }

  // ^a rule has been updated$
  def aRuleHasBeenUpdated(context: InterestForecastingContext, input: InterestForecastingBuilder.InterestTypeRequestBodyInput): Unit = {
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

  // ^(.*) debt items where interest rate changes from 3\\.0 to 3\\.25$
  def debtItemsWhereInterestRateChangesFrom30To325(context: InterestForecastingContext, numberItems: Int): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // var debtItems: String = null
    // var n                 = 0
    // while (n < numberItems) {
    // val debtItem = getBodyAsString("debtItem")
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item has payment history$
  def theDebtItemHasPaymentHistory(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.PaymentHistoryInput
  }

  // ^the debt item has no payment history$
  def theDebtItemHasNoPaymentHistory(context: InterestForecastingContext): Unit = {
    // customerWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item(s) is sent to the ifs service$
  def theDebtItemIsSentToTheIfsService(context: InterestForecastingContext, p1: String): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // val request  = InterestForecastingContext.get("debtItems").toString
    // println(s"IFS REQUEST --> $request")
    // val response = getDebtCalculation(request)
    // println(s"IFS RESPONSE --> ${response.body}")
    // TODO: Implement typed helper for this step.
  }

  // ^the debt interest type request is sent to the ifs service$
  def theDebtInterestTypeRequestIsSentToTheIfsService(context: InterestForecastingContext): Unit = {
    // Migration hint: legacy InterestForecastingContext usage
    // val request  = InterestForecastingContext.get("debtInterestTypes").toString
    // println(s"IFS REQUEST --> $request")
    // val response = getDebtInterestTypeRequestBody(request)
    // println(s"IFS Service RESPONSE --> ${response.body}")
    // TODO: Implement typed helper for this step.
  }

  // ^the ifs service wilL return a total debts summary of$
  def theIfsServiceWillReturnATotalDebtsSummaryOf(context: InterestForecastingContext): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // val responseBody = Json.parse(response.body).as[DebtCalculationsSummary]
    // locally {
    // val fieldName = "combinedDailyAccrual"
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) debt summary will contain$
  def theDebtSummaryWillContain(context: InterestForecastingContext, index: Int): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.status should be(200)
    // val responseBody: DebtCalculation =
    // Json.parse(response.body).as[DebtCalculationsSummary].debtCalculations(index - 1)
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
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
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt summary will have calculation windows$
  def theDebtSummaryWillHaveCalculationWindows(context: InterestForecastingContext, summaryIndex: Int): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // asMapTransposed.asScala.zipWithIndex.foreach { case (window, index) =>
    // val responseBody =
    // Json
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt summary will have suppression applied calculation windows$
  def theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context: InterestForecastingContext, summaryIndex: Int): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // asMapTransposed.asScala.zipWithIndex.foreach { case (window, index) =>
    // val calculationWindows = Json
    // .parse(response.body)
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
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
  def theDebtItemHasBreathingSpacesApplied(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.BreathingSpaceInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.BreathingSpaceInput
  }

  // ^no breathing spaces have been applied to the debt item$
  def noBreathingSpacesHaveBeenAppliedToTheDebtItem(context: InterestForecastingContext): Unit = {
    // noBreathingSpace()
    // TODO: Implement typed helper for this step.
  }

  // ^the customer has post codes$
  def theCustomerHasPostCodes(context: InterestForecastingContext, inputs: Seq[InterestForecastingBuilder.CustomerPostCodesInput]): Unit = {
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
    // getCountOfCalculationWindows(summaryIndex) shouldBe 0
    // TODO: Implement typed helper for this step.
  }

  // ^a debt interest type item$
  def aDebtInterestTypeItem(context: InterestForecastingContext): Unit = {
    // createInterestTypeRequestBody(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt interest type response summary will contain$
  def theDebtInterestTypeResponseSummaryWillContain(context: InterestForecastingContext, index: Int): Unit = {
    // val response: StandaloneWSResponse = InterestForecastingContext.get("response")
    // response.status should be(200)
    // val responseBody: DebtInterestType = Json.parse(response.body).as[DebtInterestTypeResponse].debts(index - 1)
    // locally {
    // Inferred legacy table keys: response
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the ([0-9])(?:st|nd|rd|th) debt applied suppression summary contains values as$
  def theDebtAppliedSuppressionSummaryContainsValuesAs(context: InterestForecastingContext, summaryIndex: Int, inputs: Seq[SuppressionRulesBuilder.SuppressionsInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using SuppressionRulesBuilder.
    // Suggested type: SuppressionRulesBuilder.SuppressionsInput
  }

}
