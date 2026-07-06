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
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.sol.{FCSolCalculationSummaryResponse, SolMultipleDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.FCStatementOfLiabilityBuilder
import uk.gov.hmrc.test.api.scalatest.builders.FCStatementOfLiabilityBuilder.{FCSolCalculationExpected, FCSolCalculationSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext

trait FCStatementOfLiabilityStepHelpers {
  this: Matchers =>

  def fcSolRequest(
    context: FCStatementOfLiabilityContext,
    request: SolMultipleDebtsRequest
  ): Unit                                                                                  =
    context.request = Some(request)
  def aDebtFcStatementOfLiabilityIsRequested(context: FCStatementOfLiabilityContext): Unit = {
    val response     = FCStatementOfLiabilityBuilder.getFCStatementOfLiability(context.request)
    val jsonResponse = response.body[JsValue]
    context.status = response.status
    context.responseBody = Some(jsonResponse.as[FCSolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def serviceReturnsFcDebtStatementOfLiabilityData(
    context: FCStatementOfLiabilityContext,
    expectedResponse: FCSolCalculationSummaryExpected
  ): Unit = {
    context.status shouldBe 200

    val actualFcSolResponse = context.responseBody.getOrElse(fail("Missing actual FCSolCalculationSummaryResponse"))

    withClue("FCSolCalculationSummaryResponse") {
      expectedResponse.amountIntTotal.foreach { e =>
        withClue("amountIntTotal") {
          actualFcSolResponse.amountIntTotal shouldBe e
        }
      }

      expectedResponse.combinedDailyAccrual.foreach { e =>
        withClue("combinedDailyAccrual") {
          actualFcSolResponse.combinedDailyAccrual shouldBe e
        }
      }
    }
  }

  def theFcStatementOfLiabilityDebtSummaryWillContainDuties(
    context: FCStatementOfLiabilityContext,
    expectedDebts: Seq[FCSolCalculationExpected]
  ): Unit = {

    context.status shouldBe 200
    val actualDebts = context.responseBody.getOrElse(fail("Missing actual FCSolCalculationSummaryResponse")).debts

    expectedDebts.zipWithIndex.foreach { case (expectedDebt, index) =>
      actualDebts(index).debtId               shouldBe expectedDebt.debtId.getOrElse(fail("Missing expected debtId field"))
      actualDebts(index).interestDueDebtTotal shouldBe expectedDebt.interestDueDebtTotal.getOrElse(
        fail("Missing expected interestDueDebtTotal field")
      )
      actualDebts(index).totalAmountIntDebt   shouldBe expectedDebt.totalAmountIntDebt.getOrElse(
        fail("Missing expected totalAmountIntDebt field")
      )
    }

  }

  def theFcSolServiceWillRespondWith(context: FCStatementOfLiabilityContext, expectedMessage: String): Unit = {
    val response = FCStatementOfLiabilityBuilder.getFCStatementOfLiability(context.request)
    response.status shouldBe 400
    response.body     should include(expectedMessage)
  }
}
