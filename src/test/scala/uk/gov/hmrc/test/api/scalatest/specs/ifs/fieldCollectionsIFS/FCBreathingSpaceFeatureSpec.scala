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

package uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

class FCBreathingSpaceFeatureSpec
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

  Feature("FC Debt Calculation Breathing Space") {

    ignore("Breathing space for interest bearing debt with no payments.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Breathing space for interest bearing debt with payments.") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the debt item has fc payment history")
      // TODO: Helper 'theDebtItemHasFcPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasFcPaymentHistory(context)

      And("the fc customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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

      And("the 1st fc debt summary will have calculation windows")
      // TODO: Helper 'theFcDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Non interest bearing debt should not have breathing space applied") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Multiple debts with multiple breathing Spaces") { context =>
      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

      Given("a fc debt item")
      // TODO: Helper 'aFcDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aFcDebtItem(context)

      And("the fc debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
  }
}
