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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(4),
        planInterest = Some(27),
        interestAccrued = Some(300),
        totalInterest = Some(327),
        duration = Some(4),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(1000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(4),
              dueDate = Some(LocalDate.parse("2021-11-01")),
              amountDue = Some(1327)
            )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(5),
        planInterest = Some(27),
        interestAccrued = Some(300),
        totalInterest = Some(327),
        duration = Some(5),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(1000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(5),
              dueDate = Some(LocalDate.parse("2021-12-01")),
              amountDue = Some(327)
            )
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
        val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
          numberOfInstalments = Some(5),
          planInterest = Some(0),
          interestAccrued = Some(0),
          totalInterest = Some(0),
          duration = Some(5),
          instalments = Some(
            Seq(
              InstalmentResponseExpected(
                instalmentNumber = Some(1),
                dueDate = Some(LocalDate.parse("2021-08-01")),
                amountDue = Some(999)
              ),
              InstalmentResponseExpected(
                instalmentNumber = Some(5),
                dueDate = Some(LocalDate.parse("2021-12-01")),
                amountDue = Some(4)
              )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(4),
        planInterest = Some(0),
        interestAccrued = Some(0),
        totalInterest = Some(0),
        duration = Some(4),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(999)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(4),
              dueDate = Some(LocalDate.parse("2021-11-01")),
              amountDue = Some(1003)
            )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(4),
        planInterest = Some(18),
        interestAccrued = Some(300),
        totalInterest = Some(318),
        duration = Some(3),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-07-01")),
              amountDue = Some(1000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(2),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(999)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(4),
              dueDate = Some(LocalDate.parse("2021-10-01")),
              amountDue = Some(1320)
            )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(6),
        planInterest = Some(185),
        interestAccrued = Some(1000),
        totalInterest = Some(1185),
        duration = Some(4),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-07-01")),
              amountDue = Some(5000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(2),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(6000)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(6),
              dueDate = Some(LocalDate.parse("2021-11-01")),
              amountDue = Some(8185)
            )
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
      val instalmentCalculationResponse = InstalmentCalculationSummaryResponseExpected(
        numberOfInstalments = Some(5),
        planInterest = Some(27),
        interestAccrued = Some(1003),
        totalInterest = Some(1030),
        duration = Some(5),
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2021-08-01")),
              amountDue = Some(1006)
            ),
            InstalmentResponseExpected(
              instalmentNumber = Some(5),
              dueDate = Some(LocalDate.parse("2021-12-01")),
              amountDue = Some(1006)
            )
          )
        )
      )
      ifsResponseContainsExpectedValues(context, instalmentCalculationResponse)

    }

  }
}
