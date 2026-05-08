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

package uk.gov.hmrc.test.api.scalatest.specs.sol.fcsol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.builders.{FCStatementOfLiabilityBuilder, InterestForecastingBuilder}
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}

class FCSolrequestFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCStatementOfLiabilityStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = FCStatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = FCStatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("fc statement of liability multiple debts") {

    ignore("0. FC Sol request with multiple debt ID's and multiple payments and cotax interest charge.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("fc sol debt item has multiple debts with charge interest")
      fcSolDebtItemHasMultipleDebtsWithChargeInterest(context)

      And("the debt item has fc sol payment history")
      theDebtItemHasFcSolPaymentHistory(
        context,
        Seq(
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("300")),
            paymentDate = Some("2021-04-06"),
            payments = None
          ),
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("100")),
            paymentDate = Some("2021-05-06"),
            payments = None
          )
        )
      )

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
    ignore("1. FC Sol request with multiple debt ID's and multiple payments.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the debt item has fc sol payment history")
      theDebtItemHasFcSolPaymentHistory(
        context,
        Seq(
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("300")),
            paymentDate = Some("2021-04-06"),
            payments = None
          ),
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("100")),
            paymentDate = Some("2021-05-06"),
            payments = None
          )
        )
      )

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
    ignore("2. FC Sol request with Single debt ID's and single payments.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the debt item has fc sol payment history")
      theDebtItemHasFcSolPaymentHistory(
        context,
        Seq(
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("300")),
            paymentDate = Some("2021-04-06"),
            payments = None
          )
        )
      )

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
    ignore("3. FC Sol request with Single debt ID's and single payments with Interest Indicator as N.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the debt item has fc sol payment history")
      theDebtItemHasFcSolPaymentHistory(
        context,
        Seq(
          InterestForecastingBuilder.PaymentHistoryInput(
            debtItems = None,
            paymentAmount = Some(BigDecimal("300")),
            paymentDate = Some("2021-04-06"),
            payments = None
          )
        )
      )

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
    ignore("4. FC Sol request with invalid or empty original amount.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the fc sol debt item has no payment history")
      theFcSolDebtItemHasNoPaymentHistory(context)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("the fc sol service will respond with Invalid Json")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    ignore("5. FC Sol request with no debt items.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has no debts")
      theFcSolDebtItemHasNoDebts(context)

      And("the fc sol debt item has no payment history")
      theFcSolDebtItemHasNoPaymentHistory(context)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("the fc sol service will respond with Invalid Json")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    ignore("6. FC Sol request with missing Interest Indicator.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-05-13")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the fc sol debt item has no payment history")
      theFcSolDebtItemHasNoPaymentHistory(context)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("the fc sol service will respond with Field at path /debts(0)/interestIndicator missing or invalid")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
    ignore("7. Large Interest bearing debt with no payments.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-08-01")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the fc sol debt item has no payment history")
      theFcSolDebtItemHasNoPaymentHistory(context)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
    ignore("8. Large Non Interest bearing debt with no payments.") { context =>
      Given("fc sol request")
      fcSolRequest(
        context,
        Seq(
          FCStatementOfLiabilityBuilder.FcSolRequestInput(
            customerUniqueRef = Some("NEHA1234"),
            debtDetails = None,
            solRequestedDate = Some("2021-08-01")
          )
        )
      )

      And("the fc sol debt item has multiple debts")
      theFcSolDebtItemHasMultipleDebts(context)

      And("the fc sol debt item has no payment history")
      theFcSolDebtItemHasNoPaymentHistory(context)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      // TODO: Step table was parsed, but the helper parameter type 'DebtCalculationsSummary' could not be matched to a generated builder input.
      // Validate the helper signature and call serviceReturnsFcDebtStatementOfLiabilityData(context, ...)

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      // TODO: Helper 'theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties' has multiple non-context parameters: summaryIndex: Int, inputs: Seq[FCSolCalculation].
      // theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(context, /* TODO */)

    }
  }
}
