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
import uk.gov.hmrc.test.api.models.sol._
import uk.gov.hmrc.test.api.models.{SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder.{SolCalculationExpected, SolCalculationSummaryResponseExpected, SolDutyExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.{StatementOfLiabilityContext, SuppressionRulesContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.StatementOfLiabilityStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers

class SolWithSuppressionFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with StatementOfLiabilityStepHelpers
    with SuppressionStepHelpers {

  override type FixtureParam = StatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = StatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("Sol With Suppression") {

    val suppressionContext = SuppressionRulesContext()

    Scenario("Customer Outputs SoL where suppression is applied") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1090"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(177)),
        totalAmountIntDebt = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("CO: TGPEN"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("Customer Outputs SoL suppression NOT applied to a different postcode") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("NW23 4PT"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500249)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(249)),
        totalAmountIntDebt = Some(BigInt(500249)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)
    }

    Scenario("Customer Outputs SoL where suppression is applied by Period End") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = None,
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = Some(true)
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(177)),
        totalAmountIntDebt = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("CO: TGPEN"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("Customer Outputs SoL where suppression is applied by Main Trans") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = None,
            mainTrans = Some("1545"),
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(177)),
        totalAmountIntDebt = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("CO: TGPEN"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("Customer Outputs SoL suppression NOT applied to a different subTrans") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = None,
            mainTrans = Some("1090"),
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500249)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(249)),
        totalAmountIntDebt = Some(BigInt(500249)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)
    }

    Scenario("Customer Outputs SoL where suppression is applied - based on testRegime") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-03-04",
            dateTo = Some("2021-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("TW33 4QQ"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debt008", interestRequestedTo = "2021-03-08")
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debt008"),
        mainTrans = Some("1545"),
        debtTypeDescription = Some("CO: TPSS Contract Settlement"),
        interestDueDebtTotal = Some(BigInt(177)),
        totalAmountIntDebt = Some(BigInt(500177)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("CO: TGPEN"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

  }
}
