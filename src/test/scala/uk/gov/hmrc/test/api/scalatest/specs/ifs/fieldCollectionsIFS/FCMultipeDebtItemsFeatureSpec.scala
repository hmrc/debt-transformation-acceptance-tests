package uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

class FCMultipeDebtItemsFeatureSpec
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

  Feature("FC Debt Calculation End point testing") {

    Scenario("0. Interest Indicators. 2 debt. 1 payment history and cotax charge interest") { context =>
      Given("fc debt item with cotax charge interest")
      // TODO: Helper 'fcDebtItemWithCotaxChargeInterest' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // fcDebtItemWithCotaxChargeInterest(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

    }
    Scenario("1. Interest Indicators. 2 debt. 1 payment history") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

    }
    Scenario("2. Interest Indicator. 1 Payment of 1 debt.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

    }
    Scenario("3. No Interest Indicator. 1 Payment of 1 debt.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will not have any calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillNotHaveAnyCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillNotHaveAnyCalculationWindows(context)

    }
    Scenario("4. Interest Indicator. 1 Payment of 1 debt. No breathing space.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

    }
    Scenario("5. 1 debt, no payment history") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

    }
    Scenario("6. Interest Indicator. 1 Payment of 1 debt. Payment Done.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("7. Total Payments cannot be 0.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will respond with Could not parse body due to requirement failed: Payment amount must not be zero")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("8. Total Payments cannot be negative.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will respond with Could not parse body due to requirement failed: Payment amount must be positive")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("9. Total Payment amounts cannot be more than the original amount.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will respond with Could not parse body due to requirement failed: Total Payment amounts cannot be more than the original amount")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("10. No InterestStartDate but InterestIndicator is Yes.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will respond with Field at path /debtItems(0)/interestStartDate missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    Scenario("FC Debt ending in a leap year") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Debt starting in a leap year") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Debt crossing a leap year") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Debt spanning multiple leap years") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 3% to 3.25%") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 3% to 3.25% after a payment is made") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 3% to 3.25% with 2 payments on same date") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC 2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 2nd fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    Scenario("FC Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has no post codes")
      // TODO: Helper 'theFcCustomerHasNoPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasNoPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      // TODO: Helper 'theFcIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st fc debt summary will contain")
      // TODO: Helper 'theFcDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillContain(context)

      And("the 1st fc debt summary will not have any calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillNotHaveAnyCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillNotHaveAnyCalculationWindows(context)

    }
    Scenario("periodEnd and interestStartDate is missing or invalid.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("no breathing spaces have been applied to the fc debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheFcDebtItem(context)

      And("the fc customer has post codes")
      // TODO: Helper 'theFcCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasPostCodes(context)

      When("the debt item is sent to the fc ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will respond with Field at path /debtItems(0)/periodEnd missing or invalid\\nField at path /debtItems(0)/interestStartDate missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
