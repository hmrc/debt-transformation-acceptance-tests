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

class InterestRateChangesFeatureSpec
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

  Feature("Interest Rate Changes") {

    Scenario("Interest rate changes from 3% to 3.25%") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2017-12-01"),
            interestRequestedTo = "2019-03-31",
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
        combinedDailyAccrual = 44,
        interestDueCallTotal = 20695,
        amountIntTotal = 520695,
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
        numberOfChargeableDays = 485,
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 20695,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 520695,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 262,
          interestRate = 3.0,
          interestDueWindow = 10767,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 510767,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueWindow = 9928,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 509928,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2019-01-01"),
            interestRequestedTo = "2020-03-31",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-02-01"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-02-01"
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
        combinedDailyAccrual = 22,
        interestDueCallTotal = 19188,
        amountIntTotal = 319188,
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
        numberOfChargeableDays = 851,
        interestDueDailyAccrual = 22,
        interestDueDutyTotal = 19188,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 319188,
        unpaidAmountDuty = 300000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-01-01"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 364,
          interestRate = 3.25,
          interestDueWindow = 6482,
          interestDueDailyAccrual = 17,
          amountOnIntDueWindow = 200000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 206482,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-02-01"),
          numberOfDays = 32,
          interestRate = 3.25,
          interestDueWindow = 568,
          interestDueDailyAccrual = 17,
          amountOnIntDueWindow = 200000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 200568,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-01-01"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 364,
          interestRate = 3.25,
          interestDueWindow = 9723,
          interestDueDailyAccrual = 26,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 309723,
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
          periodTo = LocalDate.parse("2020-03-31"),
          numberOfDays = 2,
          interestRate = 2.75,
          interestDueWindow = 45,
          interestDueDailyAccrual = 22,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 300045,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest rate changes from 3% to 3.25% after a payment is made") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2019-03-31",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-03-15"
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 16136,
        amountIntTotal = 416136,
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
        numberOfChargeableDays = 527,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 16136,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 416136,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-03-15"),
          numberOfDays = 73,
          interestRate = 3.0,
          interestDueWindow = 600,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100600,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3.0,
          interestDueWindow = 7594,
          interestDueDailyAccrual = 32,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 407594,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueWindow = 7942,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 407942,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Interest rate changes from 3% to 3.25% with 2 payments on same date") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2019-03-31",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-09-01"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-09-01"
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
        combinedDailyAccrual = 26,
        interestDueCallTotal = 15661,
        amountIntTotal = 315661,
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
        numberOfChargeableDays = 697,
        interestDueDailyAccrual = 26,
        interestDueDutyTotal = 15661,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 315661,
        unpaidAmountDuty = 300000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3.0,
          interestDueWindow = 3797,
          interestDueDailyAccrual = 16,
          amountOnIntDueWindow = 200000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 203797,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2018-09-01"),
          numberOfDays = 12,
          interestRate = 3.25,
          interestDueWindow = 213,
          interestDueDailyAccrual = 17,
          amountOnIntDueWindow = 200000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 200213,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3.0,
          interestDueWindow = 5695,
          interestDueDailyAccrual = 24,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 305695,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueWindow = 5956,
          interestDueDailyAccrual = 26,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 305956,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts") {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1000",
              mainTrans = "1525",
              interestStartDate = Some("2018-01-01"),
              interestRequestedTo = "2019-03-31",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(
                List(
                  PaymentHistory(100000, "2019-03-15"),
                  PaymentHistory(100000, "2019-04-15")
                )
              )
            ),
            DebtItem(
              debtID = Some("456"),
              originalAmount = 500000,
              subTrans = "1090",
              mainTrans = "1545",
              interestStartDate = Some("2018-01-16"),
              interestRequestedTo = "2019-04-14",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(
                List(
                  PaymentHistory(100000, "2019-01-20"),
                  PaymentHistory(100000, "2019-03-10")
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
          combinedDailyAccrual = 52,
          interestDueCallTotal = 37775,
          amountIntTotal = 637775,
          amountOnIntDueTotal = 600000,
          unpaidAmountTotal = 600000,
          debtCalculations = List.empty
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 1361,
          interestDueDailyAccrual = 26,
          interestDueDutyTotal = 19409,
          amountOnIntDueDuty = 300000,
          totalAmountIntDuty = 319409,
          unpaidAmountDuty = 300000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueWindow = 1898,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101898,
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
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueWindow = 1898,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101898,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-04-15"),
            numberOfDays = 238,
            interestRate = 3.25,
            interestDueWindow = 2119,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 102119,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueWindow = 5695,
            interestDueDailyAccrual = 24,
            amountOnIntDueWindow = 300000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 305695,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-03-31"),
            numberOfDays = 223,
            interestRate = 3.25,
            interestDueWindow = 5956,
            interestDueDailyAccrual = 26,
            amountOnIntDueWindow = 300000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 305956,
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
          numberOfChargeableDays = 1240,
          interestDueDailyAccrual = 26,
          interestDueDutyTotal = 18366,
          amountOnIntDueDuty = 300000,
          totalAmountIntDuty = 318366,
          unpaidAmountDuty = 300000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

        And("the 2nd debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueWindow = 1775,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101775,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-01-20"),
            numberOfDays = 153,
            interestRate = 3.25,
            interestDueWindow = 1362,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101362,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueWindow = 1775,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101775,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-03-10"),
            numberOfDays = 202,
            interestRate = 3.25,
            interestDueWindow = 1798,
            interestDueDailyAccrual = 8,
            amountOnIntDueWindow = 100000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 101798,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueWindow = 5326,
            interestDueDailyAccrual = 24,
            amountOnIntDueWindow = 300000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 305326,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-04-14"),
            numberOfDays = 237,
            interestRate = 3.25,
            interestDueWindow = 6330,
            interestDueDailyAccrual = 26,
            amountOnIntDueWindow = 300000,
            breathingSpaceApplied = false,
            unpaidAmountWindow = 306330,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario("Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2020-04-10"),
            interestRequestedTo = "2020-03-31",
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

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("Interest rate changes from 2.6% -> 2.75% -> 3") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2021-04-03"),
            interestRequestedTo = "2022-04-04",
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
        combinedDailyAccrual = 41,
        interestDueCallTotal = 13363,
        amountIntTotal = 513363,
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
        numberOfChargeableDays = 366,
        interestDueDailyAccrual = 41,
        interestDueDutyTotal = 13363,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 513363,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-04-03"),
          periodTo = LocalDate.parse("2022-01-06"),
          numberOfDays = 278,
          interestRate = 2.6,
          interestDueWindow = 9901,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 509901,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-07"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 45,
          interestRate = 2.75,
          interestDueWindow = 1695,
          interestDueDailyAccrual = 37,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501695,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 43,
          interestRate = 3.0,
          interestDueWindow = 1767,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501767,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
