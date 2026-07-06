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
import uk.gov.hmrc.test.api.models.ifs.{BreathingSpaces, FCVATDebtCalculationRequest, FCVATDebtItems, PaymentHistory}
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsVATBuilder.{FCVATDebtCalculationExpected, FCVATDebtCalculationsSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsVATContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCVATInterestForecastingStepHelpers

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
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        unpaidAmountTotal = Some(400000)
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("debtItemChargeId1"),
        interestDueDailyAccrual = Some(0),
        interestRate = Some(0)
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
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(32),
        unpaidAmountTotal = Some(460000)
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("123"),
        interestDueDailyAccrual = Some(32),
        interestRate = Some(2.6)
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
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(56),
        unpaidAmountTotal = Some(800000)
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("debtItemChargeId1"),
        interestDueDailyAccrual = Some(28),
        interestRate = Some(2.6)
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 2nd fc vat debt summary will contain")
      val expected2ndDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("debtItemChargeId2"),
        interestDueDailyAccrual = Some(28),
        interestRate = Some(2.6)
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
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(500000)
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("debtItemChargeId1"),
        interestDueDailyAccrual = Some(35),
        interestRate = Some(2.6)
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
      val FCVATDebtCalculationSummaryResponse = FCVATDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        unpaidAmountTotal = Some(500000)
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculationExpected(
        debtItemChargeId = Some("debtItemChargeId1"),
        interestDueDailyAccrual = Some(0),
        interestRate = Some(0)
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

  }
}
