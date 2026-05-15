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
import uk.gov.hmrc.test.api.models.SuppressionInformation
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.StatementOfLiabilityStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppresionStepHelpers

class SolWilthSuppressionFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with StatementOfLiabilityStepHelpers
    with SuppresionStepHelpers {

  override type FixtureParam = SuppressionRulesContext

  override def withFixture(test: OneArgTest) = {
    val context = SuppressionRulesContext()
    try test(context)
    finally ()
  }

  Feature("Sol With Suppression") {

    Scenario("Customer Outputs SoL where suppression is applied") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = None,
        mainTrans = None,
        subTrans = Some("1090"),
        checkPeriodEnd = None
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        context
      )

      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(177),
        totalAmountIntDebt = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )

      And("the 1st sol debt summary will contain duties")
      checkSolDutyOfFirstSolCalculationContains(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = Some("CO: TGPEN"),
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario("Customer Outputs SoL suppression NOT applied to a different postcode") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = Some("NW23 4PT"),
        mainTrans = None,
        subTrans = None,
        checkPeriodEnd = None
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500249),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(249),
        totalAmountIntDebt = BigInt(500249),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )

    }
    Scenario("Customer Outputs SoL where suppression is applied by Period End") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = None,
        mainTrans = None,
        subTrans = None,
        checkPeriodEnd = Some(true)
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        context
      )

      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(177),
        totalAmountIntDebt = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )

      And("the 1st sol debt summary will contain duties")
      checkSolDutyOfFirstSolCalculationContains(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = Some("CO: TGPEN"),
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }
    Scenario("Customer Outputs SoL where suppression is applied by Main Trans") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = None,
        mainTrans = Some("1545"),
        subTrans = None,
        checkPeriodEnd = None
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        context
      )

      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(177),
        totalAmountIntDebt = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )

      And("the 1st sol debt summary will contain duties")
      checkSolDutyOfFirstSolCalculationContains(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = Some("CO: TGPEN"),
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )

    }
    Scenario("Customer Outputs SoL suppression NOT applied to a different subTrans") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = None,
        mainTrans = Some("1090"),
        subTrans = None,
        checkPeriodEnd = None
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500249),
        combinedDailyAccrual = BigInt(35),
        context
      )

      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(249),
        totalAmountIntDebt = BigInt(500249),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
    }
    Scenario("Customer Outputs SoL where suppression is applied - based on testRegime") { context =>
      Given("suppression configuration data is created")
      val ifsRequest = SuppressionInformation(
        dateFrom = "2021-03-04",
        dateTo = Some("2021-03-05"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        suppressionChargeDescription = "SA-Suppression",
        postcode = Some("TW33 4QQ"),
        mainTrans = None,
        subTrans = None,
        checkPeriodEnd = None
      )
      suppressionConfigurationDataIsCreated(context, ifsRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(context)

      And("debt details")
      val solRequest = SolDebtsRequest(
        solType = "CO",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debt008",
            interestRequestedTo = "2021-03-08"
          )
        )
      )
      debtDetails(context, solRequest)

      When("a debt statement of liability is requested")
      aRequestIsSentToSolServiceToGetSolCalculation(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        context
      )

      And("the 1st sol debt summary will contain")
      checkDebtSummaryContains(
        debtSummaryEntry = 1,
        debtId = "debt008",
        mainTrans = "1545",
        debtTypeDescription = "CO: TPSS Contract Settlement",
        interestDueDebtTotal = BigInt(177),
        totalAmountIntDebt = BigInt(500177),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )

      And("the 1st sol debt summary will contain duties")
      checkSolDutyOfFirstSolCalculationContains(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = Some("CO: TGPEN"),
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )

    }
  }
}
