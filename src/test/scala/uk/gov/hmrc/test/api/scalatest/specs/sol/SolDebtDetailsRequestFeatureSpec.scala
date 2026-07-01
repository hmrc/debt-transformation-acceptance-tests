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
import uk.gov.hmrc.test.api.models.sol.{Debt, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder.{SolCalculationExpected, SolCalculationSummaryResponseExpected, SolDutyExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.StatementOfLiabilityStepHelpers

class SolDebtDetailsRequestFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
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
          Debt(debtId = "debt001", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(907817)),
        combinedDailyAccrual = Some(BigInt(63))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt001"),
        mainTrans = Some("1525"),
        debtTypeDescription = Some("TPSS Account Tax Assessment"),
        interestDueDebtTotal = Some(BigInt(7817)),
        totalAmountIntDebt = Some(BigInt(907817)),
        combinedDailyAccrual = Some(BigInt(63))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        ),
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(400000)),
          combinedDailyAccrual = Some(BigInt(28)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("2. Child benefit debt statement of liability, 2 duties, with payment history.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt003", interestRequestedTo = "2023-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(625127)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt003"),
        mainTrans = Some("5330"),
        debtTypeDescription = Some("UI: ChB Debt"),
        interestDueDebtTotal = Some(BigInt(25127)),
        totalAmountIntDebt = Some(BigInt(625127)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("7006"),
          dutyTypeDescription = Some("UI: Child Benefit Debt"),
          unpaidAmountDuty = Some(BigInt(400000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(false)
        ),
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(200000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("3. Non interest bearing with payment history and breathing space.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt005", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(200000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt005"),
        mainTrans = Some("5350"),
        debtTypeDescription = Some("CO: ChB Migrated Debt"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(200000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("7012"),
          dutyTypeDescription = Some("CO: Child Benefit Migrated Debt"),
          unpaidAmountDuty = Some(BigInt(200000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("4. Large interest bearing debt with breathing space and no payment history - 9999999999.") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt009", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(10101841602L)),
        combinedDailyAccrual = Some(BigInt(712328))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt009"),
        mainTrans = Some("1525"),
        debtTypeDescription = Some("TPSS Account Tax Assessment"),
        interestDueDebtTotal = Some(BigInt(101841603)),
        totalAmountIntDebt = Some(BigInt(10101841602L)),
        combinedDailyAccrual = Some(BigInt(712328))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(9999999999L)),
          combinedDailyAccrual = Some(BigInt(712328)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("5. Interest bearing debts - 2 duties each with payment history and breathing space") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt0010", interestRequestedTo = "2023-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(15916039)),
        combinedDailyAccrual = Some(BigInt(2314))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt0010"),
        mainTrans = Some("1525"),
        debtTypeDescription = Some("TPSS Account Tax Assessment"),
        interestDueDebtTotal = Some(BigInt(2916039)),
        totalAmountIntDebt = Some(BigInt(15916039)),
        combinedDailyAccrual = Some(BigInt(2314))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(10000000)),
          combinedDailyAccrual = Some(BigInt(1780)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        ),
        SolDutyExpected(
          subTrans = Some("1000"),
          dutyTypeDescription = Some("IT"),
          unpaidAmountDuty = Some(BigInt(3000000)),
          combinedDailyAccrual = Some(BigInt(534)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

  }
}
