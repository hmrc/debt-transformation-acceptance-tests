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
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class InstalmentCalculationMultipleDutiesFeatureSpec
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

  Feature("Instalment calculation for 1 debt and multiple duties with initial payment") {

    Scenario("Calculate quote details for 1 debt and multiple duties with non-interest bearing - weekly") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 50000,
              mainTrans = "5350",
              subTrans = "7012"
            ),
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 50000,
              mainTrans = "5350",
              subTrans = "7013"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "single",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns an non-interest bearing payment instalment plan")
      ifsServiceReturnsAnNonInterestBearingPaymentInstalmentPlan(context)

    }

    Scenario("Calculate quote details for 1 debt and multiple duties with interest bearing - weekly") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 50000,
              mainTrans = "1545",
              subTrans = "1000"
            ),
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 50000,
              mainTrans = "1545",
              subTrans = "1090"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "single",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns an interest bearing payment instalment plan")
      ifsServiceReturnsAnInterestBearingPaymentInstalmentPlan(context)

    }
  }
}
