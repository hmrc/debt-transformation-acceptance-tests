package uk.gov.hmrc.test.api.scalatest.specs.ifs

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class MultipeDebtItemsEdgeCasesFeatureSpec
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

  Feature("Multiple Debt Items - Edge Cases") {

    Scenario("1. 2 debts. 1 interest bearing and 1 non interest bearing") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 2nd debt summary will not have any calculation windows")
      // TODO: Helper 'theDebtSummaryWillNotHaveAnyCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillNotHaveAnyCalculationWindows(context)

    }
    Scenario("2. 2 debts each with 1 payment of different amounts") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 2nd debt summary will not have any calculation windows")
      // TODO: Helper 'theDebtSummaryWillNotHaveAnyCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillNotHaveAnyCalculationWindows(context)

    }
    Scenario("3. 3 debts, 1 with a payment") { context =>
      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 2nd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 3rd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 3rd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("4. 300 debt items") { context =>
      Given("300 debt items")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt items is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 300th debt summary will contain")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

    }
    Scenario("5. 2 debts, 5 payments on one of the debts") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 2nd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("6. 1 debt with payment where payment date before date created") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("7. 1 debt with a payment amount greater than original debt amount") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will respond with Could not parse body due to requirement failed: Amount paid in payments cannot be greater than Original Amount")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("8. 1 debt with an interest start date before the debt created", DTD_2216) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("9. 2 SA debts where one has an original amount less than zero") { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will respond with Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
