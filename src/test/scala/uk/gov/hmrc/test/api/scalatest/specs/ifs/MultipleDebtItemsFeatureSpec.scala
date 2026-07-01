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
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.{CalculationWindowExpected, DebtCalculationExpected, DebtCalculationsSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class MultipleDebtItemsFeatureSpec
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        interestDueCallTotal = Some(0),
        unpaidAmountTotal = Some(400000),
        amountIntTotal = Some(400000),
        amountOnIntDueTotal = Some(400000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        unpaidAmountDuty = Some(400000),
        totalAmountIntDuty = Some(400000),
        amountOnIntDueDuty = Some(400000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will not have any calculation windows")
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(26),
        interestDueCallTotal = Some(4495),
        unpaidAmountTotal = Some(300000),
        amountIntTotal = Some(304495)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(267),
        interestDueDailyAccrual = Some(26),
        interestDueDutyTotal = Some(4495),
        unpaidAmountDuty = Some(300000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-03-05")),
          numberOfDays = Some(79),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(703),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-23")),
          numberOfDays = Some(69),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(614),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(3178),
          amountOnIntDueWindow = Some(300000)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(79),
        amountIntTotal = Some(909971)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(168),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(404674)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          unpaidAmountWindow = Some(100436)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          unpaidAmountWindow = Some(404238)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(119),
        interestDueDailyAccrual = Some(44),
        totalAmountIntDuty = Some(505297)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          unpaidAmountWindow = Some(505297)
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        amountIntTotal = Some(1000000),
        amountOnIntDueTotal = Some(1000000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(1000000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will not have any calculation windows")
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(75),
        amountIntTotal = Some(1000000),
        amountOnIntDueTotal = Some(1000000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(75),
        totalAmountIntDuty = Some(1000000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-02")),
          periodTo = Some(LocalDate.parse("2022-02-02")),
          numberOfDays = Some(0),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(75),
          unpaidAmountWindow = Some(1000000),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        amountIntTotal = Some(900000),
        amountOnIntDueTotal = Some(900000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(900000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will not have any calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }
  }
}
