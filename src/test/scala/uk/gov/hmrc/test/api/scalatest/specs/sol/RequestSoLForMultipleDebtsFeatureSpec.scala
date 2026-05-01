package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

class RequestSoLForMultipleDebtsFeatureSpec
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

  Feature("statement of liability multiple debts") {

    ignore("1. TPSS Account Tax Assessment debt statement of liability, 2 debts with breathing spaces", DTD_2940) { context =>
      Given("statement of liability multiple debt requests")
      // TODO: Helper 'statementOfLiabilityMultipleDebtRequests' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // statementOfLiabilityMultipleDebtRequests(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 1st customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

      And("the 2nd customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 2nd customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

    }
    ignore("2. Statement of liability for customer - 2 SA Non Interest bearing debts") { context =>
      Given("statement of liability multiple debt requests")
      // TODO: Helper 'statementOfLiabilityMultipleDebtRequests' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // statementOfLiabilityMultipleDebtRequests(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 1st customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

      And("the 2nd customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

      And("the 2nd customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 2nd customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

    }
  }
}
