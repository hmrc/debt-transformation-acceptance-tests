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

package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.sol.{Debt, SolCalculation, SolCalculationSummaryResponse, SolDuty, SolMultipleDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, StatementOfLiabilityContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

class RequestSoLForMultipleDebtsFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCStatementOfLiabilityStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = StatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = StatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("statement of liability multiple debts") {

    Scenario("1. TPSS Account Tax Assessment debt statement of liability, 2 debts with breathing spaces", DTD_2940) {
      context =>
        Given("statement of liability multiple debt requests")
        val request = SolMultipleDebtsRequest(
          solType = "UI",
          customerUniqueRef = "customer-1",
          debts = List(
            Debt(
              debtId = "debt001",
              interestRequestedTo = "2021-08-10"
            ),
            Debt(
              debtId = "debt004",
              interestRequestedTo = "2021-08-10"
            )
          )
        )

        statementOfLiabilityMultipleDebtRequests(context, request)

        When("a debt statement of liability is requested")
         aDebtStatementOfLiabilityIsRequested(context)

        Then("service returns debt statement of liability data")
        val response = SolCalculationSummaryResponse(
          amountIntTotal = 1107817,
          combinedDailyAccrual = 63,
          debts = List(
            SolCalculation(
              debtId = "debt001",
              mainTrans = "1525",
              debtTypeDescription = "TPSS Account Tax Assessment",
              interestDueDebtTotal = 7817,
              totalAmountIntDebt = 907817,
              combinedDailyAccrual = 63,
              parentMainTrans = None,
              duties = Seq(
                SolDuty(
                  subTrans = "1000",
                  dutyTypeDescription = Some("IT"),
                  unpaidAmountDuty = 500000,
                  combinedDailyAccrual = 35,
                  interestBearing = true,
                  interestOnlyIndicator = false
                ),
                SolDuty(
                  subTrans = "1000",
                  dutyTypeDescription = Some("IT"),
                  unpaidAmountDuty = 400000,
                  combinedDailyAccrual = 28,
                  interestBearing = true,
                  interestOnlyIndicator = false
                )
              )
            ),
            SolCalculation(
              debtId = "debt004",
              mainTrans = "5350",
              debtTypeDescription = "UI: ChB Migrated Debt",
              interestDueDebtTotal = 0,
              totalAmountIntDebt = 200000,
              combinedDailyAccrual = 0,
              parentMainTrans = None,
              duties = Seq(
                SolDuty(
                  subTrans = "7012",
                  dutyTypeDescription = Some("UI: Child Benefit Migrated Debt"),
                  unpaidAmountDuty = 200000,
                  combinedDailyAccrual = 0,
                  interestBearing = false,
                  interestOnlyIndicator = false
                )
              )
            )
          )
        )

        serviceReturnsDebtStatementOfLiabilityData(context, response)
    }

    ignore("2. Statement of liability for customer - 2 SA Non Interest bearing debts") { context =>
      Given("statement of liability multiple debt requests")
      // TODO: Helper 'statementOfLiabilityMultipleDebtRequests' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // statementOfLiabilityMultipleDebtRequests(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      // TODO: Helper 'serviceReturnsDebtStatementOfLiabilityData' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // serviceReturnsDebtStatementOfLiabilityData(context)

      And("the 1st customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 1st customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

      And("the 2nd customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

      And("the 2nd customer statement of liability contains debt values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDebtValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDebtValuesAs(context)

      And("the 2nd customer statement of liability contains duty values as")
      // TODO: Helper 'theIntCustomerStatementOfLiabilityContainsDutyValuesAs' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIntCustomerStatementOfLiabilityContainsDutyValuesAs(context)

    }
  }
}
