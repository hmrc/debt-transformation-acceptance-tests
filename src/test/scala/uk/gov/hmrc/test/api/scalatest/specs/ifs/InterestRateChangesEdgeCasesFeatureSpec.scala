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
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.{CalculationWindowExpected, DebtCalculationExpected, DebtCalculationsSummaryExpected}
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(13200),
        interestDueCallTotal = Some(3795900),
        amountIntTotal = Some(153795900),
        amountOnIntDueTotal = Some(150000000),
        unpaidAmountTotal = Some(150000000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 300th debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(302),
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(12653),
        unpaidAmountDuty = Some(500000)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(62),
        interestDueCallTotal = Some(48638),
        amountOnIntDueTotal = Some(800000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(2005),
        interestDueDailyAccrual = Some(21),
        interestDueDutyTotal = Some(33983),
        unpaidAmountDuty = Some(300000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-06-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(80),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(657),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-15")),
          numberOfDays = Some(207),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(1843),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-06-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(80),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(657),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(498),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(4434),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(790),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(60),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-04-15")),
          numberOfDays = Some(9),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(63),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-06-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(80),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(24),
          interestDueWindow = Some(1972),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(498),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(13302),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(2370),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(22),
          interestDueWindow = Some(180),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          numberOfDays = Some(269),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(21),
          interestDueWindow = Some(5732),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-03-31")),
          numberOfDays = Some(90),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(21),
          interestDueWindow = Some(1923),
          amountOnIntDueWindow = Some(300000)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(365),
        interestDueDailyAccrual = Some(41),
        interestDueDutyTotal = Some(14655),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2009-01-01")),
          periodTo = Some(LocalDate.parse("2009-01-05")),
          numberOfDays = Some(4),
          interestRate = Some(5.5),
          interestDueDailyAccrual = Some(75),
          interestDueWindow = Some(301),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2009-01-06")),
          periodTo = Some(LocalDate.parse("2009-01-26")),
          numberOfDays = Some(21),
          interestRate = Some(4.5),
          interestDueDailyAccrual = Some(61),
          interestDueWindow = Some(1294),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2009-01-27")),
          periodTo = Some(LocalDate.parse("2009-03-23")),
          numberOfDays = Some(56),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(47),
          interestDueWindow = Some(2684),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2009-03-24")),
          periodTo = Some(LocalDate.parse("2009-09-28")),
          numberOfDays = Some(189),
          interestRate = Some(2.5),
          interestDueDailyAccrual = Some(34),
          interestDueWindow = Some(6472),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2009-09-29")),
          periodTo = Some(LocalDate.parse("2010-01-01")),
          numberOfDays = Some(95),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(3904),
          amountOnIntDueWindow = Some(500000)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        interestDueCallTotal = Some(14420),
        unpaidAmountTotal = Some(400000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(552),
        totalAmountIntDuty = Some(414420)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(781),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-07")),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(67),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3125),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(30),
          interestDueWindow = Some(240),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(7643),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-03-31")),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(2564),
          amountOnIntDueWindow = Some(400000)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
