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
                mainTrans = "1525"
              ),
              DebtItemCharge(
                debtId = "12345",
                debtAmount = 70000,
                subTrans = "2000",
                mainTrans = "1541"
              )
            )
          ),
          quoteDate = LocalDate.parse("2020-03-13"),
          quoteType = "duration",
          instalmentPaymentDate = LocalDate.parse("2020-03-14"),
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
            expectedPayment = 80100,
            intRate = 0
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario(
      "InterestStartDate is included but in the Future, then interestStartDate should be used",
      DTD_3163,
      DTD_4201
    ) { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "1234",
              debtAmount = 80000,
              subTrans = "1000",
              mainTrans = "1525",
              interestStartDate = Some(LocalDate.now().plusDays(15))
            ),
            DebtItemCharge(
              debtId = "12345",
              debtAmount = 70000,
              subTrans = "2000",
              mainTrans = "1541",
              interestStartDate = Some(LocalDate.now().plusDays(15))
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.now().minusDays(174),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(10000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 5900,
        initialPaymentDate = Some(LocalDate.now().minusDays(190)),
        initialPaymentAmount = Some(100)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentsResponse = Seq(
        InstalmentResponse(
          debtId = "1234",
          instalmentNumber = 8,
          dueDate = LocalDate.now().minusDays(174).plusMonths(6),
          amountDue = 10000,
          instalmentBalance = 19900,
          instalmentInterestAccrued = 0,
          expectedPayment = 70100,
          intRate = 3.25
        ),
        InstalmentResponse(
          debtId = "1234",
          instalmentNumber = 9,
          dueDate = LocalDate.now().minusDays(174).plusMonths(7),
          amountDue = 9900,
          instalmentBalance = 9900,
          instalmentInterestAccrued = 40,
          expectedPayment = 80000,
          intRate = 6.5
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario("Should calculate quote for multiple debts both with interest bearing & 1 initial payment history") {
      context =>
        Given("debt instalment calculation with details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "1234",
                debtAmount = 80000,
                subTrans = "1000",
                mainTrans = "1525"
              ),
              DebtItemCharge(
                debtId = "12345",
                debtAmount = 70000,
                subTrans = "1000",
                mainTrans = "1530"
              )
            )
          ),
          quoteDate = LocalDate.parse("2020-03-13"),
          quoteType = "duration",
          instalmentPaymentDate = LocalDate.parse("2020-03-14"),
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
            debtId = "1234",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-03-14"),
            amountDue = 10100,
            instalmentBalance = 80000,
            instalmentInterestAccrued = 14,
            expectedPayment = 10100,
            intRate = 3.25
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    // Input 2
    Scenario("Should calculate debts amount for 2 debts with initial payment (input 2)") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "TPSSDebt1",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1525"
            ),
            DebtItemCharge(
              debtId = "DRIERDebt1",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1085"
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "instalmentAmount",
        instalmentPaymentDate = LocalDate.parse("2020-03-14"),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0,
        initialPaymentDate = Some(LocalDate.parse("2020-03-14")),
        initialPaymentAmount = Some(100)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2020-03-13"),
        numberOfInstalments = 25,
        planInterest = 1232,
        interestAccrued = 0,
        totalInterest = 1232,
        duration = 24,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-03-14"),
            amountDue = 8480,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 17,
            expectedPayment = 8480,
            intRate = 3.25
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario(
      "Multiple debt item charges - duration should not include initial payment (initial payment date before instalment date)"
    ) { context =>
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
        quoteDate = LocalDate.parse("2020-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2020-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(6000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1000,
        initialPaymentDate = Some(LocalDate.parse("2020-07-01")),
        initialPaymentAmount = Some(5000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2020-06-10"),
        numberOfInstalments = 7,
        planInterest = 187,
        interestAccrued = 1000,
        totalInterest = 1187,
        duration = 5,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-07-01"),
            amountDue = 5000,
            instalmentBalance = 16000,
            instalmentInterestAccrued = 25,
            expectedPayment = 5000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 2,
            dueDate = LocalDate.parse("2020-08-01"),
            amountDue = 6000,
            instalmentBalance = 11000,
            instalmentInterestAccrued = 24,
            expectedPayment = 11000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 7,
            dueDate = LocalDate.parse("2020-12-01"),
            amountDue = 2187,
            instalmentBalance = 1000,
            instalmentInterestAccrued = 2,
            expectedPayment = 31187,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("InterestStartDate is included but not in the Future, then quote date should be used", DTD_3163) {
      context =>
        Given("debt instalment calculation with details")
        val ifsRequest = InstalmentCalculationRequest(
          debtItemCharges = Some(
            List(
              DebtItemCharge(
                debtId = "TPSSDebt1",
                debtAmount = 16000,
                subTrans = "1000",
                mainTrans = "1525",
                interestStartDate = Some(LocalDate.parse("2025-01-01"))
              ),
              DebtItemCharge(
                debtId = "DRIERDebt1",
                debtAmount = 14000,
                subTrans = "1000",
                mainTrans = "1525",
                interestStartDate = Some(LocalDate.parse("2025-03-01"))
              )
            )
          ),
          quoteDate = LocalDate.parse("2025-05-31"),
          quoteType = "duration",
          instalmentPaymentDate = LocalDate.parse("2025-06-30"),
          paymentFrequency = "monthly",
          instalmentPaymentAmount = Some(6000),
          customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
          interestCallDueTotal = 1000,
          initialPaymentDate = Some(LocalDate.parse("2025-06-01")),
          initialPaymentAmount = Some(5000)
        )
        instalmentCalculationDetails(context, ifsRequest)

        When("the instalment calculation detail is sent to the ifs service")
        theInstalmentCalculationDetailIsSentToTheIfsService(context)

        Then("IFS response contains expected values")
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
          dateOfCalculation = LocalDate.parse("2025-05-31"),
          numberOfInstalments = 7,
          planInterest = 353,
          interestAccrued = 1000,
          totalInterest = 1353,
          duration = 5,
          instalments = Seq(
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 1,
              dueDate = LocalDate.parse("2025-06-01"),
              amountDue = 5000,
              instalmentBalance = 16000,
              instalmentInterestAccrued = 5,
              expectedPayment = 5000,
              intRate = 6.5
            ),
            InstalmentResponse(
              debtId = "TPSSDebt1",
              instalmentNumber = 2,
              dueDate = LocalDate.parse("2025-06-30"),
              amountDue = 6000,
              instalmentBalance = 11000,
              instalmentInterestAccrued = 56,
              expectedPayment = 11000,
              intRate = 6.5
            ),
            InstalmentResponse(
              debtId = "DRIERDebt1",
              instalmentNumber = 7,
              dueDate = LocalDate.parse("2025-10-30"),
              amountDue = 2353,
              instalmentBalance = 1000,
              instalmentInterestAccrued = 5,
              expectedPayment = 31353,
              intRate = 6.5
            )
          )
        )
        ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario(
      "Multiple debt item charges - duration should not include initial payment (initial payment on instalment date)"
    ) { context =>
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
        quoteDate = LocalDate.parse("2020-06-10"),
        quoteType = "duration",
        instalmentPaymentDate = LocalDate.parse("2020-08-01"),
        paymentFrequency = "monthly",
        instalmentPaymentAmount = Some(6000),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1000,
        initialPaymentDate = Some(LocalDate.parse("2020-08-01")),
        initialPaymentAmount = Some(5000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2020-06-10"),
        numberOfInstalments = 6,
        planInterest = 198,
        interestAccrued = 1000,
        totalInterest = 1198,
        duration = 5,
        instalments = Seq(
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-08-01"),
            amountDue = 11000,
            instalmentBalance = 16000,
            instalmentInterestAccrued = 60,
            expectedPayment = 11000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "TPSSDebt1",
            instalmentNumber = 2,
            dueDate = LocalDate.parse("2020-09-01"),
            amountDue = 5000,
            instalmentBalance = 5000,
            instalmentInterestAccrued = 11,
            expectedPayment = 16000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 3,
            dueDate = LocalDate.parse("2020-09-01"),
            amountDue = 1000,
            instalmentBalance = 14000,
            instalmentInterestAccrued = 83,
            expectedPayment = 17000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 4,
            dueDate = LocalDate.parse("2020-10-01"),
            amountDue = 6000,
            instalmentBalance = 13000,
            instalmentInterestAccrued = 27,
            expectedPayment = 23000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 5,
            dueDate = LocalDate.parse("2020-11-01"),
            amountDue = 6000,
            instalmentBalance = 7000,
            instalmentInterestAccrued = 15,
            expectedPayment = 29000,
            intRate = 2.6
          ),
          InstalmentResponse(
            debtId = "DRIERDebt1",
            instalmentNumber = 6,
            dueDate = LocalDate.parse("2020-12-01"),
            amountDue = 2198,
            instalmentBalance = 1000,
            instalmentInterestAccrued = 2,
            expectedPayment = 31198,
            intRate = 2.6
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

    Scenario("Multiple Debts should be returned in the order they are sent in") { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "DebtId1",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1525"
            ),
            DebtItemCharge(
              debtId = "DebtId2",
              debtAmount = 200000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId3",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1525"
            ),
            DebtItemCharge(
              debtId = "DebtId4",
              debtAmount = 70000,
              subTrans = "2000",
              mainTrans = "1541"
            ),
            DebtItemCharge(
              debtId = "DebtId5",
              debtAmount = 200000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId6",
              debtAmount = 6000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId7",
              debtAmount = 7000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId7",
              debtAmount = 8000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId8",
              debtAmount = 8000,
              subTrans = "1000",
              mainTrans = "1540"
            ),
            DebtItemCharge(
              debtId = "DebtId9",
              debtAmount = 9000,
              subTrans = "1000",
              mainTrans = "1085"
            ),
            DebtItemCharge(
              debtId = "DebtId10",
              debtAmount = 17000,
              subTrans = "1000",
              mainTrans = "1535"
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "instalmentAmount",
        instalmentPaymentDate = LocalDate.parse("2020-03-14"),
        paymentFrequency = "monthly",
        duration = Some(12),
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
          debtId = "DebtId1",
          instalmentNumber = 1,
          dueDate = LocalDate.parse("2020-03-14"),
          amountDue = 61160,
          instalmentBalance = 100000,
          instalmentInterestAccrued = 17,
          expectedPayment = 61160,
          intRate = 3.25
        ),
        InstalmentResponse(
          debtId = "DebtId2",
          instalmentNumber = 3,
          dueDate = LocalDate.parse("2020-04-14"),
          amountDue = 21142,
          instalmentBalance = 200000,
          instalmentInterestAccrued = 0,
          expectedPayment = 122220,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId3",
          instalmentNumber = 8,
          dueDate = LocalDate.parse("2020-08-14"),
          amountDue = 61060,
          instalmentBalance = 97860,
          instalmentInterestAccrued = 215,
          expectedPayment = 366460,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId4",
          instalmentNumber = 10,
          dueDate = LocalDate.parse("2020-09-14"),
          amountDue = 23204,
          instalmentBalance = 70000,
          instalmentInterestAccrued = 0,
          expectedPayment = 427520,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId5",
          instalmentNumber = 12,
          dueDate = LocalDate.parse("2020-10-14"),
          amountDue = 13509,
          instalmentBalance = 200000,
          instalmentInterestAccrued = 0,
          expectedPayment = 488580,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId6",
          instalmentNumber = 17,
          dueDate = LocalDate.parse("2021-02-14"),
          amountDue = 6065,
          instalmentBalance = 6000,
          instalmentInterestAccrued = 0,
          expectedPayment = 683295,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId7",
          instalmentNumber = 18,
          dueDate = LocalDate.parse("2021-02-14"),
          amountDue = 15163,
          instalmentBalance = 15000,
          instalmentInterestAccrued = 0,
          expectedPayment = 698458,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId8",
          instalmentNumber = 19,
          dueDate = LocalDate.parse("2021-02-14"),
          amountDue = 8087,
          instalmentBalance = 8000,
          instalmentInterestAccrued = 193,
          expectedPayment = 706545,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId9",
          instalmentNumber = 20,
          dueDate = LocalDate.parse("2021-02-14"),
          amountDue = 9098,
          instalmentBalance = 9000,
          instalmentInterestAccrued = 0,
          expectedPayment = 715643,
          intRate = 0
        ),
        InstalmentResponse(
          debtId = "DebtId10",
          instalmentNumber = 21,
          dueDate = LocalDate.parse("2021-02-14"),
          amountDue = 17187,
          instalmentBalance = 17000,
          instalmentInterestAccrued = 413,
          expectedPayment = 732830,
          intRate = 2.6
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

    Scenario("Multiple Debts can be paid off within the same instalment period", DTD_1874) { context =>
      Given("debt instalment calculation with details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "DebtId1",
              debtAmount = 100000,
              subTrans = "1000",
              mainTrans = "1525"
            ),
            DebtItemCharge(
              debtId = "DebtId2",
              debtAmount = 2000,
              subTrans = "1355",
              mainTrans = "2130"
            ),
            DebtItemCharge(
              debtId = "DebtId3",
              debtAmount = 1000,
              subTrans = "1090",
              mainTrans = "4766"
            ),
            DebtItemCharge(
              debtId = "DebtId4",
              debtAmount = 700,
              subTrans = "1090",
              mainTrans = "4745"
            ),
            DebtItemCharge(
              debtId = "DebtId5",
              debtAmount = 60000,
              subTrans = "1090",
              mainTrans = "4770"
            ),
            DebtItemCharge(
              debtId = "DebtId6",
              debtAmount = 30000,
              subTrans = "1174",
              mainTrans = "4700"
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "instalmentAmount",
        instalmentPaymentDate = LocalDate.parse("2020-03-14"),
        paymentFrequency = "monthly",
        duration = Some(6),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0,
        initialPaymentDate = Some(LocalDate.parse("2020-03-14")),
        initialPaymentAmount = Some(100)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val instalmentsResponse = Seq(
        InstalmentResponse(
          debtId = "DebtId1",
          instalmentNumber = 1,
          dueDate = LocalDate.parse("2020-03-14"),
          amountDue = 32553,
          instalmentBalance = 100000,
          instalmentInterestAccrued = 17,
          expectedPayment = 32553,
          intRate = 3.25
        ),
        InstalmentResponse(
          debtId = "DebtId1",
          instalmentNumber = 4,
          dueDate = LocalDate.parse("2020-06-14"),
          amountDue = 3119,
          instalmentBalance = 3102,
          instalmentInterestAccrued = 6,
          expectedPayment = 100578,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId2",
          instalmentNumber = 5,
          dueDate = LocalDate.parse("2020-06-14"),
          amountDue = 2011,
          instalmentBalance = 2000,
          instalmentInterestAccrued = 13,
          expectedPayment = 102589,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId3",
          instalmentNumber = 6,
          dueDate = LocalDate.parse("2020-06-14"),
          amountDue = 1005,
          instalmentBalance = 1000,
          instalmentInterestAccrued = 5,
          expectedPayment = 103594,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId4",
          instalmentNumber = 7,
          dueDate = LocalDate.parse("2020-06-14"),
          amountDue = 704,
          instalmentBalance = 700,
          instalmentInterestAccrued = 4,
          expectedPayment = 104298,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId5",
          instalmentNumber = 8,
          dueDate = LocalDate.parse("2020-06-14"),
          amountDue = 25614,
          instalmentBalance = 60000,
          instalmentInterestAccrued = 420,
          expectedPayment = 129912,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId5",
          instalmentNumber = 9,
          dueDate = LocalDate.parse("2020-07-14"),
          amountDue = 32453,
          instalmentBalance = 34536,
          instalmentInterestAccrued = 73,
          expectedPayment = 162365,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId5",
          instalmentNumber = 10,
          dueDate = LocalDate.parse("2020-08-14"),
          amountDue = 2283,
          instalmentBalance = 2270,
          instalmentInterestAccrued = 4,
          expectedPayment = 164648,
          intRate = 2.6
        ),
        InstalmentResponse(
          debtId = "DebtId6",
          instalmentNumber = 11,
          dueDate = LocalDate.parse("2020-08-14"),
          amountDue = 30177,
          instalmentBalance = 30000,
          instalmentInterestAccrued = 340,
          expectedPayment = 194825,
          intRate = 2.6
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

  }
}
