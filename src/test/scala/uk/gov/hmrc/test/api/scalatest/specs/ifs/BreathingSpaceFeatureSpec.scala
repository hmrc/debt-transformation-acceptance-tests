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
import uk.gov.hmrc.test.api.models.{CalculationWindow, DebtCalculation, DebtCalculationsSummary, FCDebtCalculation, FCDebtCalculationsSummary}
import uk.gov.hmrc.test.api.models.ifs.{BreathingSpaces, DebtCalculationRequest, DebtItem, DebtItems, FCDebtCalculationRequest, PaymentHistory}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, FieldCollectionsContext, InterestForecastingContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class BreathingSpaceFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Breathing Space") {

    Scenario("Interest Bearing. Single debt with breathing space and no payment history (SA)", DTD_2244) { context =>
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
      aDebtCalculation(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the IFS service will return a total debts summary")
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummary(
          combinedDailyAccrual = 44,
          interestDueCallTotal = 3872,
          amountIntTotal = 503872,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 87L,
          interestDueDailyAccrual = 44,
          interestDueDutyTotal = 3872,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 503872,
          unpaidAmountDuty = 500000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )
      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2018-12-16"),
            periodTo = LocalDate.parse("2019-01-02"),
            numberOfDays = 17L,
            interestRate = 3.25,
            interestDueDailyAccrual = 44,
            interestDueWindow = 756,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500756,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
          ,
          CalculationWindow(
            periodFrom = LocalDate.parse("2019-01-03"),
            periodTo = LocalDate.parse("2019-02-03"),
            numberOfDays = 32,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2019-02-04"),
            periodTo = LocalDate.parse("2019-04-14"),
            numberOfDays = 70,
            interestRate = 3.25,
            interestDueDailyAccrual = 44,
            interestDueWindow = 3116,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 503116,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
      )
    }

    Scenario("2 debts with breathing space. No payment history (Scenario 1 - step 6) (SA)", DTD_2140, DTD_2243) { context =>

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

      aDebtCalculation(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummary(
          combinedDailyAccrual = 8,
          interestDueCallTotal = 356,
          amountIntTotal = 100356,
          amountOnIntDueTotal = 100000,
          unpaidAmountTotal = 100000,
          debtCalculations = List.empty
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 44L,
          interestDueDailyAccrual = 4,
          interestDueDutyTotal = 178,
          amountOnIntDueDuty = 50000,
          totalAmountIntDuty = 50178,
          unpaidAmountDuty = 50000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-01-31"),
            periodTo = LocalDate.parse("2022-02-20"),
            numberOfDays = 20,
            interestRate = 2.75,
            interestDueDailyAccrual = 3,
            interestDueWindow = 75,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50075,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-02-21"),
            periodTo = LocalDate.parse("2022-02-28"),
            numberOfDays = 8,
            interestRate = 3.0,
            interestDueDailyAccrual = 4,
            interestDueWindow = 32,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50032,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-03-01"),
            periodTo = LocalDate.parse("2022-04-04"),
            numberOfDays = 35,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-05"),
            periodTo = LocalDate.parse("2022-04-29"),
            numberOfDays = 25,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-30"),
            periodTo = LocalDate.parse("2022-05-15"),
            numberOfDays = 16,
            interestRate = 3.25,
            interestDueDailyAccrual = 4,
            interestDueWindow = 71,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50071,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
      )

      And("the 2nd debt summary will contain")
      theDebtSummaryWillContain(
        context,
        2,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("456"),
          interestBearing = true,
          numberOfChargeableDays = 44L,
          interestDueDailyAccrual = 4,
          interestDueDutyTotal = 178,
          amountOnIntDueDuty = 50000,
          totalAmountIntDuty = 50178,
          unpaidAmountDuty = 50000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 2nd debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        2,
        List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-01-31"),
            periodTo = LocalDate.parse("2022-02-20"),
            numberOfDays = 20,
            interestRate = 2.75,
            interestDueDailyAccrual = 3,
            interestDueWindow = 75,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50075,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-02-21"),
            periodTo = LocalDate.parse("2022-02-28"),
            numberOfDays = 8,
            interestRate = 3.0,
            interestDueDailyAccrual = 4,
            interestDueWindow = 32,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50032,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-03-01"),
            periodTo = LocalDate.parse("2022-04-04"),
            numberOfDays = 35,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-05"),
            periodTo = LocalDate.parse("2022-04-29"),
            numberOfDays = 25,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-30"),
            periodTo = LocalDate.parse("2022-05-15"),
            numberOfDays = 16,
            interestRate = 3.25,
            interestDueDailyAccrual = 4,
            interestDueWindow = 71,
            amountOnIntDueWindow = 50000,
            unpaidAmountWindow = 50071,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
      )
    }

    Scenario("Single debt with breathing space AND payment history (SA)", DTD_2140, DTD_2243, DTD_2244) { context =>

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

      aDebtCalculation(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the IFS service will return a total debts summary")
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummary(
          combinedDailyAccrual = 2,
          interestDueCallTotal = 44,
          amountIntTotal = 30044,
          amountOnIntDueTotal = 30000,
          unpaidAmountTotal = 30000,
          debtCalculations = List.empty
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 19L,
          interestDueDailyAccrual = 2,
          interestDueDutyTotal = 44,
          amountOnIntDueDuty = 30000,
          totalAmountIntDuty = 30044,
          unpaidAmountDuty = 30000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-06"),
            periodTo = LocalDate.parse("2022-04-09"),
            numberOfDays = 3,
            interestRate = 3.25,
            interestDueDailyAccrual = 1,
            interestDueWindow = 5,
            amountOnIntDueWindow = 20000,
            unpaidAmountWindow = 20005,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-10"),
            periodTo = LocalDate.parse("2022-04-20"),
            numberOfDays = 11,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 20000,
            unpaidAmountWindow = 20000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-21"),
            periodTo = LocalDate.parse("2022-04-24"),
            numberOfDays = 4,
            interestRate = 3.25,
            interestDueDailyAccrual = 1,
            interestDueWindow = 7,
            amountOnIntDueWindow = 20000,
            unpaidAmountWindow = 20007,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-06"),
            periodTo = LocalDate.parse("2022-04-09"),
            numberOfDays = 3,
            interestRate = 3.25,
            interestDueDailyAccrual = 2,
            interestDueWindow = 8,
            amountOnIntDueWindow = 30000,
            unpaidAmountWindow = 30008,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-10"),
            periodTo = LocalDate.parse("2022-04-20"),
            numberOfDays = 11,
            interestRate = 0.0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 30000,
            unpaidAmountWindow = 30000,
            breathingSpaceApplied = true,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2022-04-21"),
            periodTo = LocalDate.parse("2022-04-29"),
            numberOfDays = 9,
            interestRate = 3.25,
            interestDueDailyAccrual = 2,
            interestDueWindow = 24,
            amountOnIntDueWindow = 30000,
            unpaidAmountWindow = 30024,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
      )
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

      aDebtCalculation(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummary(
          combinedDailyAccrual = 2,
          interestDueCallTotal = 271,
          amountIntTotal = 26771,
          amountOnIntDueTotal = 26500,
          unpaidAmountTotal = 26500,
          debtCalculations = List.empty
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 129L,
          interestDueDailyAccrual = 2,
          interestDueDutyTotal = 271,
          amountOnIntDueDuty = 25000,
          totalAmountIntDuty = 25271,
          unpaidAmountDuty = 25000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
          List(
            CalculationWindow(LocalDate.parse("2022-01-31"), LocalDate.parse("2022-02-20"), 20, 2.75, 37, 1, 25000, false, 25037, None, None),
            CalculationWindow(LocalDate.parse("2022-02-21"), LocalDate.parse("2022-02-28"), 8, 3.0, 16, 2, 25000, false, 25016, None, None),
            CalculationWindow(LocalDate.parse("2022-03-01"), LocalDate.parse("2022-04-04"), 35, 0.0, 0, 0, 25000, true, 25000, None, None),
            CalculationWindow(LocalDate.parse("2022-04-05"), LocalDate.parse("2022-04-29"), 25, 0.0, 0, 0, 25000, true, 25000, None, None),
            CalculationWindow(LocalDate.parse("2022-04-30"), LocalDate.parse("2022-05-23"), 24, 3.25, 53, 2, 25000, false, 25053, None, None),
            CalculationWindow(LocalDate.parse("2022-05-24"), LocalDate.parse("2022-05-30"), 7, 3.5, 16, 2, 25000, false, 25016, None, None),
            CalculationWindow(LocalDate.parse("2022-01-31"), LocalDate.parse("2022-02-20"), 20, 2.75, 37, 1, 25000, false, 25037, None, None),
            CalculationWindow(LocalDate.parse("2022-02-21"), LocalDate.parse("2022-02-28"), 8, 3.0, 16, 2, 25000, false, 25016, None, None),
            CalculationWindow(LocalDate.parse("2022-03-01"), LocalDate.parse("2022-04-04"), 35, 0.0, 0, 0, 25000, true, 25000, None, None),
            CalculationWindow(LocalDate.parse("2022-04-05"), LocalDate.parse("2022-04-29"), 25, 0.0, 0, 0, 25000, true, 25000, None, None),
            CalculationWindow(LocalDate.parse("2022-04-30"), LocalDate.parse("2022-05-23"), 24, 3.25, 53, 2, 25000, false, 25053, None, None),
            CalculationWindow(LocalDate.parse("2022-05-24"), LocalDate.parse("2022-06-10"), 18, 3.5, 43, 2, 25000, false, 25043, None, None)
          )
      )

      And("the 2nd debt summary will contain")
      theDebtSummaryWillContain(
        context,
        2,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("456"),
          interestBearing = false,
          numberOfChargeableDays = 0L,
          interestDueDailyAccrual = 0,
          interestDueDutyTotal = 0,
          amountOnIntDueDuty = 1500,
          totalAmountIntDuty = 1500,
          unpaidAmountDuty = 1500,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)
    }

    Scenario(
      "1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)",
      DTD_2140
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
                BreathingSpaces(
                  debtRespiteFrom = "2022-03-01",
                  debtRespiteTo = "2022-04-29"
                ),
                BreathingSpaces(
                  debtRespiteFrom = "2022-06-01",
                  debtRespiteTo = "2034-06-17"
                )
              )
            ),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 25000,
                  paymentDate = "2022-05-30"
                )
              )
            )
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
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-08-01",
                  debtRespiteTo = "2034-06-17"
                )
              )
            ),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )

      aDebtCalculation(context, request)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      theIfsServiceWillReturnATotalDebtsSummaryOf(
        context,
        DebtCalculationsSummary(
          combinedDailyAccrual = 0,
          interestDueCallTotal = 252,
          amountIntTotal = 76752,
          amountOnIntDueTotal = 76500,
          unpaidAmountTotal = 76500,
          debtCalculations = List.empty
        )
      )

      And("the 1st debt summary will contain")
      theDebtSummaryWillContain(
        context,
        1,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 119,
          interestDueDailyAccrual = 0,
          interestDueDutyTotal = 247,
          amountOnIntDueDuty = 25000,
          totalAmountIntDuty = 25247,
          unpaidAmountDuty = 25000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 1st debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        1,
        List(
          CalculationWindow(LocalDate.parse("2022-01-31"), LocalDate.parse("2022-02-20"), 20, 2.75, 37, 1, 25000, false, 25037, None, None),
          CalculationWindow(LocalDate.parse("2022-02-21"), LocalDate.parse("2022-02-28"), 8, 3.0, 16, 2, 25000, false, 25016, None, None),
          CalculationWindow(LocalDate.parse("2022-03-01"), LocalDate.parse("2022-04-04"), 35, 0.0, 0, 0, 25000, true, 25000, None, None),
          CalculationWindow(LocalDate.parse("2022-04-05"), LocalDate.parse("2022-04-29"), 25, 0.0, 0, 0, 25000, true, 25000, None, None),
          CalculationWindow(LocalDate.parse("2022-04-30"), LocalDate.parse("2022-05-23"), 24, 3.25, 53, 2, 25000, false, 25053, None, None),
          CalculationWindow(LocalDate.parse("2022-05-24"), LocalDate.parse("2022-05-30"), 7, 3.5, 16, 2, 25000, false, 25016, None, None),
          CalculationWindow(LocalDate.parse("2022-01-31"), LocalDate.parse("2022-02-20"), 20, 2.75, 37, 1, 25000, false, 25037, None, None),
          CalculationWindow(LocalDate.parse("2022-02-21"), LocalDate.parse("2022-02-28"), 8, 3.0, 16, 2, 25000, false, 25016, None, None),
          CalculationWindow(LocalDate.parse("2022-03-01"), LocalDate.parse("2022-04-04"), 35, 0.0, 0, 0, 25000, true, 25000, None, None),
          CalculationWindow(LocalDate.parse("2022-04-05"), LocalDate.parse("2022-04-29"), 25, 0.0, 0, 0, 25000, true, 25000, None, None),
          CalculationWindow(LocalDate.parse("2022-04-30"), LocalDate.parse("2022-05-23"), 24, 3.25, 53, 2, 25000, false, 25053, None, None),
          CalculationWindow(LocalDate.parse("2022-05-24"), LocalDate.parse("2022-05-31"), 8, 3.5, 19, 2, 25000, false, 25019, None, None),
          CalculationWindow(LocalDate.parse("2022-06-01"), LocalDate.parse("2022-06-19"), 19, 0.0, 0, 0, 25000, true, 25000, None, None)
        )
      )

      And("the 2nd debt summary will contain")
      theDebtSummaryWillContain(
        context,
        2,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("456"),
          interestBearing = false,
          numberOfChargeableDays = 0,
          interestDueDailyAccrual = 0,
          interestDueDutyTotal = 0,
          amountOnIntDueDuty = 1500,
          totalAmountIntDuty = 1500,
          unpaidAmountDuty = 1500,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 2nd debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 2)

      And("the 3rd debt summary will contain")
      theDebtSummaryWillContain(
        context,
        3,
        DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("789"),
          interestBearing = true,
          numberOfChargeableDays = 1,
          interestDueDailyAccrual = 0,
          interestDueDutyTotal = 5,
          amountOnIntDueDuty = 50000,
          totalAmountIntDuty = 50005,
          unpaidAmountDuty = 50000,
          interestOnlyIndicator = false,
          calculationWindows = Nil
        )
      )

      And("the 3rd debt summary will have calculation windows")
      theDebtSummaryWillHaveCalculationWindows(
        context,
        3,
        List(
          CalculationWindow(LocalDate.parse("2022-07-30"), LocalDate.parse("2022-07-31"), 1, 3.75, 5, 5, 50000, false, 50005, None, None),
          CalculationWindow(LocalDate.parse("2022-08-01"), LocalDate.parse("2022-08-10"), 10, 0.0, 0, 0, 50000, true, 50000, None, None)
        )
      )
    }




//    Scenario(
//      "1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)",
//      DTD_2140
//    ) { context =>
//      Given("a fc debt calculation")
//      val ifsRequest = FCDebtCalculationRequest(
//        debtItems = List(
//          DebtItems(
//            debtItemChargeId = Some("123"),
//            originalAmount = 50000,
//            interestIndicator = "Y",
//            periodEnd = "2022-05-15",
//            interestStartDate = Some("2022-01-31"),
//            interestRequestedTo = "2022-06-19",
//            breathingSpaces = Some(
//              List(
//                BreathingSpaces(
//                  debtRespiteFrom = "2022-03-01",
//                  debtRespiteTo = "2022-04-29"
//                ),
//                BreathingSpaces(
//                  debtRespiteFrom = "2022-06-01",
//                  debtRespiteTo = "2034-06-17"
//                )
//              )
//            ),
//            paymentHistory = Some(
//              List(
//                PaymentHistory(
//                  paymentAmount = 25000,
//                  paymentDate = "2022-05-30"
//                )
//              )
//            ),
//            customerPostcodes = Some(List.empty)
//          ),
//          DebtItems(
//            debtItemChargeId = Some("456"),
//            originalAmount = 1500,
//            interestIndicator = "Y",
//            periodEnd = "2022-05-15",
//            interestStartDate = Some("2034-11-12"),
//            interestRequestedTo = "2022-06-10",
//            breathingSpaces = Some(List.empty),
//            paymentHistory = Some(List.empty),
//            customerPostcodes = Some(List.empty)
//          ),
//          DebtItems(
//            debtItemChargeId = Some("789"),
//            originalAmount = 50000,
//            interestIndicator = "Y",
//            periodEnd = "2022-05-15",
//            interestStartDate = Some("2022-07-30"),
//            interestRequestedTo = "2022-08-10",
//            breathingSpaces = Some(
//              List(
//                BreathingSpaces(
//                  debtRespiteFrom = "2022-08-01",
//                  debtRespiteTo = "2034-06-17"
//                )
//              )
//            ),
//            paymentHistory = Some(List.empty),
//            customerPostcodes = Some(List.empty)
//          )
//        )
//      )
//
//      aFcDebtCalculation(context, ifsRequest)
//
//      When("the debt item is sent to the IFS service")
//      theDebtItemIsSentToTheFcIfsService(context)
//
//      Then("the ifs service wilL return a total debts summary of")
//      val result: FCDebtCalculationsSummary =
//        context.ifsResponseBody.get
//
//      result.combinedDailyAccrual shouldBe 0
//      result.totalAmountIntTotal  shouldBe 76752
//
//      And("the 1st debt summary will contain")
//      val firstDebt = result.debtCalculations.head
//
//      firstDebt.interestDueDailyAccrual shouldBe 0
//      firstDebt.totalAmountIntDuty      shouldBe 25247
//
//      And("the 1st debt summary will have calculation windows")
//      firstDebt.calculationWindows.map(_.numberOfDays)            shouldBe
//        List(20, 8, 35, 25, 24, 7, 20, 8, 35, 25, 24, 8, 19)
//      firstDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
//        List(1, 2, 0, 0, 2, 2, 1, 2, 0, 0, 2, 2, 0)
//      firstDebt.calculationWindows.map(_.interestRate)            shouldBe
//        List(2.75, 3.0, 0.0, 0.0, 3.25, 3.5, 2.75, 3.0, 0.0, 0.0, 3.25, 3.5, 0.0)
//      firstDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
//        List(25037, 25016, 25000, 25000, 25053, 25016, 25037, 25016, 25000, 25000, 25053, 25019, 25000)
//
//      And("the 2nd debt summary will contain")
//      val secondDebt = result.debtCalculations(1)
//
//      secondDebt.interestDueDailyAccrual shouldBe 0
//      secondDebt.totalAmountIntDuty      shouldBe 1500
//
//      And("the 3nd debt summary will contain")
//      val thirdDebt = result.debtCalculations(2)
//
//      thirdDebt.interestDueDailyAccrual shouldBe 0
//      thirdDebt.totalAmountIntDuty      shouldBe 50005
//
//      And("the 3st debt summary will have calculation windows")
//
//      thirdDebt.calculationWindows.map(_.numberOfDays)            shouldBe
//        List(1, 10)
//      thirdDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
//        List(5, 0)
//      thirdDebt.calculationWindows.map(_.interestRate)            shouldBe
//        List(3.75, 0)
//      thirdDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
//        List(50005, 50000)
//
//    }

    ignore("DELETE THISSSS Interest Bearing. Single debt with breathing space and no payment history (SA)", DTD_2244) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 1st debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore(
      "DELETE THISSSS2 debts with breathing space. No payment history (Scenario 1 - step 6) (SA)",
      DTD_2140,
      DTD_2243
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 2nd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("DELETE THISSS Single debt with breathing space AND payment history (SA)", DTD_2140, DTD_2243) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore(
      "DELETE THISSS2 debts one with a breathing space and payment history plus a late payment debt (Scenario 1, Step 7) (SA)",
      DTD_2140,
      DTD_2243
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

    }
    ignore(
      "DELETE THISsss 1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)",
      DTD_2140
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has payment history")
      // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("no breathing spaces have been applied to the debt item")
      // TODO: Helper 'noBreathingSpacesHaveBeenAppliedToTheDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noBreathingSpacesHaveBeenAppliedToTheDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

      And("the 2nd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 3rd debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 3rd debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Customer makes payment whilst in an active Breathing Space period (Scenario 4) (SA)", DTD_2167, DTD_2244) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has payment history")
        // TODO: Helper 'theDebtItemHasPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 1st debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

        And("the 2nd debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 2nd debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Interest Bearing. Breathing space that starts before the interest start date (SA)", DTD_2167, DTD_2244) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 1st debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore(
      "Interest Bearing. Breathing space that starts before the interest start date and ends after the interest end date (VAT)",
      DTD_2168,
      DTD_2244
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Interest Bearing. Breathing space that starts same day as interest start date (SA)", DTD_2168, DTD_2244) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 1st debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Non Interest Bearing. Breathing space that starts same day as interest start date (SA)", DTD_2371) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

    }
    ignore("Breathing space that ends same day as interest requested", DTD_2371, DTD_3180) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("the customer has post codes")
      // TODO: Helper 'theCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theCustomerHasPostCodes(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Breathing space that ends same day as interest requested to with a suppression(SA)", DTD_2371) { context =>
      Given("suppression configuration data is created")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.
      // TODO: This step had a feature table; convert the values into typed builder/model inputs.

      When("suppression configuration is sent to ifs service")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

      And("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("the customer has post codes")
      // TODO: Helper 'theCustomerHasPostCodes' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theCustomerHasPostCodes(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore(
      "Interest Bearing. Breathing space that ends same day as interest requested to. Breathing space includes interest rate change(SA)",
      DTD_2371
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore("Interest Bearing. 2 breathing spaces. First ends same day as interest requested to (SA)", DTD_2351) {
      context =>
        Given("a debt item")
        // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // aDebtItem(context)

        And("the debt item has no payment history")
        // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasNoPaymentHistory(context)

        And("the debt item has breathing spaces applied")
        // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemHasBreathingSpacesApplied(context)

        And("no post codes have been provided for the customer")
        // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // noPostCodesHaveBeenProvidedForTheCustomer(context)

        When("the debt item is sent to the ifs service")
        // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

        And("the 1st debt summary will contain")
        // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillContain(context)

        And("the 1st debt summary will have calculation windows")
        // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
        // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
        // theDebtSummaryWillHaveCalculationWindows(context)

    }
    ignore(
      "Interest Bearing. Overlapping breathing spaces should be merged into 1 calculation window. No interest rate changes (SA)"
    ) { context =>
      Given("a debt item")
      // TODO: Helper 'aDebtItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtItem(context)

      And("the debt item has no payment history")
      // TODO: Helper 'theDebtItemHasNoPaymentHistory' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasNoPaymentHistory(context)

      And("the debt item has breathing spaces applied")
      // TODO: Helper 'theDebtItemHasBreathingSpacesApplied' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemHasBreathingSpacesApplied(context)

      And("no post codes have been provided for the customer")
      // TODO: Helper 'noPostCodesHaveBeenProvidedForTheCustomer' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // noPostCodesHaveBeenProvidedForTheCustomer(context)

      When("the debt item is sent to the ifs service")
      // TODO: Helper 'theDebtItemIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      // TODO: Helper 'theIfsServiceWillReturnATotalDebtsSummaryOf' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillReturnATotalDebtsSummaryOf(context)

      And("the 1st debt summary will contain")
      // TODO: Helper 'theDebtSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillContain(context)

      And("the 1st debt summary will have calculation windows")
      // TODO: Helper 'theDebtSummaryWillHaveCalculationWindows' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtSummaryWillHaveCalculationWindows(context)

    }
  }
}
