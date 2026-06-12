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
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers

import java.time.LocalDate

class InstalmentCalculationCombinedInstalmentsFeatureSpec
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

  Feature("Instalment calculation with combined instalments") {

    Scenario("Final two instalments are merged when requested") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 4000,
              subTrans = "1000",
              mainTrans = "1525"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(1000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 300
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "true")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 4,
        planInterest = 27,
        interestAccrued = 300,
        totalInterest = 327,
        duration = 4,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 1000,
            instalmentBalance = 4000,
            instalmentInterestAccrued = 15,
            expectedPayment = 1000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 4,
            dueDate = LocalDate.parse("2021-11-01"),
            amountDue = 1327,
            instalmentBalance = 1000,
            instalmentInterestAccrued = 2,
            expectedPayment = 4327,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Final two instalments are not merged when request to merge is false") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 4000,
              subTrans = "1000",
              mainTrans = "1525"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(1000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 300
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "false")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 5,
        planInterest = 27,
        interestAccrued = 300,
        totalInterest = 327,
        duration = 5,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 1000,
            instalmentBalance = 4000,
            instalmentInterestAccrued = 15,
            expectedPayment = 1000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 5,
            dueDate = LocalDate.parse("2021-12-01"),
            amountDue = 327,
            instalmentBalance = 0,
            instalmentInterestAccrued = 0,
            expectedPayment = 4327,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Final two instalments of non interest bearing debts are not merged when request to merge is false") {
      context =>
        Given("debt instalment calculation with details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "TPSSDebt1",
                debtAmount = 4000,
                subTrans = "2000",
                mainTrans = "1541"
              )
            )
          ),
          quoteDate = LocalDate.parse("2021-06-10"),
          quoteType = "duration",
          instalmentPaymentDate = LocalDate.parse("2021-08-01"),
          paymentFrequency = "monthly",
          instalmentPaymentAmount = Some(999),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 0
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation is sent to the ifs service with query parameters")
        theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "false")

        Then("IFS response contains expected values")
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
          dateOfCalculation = LocalDate.parse("2021-06-10"),
          numberOfInstalments = 5,
          planInterest = 0,
          interestAccrued = 0,
          totalInterest = 0,
          duration = 5,
          instalments = Seq(
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 1,
              dueDate = LocalDate.parse("2021-08-01"),
              amountDue = 999,
              instalmentBalance = 4000,
              instalmentInterestAccrued = 0,
              expectedPayment = 999,
              intRate = 0
            ),
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 5,
              dueDate = LocalDate.parse("2021-12-01"),
              amountDue = 4,
              instalmentBalance = 4,
              instalmentInterestAccrued = 0,
              expectedPayment = 4000,
              intRate = 0
            )
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Final two instalments of non interest bearing debts are merged when requested") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 4000,
              subTrans = "2000",
              mainTrans = "1541"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(999),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0,
        initialPaymentDate = None,
        initialPaymentAmount = None
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "true")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 4,
        planInterest = 0,
        interestAccrued = 0,
        totalInterest = 0,
        duration = 4,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 999,
            instalmentBalance = 4000,
            instalmentInterestAccrued = 0,
            expectedPayment = 999,
            intRate = 0
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 4,
            dueDate = LocalDate.parse("2021-11-01"),
            amountDue = 1003,
            instalmentBalance = 1003,
            instalmentInterestAccrued = 0,
            expectedPayment = 4000,
            intRate = 0
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Final two instalments are merged when requested with initial payment") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 4000,
              subTrans = "1000",
              mainTrans = "1525"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(999),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 300,
        initialPaymentDate = Some(LocalDate.parse("2021-07-01")),
        initialPaymentAmount = Some(1000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "true")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 4,
        planInterest = 18,
        interestAccrued = 300,
        totalInterest = 318,
        duration = 3,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-07-01"),
            amountDue = 1000,
            instalmentBalance = 4000,
            instalmentInterestAccrued = 6,
            expectedPayment = 1000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 2,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 999,
            instalmentBalance = 3000,
            instalmentInterestAccrued = 6,
            expectedPayment = 1999,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 4,
            dueDate = LocalDate.parse("2021-10-01"),
            amountDue = 1320,
            instalmentBalance = 1002,
            instalmentInterestAccrued = 2,
            expectedPayment = 4318,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Multiple debt item charges final two instalments are merged when requested") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 16000,
              subTrans = "1000",
              mainTrans = "1525"
            ),
            DebtItemCharge(
              debtId = "DRIERDebt1",
              debtAmount = 14000,
              subTrans = "1000",
              mainTrans = "1525"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(6000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1000,
        initialPaymentDate = Some(LocalDate.parse("2021-07-01")),
        initialPaymentAmount = Some(5000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "true")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 6,
        planInterest = 185,
        interestAccrued = 1000,
        totalInterest = 1185,
        duration = 4,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-07-01"),
            amountDue = 5000,
            instalmentBalance = 16000,
            instalmentInterestAccrued = 25,
            expectedPayment = 5000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 2,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 6000,
            instalmentBalance = 11000,
            instalmentInterestAccrued = 24,
            expectedPayment = 11000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 6,
            dueDate = LocalDate.parse("2021-11-01"),
            amountDue = 8185,
            instalmentBalance = 7000,
            instalmentInterestAccrued = 15,
            expectedPayment = 31185,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Last 2 instalments are only combined where final instalment amount is lower than previous") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 4000,
              subTrans = "1000",
              mainTrans = "1525"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2021-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(1006),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1003
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation is sent to the ifs service with query parameters")
      theInstalmentCalculationIsSentToTheIfsServiceWithQueryParameters(context, "true")

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 5,
        planInterest = 27,
        interestAccrued = 1003,
        totalInterest = 1030,
        duration = 5,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 1006,
            instalmentBalance = 4000,
            instalmentInterestAccrued = 15,
            expectedPayment = 1006,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 5,
            dueDate = LocalDate.parse("2021-12-01"),
            amountDue = 1006,
            instalmentBalance = 0,
            instalmentInterestAccrued = 0,
            expectedPayment = 5030,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

  }
}
