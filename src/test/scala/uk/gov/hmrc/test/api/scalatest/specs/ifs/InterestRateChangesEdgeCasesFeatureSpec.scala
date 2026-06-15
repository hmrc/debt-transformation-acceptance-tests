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

package uk.gov.hmrc.test.api.scalatest.specs.ifs

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.ifs.{DebtCalculationRequest, DebtItem, PaymentHistory}
import uk.gov.hmrc.test.api.models.{CalculationWindow, DebtCalculation, DebtCalculationsSummary}
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers

import java.time.LocalDate

class InterestRateChangesEdgeCasesFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Interest Rate Changes - Edge cases") {

    Scenario("300 Debt items - Interest rate changes from 3.0% to 3.25%") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = (1 to 300).map { index =>
          DebtItem(
            debtID = Some(index.toString),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2018-10-30",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        }.toList,
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt items are sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 13200,
        interestDueCallTotal = 3795900,
        amountIntTotal = 153795900,
        amountOnIntDueTotal = 150000000,
        unpaidAmountTotal = 150000000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 300th debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("300"),
        interestBearing = true,
        numberOfChargeableDays = 302,
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 12653,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 512653,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 300, expectedDebtSummary)
    }

    Scenario(
      "2 Debts - Interest rate changes from 3.25% to 2.75% - leap year - payment is made for 1 debt - Interest rate changes from 2.75% to 2.6%"
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-06-01"),
            interestRequestedTo = "2021-03-31",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-04-15"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-03-15"
                )
              )
            )
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 500000,
            subTrans = "1090",
            mainTrans = "1545",
            interestStartDate = Some("2009-01-01"),
            interestRequestedTo = "2010-01-01",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 62,
        interestDueCallTotal = 48638,
        amountIntTotal = 848638,
        amountOnIntDueTotal = 800000,
        unpaidAmountTotal = 800000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 2005,
        interestDueDailyAccrual = 21,
        interestDueDutyTotal = 33983,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 333983,
        unpaidAmountDuty = 300000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-06-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 80,
          interestRate = 3.0,
          interestDueWindow = 657,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100657,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-15"),
          numberOfDays = 207,
          interestRate = 3.25,
          interestDueWindow = 1843,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 101843,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-06-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 80,
          interestRate = 3.0,
          interestDueWindow = 657,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100657,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 498,
          interestRate = 3.25,
          interestDueWindow = 4434,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 104434,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueWindow = 790,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100790,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueWindow = 60,
          interestDueDailyAccrual = 7,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100060,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-04-15"),
          numberOfDays = 9,
          interestRate = 2.6,
          interestDueWindow = 63,
          interestDueDailyAccrual = 7,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100063,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-06-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 80,
          interestRate = 3.0,
          interestDueWindow = 1972,
          interestDueDailyAccrual = 24,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 301972,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 498,
          interestRate = 3.25,
          interestDueWindow = 13302,
          interestDueDailyAccrual = 26,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 313302,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueWindow = 2370,
          interestDueDailyAccrual = 26,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 302370,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueWindow = 180,
          interestDueDailyAccrual = 22,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 300180,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 269,
          interestRate = 2.6,
          interestDueWindow = 5732,
          interestDueDailyAccrual = 21,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 305732,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-03-31"),
          numberOfDays = 90,
          interestRate = 2.6,
          interestDueWindow = 1923,
          interestDueDailyAccrual = 21,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 301923,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = true,
        numberOfChargeableDays = 365,
        interestDueDailyAccrual = 41,
        interestDueDutyTotal = 14655,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 514655,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2009-01-01"),
          periodTo = LocalDate.parse("2009-01-05"),
          numberOfDays = 4,
          interestRate = 5.5,
          interestDueWindow = 301,
          interestDueDailyAccrual = 75,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500301,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2009-01-06"),
          periodTo = LocalDate.parse("2009-01-26"),
          numberOfDays = 21,
          interestRate = 4.5,
          interestDueWindow = 1294,
          interestDueDailyAccrual = 61,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501294,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2009-01-27"),
          periodTo = LocalDate.parse("2009-03-23"),
          numberOfDays = 56,
          interestRate = 3.5,
          interestDueWindow = 2684,
          interestDueDailyAccrual = 47,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502684,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2009-03-24"),
          periodTo = LocalDate.parse("2009-09-28"),
          numberOfDays = 189,
          interestRate = 2.5,
          interestDueWindow = 6472,
          interestDueDailyAccrual = 34,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 506472,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2009-09-29"),
          periodTo = LocalDate.parse("2010-01-01"),
          numberOfDays = 95,
          interestRate = 3.0,
          interestDueWindow = 3904,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 503904,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario(
      "Interest rate changes from 2.75% to 2.6% - leap year - payment is made for 1 debt on the same day the interest rate changes"
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2020-01-01"),
            interestRequestedTo = "2021-03-31",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-04-07"
                )
              )
            )
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 28,
        interestDueCallTotal = 14420,
        amountIntTotal = 414420,
        amountOnIntDueTotal = 400000,
        unpaidAmountTotal = 400000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 552,
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 14420,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 414420,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 88,
          interestRate = 3.25,
          interestDueWindow = 781,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100781,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-07"),
          numberOfDays = 9,
          interestRate = 2.75,
          interestDueWindow = 67,
          interestDueDailyAccrual = 7,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100067,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 88,
          interestRate = 3.25,
          interestDueWindow = 3125,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 403125,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueWindow = 240,
          interestDueDailyAccrual = 30,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 400240,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 269,
          interestRate = 2.6,
          interestDueWindow = 7643,
          interestDueDailyAccrual = 28,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 407643,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-03-31"),
          numberOfDays = 90,
          interestRate = 2.6,
          interestDueWindow = 2564,
          interestDueDailyAccrual = 28,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 402564,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
