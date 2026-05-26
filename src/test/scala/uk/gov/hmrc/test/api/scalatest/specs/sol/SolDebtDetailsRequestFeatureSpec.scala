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

package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.sol.{Debt, SolCalculation, SolCalculationSummaryResponse, SolDebtsRequest, SolDuty}
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.CommonStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.StatementOfLiabilityStepHelpers

class SolDebtDetailsRequestFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with CommonStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = StatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = StatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("statement of liability Debt details") {

    Scenario("1. TPSS Account Tax Assessment debt statement of liability, 2 duties, no payment history.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt001",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val response = SolCalculationSummaryResponse(
        amountIntTotal = 907817,
        combinedDailyAccrual = 63,
        debts = List(
          SolCalculation(
            debtId = "debt001",
            mainTrans = "1525",
            debtTypeDescription = "TPSS Account Tax Assessment",
            interestDueDebtTotal = 7817,
            totalAmountIntDebt = 907817,
            combinedDailyAccrual = 63,
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = 500000,
                combinedDailyAccrual = 35,
                interestBearing = true,
                interestOnlyIndicator = false
              ),
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = 400000,
                combinedDailyAccrual = 28,
                interestBearing = true,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )

      serviceReturnsDebtStatementOfLiabilityData(context, response)
    }

    Scenario("2. Child benefit debt statement of liability, 2 duties, with payment history.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt003",
            interestRequestedTo = "2023-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val response = SolCalculationSummaryResponse(
        amountIntTotal = 625127,
        combinedDailyAccrual = 35,
        debts = List(
          SolCalculation(
            debtId = "debt003",
            mainTrans = "5330",
            debtTypeDescription = "UI: ChB Debt",
            interestDueDebtTotal = 25127,
            totalAmountIntDebt = 625127,
            combinedDailyAccrual = 35,
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                subTrans = "7006",
                dutyTypeDescription = Some("UI: Child Benefit Debt"),
                unpaidAmountDuty = 400000,
                combinedDailyAccrual = 0,
                interestBearing = false,
                interestOnlyIndicator = false
              ),
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = 200000,
                combinedDailyAccrual = 35,
                interestBearing = true,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )

      serviceReturnsDebtStatementOfLiabilityData(context, response)
    }

    Scenario("3. Non interest bearing with payment history and breathing space.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt005",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val response = SolCalculationSummaryResponse(
        amountIntTotal = 200000,
        combinedDailyAccrual = 0,
        debts = List(
          SolCalculation(
            debtId = "debt005",
            mainTrans = "5350",
            debtTypeDescription = "CO: ChB Migrated Debt",
            interestDueDebtTotal = 0,
            totalAmountIntDebt = 200000,
            combinedDailyAccrual = 0,
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                subTrans = "7012",
                dutyTypeDescription = Some("CO: Child Benefit Migrated Debt"),
                unpaidAmountDuty = 200000,
                combinedDailyAccrual = 0,
                interestBearing = false,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )
      serviceReturnsDebtStatementOfLiabilityData(context, response)
    }

    Scenario("4. Large interest bearing debt with breathing space and no payment history - 9999999999.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt009",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val response = SolCalculationSummaryResponse(
        amountIntTotal = BigInt(10101841602L),
        combinedDailyAccrual = BigInt(712328L),
        debts = List(
          SolCalculation(
            debtId = "debt009",
            mainTrans = "1525",
            debtTypeDescription = "TPSS Account Tax Assessment",
            interestDueDebtTotal = BigInt(101841603L),
            totalAmountIntDebt = BigInt(10101841602L),
            combinedDailyAccrual = BigInt(712328L),
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = BigInt(9999999999L),
                combinedDailyAccrual = BigInt(712328L),
                interestBearing = true,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )
      serviceReturnsDebtStatementOfLiabilityData(context, response)
    }

    Scenario("5. Interest bearing debts - 2 duties each with payment history and breathing space") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt0010",
            interestRequestedTo = "2023-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val response = SolCalculationSummaryResponse(
        amountIntTotal = BigInt(15916039L),
        combinedDailyAccrual = BigInt(2314L),
        debts = List(
          SolCalculation(
            debtId = "debt0010",
            mainTrans = "1525",
            debtTypeDescription = "TPSS Account Tax Assessment",
            interestDueDebtTotal = BigInt(2916039L),
            totalAmountIntDebt = BigInt(15916039L),
            combinedDailyAccrual = BigInt(2314L),
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = BigInt(10000000L),
                combinedDailyAccrual = BigInt(1780L),
                interestBearing = true,
                interestOnlyIndicator = false
              ),
              SolDuty(
                subTrans = "1000",
                dutyTypeDescription = Some("IT"),
                unpaidAmountDuty = BigInt(3000000L),
                combinedDailyAccrual = BigInt(534L),
                interestBearing = true,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )
      serviceReturnsDebtStatementOfLiabilityData(context, response)
    }
  }
}
