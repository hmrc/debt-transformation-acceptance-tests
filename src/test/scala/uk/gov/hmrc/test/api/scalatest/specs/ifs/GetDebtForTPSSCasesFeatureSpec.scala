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

class GetDebtForTPSSCasesFeatureSpec
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

  Feature("Debt Calculation For Interest & Non Interest Bearing cases") {

    Scenario("Interest Bearing TPSS MainTrans 1525 debt") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 249,
        amountIntTotal = 500249,
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
        numberOfChargeableDays = 7,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 249,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500249,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-03-08"),
          numberOfDays = 7,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 249,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500249,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Non Interest Bearing TPSS MainTrans 1520 debt") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
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

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario(
      "interestBearing flag should be true where amount has been paid off. Payment date AFTER interest start date (for bug DTD-509)"
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 500000,
                  paymentDate = "2021-03-04"
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
        combinedDailyAccrual = 0,
        interestDueCallTotal = 106,
        amountIntTotal = 106,
        amountOnIntDueTotal = 0,
        unpaidAmountTotal = 0,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 3,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 106,
        amountOnIntDueDuty = 0,
        totalAmountIntDuty = 106,
        unpaidAmountDuty = 0,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-03-04"),
          numberOfDays = 3,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 106,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500106,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "interestBearing flag should be true even when debt has been paid off. Payment date BEFORE interest start date (for bug DTD-509)"
    ) { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 500000,
                  paymentDate = "2021-02-04"
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
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 0,
        amountOnIntDueTotal = 0,
        unpaidAmountTotal = 0,
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
        amountOnIntDueDuty = 0,
        totalAmountIntDuty = 0,
        unpaidAmountDuty = 0,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("Non Interest Bearing where amount has been paid off") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-06-08",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 300000,
                  paymentDate = "2021-03-23"
                ),
                PaymentHistory(
                  paymentAmount = 200000,
                  paymentDate = "2021-04-05"
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
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 0,
        amountOnIntDueTotal = 0,
        unpaidAmountTotal = 0,
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
        amountOnIntDueDuty = 0,
        totalAmountIntDuty = 0,
        unpaidAmountDuty = 0,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("interestStartDate should be optional for non interest bearing debt. Without payments (for bug DTD-496)") {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1090",
              mainTrans = "1520",
              interestStartDate = None,
              interestRequestedTo = "2021-03-08",
              breathingSpaces = Some(List.empty),
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

        And("the debt summary will have no calculation windows")
        theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

    Scenario("interestStartDate should be optional for non interest bearing debt. With payments (for bug DTD-496)") {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1090",
              mainTrans = "1520",
              interestStartDate = None,
              interestRequestedTo = "2021-03-08",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(
                List(
                  PaymentHistory(
                    paymentAmount = 100000,
                    paymentDate = "2021-03-04"
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

    Scenario("interestStartDate should be optional for non interest bearing debt. Multiple debts (for bug DTD-496)") {
      context =>
        Given("a debt calculation")
        val request = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1000",
              mainTrans = "1525",
              interestStartDate = Some("2021-03-01"),
              interestRequestedTo = "2021-03-08",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(
                List(
                  PaymentHistory(
                    paymentAmount = 100000,
                    paymentDate = "2021-03-04"
                  )
                )
              )
            ),
            DebtItem(
              debtID = Some("456"),
              originalAmount = 500000,
              subTrans = "1090",
              mainTrans = "1520",
              interestStartDate = None,
              interestRequestedTo = "2021-03-08",
              breathingSpaces = Some(List.empty),
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
          combinedDailyAccrual = 28,
          interestDueCallTotal = 220,
          amountIntTotal = 900220,
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
          numberOfChargeableDays = 10,
          interestDueDailyAccrual = 28,
          interestDueDutyTotal = 220,
          amountOnIntDueDuty = 400000,
          totalAmountIntDuty = 400220,
          unpaidAmountDuty = 400000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-03-01"),
            periodTo = LocalDate.parse("2021-03-04"),
            numberOfDays = 3,
            interestRate = 2.6,
            interestDueDailyAccrual = 7,
            interestDueWindow = 21,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 100021,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-03-01"),
            periodTo = LocalDate.parse("2021-03-08"),
            numberOfDays = 7,
            interestRate = 2.6,
            interestDueDailyAccrual = 28,
            interestDueWindow = 199,
            amountOnIntDueWindow = 400000,
            unpaidAmountWindow = 400199,
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

    Scenario("Interest only debt that is interest bearing") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1525",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
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
        combinedDailyAccrual = 35,
        interestDueCallTotal = 249,
        amountIntTotal = 500249,
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
        numberOfChargeableDays = 7,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 249,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500249,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-03-08"),
          numberOfDays = 7,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 249,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500249,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Non Interest Bearing TPSS MainTrans 2421 debt") { context =>
      Given("a debt calculation")
      val request = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1150",
            mainTrans = "2421",
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-03-08",
            breathingSpaces = Some(List.empty),
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

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }
  }
}
