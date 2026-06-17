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
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2020-03-13"),
        numberOfInstalments = 25,
        planInterest = 6343,
        interestAccrued = 0,
        totalInterest = 6343,
        duration = 24,
        instalments = Seq(
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2020-03-14"),
            amountDue = 10680,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 17,
            expectedPayment = 10680,
            intRate = 3.25
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
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2022-11-22"),
        numberOfInstalments = 3,
        planInterest = 640,
        interestAccrued = 0,
        totalInterest = 640,
        duration = 2,
        instalments = Seq(
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2022-11-23"),
            amountDue = 100256,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 30,
            expectedPayment = 100256,
            intRate = 5.5
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
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2022-11-21"),
        numberOfInstalments = 3,
        planInterest = 562,
        interestAccrued = 0,
        totalInterest = 562,
        duration = 2,
        instalments = Seq(
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2022-11-20"),
            amountDue = 100224,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 0,
            expectedPayment = 100224,
            intRate = 4.75
          )
        )
      )
      ifsResponseContainsExpectedValues(context, expectedInstalmentResponse)

    }
  }
}
