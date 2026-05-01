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

class VATFCBreathingSpaceFeatureSpec
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

  Feature("FC VAT Debt Calculation with Breathing Space") {

    ignore("Breathing space for interest bearing debt with payments.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Breathing space for interest bearing debt with no payments.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Non interest bearing debt should not have breathing space applied") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has no payment history")
      // TODO: Helper 'theFcDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcDebtItemHasNoPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Multiple debts with multiple breathing Spaces") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Multiple debts, 1 with a breathing Space, 1 without") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

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
    ignore("Breathing space for  interest bearing debt with payments -no debtRespiteFrom") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Field at path /debtItems(0)/breathingSpaces(0)/debtRespiteFrom missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    ignore("Breathing space -no interest bearing indictor.") { context =>
      Given("a fc vat debt item")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      And("the fc vat debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the fc vat customer has breathing spaces applied")
      // TODO: Helper 'theFcCustomerHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theFcCustomerHasBreathingSpacesApplied(context)

      When("the debt item is sent to the fc vat ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheFcIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc vat ifs service will respond with Field at path /debtItems(0)/interestIndicator missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
