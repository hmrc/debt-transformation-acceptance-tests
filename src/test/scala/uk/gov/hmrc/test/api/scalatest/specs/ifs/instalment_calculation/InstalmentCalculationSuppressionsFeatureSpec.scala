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

package uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

class InstalmentCalculationSuppressionsFeatureSpec
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

  Feature("Suppression Period ends after quote date") {

    ignore("Instalment calculation has been requested where a postcode suppression period ends after the quote date") {
      context =>
        Given("suppression configuration data is created")
        // TODO: No matching helper method found for this step. Validate and call the correct helper.
        // TODO: This step had a feature table; convert the values into typed builder/model inputs.

        When("suppression configuration is sent to ifs service")
        // TODO: No matching helper method found for this step. Validate and call the correct helper.

        And("debt instalment calculation with details")
        // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // debtInstalmentCalculationWithDetails(context)

        And("the instalment calculation has postcode BS39 5DP with postcode date a year in the past")
        // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

        And("no initial payment for the debt item charge")
        // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noInitialPaymentForTheDebtItemCharge(context)

        And("the instalment calculation has debt item charges")
        // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationHasDebtItemCharges(context)

        When("the instalment calculation detail is sent to the ifs service")
        // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("the IFS request should return status 200")
        // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsRequestShouldReturnStatus(context)

        And("the 1st instalment should have an interest accrued of 0")
        // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentShouldHaveAnInterestAccruedOf(context)

        And("the 2nd instalment should have an interest accrued of 0")
        // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
    ignore(
      "Instalment calculation has been requested where a period end suppression period ends after the quote date"
    ) { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has postcode TW3 with postcode date a year in the past")
      // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsRequestShouldReturnStatus(context)

      And("the 1st instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

      And("the 2nd instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
    ignore(
      "Instalment calculation has been requested where a main trans suppression period ends after the quote date"
    ) { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has postcode TW3 with postcode date a year in the past")
      // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsRequestShouldReturnStatus(context)

      And("the 1st instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

      And("the 2nd instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
    ignore("Should calculate instalment where suppression period ends after the quote date") { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has postcode BS39 5DP with postcode date a year in the past")
      // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsRequestShouldReturnStatus(context)

      And("the 1st instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

      And("the 2nd instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
    ignore("Should calculate instalment where a period end suppression period ends after the quote date") { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has postcode TW3 with postcode date a year in the past")
      // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsRequestShouldReturnStatus(context)

      And("the 1st instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

      And("the 2nd instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
    ignore("Should calculate instalment where a main trans suppression period ends after the quote date") { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has postcode TW3 with postcode date a year in the past")
      // TODO: Helper 'theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasPostcodeWithPostcodeDateAYearInThePast(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation detail is sent to the ifs service")
      // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      // TODO: Helper 'theIfsRequestShouldReturnStatus' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsRequestShouldReturnStatus(context)

      And("the 1st instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

      And("the 2nd instalment should have an interest accrued of 0")
      // TODO: Helper 'theInstalmentShouldHaveAnInterestAccruedOf' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentShouldHaveAnInterestAccruedOf(context)

    }
  }
}
