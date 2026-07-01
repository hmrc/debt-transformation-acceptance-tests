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
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class InstalmentCalculationMultipleDebtsInput2FeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = IFSInstalmentCalculationContext

  override def withFixture(test: OneArgTest) = {
    val context = IFSInstalmentCalculationContext()
    try test(context)
    finally ()
  }

  Feature("Instalment calculation for multiple debts - Input 2") {

    Scenario("Should calculate instalment amount for multiple debts no initial payment debt 1 (input 2)") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "Debt1",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            ),
            DebtItemCharge(
              debtId = "Debt2",
              debtAmount = 150000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2020-03-13"),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
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
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              dueDate = Some(LocalDate.parse("2020-03-14")),
              amountDue = Some(10680),
              instalmentBalance = Some(100000),
              intRate = Some(3.25)
            )
          )
        )
      )
      ifsResponseContainsExpectedValues(context, expectedInstalmentResponse)

    }

    Scenario("calculate instalment amount -On day of interest rate change") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "Debt1",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            ),
            DebtItemCharge(
              debtId = "Debt2",
              debtAmount = 150000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-11-22"),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-11-23"),
        paymentFrequency = "monthly",
        duration = Some(2),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              amountDue = Some(100256),
              instalmentBalance = Some(100000),
              intRate = Some(5.5)
            )
          )
        )
      )
      ifsResponseContainsExpectedValues(context, expectedInstalmentResponse)

    }

    Scenario("calculate instalment amount -Day before interest rate change") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "Debt1",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            ),
            DebtItemCharge(
              debtId = "Debt2",
              debtAmount = 150000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-11-21"),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-11-20"),
        paymentFrequency = "monthly",
        duration = Some(2),
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 0
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponseExpected(
        instalments = Some(
          Seq(
            InstalmentResponseExpected(
              instalmentNumber = Some(1),
              amountDue = Some(100224),
              instalmentBalance = Some(100000),
              intRate = Some(4.75)
            )
          )
        )
      )
      ifsResponseContainsExpectedValues(context, expectedInstalmentResponse)

    }
  }
}
