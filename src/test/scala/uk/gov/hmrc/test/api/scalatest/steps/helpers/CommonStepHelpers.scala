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

package uk.gov.hmrc.test.api.scalatest.steps.helpers

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.sol.SolCalculationSummaryResponse
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, StatementOfLiabilityContext}

// TODO: Validate that FCStatementOfLiabilityContext is the correct context for helpers migrated from commonSteps.scala.
trait CommonStepHelpers { this: Matchers =>

  // ^service returns debt statement of liability data$
  def serviceReturnsDebtStatementOfLiabilityData(
    context: StatementOfLiabilityContext,
    expectedResponse: SolCalculationSummaryResponse
  ): Unit = {
    val actual = context.responseBody
    println(s"actualResponseBody : " + actual)
    println(s"expectedResponse : " + Some(expectedResponse))

    context.status shouldBe 200

    actual match {
      case Some(actual) =>
        withClue("amountIntTotal: ") {
          actual.amountIntTotal shouldBe expectedResponse.amountIntTotal
        }
        withClue("combinedDailyAccrual: ") {
          actual.combinedDailyAccrual shouldBe expectedResponse.combinedDailyAccrual
        }
        withClue("debts list length: ") {
          actual.debts.length shouldBe expectedResponse.debts.length
        }

        // Verify each SolCalculation in debts list
        actual.debts.zip(expectedResponse.debts).zipWithIndex.foreach { case ((actualDebt, expectedDebt), debtIndex) =>
          withClue(s"debts[$debtIndex].debtId: ") {
            actualDebt.debtId shouldBe expectedDebt.debtId
          }
          withClue(s"debts[$debtIndex].mainTrans: ") {
            actualDebt.mainTrans shouldBe expectedDebt.mainTrans
          }
          withClue(s"debts[$debtIndex].debtTypeDescription: ") {
            actualDebt.debtTypeDescription shouldBe expectedDebt.debtTypeDescription
          }
          withClue(s"debts[$debtIndex].interestDueDebtTotal: ") {
            actualDebt.interestDueDebtTotal shouldBe expectedDebt.interestDueDebtTotal
          }
          withClue(s"debts[$debtIndex].totalAmountIntDebt: ") {
            actualDebt.totalAmountIntDebt shouldBe expectedDebt.totalAmountIntDebt
          }
          withClue(s"debts[$debtIndex].combinedDailyAccrual: ") {
            actualDebt.combinedDailyAccrual shouldBe expectedDebt.combinedDailyAccrual
          }
          withClue(s"debts[$debtIndex].parentMainTrans: ") {
            actualDebt.parentMainTrans shouldBe expectedDebt.parentMainTrans
          }
          withClue(s"debts[$debtIndex].duties list length: ") {
            actualDebt.duties.length shouldBe expectedDebt.duties.length
          }

          // Verify each SolDuty in duties list
          actualDebt.duties.zip(expectedDebt.duties).zipWithIndex.foreach {
            case ((actualDuty, expectedDuty), dutyIndex) =>
              withClue(s"debts[$debtIndex].duties[$dutyIndex].subTrans: ") {
                actualDuty.subTrans shouldBe expectedDuty.subTrans
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].dutyTypeDescription: ") {
                actualDuty.dutyTypeDescription shouldBe expectedDuty.dutyTypeDescription
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].unpaidAmountDuty: ") {
                actualDuty.unpaidAmountDuty shouldBe expectedDuty.unpaidAmountDuty
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].combinedDailyAccrual: ") {
                actualDuty.combinedDailyAccrual shouldBe expectedDuty.combinedDailyAccrual
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].interestBearing: ") {
                actualDuty.interestBearing shouldBe expectedDuty.interestBearing
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].interestOnlyIndicator: ") {
                actualDuty.interestOnlyIndicator shouldBe expectedDuty.interestOnlyIndicator
              }
          }
        }
      case None         => fail("Response body is empty")
    }
  }

  // ^suppression data has been created$
  def suppressionDataHasBeenCreated(context: FCStatementOfLiabilityContext): Unit = {
    // addSuppressions(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^service returns response code (.*)$
  def serviceReturnsResponseCode(context: FCStatementOfLiabilityContext, expectedCode: Int): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // val response: StandaloneWSResponse = ScenarioContext.get("response")
    // response.status should be(expectedCode)
    // TODO: Implement typed helper for this step.
  }

  // ^service returns error message (.*)$
  def serviceReturnsErrorMessage(context: FCStatementOfLiabilityContext, expectedMessage: String): Unit = {
    // Migration hint: legacy ScenarioContext usage
    // val response: StandaloneWSResponse = ScenarioContext.get("response")
    // val responseBody                   = response.body.stripMargin
    // print("response message*****************************" + responseBody)
    // responseBody should be(expectedMessage)
    // TODO: Implement typed helper for this step.
  }

}
