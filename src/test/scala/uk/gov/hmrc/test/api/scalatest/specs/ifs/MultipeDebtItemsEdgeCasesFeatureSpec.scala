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
import uk.gov.hmrc.test.api.scalatest.tags.DTD_2216

import java.time.LocalDate

class MultipeDebtItemsEdgeCasesFeatureSpec
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

  Feature("Multiple Debt Items - Edge Cases") {

    Scenario("1. 2 debts. 1 interest bearing and 1 non interest bearing") { context =>
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
            subTrans = "1090",
            mainTrans = "1520",
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 4674,
        amountIntTotal = 904674,
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
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)
    }

    Scenario("2. 2 debts each with 1 payment of different amounts") { context =>
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 4674,
        amountIntTotal = 804674,
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
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)
    }

    Scenario("3. 3 debts, 1 with a payment") { context =>
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
            paymentHistory = Some(List.empty)
          ),
          DebtItem(
            debtID = Some("456"),
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
            debtID = Some("789"),
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
        combinedDailyAccrual = 123,
        interestDueCallTotal = 15268,
        amountIntTotal = 1415268,
        amountOnIntDueTotal = 1400000,
        unpaidAmountTotal = 1400000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
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
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
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
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
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
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-03"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueWindow = 436,
          interestDueDailyAccrual = 8,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100436,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 3.25,
          interestDueWindow = 4238,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 400000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 404238,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)

      And("the 3rd debt summary will contain")
      val expected3rdDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("789"),
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
      theDebtSummaryWillContain(context, 3, expected3rdDebtSummary)

      And("the 3rd debt summary will have calculation windows")
      val expected3rdCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-04-14"),
          numberOfDays = 119,
          interestRate = 3.25,
          interestDueWindow = 5297,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 505297,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 3, expected3rdCalculationWindows)
    }

    Scenario("4. 300 debt items") { context =>
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

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
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

    Scenario("5. 2 debts, 5 payments on one of the debts") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 1000000,
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
                ),
                PaymentHistory(
                  paymentAmount = 200000,
                  paymentDate = "2019-02-03"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-13"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-06"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-13"
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
        interestDueCallTotal = 12356,
        amountIntTotal = 912356,
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
        numberOfChargeableDays = 279,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 7059,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 407059,
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
          interestDueDailyAccrual = 26,
          interestDueWindow = 1308,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 301308,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-13"),
          numberOfDays = 59,
          interestRate = 3.25,
          interestDueDailyAccrual = 17,
          interestDueWindow = 1050,
          amountOnIntDueWindow = 200000,
          unpaidAmountWindow = 201050,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-06"),
          numberOfDays = 52,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 463,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100463,
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

    Scenario("6. 1 debt with payment where payment date before date created") { context =>
      Given("a debt calculation")
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
        combinedDailyAccrual = 28,
        interestDueCallTotal = 4759,
        amountIntTotal = 404759,
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
        numberOfChargeableDays = 141,
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 4759,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404759,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2019-12-16"),
          periodTo = LocalDate.parse("2019-02-03"),
          numberOfDays = 0,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100000,
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

    Scenario("7. 1 debt with a payment amount greater than original debt amount") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 50,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2019-12-16"),
            interestRequestedTo = "2020-05-05",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 1000,
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
      theDebtItemIsSentToTheIfsServiceAndFails(context)

      Then("the ifs service will respond with an error")
      theIfsServiceWillRespondWith(
        context,
        "Could not parse body due to requirement failed: Amount paid in payments cannot be greater than Original Amount"
      )
    }

    Scenario("8. 1 debt with an interest start date before the debt created") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            dateCreated = Some("2020-12-22"),
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2020-10-16"),
            interestRequestedTo = "2021-02-22",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-02-23"
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
        combinedDailyAccrual = 28,
        interestDueCallTotal = 4592,
        amountIntTotal = 404592,
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
        numberOfChargeableDays = 259,
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 4592,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404592,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-10-16"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 76,
          interestRate = 2.6,
          interestDueDailyAccrual = 7,
          interestDueWindow = 539,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100539,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-02-23"),
          numberOfDays = 54,
          interestRate = 2.6,
          interestDueDailyAccrual = 7,
          interestDueWindow = 384,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100384,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-10-16"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 76,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 2159,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 402159,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-02-22"),
          numberOfDays = 53,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 1510,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 401510,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("9. 2 SA debts where one has an original amount less than zero", DTD_2216){ context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1554",
            mainTrans = "6010",
            interestStartDate = Some("2019-12-16"),
            interestRequestedTo = "2020-05-05",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = -1,
            subTrans = "1553",
            mainTrans = "5070",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 2,
                  paymentDate = "2019-12-16"
                )
              )
            )
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsServiceAndFails(context)

      Then("the ifs service will respond with an error")
      theIfsServiceWillRespondWith(
        context,
        "Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted"
      )
    }
  }
}
