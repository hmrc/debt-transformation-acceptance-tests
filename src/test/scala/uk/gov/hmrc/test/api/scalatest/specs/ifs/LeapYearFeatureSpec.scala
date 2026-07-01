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
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class LeapYearFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Leap years") {

    Scenario("2.Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made.") { context =>
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
                  paymentDate = "2020-05-03"
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
      val expectedSummary = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(28),
        interestDueCallTotal = Some(5933),
        amountIntTotal = Some(405933),
        amountOnIntDueTotal = Some(400000),
        unpaidAmountTotal = Some(400000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedSummary)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(280),
        interestDueDailyAccrual = Some(28),
        interestDueDutyTotal = Some(5933),
        unpaidAmountDuty = Some(400000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(15),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(8),
          interestDueWindow = Some(133),
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
          periodTo = Some(LocalDate.parse("2020-05-03")),
          numberOfDays = Some(27),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(191),
          amountOnIntDueWindow = Some(100000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2019-12-16")),
          periodTo = Some(LocalDate.parse("2019-12-31")),
          numberOfDays = Some(15),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(534),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-01-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(89),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(3161),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(8),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(30),
          interestDueWindow = Some(240),
          amountOnIntDueWindow = Some(400000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-07")),
          periodTo = Some(LocalDate.parse("2020-05-05")),
          numberOfDays = Some(29),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(28),
          interestDueWindow = Some(824),
          amountOnIntDueWindow = Some(400000)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

  }
}
