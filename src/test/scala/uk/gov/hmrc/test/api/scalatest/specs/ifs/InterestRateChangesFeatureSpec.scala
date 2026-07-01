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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(44),
        interestDueCallTotal = Some(20695),
        amountIntTotal = Some(520695),
        amountOnIntDueTotal = Some(500000),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        debtID = Some("123"),
        interestBearing = Some(true),
        numberOfChargeableDays = Some(485),
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(20695),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(520695),
        unpaidAmountDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2017-12-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(262),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(10767),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(510767)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-31")),
          numberOfDays = Some(223),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(9928),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(509928)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2020-02-01"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2020-02-01")
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(22),
        interestDueCallTotal = Some(19188),
        amountIntTotal = Some(319188),
        amountOnIntDueTotal = Some(300000),
        unpaidAmountTotal = Some(300000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(851)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-01-01")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(364),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(6482),
          unpaidAmountWindow = Some(206482),
          amountOnIntDueWindow = Some(200000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-02-01")),
          numberOfDays = Some(32),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(568),
          unpaidAmountWindow = Some(200568),
          amountOnIntDueWindow = Some(200000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-01-01")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(364),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(9723),
          unpaidAmountWindow = Some(309723),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(2370),
          unpaidAmountWindow = Some(302370),
          amountOnIntDueWindow = Some(300000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-03-31")),
          numberOfDays = Some(2),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(22),
          interestDueWindow = Some(45),
          unpaidAmountWindow = Some(300045),
          amountOnIntDueWindow = Some(300000)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2018-03-15")
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(16136),
        unpaidAmountTotal = Some(400000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(527)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-03-15")),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(8),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(32),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-31")),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          amountOnIntDueWindow = Some(400000)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2018-09-01"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2018-09-01")
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(26),
        interestDueCallTotal = Some(15661),
        amountIntTotal = Some(315661),
        unpaidAmountTotal = Some(300000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3.0),
          interestDueWindow = Some(3797)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2018-09-01")),
          interestRate = Some(3.25),
          interestDueWindow = Some(213)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3.0),
          interestDueWindow = Some(5695)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-31")),
          interestRate = Some(3.25),
          interestDueWindow = Some(5956)
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
        val expectedSummary = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(52),
          interestDueCallTotal = Some(37775),
          amountIntTotal = Some(637775),
          amountOnIntDueTotal = Some(600000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(1361),
          interestDueDailyAccrual = Some(26),
          interestDueDutyTotal = Some(19409),
          amountOnIntDueDuty = Some(300000)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1898)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-15")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1843)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1898)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-04-15")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(2119)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(24),
            interestDueWindow = Some(5695)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-31")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(26),
            interestDueWindow = Some(5956)
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

        And("the 2nd debt summary will contain")
        val expected2ndDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(1240),
          interestDueDailyAccrual = Some(26),
          interestDueDutyTotal = Some(18366),
          amountOnIntDueDuty = Some(300000)
        )
        theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

        And("the 2nd debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1775)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-01-20")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1362)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1775)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-10")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1798)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(24),
            interestDueWindow = Some(5326)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-04-14")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(26),
            interestDueWindow = Some(6330)
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        amountIntTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will not have any calculation windows")
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(41),
        interestDueCallTotal = Some(13363),
        unpaidAmountTotal = Some(500000),
        amountIntTotal = Some(513363),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        interestDueDailyAccrual = Some(41),
        interestDueDutyTotal = Some(13363),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(513363),
        numberOfChargeableDays = Some(366),
        amountOnIntDueDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-04-03")),
          periodTo = Some(LocalDate.parse("2022-01-06")),
          numberOfDays = Some(278),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(9901),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(509901)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(45),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(1695),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(501695)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(43),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(1767),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(501767)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }
  }
}
