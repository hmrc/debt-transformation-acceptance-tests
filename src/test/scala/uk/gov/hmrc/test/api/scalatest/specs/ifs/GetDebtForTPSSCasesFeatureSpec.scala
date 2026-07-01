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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(35),
          interestDueCallTotal = Some(249),
          unpaidAmountTotal = Some(500000),
          amountIntTotal = Some(500249),
          amountOnIntDueTotal = Some(500000)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(true),
          interestDueDailyAccrual = Some(35),
          interestDueDutyTotal = Some(249),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(500249),
          numberOfChargeableDays = Some(7),
          amountOnIntDueDuty = Some(500000),
          interestOnlyIndicator = Some(false)
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-03-01")),
            periodTo = Some(LocalDate.parse("2021-03-08")),
            numberOfDays = Some(7),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            interestDueWindow = Some(249),
            amountOnIntDueWindow = Some(500000),
            unpaidAmountWindow = Some(500249)
          )
        )
      )
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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(0),
          unpaidAmountTotal = Some(500000),
          amountIntTotal = Some(500000),
          amountOnIntDueTotal = Some(500000)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(false),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(0),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(500000),
          numberOfChargeableDays = Some(0),
          amountOnIntDueDuty = Some(500000),
          interestOnlyIndicator = Some(false)
        )
      )

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
                PaymentHistory(paymentAmount = 500000, paymentDate = "2021-03-04")
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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(106),
          unpaidAmountTotal = Some(0),
          amountIntTotal = Some(106),
          amountOnIntDueTotal = Some(0)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(true),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(106),
          unpaidAmountDuty = Some(0),
          totalAmountIntDuty = Some(106),
          numberOfChargeableDays = Some(3),
          amountOnIntDueDuty = Some(0),
          interestOnlyIndicator = Some(false)
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-03-01")),
            periodTo = Some(LocalDate.parse("2021-03-04")),
            numberOfDays = Some(3),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            interestDueWindow = Some(106),
            amountOnIntDueWindow = Some(500000),
            unpaidAmountWindow = Some(500106)
          )
        )
      )
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
                PaymentHistory(paymentAmount = 500000, paymentDate = "2021-02-04")
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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(0),
          unpaidAmountTotal = Some(0),
          amountIntTotal = Some(0),
          amountOnIntDueTotal = Some(0)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(true),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(0),
          unpaidAmountDuty = Some(0),
          totalAmountIntDuty = Some(0),
          numberOfChargeableDays = Some(0),
          amountOnIntDueDuty = Some(0),
          interestOnlyIndicator = Some(false)
        )
      )
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
                PaymentHistory(paymentAmount = 300000, paymentDate = "2021-03-23"),
                PaymentHistory(paymentAmount = 200000, paymentDate = "2021-04-05")
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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(0),
          unpaidAmountTotal = Some(0),
          amountIntTotal = Some(0),
          amountOnIntDueTotal = Some(0)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(false),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(0),
          unpaidAmountDuty = Some(0),
          totalAmountIntDuty = Some(0),
          numberOfChargeableDays = Some(0),
          amountOnIntDueDuty = Some(0),
          interestOnlyIndicator = Some(false)
        )
      )

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
        theIfsServiceWillReturnATotalDebtsSummaryOf(
          context,
          DebtCalculationsSummaryExpected(
            combinedDailyAccrual = Some(0),
            interestDueCallTotal = Some(0),
            unpaidAmountTotal = Some(500000),
            amountIntTotal = Some(500000),
            amountOnIntDueTotal = Some(500000)
          )
        )

        And("the 1st debt summary will contain")
        theDebtSummaryWillContain(
          context,
          1,
          DebtCalculationExpected(
            interestBearing = Some(false),
            interestDueDailyAccrual = Some(0),
            interestDueDutyTotal = Some(0),
            unpaidAmountDuty = Some(500000),
            totalAmountIntDuty = Some(500000),
            numberOfChargeableDays = Some(0),
            amountOnIntDueDuty = Some(500000),
            interestOnlyIndicator = Some(false)
          )
        )

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
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2021-03-04")
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
        theIfsServiceWillReturnATotalDebtsSummaryOf(
          context,
          DebtCalculationsSummaryExpected(
            combinedDailyAccrual = Some(0),
            interestDueCallTotal = Some(0),
            unpaidAmountTotal = Some(400000),
            amountIntTotal = Some(400000),
            amountOnIntDueTotal = Some(400000)
          )
        )

        And("the 1st debt summary will contain")
        theDebtSummaryWillContain(
          context,
          1,
          DebtCalculationExpected(
            interestBearing = Some(false),
            interestDueDailyAccrual = Some(0),
            interestDueDutyTotal = Some(0),
            unpaidAmountDuty = Some(400000),
            totalAmountIntDuty = Some(400000),
            numberOfChargeableDays = Some(0),
            amountOnIntDueDuty = Some(400000),
            interestOnlyIndicator = Some(false)
          )
        )

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
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2021-03-04")
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
        theIfsServiceWillReturnATotalDebtsSummaryOf(
          context,
          DebtCalculationsSummaryExpected(
            combinedDailyAccrual = Some(28),
            interestDueCallTotal = Some(220),
            unpaidAmountTotal = Some(900000),
            amountIntTotal = Some(900220),
            amountOnIntDueTotal = Some(900000)
          )
        )

        And("the 1st debt summary will contain")
        theDebtSummaryWillContain(
          context,
          1,
          DebtCalculationExpected(
            interestBearing = Some(true),
            interestDueDailyAccrual = Some(28),
            interestDueDutyTotal = Some(220),
            unpaidAmountDuty = Some(400000),
            totalAmountIntDuty = Some(400220),
            numberOfChargeableDays = Some(10),
            amountOnIntDueDuty = Some(400000),
            interestOnlyIndicator = Some(false)
          )
        )

        And("the 1st debt summary will have calculation windows")
        theDebtSummaryWillHaveCalculationWindows(
          context,
          1,
          List(
            CalculationWindowExpected(
              periodFrom = Some(LocalDate.parse("2021-03-01")),
              periodTo = Some(LocalDate.parse("2021-03-04")),
              numberOfDays = Some(3),
              interestRate = Some(2.6),
              interestDueDailyAccrual = Some(7),
              interestDueWindow = Some(21),
              amountOnIntDueWindow = Some(100000),
              unpaidAmountWindow = Some(100021)
            ),
            CalculationWindowExpected(
              periodFrom = Some(LocalDate.parse("2021-03-01")),
              periodTo = Some(LocalDate.parse("2021-03-08")),
              numberOfDays = Some(7),
              interestRate = Some(2.6),
              interestDueDailyAccrual = Some(28),
              interestDueWindow = Some(199),
              amountOnIntDueWindow = Some(400000),
              unpaidAmountWindow = Some(400199)
            )
          )
        )

        And("the 2nd debt summary will contain")
        theDebtSummaryWillContain(
          context,
          2,
          DebtCalculationExpected(
            interestBearing = Some(false),
            interestDueDailyAccrual = Some(0),
            interestDueDutyTotal = Some(0),
            unpaidAmountDuty = Some(500000),
            totalAmountIntDuty = Some(500000),
            numberOfChargeableDays = Some(0),
            amountOnIntDueDuty = Some(500000),
            interestOnlyIndicator = Some(false)
          )
        )

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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(35),
          interestDueCallTotal = Some(249),
          unpaidAmountTotal = Some(500000),
          amountIntTotal = Some(500249),
          amountOnIntDueTotal = Some(500000)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(true),
          interestDueDailyAccrual = Some(35),
          interestDueDutyTotal = Some(249),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(500249),
          numberOfChargeableDays = Some(7),
          amountOnIntDueDuty = Some(500000),
          interestOnlyIndicator = Some(false)
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-03-01")),
            periodTo = Some(LocalDate.parse("2021-03-08")),
            numberOfDays = Some(7),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            interestDueWindow = Some(249),
            amountOnIntDueWindow = Some(500000),
            unpaidAmountWindow = Some(500249)
          )
        )
      )
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
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(0),
          interestDueCallTotal = Some(0),
          unpaidAmountTotal = Some(500000),
          amountIntTotal = Some(500000),
          amountOnIntDueTotal = Some(500000)
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculationExpected(
          interestBearing = Some(false),
          interestDueDailyAccrual = Some(0),
          interestDueDutyTotal = Some(0),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(500000),
          numberOfChargeableDays = Some(0),
          amountOnIntDueDuty = Some(500000),
          interestOnlyIndicator = Some(false)
        )
      )

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

  }
}
