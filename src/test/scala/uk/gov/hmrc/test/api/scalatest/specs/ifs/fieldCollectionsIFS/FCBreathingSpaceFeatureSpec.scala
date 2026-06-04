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
import uk.gov.hmrc.test.api.models.{FCCalculationWindow, FCDebtCalculation, FCDebtCalculationsSummary}
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 35,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 8582,
        totalAmountIntTotal = 508582,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 8582,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 508582,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-06-14"),
          numberOfDays = 133,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4736,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504736,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-06-15"),
          periodTo = LocalDate.parse("2021-08-14"),
          numberOfDays = 61,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-15"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 108,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3846,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503846,
          suppressionApplied = None
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 28,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 6880,
        totalAmountIntTotal = 406880,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 6880,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 406880,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-02-03"),
          numberOfDays = 2,
          interestRate = 2.6,
          interestDueDailyAccrual = 7,
          interestDueWindow = 14,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100014,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-06-14"),
          numberOfDays = 133,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 3789,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 403789,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-06-15"),
          periodTo = LocalDate.parse("2021-08-14"),
          numberOfDays = 61,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-15"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 108,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 3077,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 403077,
          suppressionApplied = None
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 0,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 0,
        totalAmountIntTotal = 500000,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500000,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 70,
        unpaidAmountTotal = 1000000,
        interestDueCallTotal = 17057,
        totalAmountIntTotal = 1017057,
        amountOnIntDueTotal = 1000000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 8582,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 508582,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-06-14"),
          numberOfDays = 133,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4736,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504736,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-06-15"),
          periodTo = LocalDate.parse("2021-08-14"),
          numberOfDays = 61,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-15"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 108,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3846,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503846,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

      And("the 2nd fc debt summary will contain")
      val expected2ndDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 8475,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 508475,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

      And("the 2nd fc debt summary will have calculation windows")
      val expected2ndCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-06-14"),
          numberOfDays = 133,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4736,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504736,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-06-15"),
          periodTo = LocalDate.parse("2021-08-14"),
          numberOfDays = 61,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-15"),
          periodTo = LocalDate.parse("2021-08-15"),
          numberOfDays = 1,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 35,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500035,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-16"),
          periodTo = LocalDate.parse("2021-08-18"),
          numberOfDays = 3,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-19"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 104,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3704,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503704,
          suppressionApplied = None
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 70,
        unpaidAmountTotal = 1000000,
        interestDueCallTotal = 19338,
        totalAmountIntTotal = 1019338,
        amountOnIntDueTotal = 1000000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 10756,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 510756,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 302,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 10756,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 510756,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

      And("the 2nd fc debt summary will contain")
      val expected2ndDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 8582,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 508582,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculations)

      And("the 2nd fc debt summary will have calculation windows")
      val expected2ndCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-06-14"),
          numberOfDays = 133,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4736,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504736,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-06-15"),
          periodTo = LocalDate.parse("2021-08-14"),
          numberOfDays = 61,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-08-15"),
          periodTo = LocalDate.parse("2021-11-30"),
          numberOfDays = 108,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3846,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503846,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindow)

    }

  }
}
