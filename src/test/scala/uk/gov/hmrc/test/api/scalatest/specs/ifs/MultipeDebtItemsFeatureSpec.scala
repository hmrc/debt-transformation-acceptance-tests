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

import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{GivenWhenThen, Outcome}
import uk.gov.hmrc.test.api.models.ifs.{DebtCalculationRequest, DebtItem, PaymentHistory}
import uk.gov.hmrc.test.api.models.{CalculationWindow, DebtCalculation, DebtCalculationsSummary}
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class MultipeDebtItemsFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest): Outcome = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Multiple Debt Items") {

    Scenario("Non Interest Bearing. 1 Payment of 1 debt.") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
                )
              )
            )
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 400000,
        amountOnIntDueTotal = 400000,
        unpaidAmountTotal = 400000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = false,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 400000,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("Interest Bearing. 2 Payments of 1 debt.") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-23"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-03-05"
                )
              )
            )
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 26,
        interestDueCallTotal = 4495,
        amountIntTotal = 304495,
        amountOnIntDueTotal = 300000,
        unpaidAmountTotal = 300000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 267,
        interestDueDailyAccrual = 26,
        interestDueDutyTotal = 4495,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 304495,
        unpaidAmountDuty = 300000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-03-05"),
          numberOfDays = 79,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 703,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100703,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-23"),
          numberOfDays = 69,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 614,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100614,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 3.25,
          interestDueDailyAccrual = 26,
          interestDueWindow = 3178,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 303178,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest Bearing. 2 debts. 1 debt with payment the second debt with no payment.") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
                )
              )
            )
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 79,
        interestDueCallTotal = 9971,
        amountIntTotal = 909971,
        amountOnIntDueTotal = 900000,
        unpaidAmountTotal = 900000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 168,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4674,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404674,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-03"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 436,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100436,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4238,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 404238,
          breathingSpaceApplied = false,
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
        numberOfChargeableDays = 119,
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 5297,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 505297,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 5297,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 505297,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario("1 debt, no payment interest requested to date is before the interest start date") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 1000000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2023-03-03"),
            interestRequestedTo = "2022-02-02",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 1000000,
        amountOnIntDueTotal = 1000000,
        unpaidAmountTotal = 1000000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 1000000,
        totalAmountIntDuty = 1000000,
        unpaidAmountDuty = 1000000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("1 debt, no payment interest requested to date is same as interest start date") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 1000000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2022-02-02"),
            interestRequestedTo = "2022-02-02",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 75,
        interestDueCallTotal = 0,
        amountIntTotal = 1000000,
        amountOnIntDueTotal = 1000000,
        unpaidAmountTotal = 1000000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 75,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 1000000,
        totalAmountIntDuty = 1000000,
        unpaidAmountDuty = 1000000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-02"),
          periodTo = LocalDate.parse("2022-02-02"),
          numberOfDays = 0,
          interestRate = 2.75,
          interestDueDailyAccrual = 75,
          interestDueWindow = 0,
          amountOnIntDueWindow = 1000000,
          unpaidAmountWindow = 1000000,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "1 debt, non interest bearing, interest requested to date is before the interest start date, no dates are required"
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 1000000,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2023-03-02"),
            interestRequestedTo = "2022-02-02",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-01-01"
                )
              )
            )
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 900000,
        amountOnIntDueTotal = 900000,
        unpaidAmountTotal = 900000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = false,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 900000,
        totalAmountIntDuty = 900000,
        unpaidAmountDuty = 900000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }
  }
}
