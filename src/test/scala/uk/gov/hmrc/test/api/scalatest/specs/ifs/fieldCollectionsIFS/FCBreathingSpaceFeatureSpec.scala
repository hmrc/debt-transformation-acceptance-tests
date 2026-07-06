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

package uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.ifs._
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsBuilder.{FCCalculationWindowExpected, FCDebtCalculationExpected, FCDebtCalculationsSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import java.time.LocalDate

class FCBreathingSpaceFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers {

  override type FixtureParam = FieldCollectionsContext

  override def withFixture(test: OneArgTest) = {
    val context = FieldCollectionsContext()
    try test(context)
    finally ()
  }

  Feature("FC Debt Calculation Breathing Space") {

    Scenario("Breathing space for interest bearing debt with no payments.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                )
              )
            ),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(8582),
        totalAmountIntTotal = Some(508582),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(8582),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(508582),
        unpaidAmountDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-06-14")),
          numberOfDays = Some(133),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4736),
          unpaidAmountWindow = Some(504736)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-06-15")),
          periodTo = Some(LocalDate.parse("2021-08-14")),
          numberOfDays = Some(61),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-15")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(108),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3846),
          unpaidAmountWindow = Some(503846)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Breathing space for interest bearing debt with payments.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-02-03"
                )
              )
            ),
            customerPostcodes = Some(
              List(
                FCCustomerPostCode(
                  addressPostcode = "TW3 4QQ",
                  postcodeDate = "2019-07-06"
                )
              )
            )
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(6880)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(28),
        interestDueDutyTotal = Some(6880),
        unpaidAmountDuty = Some(400000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-02-03")),
          numberOfDays = Some(2),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(14),
          unpaidAmountWindow = Some(100014)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-06-14")),
          numberOfDays = Some(133),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(3789),
          unpaidAmountWindow = Some(403789)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-06-15")),
          periodTo = Some(LocalDate.parse("2021-08-14")),
          numberOfDays = Some(61),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(400000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-15")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(108),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(3077),
          unpaidAmountWindow = Some(403077)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Non interest bearing debt should not have breathing space applied") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "N",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                )
              )
            ),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service wilL return a total debts summary of")
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        totalAmountIntTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will not have any calculation windows")
      theFcDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)

    }

    Scenario("Multiple debts with multiple breathing Spaces") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                )
              )
            ),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          ),
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2021-08-16",
                  debtRespiteTo = "2021-08-18"
                )
              )
            ),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(70),
        totalAmountIntTotal = Some(1017057)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(8582)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-06-14")),
          numberOfDays = Some(133),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4736),
          unpaidAmountWindow = Some(504736)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-06-15")),
          periodTo = Some(LocalDate.parse("2021-08-14")),
          numberOfDays = Some(61),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-15")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(108),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3846),
          unpaidAmountWindow = Some(503846)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

      And("the 2nd fc debt summary will contain")
      val expected2ndDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(508475)
      )
      theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

      And("the 2nd fc debt summary will have calculation windows")
      val expected2ndCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-06-14")),
          numberOfDays = Some(133),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4736),
          unpaidAmountWindow = Some(504736)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-06-15")),
          periodTo = Some(LocalDate.parse("2021-08-14")),
          numberOfDays = Some(61),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-15")),
          periodTo = Some(LocalDate.parse("2021-08-15")),
          numberOfDays = Some(1),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(35),
          unpaidAmountWindow = Some(500035)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-16")),
          periodTo = Some(LocalDate.parse("2021-08-18")),
          numberOfDays = Some(3),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-19")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(104),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3704),
          unpaidAmountWindow = Some(503704)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindow)

    }

    Scenario("Multiple debts, 1 with a breathing Space, 1 without") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          ),
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2022-04-01",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-11-30",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2021-06-15",
                  debtRespiteTo = "2021-08-14"
                )
              )
            ),
            paymentHistory = Some(List.empty[PaymentHistory]),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(70),
        totalAmountIntTotal = Some(1019338)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(510756)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(302),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(10756),
          unpaidAmountWindow = Some(510756)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

      And("the 2nd fc debt summary will contain")
      val expected2ndDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(508582)
      )
      theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

      And("the 2nd fc debt summary will have calculation windows")
      val expected2ndCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-06-14")),
          numberOfDays = Some(133),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4736),
          unpaidAmountWindow = Some(504736)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-06-15")),
          periodTo = Some(LocalDate.parse("2021-08-14")),
          numberOfDays = Some(61),
          interestRate = Some(0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-08-15")),
          periodTo = Some(LocalDate.parse("2021-11-30")),
          numberOfDays = Some(108),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3846),
          unpaidAmountWindow = Some(503846)
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindow)

    }
  }
}
