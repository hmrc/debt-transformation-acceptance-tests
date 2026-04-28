package uk.gov.hmrc.test.api.scalatest.specs.ifs

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class LeapYearFeatureSpec
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

  Feature("Leap years") {

    Scenario("Debt ending in a leap year") { context =>
      Given("a debt item")
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

    }
    Scenario("Debt starting in a leap year") { context =>
      Given("a debt item")
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

    }
    Scenario("Debt crossing a leap year") { context =>
      Given("a debt item")
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

    }
    Scenario("2.Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made.") { context =>
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
    Scenario("Debt spanning multiple leap years") { context =>
      Given("a debt item")
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

    }
  }
}
