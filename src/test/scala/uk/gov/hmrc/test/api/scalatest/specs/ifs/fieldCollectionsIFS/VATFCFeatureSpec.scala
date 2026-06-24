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

package uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.{FCVATDebtCalculation, FCVATDebtCalculationsSummary}
import uk.gov.hmrc.test.api.models.ifs.{BreathingSpaces, FCVATDebtCalculationRequest, FCVATDebtItems, PaymentHistory}
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsVATContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCVATInterestForecastingStepHelpers

import java.time.LocalDate

class VATFCFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCVATInterestForecastingStepHelpers {

  override type FixtureParam = FieldCollectionsVATContext

  override def withFixture(test: OneArgTest) = {
    val context = FieldCollectionsVATContext()
    try test(context)
    finally ()
  }

  Feature("FC VAT Debt Calculation End point testing") {

    Scenario("Interest Indicator as No. 1 Payment of 1 debt.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 500000,
            interestIndicator = "N",
            periodEnd = "2019-04-14",
            interestRequestedTo = "2018-12-16",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
                )
              )
            )
          )
        )
      )
      aFcVatDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc vat ifs service")
      theDebtItemIsSentToTheFcVatIfsService(context)

      Then("the fc vat ifs service will return a total debts summary of")
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummary(
        dateOfCalculation = LocalDate.now(),
        combinedDailyAccrual = 0,
        unpaidAmountTotal = 400000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicator as Yes. 2 Payment of 1 debt.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-11-24",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 20000,
                  paymentDate = "2021-06-01"
                ),
                PaymentHistory(
                  paymentAmount = 20000,
                  paymentDate = "2021-07-01"
                )
              )
            )
          )
        )
      )
      aFcVatDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc vat ifs service")
      theDebtItemIsSentToTheFcVatIfsService(context)

      Then("the fc vat ifs service will return a total debts summary of")
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummary(
        dateOfCalculation = LocalDate.now(),
        combinedDailyAccrual = 32,
        unpaidAmountTotal = 460000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 32,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicator as Yes. 2 Payment of 2 debt.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-12-16",
            interestRequestedTo = "2021-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
                )
              )
            )
          ),
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId2"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-12-16",
            interestRequestedTo = "2021-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
                )
              )
            )
          )
        )
      )
      aFcVatDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc vat ifs service")
      theDebtItemIsSentToTheFcVatIfsService(context)

      Then("the fc vat ifs service will return a total debts summary of")
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummary(
        dateOfCalculation = LocalDate.now(),
        combinedDailyAccrual = 56,
        unpaidAmountTotal = 800000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 28,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 2nd fc vat debt summary will contain")
      val expected2ndDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId2",
        interestDueDailyAccrual = 28,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

    }

    Scenario("Interest Indicator as Yes. No Payment History.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-12-16",
            interestRequestedTo = "2021-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(List.empty[PaymentHistory])
          )
        )
      )
      aFcVatDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc vat ifs service")
      theDebtItemIsSentToTheFcVatIfsService(context)

      Then("the fc vat ifs service will return a total debts summary of")
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummary(
        dateOfCalculation = LocalDate.now(),
        combinedDailyAccrual = 35,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 35,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicator as No. No Payment History.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 500000,
            interestIndicator = "N",
            periodEnd = "2022-12-16",
            interestRequestedTo = "2021-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(List.empty[PaymentHistory])
          )
        )
      )
      aFcVatDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc vat ifs service")
      theDebtItemIsSentToTheFcVatIfsService(context)

      Then("the fc vat ifs service will return a total debts summary of")
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummary(
        dateOfCalculation = LocalDate.now(),
        combinedDailyAccrual = 0,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

  }
}
