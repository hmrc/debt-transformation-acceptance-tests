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
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags.*

class RequestSolForMultipleDebtsFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCStatementOfLiabilityStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = StatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = StatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("statement of liability multiple debts") {

    Scenario("1. TPSS Account Tax Assessment debt statement of liability, 2 debts with breathing spaces", DTD_2940) {
      context =>
        Given("debt details")
        val request = SolDebtsRequest(
          solType = "UI",
          customerUniqueRef = "customer-1",
          debts = List(
            Debt(debtId = "debt001", interestRequestedTo = "2021-08-10"),
            Debt(debtId = "debt004", interestRequestedTo = "2021-08-10")
          )
        )
        debtDetails(context, request)

        When("a debt statement of liability is requested")
        aDebtStatementOfLiabilityIsRequested(context)

        Then("service returns debt statement of liability data")
        val expectedSummary = SolCalculationSummaryResponseExpected(
          amountIntTotal = Some(BigInt(1107817)),
          combinedDailyAccrual = Some(BigInt(63))
        )
        serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

        And("the 1st customer statement of liability contains debt values as")
        val expected1stDebt = SolCalculationExpected(
          debtId = Some("debt001"),
          mainTrans = Some("1525"),
          debtTypeDescription = Some("TPSS Account Tax Assessment"),
          interestDueDebtTotal = Some(BigInt(7817)),
          totalAmountIntDebt = Some(BigInt(907817)),
          combinedDailyAccrual = Some(BigInt(63))
        )
        theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

        And("the 1st customer statement of liability contains duty values as")
        val expected1stDuties = List(
          SolDutyExpected(
            subTrans = Some("1000"),
            dutyTypeDescription = Some("IT"),
            unpaidAmountDuty = Some(BigInt(500000)),
            combinedDailyAccrual = Some(BigInt(35)),
            interestBearing = Some(true),
            interestOnlyIndicator = Some(false)
          )
        )
        theCustomerStatementOfLiabilityContainsDutyValuesAs(context, 1, expected1stDuties)

        And("the 2nd customer statement of liability contains debt values as")
        val expected2ndDebt = SolCalculationExpected(
          debtId = Some("debt004"),
          mainTrans = Some("5350"),
          debtTypeDescription = Some("UI: ChB Migrated Debt"),
          interestDueDebtTotal = Some(BigInt(0)),
          totalAmountIntDebt = Some(BigInt(200000)),
          combinedDailyAccrual = Some(BigInt(0))
        )
        theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 2, expected2ndDebt)

        And("the 2nd customer statement of liability contains duty values as")
        val expected2ndDuties = List(
          SolDutyExpected(
            subTrans = Some("7012"),
            dutyTypeDescription = Some("UI: Child Benefit Migrated Debt"),
            unpaidAmountDuty = Some(BigInt(200000)),
            combinedDailyAccrual = Some(BigInt(0)),
            interestBearing = Some(false),
            interestOnlyIndicator = Some(false)
          )
        )
        theCustomerStatementOfLiabilityContainsDutyValuesAs(context, 2, expected2ndDuties)
    }

    Scenario("2. Statement of liability for customer - 2 SA Non Interest bearing debts") { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "customer-1",
        debts = List(
          Debt(debtId = "debtSA0016", interestRequestedTo = "2021-08-10"),
          Debt(debtId = "debtSA0014", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(1100000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st customer statement of liability contains debt values as")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0016"),
        mainTrans = Some("6010"),
        debtTypeDescription = Some("SA Balancing Charge Interest"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(600000)),
        combinedDailyAccrual = Some(BigInt(0)),
        parentMainTrans = Some("25")
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st customer statement of liability contains duty values as")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1554"),
          dutyTypeDescription = Some("SA Late Payment Interest"),
          unpaidAmountDuty = Some(BigInt(400000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(true)
        )
      )
      theCustomerStatementOfLiabilityContainsDutyValuesAs(context, 1, expected1stDuties)

      And("the 2nd customer statement of liability contains debt values as")
      val expected2ndDebt = SolCalculationExpected(
        debtId = Some("debtSA0014"),
        mainTrans = Some("6010"),
        debtTypeDescription = Some("SA Late Payment Interest"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0)),
        parentMainTrans = Some("33")
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 2, expected2ndDebt)

      And("the 2nd customer statement of liability contains duty values as")
      val expected2ndDuties = List(
        SolDutyExpected(
          subTrans = Some("1554"),
          dutyTypeDescription = Some("SA Payment on Account 2 Interest"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(true)
        )
      )
      theCustomerStatementOfLiabilityContainsDutyValuesAs(context, 2, expected2ndDuties)
    }

  }
}
