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
import uk.gov.hmrc.test.api.models.ifs.{DebtItemCharge, InstallmentCalculationCustomerPostCode, InstalmentCalculationRequest}
import uk.gov.hmrc.test.api.scalatest.builders.IFSInstalmentCalculationBuilder.{InstalmentCalculationSummaryResponseExpected, InstalmentResponseExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class InstalmentCalculationSingleDebtFeatureSpec
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

  Feature("Instalment calculation for single debt - Input 2") {

    Scenario("Should calculate debts amount for 1 debt 1 duty (input 2)") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "1234",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1545"
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = Some(false),
        instalmentPaymentDate = LocalDate.parse("2020-03-14"),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentsResponse = InstalmentResponseExpected(
        instalmentNumber = Some(1),
        dueDate = Some(LocalDate.parse("2020-03-14")),
        amountDue = Some(4271),
        instalmentBalance = Some(100000),
        intRate = Some(3.25)
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario("Plan with isQuoteDateNonInclusive flag should not include quote date in interest accrued", DTD_1730) {
      context =>
        Given("instalment calculation details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "TPSSDebt1",
                debtAmount = 100000,
                subTrans = "1000",
                mainTrans = "1525"
              )
            )
          ),
          quoteDate = LocalDate.parse("2023-03-17"),
          quoteType = "instalmentAmount",
          isQuoteDateNonInclusive = Some(true),
          instalmentPaymentDate = LocalDate.parse("2023-04-20"),
          paymentFrequency = "monthly",
          duration = Some(6),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 178
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation is sent to the ifs service with query parameters")
        theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "false")

        Then("IFS response contains expected values")
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
          numberOfInstalments = Some(6),
          planInterest = Some(1961),
          interestAccrued = Some(178),
          totalInterest = Some(2139),
          duration = Some(6),
          instalments = Some(
            Seq(
              InstalmentResponseExpected(
                instalmentNumber = Some(1),
                dueDate = Some(LocalDate.parse("2023-04-20")),
                amountDue = Some(17022),
                instalmentInterestAccrued = Some(605)
              ),
              InstalmentResponseExpected(
                instalmentNumber = Some(6),
                dueDate = Some(LocalDate.parse("2023-09-20")),
                amountDue = Some(17029),
                instalmentInterestAccrued = Some(92)
              )
            )
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Plans with initial payment and isQuoteDateNonInclusive flag should not include quote date", DTD_1730) {
      context =>
        Given("instalment calculation details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "TPSSDebt1",
                debtAmount = 1425623,
                subTrans = "1000",
                mainTrans = "1525"
              )
            )
          ),
          quoteDate = LocalDate.parse("2023-03-23"),
          quoteType = "instalmentAmount",
          isQuoteDateNonInclusive = Some(true),
          instalmentPaymentDate = LocalDate.parse("2023-05-20"),
          paymentFrequency = "monthly",
          duration = Some(4),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 0,
          initialPaymentDate = Some(LocalDate.parse("2023-04-02")),
          initialPaymentAmount = Some(100000)
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation is sent to the ifs service with query parameters")
        theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "false")

        Then("IFS response contains expected values")
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
          numberOfInstalments = Some(5),
          planInterest = Some(24727),
          interestAccrued = Some(0),
          totalInterest = Some(24727),
          duration = Some(4),
          instalments = Some(
            Seq(
              InstalmentResponseExpected(
                instalmentNumber = Some(1),
                dueDate = Some(LocalDate.parse("2023-04-02")),
                amountDue = Some(100000),
                instalmentInterestAccrued = Some(2538)
              ),
              InstalmentResponseExpected(
                instalmentNumber = Some(5),
                dueDate = Some(LocalDate.parse("2023-08-20")),
                amountDue = Some(337592),
                instalmentInterestAccrued = Some(1829)
              )
            )
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("InterestStartDate is included but not in the Future, then quote date should be used", DTD_3163) {
      context =>
        Given("instalment calculation details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "1234",
                debtAmount = 100000,
                subTrans = "1000",
                mainTrans = "1545",
                interestStartDate = Some(LocalDate.parse("2025-01-14"))
              )
            )
          ),
          quoteDate = LocalDate.parse("2020-03-13"),
          quoteType = "instalmentAmount",
          isQuoteDateNonInclusive = Some(false),
          instalmentPaymentDate = LocalDate.parse("2020-03-14"),
          paymentFrequency = "monthly",
          duration = Some(24),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 0,
          initialPaymentDate = Some(LocalDate.parse("2020-03-13")),
          initialPaymentAmount = Some(5000)
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation detail is sent to the ifs service")
        theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("IFS response contains expected values")
        val instalmentsResponse = InstalmentResponseExpected(
          instalmentNumber = Some(1),
          dueDate = Some(LocalDate.parse("2020-03-13")),
          intRate = Some(3.25)
        )
        ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario("InterestStartDate is included but in the Future, then interestStartDate should be used", DTD_3163) {
      context =>
        Given("instalment calculation details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "1234",
                debtAmount = 100000,
                subTrans = "1000",
                mainTrans = "1545",
                interestStartDate = Some(LocalDate.now().plusDays(15))
              )
            )
          ),
          quoteDate = LocalDate.parse("2025-06-01"),
          quoteType = "instalmentAmount",
          isQuoteDateNonInclusive = Some(false),
          instalmentPaymentDate = LocalDate.parse("2025-08-25"),
          paymentFrequency = "monthly",
          duration = Some(24),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 0
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation detail is sent to the ifs service")
        theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("IFS response contains expected values")
        val instalmentsResponse = InstalmentResponseExpected(
          instalmentNumber = Some(1),
          dueDate = Some(LocalDate.parse("2025-08-25")),
          intRate = Some(6.5)
        )
        ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario(
      "With initial payment - InterestStartDate is included but in the Future, then interestStartDate should be used",
      DTD_3163
    ) { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "1234",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1545",
              interestStartDate = Some(LocalDate.now().plusDays(15))
            )
          )
        ),
        quoteDate = LocalDate.parse("2025-06-01"),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = Some(false),
        instalmentPaymentDate = LocalDate.parse("2025-06-10"),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0,
        initialPaymentDate = Some(LocalDate.parse("2025-06-01")),
        initialPaymentAmount = Some(5000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentsResponse = InstalmentResponseExpected(
        instalmentNumber = Some(1),
        dueDate = Some(LocalDate.parse("2025-06-01")),
        intRate = Some(6.5)
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

  }
}
