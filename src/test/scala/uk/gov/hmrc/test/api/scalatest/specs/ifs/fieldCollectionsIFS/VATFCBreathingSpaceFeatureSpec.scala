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
import uk.gov.hmrc.test.api.models.{FCVATDebtCalculation, FCVATDebtCalculationsSummary}
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsVATContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCVATInterestForecastingStepHelpers

import java.time.LocalDate

class VATFCBreathingSpaceFeatureSpec
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

  Feature("FC VAT Debt Calculation with Breathing Space") {

    Scenario("Breathing space for interest bearing debt with payments.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-11-15",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-11-01",
                  debtRespiteTo = "2021-12-01"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-06-01"
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
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Breathing space for interest bearing debt with no payments.") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-10-04",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-09-01",
                  debtRespiteTo = "2021-12-01"
                )
              )
            ),
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
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Non interest bearing debt should not have breathing space applied") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "N",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-11-01",
                  debtRespiteTo = "2021-12-01"
                )
              )
            ),
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
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Multiple debts with multiple breathing Spaces") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2022-02-07",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-01-30",
                  debtRespiteTo = "2022-02-28"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2021-08-16",
                  debtRespiteTo = "2021-08-18"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-06-01"
                )
              )
            )
          ),
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-03-14",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-01-04",
                  debtRespiteTo = "2021-02-14"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 50000,
                  paymentDate = "2021-10-01"
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
        unpaidAmountTotal = 850000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 2nd fc vat debt summary will contain")
      val expected2ndDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 32,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

    }

    Scenario("Multiple debts, 1 with a breathing Space, 1 without") { context =>
      Given("a fc vat debt calculation")
      val ifsRequest = FCVATDebtCalculationRequest(
        debtItems = List(
          FCVATDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 800000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestRequestedTo = "2021-09-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-30",
                  debtRespiteTo = "2021-08-14"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2021-09-01",
                  debtRespiteTo = "2021-10-01"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-06-01"
                )
              )
            )
          ),
          FCVATDebtItems(
            debtItemChargeId = Some("debtItemChargeId1"),
            originalAmount = 600000,
            interestIndicator = "Y",
            periodEnd = "2019-04-01",
            interestRequestedTo = "2021-01-01",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 50000,
                  paymentDate = "2020-01-04"
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
        combinedDailyAccrual = 39,
        unpaidAmountTotal = 1250000,
        debtCalculations = List.empty[FCVATDebtCalculation]
      )
      theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(context, FCVATDebtCalculationSummaryResponse)

      And("the 1st fc vat debt summary will contain")
      val expected1stDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestRate = 0
      )
      theFcVatDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 2nd fc vat debt summary will contain")
      val expected2ndDebtCalculations = FCVATDebtCalculation(
        debtItemChargeId = "debtItemChargeId1",
        interestDueDailyAccrual = 39,
        interestRate = 2.6
      )
      theFcVatDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)
    }

  }
}
