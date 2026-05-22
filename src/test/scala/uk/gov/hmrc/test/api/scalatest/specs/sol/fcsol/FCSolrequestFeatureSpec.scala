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
import uk.gov.hmrc.test.api.models.sol.{Debt, FCSolCalculation, PaymentHistory, SolMultipleDebtsRequest}
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

    Scenario("0. FC Sol request with multiple debt ID's and multiple payments and cotax interest charge.") { context =>
      Given("fc sol request")
      val request = SolMultipleDebtsRequest(
        customerUniqueRef = "NEHA1234",
        solRequestedDate = "2021-05-13",

        debts = List(
          Debt(
            debtId = "duty01",
            originalAmount = BigDecimal("10000"),
            solDescription = "solDescription",
            interestStartDate = "2020-05-13",
            interestRequestedTo = "2021-08-01",
            interestIndicator = "Y",
            chargedInterest = BigDecimal("1000"),
            periodEnd = "2020-05-13",
            paymentHistory = List(
              PaymentHistory(
                paymentAmount = BigDecimal("300"),
                paymentDate = "2021-04-06"
              ),
              PaymentHistory(
                paymentAmount = BigDecimal("100"),
                paymentDate = "2021-05-06"
              )
            )
          ),
          Debt(
            debtId = "duty02",
            originalAmount = BigDecimal("10000"),
            solDescription = "solDescription",
            interestStartDate = "2020-05-13",
            interestRequestedTo = "2021-08-01",
            interestIndicator = "Y",
            chargedInterest = BigDecimal("2000"),
            periodEnd = "2020-05-13",

            paymentHistory = List(
              PaymentHistory(
                paymentAmount = BigDecimal("300"),
                paymentDate = "2021-04-06"
              ),
              PaymentHistory(
                paymentAmount = BigDecimal("100"),
                paymentDate = "2021-05-06"
              )
            )
          )
        )
      )
      fcSolRequest(context, request)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      serviceReturnsFcDebtStatementOfLiabilityData(
        context,
        amountIntTotal = 19200,
        combinedDailyAccrual = 0
      )

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(
        context,
        summaryIndex = 0,
        Seq(
          FCSolCalculation(
            debtId = "duty01",
            interestDueDebtTotal = BigInt("0"),
            totalAmountIntDebt = BigDecimal("10910")
          ),
          FCSolCalculation(
            debtId = "duty02",
            interestDueDebtTotal = BigInt("0"),
            totalAmountIntDebt = BigDecimal("11910")
          )
        )
      )
    }

    Scenario("1. FC Sol request with Single debt ID's and single payments.") { context =>
      Given("fc sol request")
      val request = SolMultipleDebtsRequest(
        customerUniqueRef = "NEHA1234",
        solRequestedDate = "2021-05-13",
        debts = List(
          Debt(
            debtId = "duty01",
            originalAmount = BigDecimal("10000"),
            solDescription = "solDescription",
            interestStartDate = "2020-05-13",
            interestRequestedTo = "2021-08-01",
            interestIndicator = "Y",
            chargedInterest = BigDecimal("1000"),
            periodEnd = "2020-05-13",
            paymentHistory = List(
              PaymentHistory(
                paymentAmount = BigDecimal("300"),
                paymentDate = "2021-04-06"
              )
            )
          )
        )
      )
      fcSolRequest(context, request)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      serviceReturnsFcDebtStatementOfLiabilityData(
        context,
        amountIntTotal = BigDecimal("9700"),
        combinedDailyAccrual = 0
      )
      And("the 1st multiple fc statement of liability debt summary will contain duties")
      theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(
        context,
        summaryIndex = 0,
        Seq(
          FCSolCalculation(
            debtId = "duty01",
            interestDueDebtTotal = BigInt("0"),
            totalAmountIntDebt = BigDecimal("10012")
          )
        )
      )

    }
    Scenario("2. FC Sol request with invalid or empty original amount.") { context =>
      Given("fc sol request")
      val request = SolMultipleDebtsRequest(
        customerUniqueRef = "NEHA1234",
        solRequestedDate = "2021-05-13",
        debts = List(
          Debt(
            debtId = "duty01",
            originalAmount = BigDecimal("10000"),
            solDescription = "Debt1",
            interestStartDate = "",
            interestRequestedTo = "2021-08-01",
            interestIndicator = "Y",
            chargedInterest = BigDecimal("0"),
            periodEnd = "2020-05-13",
            paymentHistory = List.empty
          )
        )
      )
      fcSolRequest(context, request)

      Then("the fc sol service will respond with invalid Json")
      theFcSolServiceWillRespondWith(context, "Invalid Json")
    }

    Scenario("3. Large Non Interest bearing debt with no payments.") { context =>
      Given("fc sol request")
      val request =
        SolMultipleDebtsRequest(
          customerUniqueRef = "NEHA1234",
          solRequestedDate = "2021-05-13",
          debts = List(
            Debt(
              debtId = "XS002610170037",
              originalAmount = BigDecimal("9999999999"),
              solDescription = "Debt1",
              interestStartDate = "2021-08-01",
              interestRequestedTo = "2021-08-01",
              interestIndicator = "N",
              chargedInterest = BigDecimal("0"),
              periodEnd = "2021-08-01",
              paymentHistory = List.empty
            )
          )
        )
      fcSolRequest(context, request)

      When("a debt fc statement of liability is requested")
      aDebtFcStatementOfLiabilityIsRequested(context)

      Then("service returns fc debt statement of liability data")
      serviceReturnsFcDebtStatementOfLiabilityData(
        context,
        amountIntTotal = BigDecimal("9999999999"),
        combinedDailyAccrual = 0
      )

      And("the 1st multiple fc statement of liability debt summary will contain duties")
      theMultipleFcStatementOfLiabilityDebtSummaryWillContainDuties(
        context,
        summaryIndex = 0,
        Seq(
          FCSolCalculation(
            debtId = "XS002610170037",
            interestDueDebtTotal = BigInt("0"),
            totalAmountIntDebt = BigDecimal("9999999999")
          )
        )
      )
    }
  }
}
