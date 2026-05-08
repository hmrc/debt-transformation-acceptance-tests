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
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

class SolSADebtDetailsRequestFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCStatementOfLiabilityStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = FCStatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = FCStatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("Statement of liability Debt details for Self Assessment Debts") {

    ignore(
      "1. SA debt statement of liability, 2 duties and multiple breathing space with no payment history.",
      DTD_1959
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore("2. SA debt statement of liability. Single duty non interest bearing.", DTD_2166) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore("3. SA debt statement of liability - 2 duties Multiple breathing space and payment history.", DTD_2714) {
      context =>
        Given("debt details")
        // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // debtDetails(context)

        When("a debt statement of liability is requested")
        // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtStatementOfLiabilityIsRequested(context)

        Then("service returns debt statement of liability data")
        // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // serviceReturnsDebtStatementOfLiabilityData(context)

        And("the 1st sol debt summary will contain")
        // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theSolDebtSummaryWillContain(context)

        And("the 1st sol debt summary will contain duties")
        // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theSolDebtSummaryWillContainDuties(context)

    }
    ignore("4. SA debt statement of liability - Single duty interest bearing ETMP debt .", DTD_2714) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore("5. SA customer statement of liability - with Single duty non interest bearing ETMP debt .", DTD_2940) {
      context =>
        Given("debt details")
        // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // debtDetails(context)

        When("a debt statement of liability is requested")
        // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtStatementOfLiabilityIsRequested(context)

        Then("service returns debt statement of liability data")
        // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // serviceReturnsDebtStatementOfLiabilityData(context)

        And("the 1st sol debt summary will contain")
        // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theSolDebtSummaryWillContain(context)

        And("the 1st sol debt summary will contain duties")
        // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "6. Statement of liability for customer with ETMP parentMainTrans   - Single Non Interest bearing debt",
      DTD_2940
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "7. Statement of liability for customer with parentMainTrans   - Single SA Non Interest bearing debt",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0017, mainTrans=4027, subTrans=1080, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0018, mainTrans=4028, subTrans=1085, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0019, mainTrans=4029, subTrans=1090, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0020, mainTrans=4031, subTrans=1095, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0021, mainTrans=4032, subTrans=1090, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0022, mainTrans=4033, subTrans=1095, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "9. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0023, mainTrans=6010, subTrans=1611, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "9. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0024, mainTrans=6010, subTrans=2090, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "9. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0025, mainTrans=6010, subTrans=2095, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
    ignore(
      "9. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0026, mainTrans=6010, subTrans=2096, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      // TODO: Helper 'debtDetails' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtDetails(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st sol debt summary will contain")
      // TODO: Helper 'theSolDebtSummaryWillContain' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContain(context)

      And("the 1st sol debt summary will contain duties")
      // TODO: Helper 'theSolDebtSummaryWillContainDuties' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolDebtSummaryWillContainDuties(context)

    }
  }
}
