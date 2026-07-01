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
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.models.ifs._
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.{CalculationWindowExpected, DebtCalculationExpected, DebtCalculationsSummaryExpected}
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(44),
        interestDueCallTotal = Some(3872),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(87L),
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(3872),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-01-02")),
          numberOfDays = Some(17L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          unpaidAmountWindow = Some(500756),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-01-03")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(32L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-02-04")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(70L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          unpaidAmountWindow = Some(503116),
          breathingSpaceApplied = Some(false)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(8),
        amountIntTotal = Some(100356)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(44L),
        interestDueDailyAccrual = Some(4),
        totalAmountIntDuty = Some(50178)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(3),
          unpaidAmountWindow = Some(50075),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(4),
          unpaidAmountWindow = Some(50032),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(50000),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(50000),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-15")),
          numberOfDays = Some(16),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(4),
          unpaidAmountWindow = Some(50071),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(44L),
        interestDueDailyAccrual = Some(4),
        totalAmountIntDuty = Some(50178)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(3),
          unpaidAmountWindow = Some(50075),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(4),
          unpaidAmountWindow = Some(50032),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(50000),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(50000),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-15")),
          numberOfDays = Some(16),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(4),
          unpaidAmountWindow = Some(50071),
          breathingSpaceApplied = Some(false)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(2),
        amountIntTotal = Some(30044)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(19L),
        interestDueDailyAccrual = Some(2),
        totalAmountIntDuty = Some(30044)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-06")),
          periodTo = Some(LocalDate.parse("2022-04-09")),
          numberOfDays = Some(3L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(20005)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-10")),
          periodTo = Some(LocalDate.parse("2022-04-20")),
          numberOfDays = Some(11L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(20000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-21")),
          periodTo = Some(LocalDate.parse("2022-04-24")),
          numberOfDays = Some(4L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(20007)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-06")),
          periodTo = Some(LocalDate.parse("2022-04-09")),
          numberOfDays = Some(3L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(30008)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-10")),
          periodTo = Some(LocalDate.parse("2022-04-20")),
          numberOfDays = Some(11L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(30000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-21")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(9L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(30024)),
          breathingSpaceApplied = Some(false)
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(2),
        amountIntTotal = Some(26771)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(129L),
        interestDueDailyAccrual = Some(2),
        totalAmountIntDuty = Some(25271)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20L),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(25037)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8L),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(24L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25053)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-05-30")),
          numberOfDays = Some(7L),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20L),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(25037)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8L),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(24L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25053)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-06-10")),
          numberOfDays = Some(18L),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25043)),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(0L),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(1500)
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
                BreathingSpaces("2022-03-01", "2022-04-29"),
                BreathingSpaces("2022-06-01", "2034-06-17")
              )
            ),
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
          ),
          DebtItem(
            debtID = Some("789"),
            originalAmount = 50000,
            subTrans = "1553",
            mainTrans = "4920",
            interestStartDate = Some("2022-07-30"),
            interestRequestedTo = "2022-08-10",
            breathingSpaces = Some(List(BreathingSpaces("2022-08-01", "2034-06-17"))),
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
        amountIntTotal = Some(76752)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(119L),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(25247)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20L),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(25037)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8L),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(24L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25053)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-05-30")),
          numberOfDays = Some(7L),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-31")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(20L),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(BigDecimal(1)),
          unpaidAmountWindow = Some(BigDecimal(25037)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-02-28")),
          numberOfDays = Some(8L),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25016)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(35L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-29")),
          numberOfDays = Some(25L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-30")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(24L),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25053)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-05-31")),
          numberOfDays = Some(8L),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(BigDecimal(2)),
          unpaidAmountWindow = Some(BigDecimal(25019)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-06-01")),
          periodTo = Some(LocalDate.parse("2022-06-19")),
          numberOfDays = Some(19L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(25000)),
          breathingSpaceApplied = Some(true)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(0L),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(1500)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)

      And("the 3rd debt summary will contain")
      val expected3rdDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(1L),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(50005)
      )
      theDebtSummaryWillContain(context, 3, expected3rdDebtSummary)

      And("the 3rd debt summary will have calculation windows")
      val expected3rdCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-07-30")),
          periodTo = Some(LocalDate.parse("2022-07-31")),
          numberOfDays = Some(1L),
          interestRate = Some(3.75),
          interestDueDailyAccrual = Some(BigDecimal(5)),
          unpaidAmountWindow = Some(BigDecimal(50005)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-08-01")),
          periodTo = Some(LocalDate.parse("2022-08-10")),
          numberOfDays = Some(10L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(50000)),
          breathingSpaceApplied = Some(true)
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
        val expectedResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(2),
          amountIntTotal = Some(30258)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(242L),
          interestDueDailyAccrual = Some(1),
          totalAmountIntDuty = Some(15254)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-01-31")),
            periodTo = Some(LocalDate.parse("2022-02-20")),
            numberOfDays = Some(20L),
            interestRate = Some(2.75),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(10015)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-02-21")),
            periodTo = Some(LocalDate.parse("2022-04-04")),
            numberOfDays = Some(43L),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(10035)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-04-05")),
            periodTo = Some(LocalDate.parse("2022-05-23")),
            numberOfDays = Some(49L),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(10043)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-05-24")),
            periodTo = Some(LocalDate.parse("2022-05-31")),
            numberOfDays = Some(8L),
            interestRate = Some(3.5),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(10007)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-06-01")),
            periodTo = Some(LocalDate.parse("2022-07-01")),
            numberOfDays = Some(31L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(10000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-01-31")),
            periodTo = Some(LocalDate.parse("2022-02-20")),
            numberOfDays = Some(20L),
            interestRate = Some(2.75),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15022)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-02-21")),
            periodTo = Some(LocalDate.parse("2022-04-04")),
            numberOfDays = Some(43L),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15053)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-04-05")),
            periodTo = Some(LocalDate.parse("2022-05-23")),
            numberOfDays = Some(49L),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15065)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-05-24")),
            periodTo = Some(LocalDate.parse("2022-05-31")),
            numberOfDays = Some(8L),
            interestRate = Some(3.5),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15011)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-06-01")),
            periodTo = Some(LocalDate.parse("2022-07-04")),
            numberOfDays = Some(34L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(15000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-07-05")),
            periodTo = Some(LocalDate.parse("2022-07-30")),
            numberOfDays = Some(26L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(15000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-07-31")),
            periodTo = Some(LocalDate.parse("2022-08-01")),
            numberOfDays = Some(2L),
            interestRate = Some(3.75),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15003)),
            breathingSpaceApplied = Some(false)
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

        And("the 2nd debt summary will contain")
        val expected2ndDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(3L),
          interestDueDailyAccrual = Some(1),
          totalAmountIntDuty = Some(15004)
        )
        theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

        And("the 2nd debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-05-30")),
            periodTo = Some(LocalDate.parse("2022-05-31")),
            numberOfDays = Some(1L),
            interestRate = Some(3.5),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15001)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-06-01")),
            periodTo = Some(LocalDate.parse("2022-07-04")),
            numberOfDays = Some(34L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(15000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-07-05")),
            periodTo = Some(LocalDate.parse("2022-07-30")),
            numberOfDays = Some(26L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(15000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2022-07-31")),
            periodTo = Some(LocalDate.parse("2022-08-01")),
            numberOfDays = Some(2L),
            interestRate = Some(3.75),
            interestDueDailyAccrual = Some(BigDecimal(1)),
            unpaidAmountWindow = Some(BigDecimal(15003)),
            breathingSpaceApplied = Some(false)
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
              breathingSpaces = Some(List(BreathingSpaces("2017-01-03", "2019-02-03"))),
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
          combinedDailyAccrual = Some(44),
          interestDueCallTotal = Some(3116),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(70L),
          interestDueDailyAccrual = Some(44),
          interestDueDutyTotal = Some(3116),
          unpaidAmountDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-12-16")),
            periodTo = Some(LocalDate.parse("2019-02-03")),
            numberOfDays = Some(49L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(500000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2019-02-04")),
            periodTo = Some(LocalDate.parse("2019-04-14")),
            numberOfDays = Some(70L),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(BigDecimal(44)),
            unpaidAmountWindow = Some(BigDecimal(503116)),
            breathingSpaceApplied = Some(false)
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
            breathingSpaces = Some(List(BreathingSpaces("2017-01-03", "2019-05-03"))),
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
        interestDueCallTotal = Some(0),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(0L),
        interestDueDailyAccrual = Some(0),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(500000)),
          breathingSpaceApplied = Some(true)
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
              breathingSpaces = Some(List(BreathingSpaces("2018-12-16", "2019-02-03"))),
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
          combinedDailyAccrual = Some(44),
          interestDueCallTotal = Some(3116),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(70L),
          interestDueDailyAccrual = Some(44),
          interestDueDutyTotal = Some(3116),
          unpaidAmountDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-12-16")),
            periodTo = Some(LocalDate.parse("2019-02-03")),
            numberOfDays = Some(49L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(500000)),
            breathingSpaceApplied = Some(true)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2019-02-04")),
            periodTo = Some(LocalDate.parse("2019-04-14")),
            numberOfDays = Some(70L),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(BigDecimal(44)),
            unpaidAmountWindow = Some(BigDecimal(503116)),
            breathingSpaceApplied = Some(false)
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
            breathingSpaces = Some(List(BreathingSpaces("2018-12-16", "2019-02-03"))),
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
        interestDueCallTotal = Some(0),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0L),
        interestDueDailyAccrual = Some(0),
        unpaidAmountDuty = Some(500000)
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
            breathingSpaces = Some(List(BreathingSpaces("2024-01-04", "2024-01-10"))),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(CustomerPostCode(postCode = "TW3 4QQ", postCodeDate = "2019-07-06"))
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        interestDueCallTotal = Some(177),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(2L),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(177),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-01-01")),
          periodTo = Some(LocalDate.parse("2024-01-03")),
          numberOfDays = Some(2L),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(BigDecimal(88)),
          unpaidAmountWindow = Some(BigDecimal(500177)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-01-04")),
          periodTo = Some(LocalDate.parse("2024-01-10")),
          numberOfDays = Some(7L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(500000)),
          breathingSpaceApplied = Some(true)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario("Breathing space that ends same day as interest requested to with a suppression(SA)", DTD_2371, DTD_3180) {
      context =>
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
              breathingSpaces = Some(List(BreathingSpaces("2024-01-04", "2024-01-10"))),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List(CustomerPostCode(postCode = "TW3 4QQ", postCodeDate = "2019-07-06"))
        )
        aDebtCalculationIsCreated(context, request)

        When("the debt item is sent to the ifs service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val expectedResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(177),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(2L),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(177),
          unpaidAmountDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2024-01-01")),
            periodTo = Some(LocalDate.parse("2024-01-03")),
            numberOfDays = Some(2L),
            interestRate = Some(6.5),
            interestDueDailyAccrual = Some(BigDecimal(88)),
            unpaidAmountWindow = Some(BigDecimal(500177)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2024-01-04")),
            periodTo = Some(LocalDate.parse("2024-01-10")),
            numberOfDays = Some(7L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(500000)),
            breathingSpaceApplied = Some(true)
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
            breathingSpaces = Some(List(BreathingSpaces("2022-01-04", "2022-01-10"))),
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
        combinedDailyAccrual = Some(0),
        interestDueCallTotal = Some(71),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(2L),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(71),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-01")),
          periodTo = Some(LocalDate.parse("2022-01-03")),
          numberOfDays = Some(2L),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(BigDecimal(35)),
          unpaidAmountWindow = Some(BigDecimal(500071)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-04")),
          periodTo = Some(LocalDate.parse("2022-01-06")),
          numberOfDays = Some(3L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(500000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-01-10")),
          numberOfDays = Some(4L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(500000)),
          breathingSpaceApplied = Some(true)
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
                  BreathingSpaces("2021-01-04", "2021-01-10"),
                  BreathingSpaces("2021-03-01", "2021-03-10")
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
        val expectedResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(71),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expectedDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(2L),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(71),
          unpaidAmountDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expectedDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expectedCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-01-01")),
            periodTo = Some(LocalDate.parse("2021-01-03")),
            numberOfDays = Some(2L),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(BigDecimal(35)),
            unpaidAmountWindow = Some(BigDecimal(500071)),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-01-04")),
            periodTo = Some(LocalDate.parse("2021-01-10")),
            numberOfDays = Some(7L),
            interestRate = Some(0.0),
            interestDueDailyAccrual = Some(BigDecimal(0)),
            unpaidAmountWindow = Some(BigDecimal(500000)),
            breathingSpaceApplied = Some(true)
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
                BreathingSpaces("2021-01-04", "2021-01-07"),
                BreathingSpaces("2021-01-07", "2021-01-09")
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(106),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(3L),
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(106),
        unpaidAmountDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-01-03")),
          numberOfDays = Some(2L),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(BigDecimal(35)),
          interestDueWindow = Some(BigDecimal(71)),
          unpaidAmountWindow = Some(BigDecimal(500071)),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-04")),
          periodTo = Some(LocalDate.parse("2021-01-09")),
          numberOfDays = Some(6L),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(BigDecimal(0)),
          interestDueWindow = Some(BigDecimal(0)),
          unpaidAmountWindow = Some(BigDecimal(500000)),
          breathingSpaceApplied = Some(true)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-10")),
          periodTo = Some(LocalDate.parse("2021-01-10")),
          numberOfDays = Some(1L),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(BigDecimal(35)),
          interestDueWindow = Some(BigDecimal(35)),
          unpaidAmountWindow = Some(BigDecimal(500035)),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

  }
}
