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

import org.scalatest.{GivenWhenThen, ScalaTestVersion}
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.{FCCalculationWindow, FCDebtCalculation, FCDebtCalculationsSummary}
import uk.gov.hmrc.test.api.models.ifs._
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers

import java.time.LocalDate

class FCMultipeDebtItemsFeatureSpec
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

  Feature("FC Debt Calculation End point testing") {

    Scenario("Interest Indicators. 2 debt. 1 payment history and cotax charge interest") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            chargedInterest = Some(200),
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
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
          ),
          FCDebtItems(
            debtItemChargeId = Some("456"),
            originalAmount = 300000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(List.empty[PaymentHistory]),
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
        combinedDailyAccrual = 61,
        unpaidAmountTotal = 700000,
        interestDueCallTotal = 8052,
        totalAmountIntTotal = 708052,
        amountOnIntDueTotal = 700000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4874,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404874,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicators. 2 debt. 1 payment history") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
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
          ),
          FCDebtItems(
            debtItemChargeId = Some("456"),
            originalAmount = 300000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(List.empty[PaymentHistory]),
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
        combinedDailyAccrual = 61,
        unpaidAmountTotal = 700000,
        interestDueCallTotal = 7852,
        totalAmountIntTotal = 707852,
        amountOnIntDueTotal = 700000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4674,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404674,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicator. 1 Payment of 1 debt.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
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
        combinedDailyAccrual = 35,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 4674,
        totalAmountIntTotal = 404674,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4674,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404674,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("No Interest Indicator. 1 Payment of 1 debt.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "N",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
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
        combinedDailyAccrual = 0,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 0,
        totalAmountIntTotal = 400000,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 400000,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will not have any calculation windows")
      theFcDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)

    }

    Scenario("Interest Indicator. 1 Payment of 1 debt. No breathing space.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2019-02-03"
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
        combinedDailyAccrual = 35,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 4674,
        totalAmountIntTotal = 404674,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4674,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 404674,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("1 debt, no payment history") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-07-16"),
            interestRequestedTo = "2019-04-16",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        combinedDailyAccrual = 44,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 12078,
        totalAmountIntTotal = 512078,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 12078,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 512078,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

    }

    Scenario("Interest Indicator. 1 Payment of 1 debt. Payment Done.") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-12-16"),
            interestRequestedTo = "2019-04-14",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 500000,
                  paymentDate = "2019-02-03"
                )
              )
            ),
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
        combinedDailyAccrual = 0,
        unpaidAmountTotal = 0,
        interestDueCallTotal = 2181,
        totalAmountIntTotal = 2181,
        amountOnIntDueTotal = 0,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 2181,
        amountOnIntDueDuty = 0,
        totalAmountIntDuty = 2181,
        unpaidAmountDuty = 0,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-12-16"),
          periodTo = LocalDate.parse("2019-02-03"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 2181,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 502181,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Debt ending in a leap year") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2020-04-01",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        combinedDailyAccrual = 37,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 35727,
        totalAmountIntTotal = 535727,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 37,
        interestDueDutyTotal = 35727,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 535727,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3,
          interestDueDailyAccrual = 41,
          interestDueWindow = 9493,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 509493,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 498,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 22171,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 522171,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 3951,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503951,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-01"),
          numberOfDays = 3,
          interestRate = 2.75,
          interestDueDailyAccrual = 37,
          interestDueWindow = 112,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500112,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Debt starting in a leap year") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2020-05-02"),
            interestRequestedTo = "2021-05-01",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        interestDueCallTotal = 12940,
        totalAmountIntTotal = 512940,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 12940,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 512940,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-05-02"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 243,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 8631,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 508631,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-05-01"),
          numberOfDays = 121,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 4309,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504309,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Debt crossing a leap year") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2021-04-01",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        interestDueCallTotal = 48710,
        totalAmountIntTotal = 548710,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 48710,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 548710,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3,
          interestDueDailyAccrual = 41,
          interestDueWindow = 9493,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 509493,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 498,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 22171,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 522171,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 3951,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503951,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueDailyAccrual = 37,
          interestDueWindow = 300,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500300,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-12-31"),
          numberOfDays = 269,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 9554,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 509554,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2021-01-01"),
          periodTo = LocalDate.parse("2021-04-01"),
          numberOfDays = 91,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3241,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 503241,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2019-12-16"),
            interestRequestedTo = "2020-05-05",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-05-03"
                )
              )
            ),
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
        combinedDailyAccrual = 28,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 5933,
        totalAmountIntTotal = 405933,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 28,
        interestDueDutyTotal = 5933,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 405933,
        unpaidAmountDuty = 400000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2019-12-16"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 15,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 133,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100133,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 8,
          interestDueWindow = 790,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100790,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueDailyAccrual = 7,
          interestDueWindow = 60,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100060,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-05-03"),
          numberOfDays = 27,
          interestRate = 2.6,
          interestDueDailyAccrual = 7,
          interestDueWindow = 191,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100191,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2019-12-16"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 15,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 534,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400534,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 3161,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 403161,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 8,
          interestRate = 2.75,
          interestDueDailyAccrual = 30,
          interestDueWindow = 240,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400240,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-04-07"),
          periodTo = LocalDate.parse("2020-05-05"),
          numberOfDays = 29,
          interestRate = 2.6,
          interestDueDailyAccrual = 28,
          interestDueWindow = 824,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400824,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Debt spanning multiple leap years") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2011-01-01"),
            interestRequestedTo = "2017-02-22",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        combinedDailyAccrual = 37,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 91506,
        totalAmountIntTotal = 591506,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 37,
        interestDueDutyTotal = 91506,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 591506,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2011-01-01"),
          periodTo = LocalDate.parse("2011-12-31"),
          numberOfDays = 364,
          interestRate = 3,
          interestDueDailyAccrual = 41,
          interestDueWindow = 14958,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 514958,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2012-01-01"),
          periodTo = LocalDate.parse("2012-12-31"),
          numberOfDays = 366,
          interestRate = 3,
          interestDueDailyAccrual = 40,
          interestDueWindow = 15000,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 515000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2013-01-01"),
          periodTo = LocalDate.parse("2015-12-31"),
          numberOfDays = 1095,
          interestRate = 3,
          interestDueDailyAccrual = 41,
          interestDueWindow = 45000,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 545000,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2016-01-01"),
          periodTo = LocalDate.parse("2016-08-22"),
          numberOfDays = 235,
          interestRate = 3,
          interestDueDailyAccrual = 40,
          interestDueWindow = 9631,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 509631,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2016-08-23"),
          periodTo = LocalDate.parse("2016-12-31"),
          numberOfDays = 131,
          interestRate = 2.75,
          interestDueDailyAccrual = 37,
          interestDueWindow = 4921,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 504921,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2017-01-01"),
          periodTo = LocalDate.parse("2017-02-22"),
          numberOfDays = 53,
          interestRate = 2.75,
          interestDueDailyAccrual = 37,
          interestDueWindow = 1996,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 501996,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Interest rate changes from 3% to 3.25%") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2017-12-01"),
            interestRequestedTo = "2019-03-31",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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
        combinedDailyAccrual = 44,
        unpaidAmountTotal = 500000,
        interestDueCallTotal = 20695,
        totalAmountIntTotal = 520695,
        amountOnIntDueTotal = 500000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculation(
        debtItemChargeId = "123",
        interestDueDailyAccrual = 44,
        interestDueDutyTotal = 20695,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 520695,
        unpaidAmountDuty = 500000,
        calculationWindows = List.empty[FCCalculationWindow]
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 262,
          interestRate = 3,
          interestDueDailyAccrual = 41,
          interestDueWindow = 10767,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 510767,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueDailyAccrual = 44,
          interestDueWindow = 9928,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 509928,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("FC Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2019-01-01"),
            interestRequestedTo = "2020-03-31",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-02-01"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2020-02-01"
                )
              )
            ),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val expectedSummary = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 22,
        unpaidAmountTotal = 300000,
        interestDueCallTotal = 19188,
        totalAmountIntTotal = 319188,
        amountOnIntDueTotal = 300000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2019-01-01"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 364,
          interestRate = 3.25,
          interestDueDailyAccrual = 17,
          interestDueWindow = 6482,
          amountOnIntDueWindow = 200000,
          unpaidAmountWindow = 206482,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-02-01"),
          numberOfDays = 32,
          interestRate = 3.25,
          interestDueDailyAccrual = 17,
          interestDueWindow = 568,
          amountOnIntDueWindow = 200000,
          unpaidAmountWindow = 200568,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2019-01-01"),
          periodTo = LocalDate.parse("2019-12-31"),
          numberOfDays = 364,
          interestRate = 3.25,
          interestDueDailyAccrual = 26,
          interestDueWindow = 9723,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 309723,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-01-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 89,
          interestRate = 3.25,
          interestDueDailyAccrual = 26,
          interestDueWindow = 2370,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 302370,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-03-31"),
          numberOfDays = 2,
          interestRate = 2.75,
          interestDueDailyAccrual = 22,
          interestDueWindow = 45,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 300045,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

    }

    Scenario("FC Interest rate changes from 3% to 3.25% after a payment is made") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2019-03-31",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-03-15"
                )
              )
            ),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val expectedSummary = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 35,
        unpaidAmountTotal = 400000,
        interestDueCallTotal = 16136,
        totalAmountIntTotal = 416136,
        amountOnIntDueTotal = 400000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-03-15"),
          numberOfDays = 73,
          interestRate = 3.0,
          interestDueDailyAccrual = 8,
          interestDueWindow = 600,
          amountOnIntDueWindow = 100000,
          unpaidAmountWindow = 100600,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3.0,
          interestDueDailyAccrual = 32,
          interestDueWindow = 7594,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 407594,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueDailyAccrual = 35,
          interestDueWindow = 7942,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 407942,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

    }

    Scenario("FC Interest rate changes from 3% to 3.25% with 2 payments on same date") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2018-01-01"),
            interestRequestedTo = "2019-03-31",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-09-01"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2018-09-01"
                )
              )
            ),
            customerPostcodes = Some(List.empty[FCCustomerPostCode])
          )
        )
      )
      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the fc ifs service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the fc ifs service will return a total debts summary of")
      val expectedSummary = FCDebtCalculationsSummary(
        dateOfCalculation = Some(LocalDate.now()),
        combinedDailyAccrual = 26,
        unpaidAmountTotal = 300000,
        interestDueCallTotal = 15661,
        totalAmountIntTotal = 315661,
        amountOnIntDueTotal = 300000,
        debtCalculations = List.empty[FCDebtCalculation]
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3,
          interestDueDailyAccrual = 16,
          interestDueWindow = 3797,
          amountOnIntDueWindow = 200000,
          unpaidAmountWindow = 203797,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2018-09-01"),
          numberOfDays = 12,
          interestRate = 3.25,
          interestDueDailyAccrual = 17,
          interestDueWindow = 213,
          amountOnIntDueWindow = 200000,
          unpaidAmountWindow = 200213,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-01-01"),
          periodTo = LocalDate.parse("2018-08-20"),
          numberOfDays = 231,
          interestRate = 3,
          interestDueDailyAccrual = 24,
          interestDueWindow = 5695,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 305695,
          suppressionApplied = None
        ),
        FCCalculationWindow(
          periodFrom = LocalDate.parse("2018-08-21"),
          periodTo = LocalDate.parse("2019-03-31"),
          numberOfDays = 223,
          interestRate = 3.25,
          interestDueDailyAccrual = 26,
          interestDueWindow = 5956,
          amountOnIntDueWindow = 300000,
          unpaidAmountWindow = 305956,
          suppressionApplied = None
        )
      )
      theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

    }

    Scenario("FC 2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts") {
      context =>
        Given("a fc debt calculation")
        val ifsRequest = FCDebtCalculationRequest(
          debtItems = List(
            FCDebtItems(
              debtItemChargeId = Some("123"),
              originalAmount = 500000,
              interestIndicator = "Y",
              periodEnd = "2018-04-06",
              interestStartDate = Some("2018-01-01"),
              interestRequestedTo = "2019-03-31",
              breathingSpaces = Some(List.empty[BreathingSpaces]),
              paymentHistory = Some(
                List(
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2019-03-15"),
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2019-04-15")
                )
              ),
              customerPostcodes = Some(List.empty[FCCustomerPostCode])
            ),
            FCDebtItems(
              debtItemChargeId = Some("456"),
              originalAmount = 500000,
              interestIndicator = "Y",
              periodEnd = "2018-04-06",
              interestStartDate = Some("2018-01-16"),
              interestRequestedTo = "2019-04-14",
              breathingSpaces = Some(List.empty[BreathingSpaces]),
              paymentHistory = Some(
                List(
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2019-01-20"),
                  PaymentHistory(paymentAmount = 100000, paymentDate = "2019-03-10")
                )
              ),
              customerPostcodes = Some(List.empty[FCCustomerPostCode])
            )
          )
        )
        aFcDebtCalculation(context, ifsRequest)

        When("the debt item is sent to the fc ifs service")
        theDebtItemIsSentToTheFcIfsService(context)

        Then("the fc ifs service will return a total debts summary of")
        val expectedSummary = FCDebtCalculationsSummary(
          dateOfCalculation = Some(LocalDate.now()),
          combinedDailyAccrual = 52,
          unpaidAmountTotal = 600000,
          interestDueCallTotal = 37775,
          totalAmountIntTotal = 637775,
          amountOnIntDueTotal = 600000,
          debtCalculations = List.empty[FCDebtCalculation]
        )
        theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

        And("the 1st fc debt summary will contain")
        val expected1stDebtCalculation = FCDebtCalculation(
          debtItemChargeId = "123",
          interestDueDailyAccrual = 26,
          interestDueDutyTotal = 19409,
          amountOnIntDueDuty = 300000,
          totalAmountIntDuty = 319409,
          unpaidAmountDuty = 300000,
          calculationWindows = List.empty[FCCalculationWindow]
        )
        theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculation)

        And("the 1st fc debt summary will have calculation windows")
        val expected1stCalculationWindows = List(
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1898,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101898,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-03-15"),
            numberOfDays = 207,
            interestRate = 3.25,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1843,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101843,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1898,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101898,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-04-15"),
            numberOfDays = 238,
            interestRate = 3.25,
            interestDueDailyAccrual = 8,
            interestDueWindow = 2119,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 102119,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 231,
            interestRate = 3.0,
            interestDueDailyAccrual = 24,
            interestDueWindow = 5695,
            amountOnIntDueWindow = 300000,
            unpaidAmountWindow = 305695,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-03-31"),
            numberOfDays = 223,
            interestRate = 3.25,
            interestDueDailyAccrual = 26,
            interestDueWindow = 5956,
            amountOnIntDueWindow = 300000,
            unpaidAmountWindow = 305956,
            suppressionApplied = None
          )
        )
        theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

        And("the 2nd fc debt summary will contain")
        val expected2ndDebtCalculation = FCDebtCalculation(
          debtItemChargeId = "456",
          interestDueDailyAccrual = 26,
          interestDueDutyTotal = 18366,
          amountOnIntDueDuty = 300000,
          totalAmountIntDuty = 318366,
          unpaidAmountDuty = 300000,
          calculationWindows = List.empty[FCCalculationWindow]
        )
        theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculation)

        And("the 2nd fc debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1775,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101775,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-01-20"),
            numberOfDays = 153,
            interestRate = 3.25,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1362,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101362,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1775,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101775,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-03-10"),
            numberOfDays = 202,
            interestRate = 3.25,
            interestDueDailyAccrual = 8,
            interestDueWindow = 1798,
            amountOnIntDueWindow = 100000,
            unpaidAmountWindow = 101798,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-01-16"),
            periodTo = LocalDate.parse("2018-08-20"),
            numberOfDays = 216,
            interestRate = 3.0,
            interestDueDailyAccrual = 24,
            interestDueWindow = 5326,
            amountOnIntDueWindow = 300000,
            unpaidAmountWindow = 305326,
            suppressionApplied = None
          ),
          FCCalculationWindow(
            periodFrom = LocalDate.parse("2018-08-21"),
            periodTo = LocalDate.parse("2019-04-14"),
            numberOfDays = 237,
            interestRate = 3.25,
            interestDueDailyAccrual = 26,
            interestDueWindow = 6330,
            amountOnIntDueWindow = 300000,
            unpaidAmountWindow = 306330,
            suppressionApplied = None
          )
        )
        theFcDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)

    }

    Scenario("FC Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate") { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          FCDebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2018-04-06",
            interestStartDate = Some("2020-04-10"),
            interestRequestedTo = "2020-03-31",
            breathingSpaces = Some(List.empty[BreathingSpaces]),
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

  }
}
