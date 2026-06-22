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
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class LeapYearFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Leap years") {

    Scenario("2.Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made.") { context =>
      Given("a fc debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2019-12-16"),
            interestRequestedTo = "2020-05-05",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-05-03"
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

      Then("the IFS service will return a total debts summary")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 28,
        interestDueCallTotal = 5933,
        amountIntTotal = 405933,
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
        numberOfChargeableDays = 280,
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 5933,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 405933,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-12-16"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 15,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 133,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100133,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 790,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100790,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueDailyAccrual = 7,
          interestDueWindow = 60,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100060,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-05-03"),
          numberOfDays = 27,
          interestRate = 2.6,
          interestDueDailyAccrual = 7,
          interestDueWindow = 191,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100191,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-12-16"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 15,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 534,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400534,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3161,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 403161,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueDailyAccrual = 30,
          interestDueWindow = 240,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400240,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-05-05"),
          numberOfDays = 29,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 824,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400824,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
