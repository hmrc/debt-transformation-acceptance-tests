package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.DebtCalculationsSummary
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests
import uk.gov.hmrc.test.api.scalatest.builders.{FCStatementOfLiabilityBuilder, InterestForecastingBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext

trait FCStatementOfLiabilityStepHelpers { this: Matchers =>

  // ^fc sol request$
  def fcSolRequest(context: FCStatementOfLiabilityContext, inputs: Seq[FCStatementOfLiabilityBuilder.FcSolRequestInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using FCStatementOfLiabilityBuilder.
    // Suggested type: FCStatementOfLiabilityBuilder.FcSolRequestInput
  }

  // ^fc sol debt item has multiple debts with charge interest$
  def fcSolDebtItemHasMultipleDebtsWithChargeInterest(context: FCStatementOfLiabilityContext): Unit = {
    // FCStatementOfLiabilityRequests.fcSolWithCotaxInterestChargeRequest(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the fc sol debt item has multiple debts$
  def theFcSolDebtItemHasMultipleDebts(context: FCStatementOfLiabilityContext): Unit = {
    // FCStatementOfLiabilityRequests.addFCDebts(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^the fc sol debt item has no debts$
  def theFcSolDebtItemHasNoDebts(context: FCStatementOfLiabilityContext): Unit = {
    // Migration hint: request object reference: FCStatementOfLiabilityRequests
    // FCStatementOfLiabilityRequests.FCSolWithNoDebts()
    // TODO: Implement typed helper for this step.
  }

  // ^the debt item has fc sol payment history$
  def theDebtItemHasFcSolPaymentHistory(context: FCStatementOfLiabilityContext, inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using InterestForecastingBuilder.
    // Suggested type: InterestForecastingBuilder.PaymentHistoryInput
  }

  // ^the fc sol debt item has no payment history$
  def theFcSolDebtItemHasNoPaymentHistory(context: FCStatementOfLiabilityContext): Unit = {
    // FCSolWithNoPaymentHistory()
    // TODO: Implement typed helper for this step.
  }

  // ^a debt fc statement of liability is requested$
  def aDebtFcStatementOfLiabilityIsRequested(context: FCStatementOfLiabilityContext): Unit = {
    val response = FCStatementOfLiabilityRequests.getFCStatementOfLiability(context.request)

    context.status = response.status
    context.responseBody = response.body
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  // ^service returns fc debt statement of liability data$
  def serviceReturnsFcDebtStatementOfLiabilityData(context: FCStatementOfLiabilityContext, input: DebtCalculationsSummary): Unit = {
//    context.debtCalculationsSummary = input
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) multiple fc statement of liability debt summary will contain duties$
//  def theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context: FCStatementOfLiabilityContext, _: Int, inputs: Seq[FCSolCalculation]): Unit = {
//    context.fCSolCalculation = inputs
//  }

  // ^the fc sol service will respond with (.*)$
  def theFcSolServiceWillRespondWith(context: FCStatementOfLiabilityContext, expectedMessage: String): Unit = {
    // Migration hint: legacy FCStatementOfLiabilityContext usage, response assertion
    // val response: StandaloneWSResponse = FCStatementOfLiabilityContext.get("response")
    // response.body   should include(expectedMessage)
    // response.status should be(400)
    // TODO: Implement typed helper for this step.
  }

}
