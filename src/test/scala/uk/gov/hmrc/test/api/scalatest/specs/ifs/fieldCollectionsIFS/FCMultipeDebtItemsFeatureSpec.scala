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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(61),
        unpaidAmountTotal = Some(700000),
        interestDueCallTotal = Some(8052),
        totalAmountIntTotal = Some(708052),
        amountOnIntDueTotal = Some(700000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4874),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(404874),
        unpaidAmountDuty = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(61),
        unpaidAmountTotal = Some(700000),
        interestDueCallTotal = Some(7852),
        totalAmountIntTotal = Some(707852),
        amountOnIntDueTotal = Some(700000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4674),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(404674),
        unpaidAmountDuty = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(4674),
        totalAmountIntTotal = Some(404674),
        amountOnIntDueTotal = Some(400000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4674),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(404674),
        unpaidAmountDuty = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(0),
        totalAmountIntTotal = Some(400000),
        amountOnIntDueTotal = Some(400000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(400000),
        unpaidAmountDuty = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(4674),
        totalAmountIntTotal = Some(404674),
        amountOnIntDueTotal = Some(400000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4674),
        amountOnIntDueDuty = Some(400000),
        totalAmountIntDuty = Some(404674),
        unpaidAmountDuty = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(44),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(12078),
        totalAmountIntTotal = Some(512078),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(12078),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(512078),
        unpaidAmountDuty = Some(500000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        unpaidAmountTotal = Some(0),
        interestDueCallTotal = Some(2181),
        totalAmountIntTotal = Some(2181),
        amountOnIntDueTotal = Some(0)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(2181),
        amountOnIntDueDuty = Some(0),
        totalAmountIntDuty = Some(2181),
        unpaidAmountDuty = Some(0)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-12-16")),
          periodTo = Some(LocalDate.parse("2019-02-03")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(2181),
          unpaidAmountWindow = Some(502181)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(37),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(35727),
        totalAmountIntTotal = Some(535727),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(37),
        interestDueDutyTotal = Some(35727),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(535727),
        unpaidAmountDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(231),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(9493),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(509493)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(498),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(22171),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(522171)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(3951),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(503951)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-01")),
          numberOfDays = Some(3),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(112),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500112)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(12940),
        totalAmountIntTotal = Some(512940),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(12940),
        unpaidAmountDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-05-02")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          numberOfDays = Some(243),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(8631),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-05-01")),
          numberOfDays = Some(121),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(4309),
          amountOnIntDueWindow = Some(500000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(48710)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(48710),
        unpaidAmountDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(231),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(9493),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(498),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(22171),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(3951),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(300),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-12-31")),
          numberOfDays = Some(269),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(9554),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-01-01")),
          periodTo = Some(LocalDate.parse("2021-04-01")),
          numberOfDays = Some(91),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3241),
          amountOnIntDueWindow = Some(500000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(5933),
        totalAmountIntTotal = Some(405933),
        amountOnIntDueTotal = Some(400000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(28),
        interestDueDutyTotal = Some(5933),
        unpaidAmountDuty = Some(400000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(15),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(133),
          amountOnIntDueWindow = Some(100000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(790),
          amountOnIntDueWindow = Some(100000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(60),
          amountOnIntDueWindow = Some(100000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-05-03")),
          numberOfDays = Some(27),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(191),
          amountOnIntDueWindow = Some(100000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(15),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(534),
          amountOnIntDueWindow = Some(400000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3161),
          amountOnIntDueWindow = Some(400000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(30),
          interestDueWindow = Some(240),
          amountOnIntDueWindow = Some(400000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-05-05")),
          numberOfDays = Some(29),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(824),
          amountOnIntDueWindow = Some(400000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(37),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(91506)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(37)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2011-01-01")),
          periodTo = Some(LocalDate.parse("2011-12-31")),
          numberOfDays = Some(364),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(14958),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2012-01-01")),
          periodTo = Some(LocalDate.parse("2012-12-31")),
          numberOfDays = Some(366),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(40),
          interestDueWindow = Some(15000),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2013-01-01")),
          periodTo = Some(LocalDate.parse("2015-12-31")),
          numberOfDays = Some(1095),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(45000),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2016-01-01")),
          periodTo = Some(LocalDate.parse("2016-08-22")),
          numberOfDays = Some(235),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(40),
          interestDueWindow = Some(9631),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2016-08-23")),
          periodTo = Some(LocalDate.parse("2016-12-31")),
          numberOfDays = Some(131),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(4921),
          amountOnIntDueWindow = Some(500000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2017-01-01")),
          periodTo = Some(LocalDate.parse("2017-02-22")),
          numberOfDays = Some(53),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(1996),
          amountOnIntDueWindow = Some(500000)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(44),
        unpaidAmountTotal = Some(500000),
        interestDueCallTotal = Some(20695),
        totalAmountIntTotal = Some(520695),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(44),
        interestDueDutyTotal = Some(20695),
        amountOnIntDueDuty = Some(500000),
        totalAmountIntDuty = Some(520695),
        unpaidAmountDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2017-12-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          numberOfDays = Some(262),
          interestRate = Some(3),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(10767),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(510767)
        ),
        FCCalculationWindowExpected(
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
      val expectedSummary = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(22),
        unpaidAmountTotal = Some(300000),
        interestDueCallTotal = Some(19188),
        totalAmountIntTotal = Some(319188),
        amountOnIntDueTotal = Some(300000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-01-01")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(364),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(6482),
          amountOnIntDueWindow = Some(200000),
          unpaidAmountWindow = Some(206482)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-02-01")),
          numberOfDays = Some(32),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(568),
          amountOnIntDueWindow = Some(200000),
          unpaidAmountWindow = Some(200568)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-01-01")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(364),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(9723),
          amountOnIntDueWindow = Some(300000),
          unpaidAmountWindow = Some(309723)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(26),
          interestDueWindow = Some(2370),
          amountOnIntDueWindow = Some(300000),
          unpaidAmountWindow = Some(302370)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-03-31")),
          numberOfDays = Some(2),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(22),
          interestDueWindow = Some(45),
          amountOnIntDueWindow = Some(300000),
          unpaidAmountWindow = Some(300045)
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
      val expectedSummary = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        unpaidAmountTotal = Some(400000),
        interestDueCallTotal = Some(16136)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-03-15")),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(8),
          amountOnIntDueWindow = Some(100000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(32),
          amountOnIntDueWindow = Some(400000)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-31")),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          amountOnIntDueWindow = Some(400000)
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
      val expectedSummary = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(26),
        unpaidAmountTotal = Some(300000),
        interestDueCallTotal = Some(15661),
        totalAmountIntTotal = Some(315661)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st fc debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3),
          interestDueWindow = Some(3797)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2018-09-01")),
          interestRate = Some(3.25),
          interestDueWindow = Some(213)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-01-01")),
          periodTo = Some(LocalDate.parse("2018-08-20")),
          interestRate = Some(3),
          interestDueWindow = Some(5695)
        ),
        FCCalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2018-08-21")),
          periodTo = Some(LocalDate.parse("2019-03-31")),
          interestRate = Some(3.25),
          interestDueWindow = Some(5956)
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
        val expectedSummary = FCDebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(52),
          interestDueCallTotal = Some(37775),
          totalAmountIntTotal = Some(637775),
          amountOnIntDueTotal = Some(600000)
        )
        theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

        And("the 1st fc debt summary will contain")
        val expected1stDebtCalculation = FCDebtCalculationExpected(
          interestDueDailyAccrual = Some(26),
          interestDueDutyTotal = Some(19409),
          amountOnIntDueDuty = Some(300000)
        )
        theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculation)

        And("the 1st fc debt summary will have calculation windows")
        val expected1stCalculationWindows = List(
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1898)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-15")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1843)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1898)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-04-15")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(2119)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-01")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(24),
            interestDueWindow = Some(5695)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-31")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(26),
            interestDueWindow = Some(5956)
          )
        )
        theFcDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

        And("the 2nd fc debt summary will contain")
        val expected2ndDebtCalculation = FCDebtCalculationExpected(
          interestDueDailyAccrual = Some(26),
          interestDueDutyTotal = Some(18366),
          amountOnIntDueDuty = Some(300000)
        )
        theFcDebtSummaryWillContain(context, 2, expected2ndDebtCalculation)

        And("the 2nd fc debt summary will have calculation windows")
        val expected2ndCalculationWindows = List(
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1775)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-01-20")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1362)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1775)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-03-10")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(8),
            interestDueWindow = Some(1798)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-01-16")),
            periodTo = Some(LocalDate.parse("2018-08-20")),
            interestRate = Some(3.0),
            interestDueDailyAccrual = Some(24),
            interestDueWindow = Some(5326)
          ),
          FCCalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2018-08-21")),
            periodTo = Some(LocalDate.parse("2019-04-14")),
            interestRate = Some(3.25),
            interestDueDailyAccrual = Some(26),
            interestDueWindow = Some(6330)
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
      val FCDebtCalculationSummaryResponse = FCDebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        totalAmountIntTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theFcIfsServiceWillReturnATotalDebtsSummaryOf(context, FCDebtCalculationSummaryResponse)

      And("the 1st fc debt summary will contain")
      val expected1stDebtCalculations = FCDebtCalculationExpected(
        interestDueDailyAccrual = Some(0),
        totalAmountIntDuty = Some(500000)
      )
      theFcDebtSummaryWillContain(context, 1, expected1stDebtCalculations)

      And("the 1st fc debt summary will not have any calculation windows")
      theFcDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)

    }

  }
}
