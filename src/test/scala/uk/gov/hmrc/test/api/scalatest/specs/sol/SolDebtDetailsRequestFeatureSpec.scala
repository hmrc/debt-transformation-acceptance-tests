package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}

class SolDebtDetailsRequestFeatureSpec
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

  Feature("statement of liability Debt details") {

    Scenario("1. TPSS Account Tax Assessment debt statement of liability, 2 duties, no payment history.") { context =>
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
    Scenario("2. Child benefit debt statement of liability, 2 duties, with payment history.") { context =>
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
    Scenario("3. Non interest bearing with payment history and breathing space.") { context =>
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
    Scenario("4. Non interest bearing with payment history and no breathing space.") { context =>
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
    Scenario("5. Large non interest bearing debt with breathing space and no payment history - 9999999999.") { context =>
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
    Scenario("6. Large interest bearing debt with breathing space and no payment history - 9999999999.") { context =>
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
    Scenario("7. Interest bearing debts - 2 duties each with payment history and breathing space") { context =>
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
