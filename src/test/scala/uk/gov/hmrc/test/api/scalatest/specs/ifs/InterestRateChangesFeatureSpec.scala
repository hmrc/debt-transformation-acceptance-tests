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

package uk.gov.hmrc.test.api.scalatest.specs.ifs

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

class InterestRateChangesFeatureSpec
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

  Feature("Interest Rate Changes") {

    ignore("Interest rate changes from 3% to 3.25%") { context =>
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
    ignore("Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year") { context =>
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

    ignore("Interest rate changes from 3% to 3.25% after a payment is made") { context =>
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
    ignore("Interest rate changes from 3% to 3.25% with 2 payments on same date") { context =>
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

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts") { context =>
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

      And("the 2nd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate") { context =>
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

      And("the 1st debt summary will not have any calculation windows")
      // TODO: Helper 'theDebtSummaryWillNotHaveAnyCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillNotHaveAnyCalculationWindows(context)

    }
    ignore("Interest rate changes from 2.6% -> 2.75% -> 3") { context =>
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
