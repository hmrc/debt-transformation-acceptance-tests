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
import uk.gov.hmrc.test.api.models.ifs._
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.scalatest.steps.context.{InterestForecastingContext, SuppressionRulesContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class BreathingSpaceFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with InterestForecastingStepHelpers
    with SuppressionStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Breathing Space") {

    Scenario(
      "Interest Bearing. Single debt with breathing space and no payment history (SA)",
      DTD_2244,
      DTD_2273,
      DTD_2274
    ) { context =>
      Given("a fc debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2019-01-03",
                  debtRespiteTo = "2019-02-03"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the IFS service will return a total debts summary")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 44,
        interestDueCallTotal = 3872,
        amountIntTotal = 503872,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 87L,
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 3872,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 503872,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-01-02"),
          numberOfDays = 17L,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 756,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500756,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-01-03"),
          periodTo = LocalDate.parse("2019-02-03"),
          numberOfDays = 32L,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-02-04"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 70L,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 3116,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503116,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("2 debts with breathing space. No payment history (Scenario 1 - step 6) (SA)", DTD_2244) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-01-31"),
            interestRequestedTo = "2022-05-15",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-03-01",
                  debtRespiteTo = "2022-04-29"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-01-31"),
            interestRequestedTo = "2022-05-15",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-03-01",
                  debtRespiteTo = "2022-04-29"
                )
              )
            ),
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
        combinedDailyAccrual = 8,
        interestDueCallTotal = 356,
        amountIntTotal = 100356,
        amountOnIntDueTotal = 100000,
        unpaidAmountTotal = 100000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 44L,
        interestDueDailyAccrual = 4,
        interestDueDutyTotal = 178,
        amountOnIntDueDuty = 50000,
        totalAmountIntDuty = 50178,
        unpaidAmountDuty = 50000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueDailyAccrual = 3,
          interestDueWindow = 75,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50075,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueDailyAccrual = 4,
          interestDueWindow = 32,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50032,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-15"),
          numberOfDays = 16,
          interestRate = 3.25,
          interestDueDailyAccrual = 4,
          interestDueWindow = 71,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50071,
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
        numberOfChargeableDays = 44L,
        interestDueDailyAccrual = 4,
        interestDueDutyTotal = 178,
        amountOnIntDueDuty = 50000,
        totalAmountIntDuty = 50178,
        unpaidAmountDuty = 50000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueDailyAccrual = 3,
          interestDueWindow = 75,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50075,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueDailyAccrual = 4,
          interestDueWindow = 32,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50032,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-15"),
          numberOfDays = 16,
          interestRate = 3.25,
          interestDueDailyAccrual = 4,
          interestDueWindow = 71,
          amountOnIntDueWindow = 50000,
          unpaidAmountWindow = 50071,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario("Single debt with breathing space AND payment history (SA)", DTD_2140, DTD_2243) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-04-06"),
            interestRequestedTo = "2022-04-29",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-04-10",
                  debtRespiteTo = "2022-04-20"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 20000,
                  paymentDate = "2022-04-24"
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
        combinedDailyAccrual = 2,
        interestDueCallTotal = 44,
        amountIntTotal = 30044,
        amountOnIntDueTotal = 30000,
        unpaidAmountTotal = 30000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 19L,
        interestDueDailyAccrual = 2,
        interestDueDutyTotal = 44,
        amountOnIntDueDuty = 30000,
        totalAmountIntDuty = 30044,
        unpaidAmountDuty = 30000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-06"),
          periodTo = LocalDate.parse("2022-04-09"),
          numberOfDays = 3,
          interestRate = 3.25,
          interestDueDailyAccrual = 1,
          interestDueWindow = 5,
          amountOnIntDueWindow = 20000,
          unpaidAmountWindow = 20005,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-10"),
          periodTo = LocalDate.parse("2022-04-20"),
          numberOfDays = 11,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 20000,
          unpaidAmountWindow = 20000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-21"),
          periodTo = LocalDate.parse("2022-04-24"),
          numberOfDays = 4,
          interestRate = 3.25,
          interestDueDailyAccrual = 1,
          interestDueWindow = 7,
          amountOnIntDueWindow = 20000,
          unpaidAmountWindow = 20007,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-06"),
          periodTo = LocalDate.parse("2022-04-09"),
          numberOfDays = 3,
          interestRate = 3.25,
          interestDueDailyAccrual = 2,
          interestDueWindow = 8,
          amountOnIntDueWindow = 30000,
          unpaidAmountWindow = 30008,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-10"),
          periodTo = LocalDate.parse("2022-04-20"),
          numberOfDays = 11,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 30000,
          unpaidAmountWindow = 30000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-21"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 9,
          interestRate = 3.25,
          interestDueDailyAccrual = 2,
          interestDueWindow = 24,
          amountOnIntDueWindow = 30000,
          unpaidAmountWindow = 30024,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "2 debts one with a breathing space and payment history plus a late payment debt (Scenario 1, Step 7) (SA)",
      DTD_2140,
      DTD_2243
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-01-31"),
            interestRequestedTo = "2022-06-10",
            breathingSpaces = Some(List(BreathingSpaces("2022-03-01", "2022-04-29"))),
            paymentHistory = Some(List(PaymentHistory(25000, "2022-05-30")))
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 1500,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2034-11-12"),
            interestRequestedTo = "2022-06-10",
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
        combinedDailyAccrual = 2,
        interestDueCallTotal = 271,
        amountIntTotal = 26771,
        amountOnIntDueTotal = 26500,
        unpaidAmountTotal = 26500,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 129L,
        interestDueDailyAccrual = 2,
        interestDueDutyTotal = 271,
        amountOnIntDueDuty = 25000,
        totalAmountIntDuty = 25271,
        unpaidAmountDuty = 25000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueWindow = 37,
          interestDueDailyAccrual = 1,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25037,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 24,
          interestRate = 3.25,
          interestDueWindow = 53,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25053,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-05-30"),
          numberOfDays = 7,
          interestRate = 3.5,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueWindow = 37,
          interestDueDailyAccrual = 1,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25037,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 24,
          interestRate = 3.25,
          interestDueWindow = 53,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25053,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-06-10"),
          numberOfDays = 18,
          interestRate = 3.5,
          interestDueWindow = 43,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25043,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = false,
        numberOfChargeableDays = 0L,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 1500,
        totalAmountIntDuty = 1500,
        unpaidAmountDuty = 1500,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)
    }

    Scenario(
      "1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)",
      DTD_2140,
      DTD_2243
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-01-31"),
            interestRequestedTo = "2022-06-19",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-03-01",
                  debtRespiteTo = "2022-04-29"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2022-06-01",
                  debtRespiteTo = "2034-06-17"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 25000,
                  paymentDate = "2022-05-30"
                )
              )
            )
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 1500,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2034-11-12"),
            interestRequestedTo = "2022-06-10",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          ),
          DebtItem(
            debtID = Some("789"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-07-30"),
            interestRequestedTo = "2022-08-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-08-01",
                  debtRespiteTo = "2034-06-17"
                )
              )
            ),
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
        interestDueCallTotal = 252,
        amountIntTotal = 76752,
        amountOnIntDueTotal = 76500,
        unpaidAmountTotal = 76500,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 119,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 247,
        amountOnIntDueDuty = 25000,
        totalAmountIntDuty = 25247,
        unpaidAmountDuty = 25000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueWindow = 37,
          interestDueDailyAccrual = 1,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25037,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 24,
          interestRate = 3.25,
          interestDueWindow = 53,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25053,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-05-30"),
          numberOfDays = 7,
          interestRate = 3.5,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-31"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 20,
          interestRate = 2.75,
          interestDueWindow = 37,
          interestDueDailyAccrual = 1,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25037,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-02-28"),
          numberOfDays = 8,
          interestRate = 3.0,
          interestDueWindow = 16,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25016,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 35,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-29"),
          numberOfDays = 25,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-30"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 24,
          interestRate = 3.25,
          interestDueWindow = 53,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25053,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-05-31"),
          numberOfDays = 8,
          interestRate = 3.5,
          interestDueWindow = 19,
          interestDueDailyAccrual = 2,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 25019,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-06-01"),
          periodTo = LocalDate.parse("2022-06-19"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 25000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 25000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = false,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 1500,
        totalAmountIntDuty = 1500,
        unpaidAmountDuty = 1500,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)

      And("the 3rd debt summary will contain")
      val expected3rdDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("789"),
        interestBearing = true,
        numberOfChargeableDays = 1,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 5,
        amountOnIntDueDuty = 50000,
        totalAmountIntDuty = 50005,
        unpaidAmountDuty = 50000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 3, expected3rdDebtSummary)

      And("the 3rd debt summary will have calculation windows")
      val expected3rdCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-07-30"),
          periodTo = LocalDate.parse("2022-07-31"),
          numberOfDays = 1,
          interestRate = 3.75,
          interestDueWindow = 5,
          interestDueDailyAccrual = 5,
          amountOnIntDueWindow = 50000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 50005,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-08-01"),
          periodTo = LocalDate.parse("2022-08-10"),
          numberOfDays = 10,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 50000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 50000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 3, expected3rdCalculationWindows)
    }

    Scenario("Customer makes payment whilst in an active Breathing Space period (Scenario 4) (SA)", DTD_2140) {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 25000,
              subTrans = "1553",
              mainTrans = "4920",
              interestStartDate = Some("2022-01-31"),
              interestRequestedTo = "2022-08-01",
              breathingSpaces = Some(List(BreathingSpaces("2022-06-01", "2022-07-30"))),
              paymentHistory = Some(List(PaymentHistory(10000, "2022-07-01")))
            ),
            DebtItem(
              debtID = Some("456"),
              originalAmount = 15000,
              subTrans = "1553",
              mainTrans = "4920",
              interestStartDate = Some("2022-05-30"),
              interestRequestedTo = "2022-08-01",
              breathingSpaces = Some(List(BreathingSpaces("2022-06-01", "2022-07-30"))),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List.empty
        )
        aDebtCalculationIsCreated(context, request)

        When("the debt item is sent to the IFS service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val expectedResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 2,
          interestDueCallTotal = 258,
          amountIntTotal = 30258,
          amountOnIntDueTotal = 30000,
          unpaidAmountTotal = 30000,
          debtCalculations = List.empty
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 242,
          interestDueDailyAccrual = 1,
          interestDueDutyTotal = 254,
          amountOnIntDueDuty = 15000,
          totalAmountIntDuty = 15254,
          unpaidAmountDuty = 15000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-01-31"),
            periodTo = LocalDate.parse("2022-02-20"),
            numberOfDays = 20,
            interestRate = 2.75,
            interestDueWindow = 15,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 10000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 10015,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-02-21"),
            periodTo = LocalDate.parse("2022-04-04"),
            numberOfDays = 43,
            interestRate = 3.0,
            interestDueWindow = 35,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 10000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 10035,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-05"),
            periodTo = LocalDate.parse("2022-05-23"),
            numberOfDays = 49,
            interestRate = 3.25,
            interestDueWindow = 43,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 10000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 10043,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-05-24"),
            periodTo = LocalDate.parse("2022-05-31"),
            numberOfDays = 8,
            interestRate = 3.5,
            interestDueWindow = 7,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 10000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 10007,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-06-01"),
            periodTo = LocalDate.parse("2022-07-01"),
            numberOfDays = 31,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 10000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 10000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-01-31"),
            periodTo = LocalDate.parse("2022-02-20"),
            numberOfDays = 20,
            interestRate = 2.75,
            interestDueWindow = 22,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15022,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-02-21"),
            periodTo = LocalDate.parse("2022-04-04"),
            numberOfDays = 43,
            interestRate = 3.0,
            interestDueWindow = 53,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15053,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-05"),
            periodTo = LocalDate.parse("2022-05-23"),
            numberOfDays = 49,
            interestRate = 3.25,
            interestDueWindow = 65,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15065,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-05-24"),
            periodTo = LocalDate.parse("2022-05-31"),
            numberOfDays = 8,
            interestRate = 3.5,
            interestDueWindow = 11,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15011,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-06-01"),
            periodTo = LocalDate.parse("2022-07-04"),
            numberOfDays = 34,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 15000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-07-05"),
            periodTo = LocalDate.parse("2022-07-30"),
            numberOfDays = 26,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 15000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-07-31"),
            periodTo = LocalDate.parse("2022-08-01"),
            numberOfDays = 2,
            interestRate = 3.75,
            interestDueWindow = 3,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15003,
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
          numberOfChargeableDays = 3,
          interestDueDailyAccrual = 1,
          interestDueDutyTotal = 4,
          amountOnIntDueDuty = 15000,
          totalAmountIntDuty = 15004,
          unpaidAmountDuty = 15000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

        And("the 2nd debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-05-30"),
            periodTo = LocalDate.parse("2022-05-31"),
            numberOfDays = 1,
            interestRate = 3.5,
            interestDueWindow = 1,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15001,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-06-01"),
            periodTo = LocalDate.parse("2022-07-04"),
            numberOfDays = 34,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 15000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-07-05"),
            periodTo = LocalDate.parse("2022-07-30"),
            numberOfDays = 26,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 15000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-07-31"),
            periodTo = LocalDate.parse("2022-08-01"),
            numberOfDays = 2,
            interestRate = 3.75,
            interestDueWindow = 3,
            interestDueDailyAccrual = 1,
            amountOnIntDueWindow = 15000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 15003,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario("Interest Bearing. Breathing space that starts before the interest start date (SA)", DTD_2167, DTD_2244) {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1553",
              mainTrans = "4920",
              interestStartDate = Some("2018-12-16"),
              interestRequestedTo = "2019-04-14",
              breathingSpaces = Some(
                List(
                  BreathingSpaces(
                    debtRespiteFrom = "2017-01-03",
                    debtRespiteTo = "2019-02-03"
                  )
                )
              ),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List.empty
        )
        aDebtCalculationIsCreated(context, request)

        When("the debt item is sent to the IFS service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val expectedResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 44,
          interestDueCallTotal = 3116,
          amountIntTotal = 503116,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 70,
          interestDueDailyAccrual = 44,
          interestDueDutyTotal = 3116,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 503116,
          unpaidAmountDuty = 500000,
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
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 500000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2019-02-04"),
            periodTo = LocalDate.parse("2019-04-14"),
            numberOfDays = 70,
            interestRate = 3.25,
            interestDueWindow = 3116,
            interestDueDailyAccrual = 44,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 503116,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Interest Bearing. Breathing space that starts before the interest start date and ends after the interest end date (VAT)",
      DTD_2167,
      DTD_2244
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1090",
            mainTrans = "4766",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2017-01-03",
                  debtRespiteTo = "2019-05-03"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 500000,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
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
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500000,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 500000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest Bearing. Breathing space that starts same day as interest start date (SA)", DTD_2168, DTD_2244) {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1553",
              mainTrans = "4920",
              interestStartDate = Some("2018-12-16"),
              interestRequestedTo = "2019-04-14",
              breathingSpaces = Some(
                List(
                  BreathingSpaces(
                    debtRespiteFrom = "2018-12-16",
                    debtRespiteTo = "2019-02-03"
                  )
                )
              ),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List.empty
        )
        aDebtCalculationIsCreated(context, request)

        When("the debt item is sent to the IFS service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val expectedResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 44,
          interestDueCallTotal = 3116,
          amountIntTotal = 503116,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 70,
          interestDueDailyAccrual = 44,
          interestDueDutyTotal = 3116,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 503116,
          unpaidAmountDuty = 500000,
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
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 500000,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2019-02-04"),
            periodTo = LocalDate.parse("2019-04-14"),
            numberOfDays = 70,
            interestRate = 3.25,
            interestDueWindow = 3116,
            interestDueDailyAccrual = 44,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 503116,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Non Interest Bearing. Breathing space that starts same day as interest start date (SA)",
      DTD_2168,
      DTD_2244
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "5071",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2018-12-16",
                  debtRespiteTo = "2019-02-03"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 500000,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
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
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500000,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)
    }

    Scenario("Breathing space that ends same day as interest requested", DTD_2371) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2024-01-01"),
            interestRequestedTo = "2024-01-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2024-01-04",
                  debtRespiteTo = "2024-01-10"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW3 4QQ",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 177,
        amountIntTotal = 500177,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 2,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 177,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500177,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-01-01"),
          periodTo = LocalDate.parse("2024-01-03"),
          numberOfDays = 2,
          interestRate = 6.5,
          interestDueWindow = 177,
          interestDueDailyAccrual = 88,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500177,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-01-04"),
          periodTo = LocalDate.parse("2024-01-10"),
          numberOfDays = 7,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 500000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Breathing space that ends same day as interest requested to with a suppression(SA)",
      DTD_2371,
      DTD_3180
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionContext = SuppressionRulesContext()
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-02-01",
            dateTo = Some("2024-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = Some("4920"),
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionContext)

      And("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2024-01-01"),
            interestRequestedTo = "2024-01-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2024-01-04",
                  debtRespiteTo = "2024-01-10"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW3 4QQ",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 177,
        amountIntTotal = 500177,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 2,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 177,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500177,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-01-01"),
          periodTo = LocalDate.parse("2024-01-03"),
          numberOfDays = 2,
          interestRate = 6.5,
          interestDueWindow = 177,
          interestDueDailyAccrual = 88,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500177,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          LocalDate.parse("2024-01-04"),
          periodTo = LocalDate.parse("2024-01-10"),
          numberOfDays = 7,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 500000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Interest Bearing. Breathing space that ends same day as interest requested to. Breathing space includes interest rate change(SA)",
      DTD_2371
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-01-01"),
            interestRequestedTo = "2022-01-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-01-04",
                  debtRespiteTo = "2022-01-10"
                )
              )
            ),
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
        combinedDailyAccrual = 0,
        interestDueCallTotal = 71,
        amountIntTotal = 500071,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 2,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 71,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500071,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-01"),
          periodTo = LocalDate.parse("2022-01-03"),
          numberOfDays = 2,
          interestRate = 2.6,
          interestDueWindow = 71,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500071,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-04"),
          periodTo = LocalDate.parse("2022-01-06"),
          numberOfDays = 3,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 500000,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-07"),
          periodTo = LocalDate.parse("2022-01-10"),
          numberOfDays = 4,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = true,
          unpaidAmountWindow = 500000,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest Bearing. 2 breathing spaces. First ends same day as interest requested to (SA)", DTD_2371) {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1553",
              mainTrans = "4920",
              interestStartDate = Some("2021-01-01"),
              interestRequestedTo = "2021-01-10",
              breathingSpaces = Some(
                List(
                  BreathingSpaces(
                    debtRespiteFrom = "2021-01-04",
                    debtRespiteTo = "2021-01-10"
                  ),
                  BreathingSpaces(
                    debtRespiteFrom = "2021-03-01",
                    debtRespiteTo = "2021-03-10"
                  )
                )
              ),
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
          combinedDailyAccrual = 0,
          interestDueCallTotal = 71,
          amountIntTotal = 500071,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 2,
          interestDueDailyAccrual = 0,
          interestDueDutyTotal = 71,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 500071,
          unpaidAmountDuty = 500000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-01-01"),
            periodTo = LocalDate.parse("2021-01-03"),
            numberOfDays = 2,
            interestRate = 2.6,
            interestDueWindow = 71,
            interestDueDailyAccrual = 35,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 500071,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-01-04"),
            periodTo = LocalDate.parse("2021-01-10"),
            numberOfDays = 7,
            interestRate = 0.0,
            interestDueWindow = 0,
            interestDueDailyAccrual = 0,
            amountOnIntDueWindow = 500000,
            breathingSpaceApplied = true,
            unpaidAmountWindow = 500000,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Interest Bearing. Overlapping breathing spaces should be merged into 1 calculation window. No interest rate changes (SA)",
      DTD_2371
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2021-01-01"),
            interestRequestedTo = "2021-01-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-01-04",
                  debtRespiteTo = "2021-01-07"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2021-01-07",
                  debtRespiteTo = "2021-01-09"
                )
              )
            ),
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 106,
        amountIntTotal = 500106,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 3,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 106,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500106,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-01-03"),
          numberOfDays = 2,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 71,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500071,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-04"),
          periodTo = LocalDate.parse("2021-01-09"),
          numberOfDays = 6,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = true,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-10"),
          periodTo = LocalDate.parse("2021-01-10"),
          numberOfDays = 1,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 35,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500035,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
