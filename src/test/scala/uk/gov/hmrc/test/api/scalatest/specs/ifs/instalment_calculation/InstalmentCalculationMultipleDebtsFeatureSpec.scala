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
        val instalmentsResponse = InstalmentResponseExpected(
          instalmentNumber = Some(9),
          dueDate = Some(LocalDate.parse("2020-10-14")),
          amountDue = Some(100),
          instalmentBalance = Some(70000),
          intRate = Some(0)
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
      val instalmentsResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(8),
              amountDue = Some(10000),
              instalmentBalance = Some(19900),
              intRate = Some(3.25)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(9),
              amountDue = Some(9900),
              instalmentBalance = Some(9900),
              intRate = Some(6.5)
            )
          )
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
        val instalmentsResponse = InstalmentResponseExpected(
          instalmentNumber = Some(1),
          dueDate = Some(LocalDate.parse("2020-03-14")),
          amountDue = Some(10100),
          instalmentBalance = Some(80000),
          intRate = Some(3.25)
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(25),
        planInterest = Some(1232),
        interestAccrued = Some(0),
        totalInterest = Some(1232),
        duration = Some(24),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2020-03-14")),
              amountDue = Some(8480),
              instalmentBalance = Some(100000),
              intRate = Some(3.25)
            )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(7),
        planInterest = Some(187),
        interestAccrued = Some(1000),
        totalInterest = Some(1187),
        duration = Some(5),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2020-07-01")),
              amountDue = Some(5000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(2),
              dueDate = Some(LocalDate.parse("2020-08-01")),
              amountDue = Some(6000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(7),
              dueDate = Some(LocalDate.parse("2020-12-01")),
              amountDue = Some(2187)
            )
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
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
          numberOfInstalments = Some(7),
          planInterest = Some(353),
          interestAccrued = Some(1000),
          totalInterest = Some(1353),
          duration = Some(5),
          instalments = Some(
            Seq(
              InstalmentResponseExpected(
                instalmentNumber = Some(1),
                dueDate = Some(LocalDate.parse("2025-06-01")),
                amountDue = Some(5000),
                intRate = Some(6.5)
              ),
              InstalmentResponseExpected(
                instalmentNumber = Some(2),
                dueDate = Some(LocalDate.parse("2025-06-30")),
                amountDue = Some(6000),
                intRate = Some(6.5)
              ),
              InstalmentResponseExpected(
                instalmentNumber = Some(7),
                dueDate = Some(LocalDate.parse("2025-10-30")),
                amountDue = Some(2353),
                intRate = Some(6.5)
              )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(6),
        planInterest = Some(198),
        interestAccrued = Some(1000),
        totalInterest = Some(1198),
        duration = Some(5),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2020-08-01")),
              amountDue = Some(11000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(2),
              dueDate = Some(LocalDate.parse("2020-09-01")),
              amountDue = Some(5000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(3),
              dueDate = Some(LocalDate.parse("2020-09-01")),
              amountDue = Some(1000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(4),
              dueDate = Some(LocalDate.parse("2020-10-01")),
              amountDue = Some(6000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(5),
              dueDate = Some(LocalDate.parse("2020-11-01")),
              amountDue = Some(6000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(6),
              dueDate = Some(LocalDate.parse("2020-12-01")),
              amountDue = Some(2198)
            )
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
      val instalmentsResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              debtId = Some("DebtId1"),
              instalmentNumber = Some(1)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId2"),
              instalmentNumber = Some(3)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId3"),
              instalmentNumber = Some(8)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId4"),
              instalmentNumber = Some(10)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId5"),
              instalmentNumber = Some(12)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId6"),
              instalmentNumber = Some(17)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId7"),
              instalmentNumber = Some(18)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId8"),
              instalmentNumber = Some(19)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId9"),
              instalmentNumber = Some(20)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId10"),
              instalmentNumber = Some(21)
            )
          )
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
      val instalmentsResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              debtId = Some("DebtId1"),
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2020-03-14")),
              instalmentInterestAccrued = Some(17),
              intRate = Some(3.25)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId1"),
              instalmentNumber = Some(4),
              dueDate = Some(LocalDate.parse("2020-06-14")),
              instalmentInterestAccrued = Some(6),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId2"),
              instalmentNumber = Some(5),
              dueDate = Some(LocalDate.parse("2020-06-14")),
              instalmentInterestAccrued = Some(13),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId3"),
              instalmentNumber = Some(6),
              dueDate = Some(LocalDate.parse("2020-06-14")),
              instalmentInterestAccrued = Some(5),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId4"),
              instalmentNumber = Some(7),
              dueDate = Some(LocalDate.parse("2020-06-14")),
              instalmentInterestAccrued = Some(4),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId5"),
              instalmentNumber = Some(8),
              dueDate = Some(LocalDate.parse("2020-06-14")),
              instalmentInterestAccrued = Some(420),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId5"),
              instalmentNumber = Some(9),
              dueDate = Some(LocalDate.parse("2020-07-14")),
              instalmentInterestAccrued = Some(73),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId5"),
              instalmentNumber = Some(10),
              dueDate = Some(LocalDate.parse("2020-08-14")),
              instalmentInterestAccrued = Some(4),
              intRate = Some(2.6)
            ),
            InstalmentResponseExpected(
              debtId = Some("DebtId6"),
              instalmentNumber = Some(11),
              dueDate = Some(LocalDate.parse("2020-08-14")),
              instalmentInterestAccrued = Some(340),
              intRate = Some(2.6)
            )
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentsResponse)

    }

  }
}
