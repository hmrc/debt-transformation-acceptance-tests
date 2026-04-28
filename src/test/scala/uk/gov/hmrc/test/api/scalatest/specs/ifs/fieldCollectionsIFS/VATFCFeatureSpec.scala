package uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class VATFCFeatureSpec
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

  Feature("FC VAT Debt Calculation End point testing") {

    Scenario("1. Interest Indicator as Yes. 1 Payment of 1 debt.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("2. Interest Indicator as No. 1 Payment of 1 debt.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("3. Interest Indicator as Yes. 2 Payment of 1 debt.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("4. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is more than Original amount") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Could not parse body due to requirement failed: Total Payment amounts cannot be more than the original amount")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("5. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is 0") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Could not parse body due to requirement failed: Payment amount must not be zero")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("5. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is -10000") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Could not parse body due to requirement failed: Payment amount must be positive")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("6. Interest Indicator as Yes. 2 Payment of 2 debt.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the 2nd fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("7. Interest Indicator as Yes. No Payment History.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("8. Interest Indicator as No. No Payment History.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc vat debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("9. periodEnd missing. Interest Indicator as No. No Payment History.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc vat customer")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Field at path /debtItems(0)/periodEnd missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
