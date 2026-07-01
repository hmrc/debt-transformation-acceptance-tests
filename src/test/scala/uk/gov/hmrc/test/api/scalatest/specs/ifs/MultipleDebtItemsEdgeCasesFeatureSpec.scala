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
import uk.gov.hmrc.test.api.scalatest.tags.DTD_2216

import java.time.LocalDate

class MultipleDebtItemsEdgeCasesFeatureSpec
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(4674),
        amountIntTotal = Some(904674),
        amountOnIntDueTotal = Some(900000),
        unpaidAmountTotal = Some(900000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(168),
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4674),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(404674),
        unpaidAmountDuty = Some(400000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(436),
          amountOnIntDueWindow = Some(100000),
          unpaidAmountWindow = Some(100436)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4238),
          amountOnIntDueWindow = Some(400000),
          unpaidAmountWindow = Some(404238)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(500000),
        unpaidAmountDuty = Some(500000),
        interestOnlyIndicator = Some(false)
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(4674),
        unpaidAmountTotal = Some(800000),
        amountIntTotal = Some(804674),
        amountOnIntDueTotal = Some(800000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
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
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(400000)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will not have any calculation windows")
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(123),
        interestDueCallTotal = Some(15268),
        unpaidAmountTotal = Some(1400000),
        amountIntTotal = Some(1415268),
        amountOnIntDueTotal = Some(1400000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(119),
        interestDueDailyAccrual = Some(44),
        totalAmountIntDuty = Some(505297)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          unpaidAmountWindow = Some(505297)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(168),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(404674)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
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
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)

      And("the 3rd debt summary will contain")
      val expected3rdDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(119),
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(5297),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(505297)
      )
      theDebtSummaryWillContain(context, 3, expected3rdDebtSummary)

      And("the 3rd debt summary will have calculation windows")
      val expected3rdCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-04-14")),
          numberOfDays = Some(119),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          unpaidAmountWindow = Some(505297)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 3, expected3rdCalculationWindows)
    }

    Scenario("4. 300 debt items") { context =>
      Given("300 debt items")
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

      When("the debt items are sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(13200),
        interestDueCallTotal = Some(3795900),
        unpaidAmountTotal = Some(150000000),
        amountIntTotal = Some(153795900),
        amountOnIntDueTotal = Some(150000000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 300th debt summary will contain")
      val expected300thDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(302),
        interestDueDailyAccrual = Some(44),
        totalAmountIntDuty = Some(512653)
      )
      theDebtSummaryWillContain(context, 300, expected300thDebtSummary)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2019-02-03"),
                PaymentHistory(paymentAmount = 200000, paymentDate = "2019-02-03"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2019-02-13"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2019-02-06"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2019-02-13")
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
        amountIntTotal = Some(912356)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(279),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(407059)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          unpaidAmountWindow = Some(301308)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-13")),
          numberOfDays = Some(59),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(17),
          unpaidAmountWindow = Some(201050)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-06")),
          numberOfDays = Some(52),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          unpaidAmountWindow = Some(100463)
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
        interestBearing = Some(true),
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        amountIntTotal = Some(404759)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(141),
        interestDueDailyAccrual = Some(28),
        totalAmountIntDuty = Some(404759)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(0),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(15),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          unpaidAmountWindow = Some(400534)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          unpaidAmountWindow = Some(403161)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(30),
          unpaidAmountWindow = Some(400240)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-05-05")),
          numberOfDays = Some(29),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          unpaidAmountWindow = Some(400824)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
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

      When("the debt item is sent to the ifs service")
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

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        amountIntTotal = Some(404592)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(259),
        interestDueDailyAccrual = Some(28),
        totalAmountIntDuty = Some(404592)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-10-16")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          numberOfDays = Some(76),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          unpaidAmountWindow = Some(100539)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-02-23")),
          numberOfDays = Some(54),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          unpaidAmountWindow = Some(100384)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-10-16")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          numberOfDays = Some(76),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          unpaidAmountWindow = Some(402159)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-02-22")),
          numberOfDays = Some(53),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          unpaidAmountWindow = Some(401510)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
    }

    Scenario("9. 2 SA debts where one has an original amount less than zero", DTD_2216) { context =>
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

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsServiceAndFails(context)

      Then("the ifs service will respond with an error")
      theIfsServiceWillRespondWith(
        context,
        "Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted"
      )
    }
  }
}
