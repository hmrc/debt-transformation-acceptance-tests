package uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class InstalmentCalculationMultipleDebtsFeatureSpec
  extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = FCStatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = FCStatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("Instalment calculation for multiple debts - Input 1 & 2") {

    Scenario("Should calculate quote for multiple debts with interest bearing & non-interest bearing debts combined", DTD_3163, DTD_4201) { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("InterestStartDate is included but in the Future, then interestStartDate should be used") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Should calculate quote for multiple debts both with interest bearing & 1 initial payment history") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Should calculate debts amount for 2 debts with initial payment (input 2)") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the instalment calculation summary contains values")
      // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationSummaryContainsValues(context)

      And("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Multiple debt item charges - duration should not include initial payment (initial payment date before instalment date)", DTD_3163) { context =>
      Given("debt instalment calculation with 129 details")
      // TODO: Helper 'debtInstalmentCalculationWith129Details' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWith129Details(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the instalment calculation summary contains values")
      // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationSummaryContainsValues(context)

      And("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("InterestStartDate is included but not in the Future, then quote date should be used") { context =>
      Given("debt instalment calculation with 129 details")
      // TODO: Helper 'debtInstalmentCalculationWith129Details' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWith129Details(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the instalment calculation summary contains values")
      // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationSummaryContainsValues(context)

      And("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Multiple debt item charges - duration should not include initial payment (initial payment on instalment date)") { context =>
      Given("debt instalment calculation with 129 details")
      // TODO: Helper 'debtInstalmentCalculationWith129Details' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWith129Details(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the instalment calculation summary contains values")
      // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationSummaryContainsValues(context)

      And("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Multiple Debts should be returned in the order they are sent in", DTD_1874) { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    Scenario("Multiple Debts can be paid off within the same instalment period") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("debt plan details with initial payment")
      // TODO: Helper 'debtPlanDetailsWithInitialPayment' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtPlanDetailsWithInitialPayment(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
  }
}
