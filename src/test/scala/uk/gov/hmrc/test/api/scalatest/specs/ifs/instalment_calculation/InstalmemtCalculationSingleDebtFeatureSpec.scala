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
import uk.gov.hmrc.test.api.scalatest.tags._

class InstalmemtCalculationSingleDebtFeatureSpec
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

  Feature("Instalment calculation for single debt - Input 2") {

    ignore("Should calculate debts amount for 1 debt 1 duty (input 2)") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

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

      Then("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    ignore("Plan with isQuoteDateNonInclusive flag should not include quote date in interest accrued") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

      And("no initial payment for the debt item charge")
      // TODO: Helper 'noInitialPaymentForTheDebtItemCharge' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noInitialPaymentForTheDebtItemCharge(context)

      And("the instalment calculation has debt item charges")
      // TODO: Helper 'theInstalmentCalculationHasDebtItemCharges' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasDebtItemCharges(context)

      When("the instalment calculation is sent to the ifs service with query parameters")
      // TODO: Helper 'theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context)

      Then("the instalment calculation summary contains values")
      // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationSummaryContainsValues(context)

      And("IFS response contains expected values")
      // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsResponseContainsExpectedValues(context)

    }
    ignore("Plans with initial payment and isQuoteDateNonInclusive flag should not include quote date", DTD_3163) {
      context =>
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

        When("the instalment calculation is sent to the ifs service with query parameters")
        // TODO: Helper 'theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context)

        Then("the instalment calculation summary contains values")
        // TODO: Helper 'theInstalmentCalculationSummaryContainsValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationSummaryContainsValues(context)

        And("IFS response contains expected values")
        // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // ifsResponseContainsExpectedValues(context)

    }
    ignore("InterestStartDate is included but not in the Future, then quote date should be used", DTD_3163) { context =>
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
    ignore("InterestStartDate is included but in the Future, then interestStartDate should be used", DTD_3163) {
      context =>
        Given("debt instalment calculation with details")
        // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // debtInstalmentCalculationWithDetails(context)

        And("the instalment calculation has no postcodes")
        // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationHasNoPostcodes(context)

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

        Then("IFS response contains expected values")
        // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // ifsResponseContainsExpectedValues(context)

    }
    ignore(
      "With initial payment - InterestStartDate is included but in the Future, then interestStartDate should be used"
    ) { context =>
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
    ignore("Should return an error from IFS if quote type is duration and duration is provided") { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

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

      Then("Ifs service returns response code 400")
      // TODO: Helper 'ifsServiceReturnsResponseCode' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsServiceReturnsResponseCode(context)

      And(
        "Ifs service returns error message {statusCode:400,reason:Missing instalment amount,message:For a quote type of `duration`, the instalmentPaymentAmount is required}"
      )
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    ignore(
      "Should return an error from IFS if quote type is instalment amount and instalment payment amount is provided"
    ) { context =>
      Given("debt instalment calculation with details")
      // TODO: Helper 'debtInstalmentCalculationWithDetails' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // debtInstalmentCalculationWithDetails(context)

      And("the instalment calculation has no postcodes")
      // TODO: Helper 'theInstalmentCalculationHasNoPostcodes' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theInstalmentCalculationHasNoPostcodes(context)

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

      Then("Ifs service returns response code 400")
      // TODO: Helper 'ifsServiceReturnsResponseCode' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // ifsServiceReturnsResponseCode(context)

      And(
        "Ifs service returns error message {statusCode:400,reason:Missing duration,message:For a quote type of `instalmentAmount`, the duration field is required}"
      )
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
