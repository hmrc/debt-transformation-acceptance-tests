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
import uk.gov.hmrc.test.api.models.InstalmentResponse
import uk.gov.hmrc.test.api.models.ifs.{DebtItemCharge, InstallmentCalculationCustomerPostCode, InstalmentCalculationRequest}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, IFSInstalmentCalculationContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class InstalmentCalculationMultipleDebtsFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with IFSInstalmentCalculationStepHelpers {

  override type FixtureParam = IFSInstalmentCalculationContext

  override def withFixture(test: OneArgTest) = {
    val context = IFSInstalmentCalculationContext()
    try test(context)
    finally ()
  }

  Feature("Instalment calculation for multiple debts - Input 1 & 2") {

    // Input 1
    Scenario("Should calculate quote for multiple debts with interest bearing & non-interest bearing debts combined") {
      context =>
        Given("instalment calculation details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "1234",
                debtAmount = 80000,
                subTrans = "1000",
                mainTrans = "1545",
              ),
              DebtItemCharge(
                debtId = "12345",
                debtAmount = 70000,
                subTrans = "2000",
                mainTrans = "1541",
              )
            )
          ),
          quoteDate = LocalDate.parse("2020-03-13"),
          quoteType = "duration",
          instalmentPaymentDate = "2020-03-14",
          paymentFrequency = "monthly",
          instalmentPaymentAmount = Some(10000),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 5900,
          initialPaymentDate = Some(LocalDate.parse("2020-03-14")),
          initialPaymentAmount = Some(100)
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation detail is sent to the ifs service")
        theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("IFS response contains expected values")
        val instalmentsResponse = Seq(
          InstalmentResponse(
            debtId = "12345",
            instalmentNumber = 9,
            dueDate = LocalDate.parse("2020-10-14"),
            amountDue = 100,
            instalmentBalance = 70000,
            instalmentInterestAccrued = 0,
            expectedPayment = 801000,
            intRate = 0
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentsResponse)

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

    ignore("Should calculate quote for multiple debts both with interest bearing & 1 initial payment history") {
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

        When("the instalment calculation detail is sent to the ifs service")
        // TODO: Helper 'theInstalmentCalculationDetailIsSentToTheIfsService' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("IFS response contains expected values")
        // TODO: Helper 'ifsResponseContainsExpectedValues' expects context 'IFSInstalmentCalculationContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // ifsResponseContainsExpectedValues(context)

    }

    // Input 2
    ignore("Should calculate debts amount for 2 debts with initial payment (input 2)") { context =>
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

    ignore(
      "Multiple debt item charges - duration should not include initial payment (initial payment date before instalment date)"
    ) { context =>
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

    ignore("InterestStartDate is included but not in the Future, then quote date should be used", DTD_3163) { context =>
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

    ignore(
      "Multiple debt item charges - duration should not include initial payment (initial payment on instalment date)"
    ) { context =>
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

    ignore("Multiple Debts should be returned in the order they are sent in") { context =>
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

    ignore("Multiple Debts can be paid off within the same instalment period", DTD_1874) { context =>
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
