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
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.scalatest.builders.IFSInstalmentCalculationBuilder.InstalmentResponseExpected
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
      val instalmentsResponse = Seq(
        InstalmentResponse(
          debtId = "1234",
          instalmentNumber = 1,
          dueDate = LocalDate.parse("2020-03-14"),
          amountDue = 4271,
          instalmentBalance = 100000,
          instalmentInterestAccrued = 17,
          expectedPayment = 4271,
          intRate = 3.25
        )
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
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
          dateOfCalculation = LocalDate.parse("2023-03-17"),
          numberOfInstalments = 6,
          planInterest = 1961,
          interestAccrued = 178,
          totalInterest = 2139,
          duration = 6,
          instalments = Seq(
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 1,
              dueDate = LocalDate.parse("2023-04-20"),
              amountDue = 17022,
              instalmentBalance = 100000,
              instalmentInterestAccrued = 605,
              expectedPayment = 17022,
              intRate = 6.5
            ),
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 6,
              dueDate = LocalDate.parse("2023-09-20"),
              amountDue = 17029,
              instalmentBalance = 16670,
              instalmentInterestAccrued = 92,
              expectedPayment = 102139,
              intRate = 6.5
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
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
          dateOfCalculation = LocalDate.parse("2023-03-23"),
          numberOfInstalments = 5,
          planInterest = 24727,
          interestAccrued = 0,
          totalInterest = 24727,
          duration = 4,
          instalments = Seq(
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 1,
              dueDate = LocalDate.parse("2023-04-02"),
              amountDue = 100000,
              instalmentBalance = 1425623,
              instalmentInterestAccrued = 2538,
              expectedPayment = 100000,
              intRate = 6.5
            ),
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 5,
              dueDate = LocalDate.parse("2023-08-20"),
              amountDue = 337592,
              instalmentBalance = 331408,
              instalmentInterestAccrued = 1829,
              expectedPayment = 1450350,
              intRate = 6.5
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
        val instalmentsResponse = Seq(
          InstalmentResponse(
            debtId = "1234",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-03-13"),
            amountDue = 5000,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 8,
            expectedPayment = 5000,
            intRate = 3.25
          )
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
        ifsResponseContainsExpectedValuesNew(context, instalmentsResponse)  //rename method and remove New from the end in next task for DTD-4626

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
      val instalmentsResponse = Seq(
        InstalmentResponse(
          debtId = "1234",
          instalmentNumber = 1,
          dueDate = LocalDate.parse("2025-06-01"),
          amountDue = 5000,
          instalmentBalance = 100000,
          instalmentInterestAccrued = 0,
          expectedPayment = 5000,
          intRate = 6.5
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

  }
}
