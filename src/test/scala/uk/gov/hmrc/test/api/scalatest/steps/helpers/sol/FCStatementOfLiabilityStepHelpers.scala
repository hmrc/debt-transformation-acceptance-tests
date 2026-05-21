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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.sol.{FCSolCalculation, FCSolCalculationSummaryResponse, SolMultipleDebtsRequest}
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests
import uk.gov.hmrc.test.api.scalatest.builders.{InterestForecastingBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import play.api.libs.json.{JsValue}
import play.api.libs.ws.JsonBodyReadables.readableAsJson

trait FCStatementOfLiabilityStepHelpers {
  this: Matchers =>

  def fcSolRequest(
    context: FCStatementOfLiabilityContext,
    request: SolMultipleDebtsRequest
  ): Unit =
    context.request = Some(request)

  def fcSolDebtItemHasMultipleDebtsWithChargeInterest(context: FCStatementOfLiabilityContext): Unit = {}

  def theFcSolDebtItemHasMultipleDebts(context: FCStatementOfLiabilityContext): Unit = {}
  def theFcSolDebtItemHasNoDebts(context: FCStatementOfLiabilityContext): Unit       = {}

  def theDebtItemHasFcSolPaymentHistory(
    context: FCStatementOfLiabilityContext,
    inputs: Seq[InterestForecastingBuilder.PaymentHistoryInput]
  ): Unit = {}

  def theFcSolDebtItemHasNoPaymentHistory(context: FCStatementOfLiabilityContext): Unit = {}

  def aDebtFcStatementOfLiabilityIsRequested(context: FCStatementOfLiabilityContext): Unit = {
    val response = FCStatementOfLiabilityRequests.getFCStatementOfLiability(context.request)

    val jsonResponse = response.body[JsValue]
    context.status = response.status
    context.responseBody = Some(jsonResponse.as[FCSolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def serviceReturnsFcDebtStatementOfLiabilityData(
    context: FCStatementOfLiabilityContext,
    amountIntTotal: BigDecimal,
    combinedDailyAccrual: Int
  ): Unit = {
    println("--verifying FC Statement of liability Response --")
    println(s"Actual Status : ${context.status}")
    context.status shouldBe 200
    println("--status verification passed--")

    println(s"Expected amountIntTotal : $amountIntTotal")
    println(s"Expected combinedDailyAccrual : $combinedDailyAccrual")

    context.responseBody.map(_.amountIntTotal)       shouldBe Some(amountIntTotal)
    context.responseBody.map(_.combinedDailyAccrual) shouldBe Some(combinedDailyAccrual)
    println("--Debt calculation summary actual vs expected verification passed --")
  }

  def theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(
    context: FCStatementOfLiabilityContext,
    summaryIndex: Int,
    inputs: Seq[FCSolCalculation]
  ): Unit = {

    context.status shouldBe 200
    val actualDebts = context.responseBody.get.debts
    actualDebts(summaryIndex).debtId shouldBe inputs.head.debtId
    actualDebts(summaryIndex).debtId shouldBe inputs.head.debtId
  }

  def aDebtFcStatementOfLiabilityIsRequestedForError(context: FCStatementOfLiabilityContext): Unit = {

    val response = FCStatementOfLiabilityRequests.getFCStatementOfLiability(context.request)
    context.status = response.status
    println(response.body)
  }

  def theFcSolServiceWillRespondWith(context: FCStatementOfLiabilityContext, expectedMessage: String): Unit = {

    val response = FCStatementOfLiabilityRequests.getFCStatementOfLiability(context.request)

    println("--verifying error response--")
    println(s"ActualStatus : ${response.status}")
    println(s"Expected Message : $expectedMessage")
    println(s"Actual Response : ${response.body}")
    response.status shouldBe 400
    response.body     should include(expectedMessage)
    println("-- error response assertion passed --")
  }

}
