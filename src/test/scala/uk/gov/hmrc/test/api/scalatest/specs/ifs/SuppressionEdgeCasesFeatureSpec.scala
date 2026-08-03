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
import uk.gov.hmrc.test.api.models.ifs.{CustomerPostCode, DebtCalculationRequest, DebtItem}
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.{CalculationWindowExpected, DebtCalculationExpected, DebtCalculationsSummaryExpected, SuppressionAppliedExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.{InterestForecastingContext, SuppressionRulesContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class SuppressionEdgeCasesFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with SuppressionStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Suppression - Edge cases") {
    val suppressionContext = SuppressionRulesContext()

    Scenario(
      "Suppression, interest rate change during suppression",
      suppression,
      DTD_2790
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-04-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = None,
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2022-04-01"),
            interestRequestedTo = "2022-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2020-01-05")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(51),
        interestDueCallTotal = Some(4251),
        amountIntTotal = Some(504251),
        unpaidAmountTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(92),
        interestDueDailyAccrual = Some(51),
        interestDueDutyTotal = Some(4251),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(504251),
        amountOnIntDueDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-01")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(3),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-05")),
          numberOfDays = Some(1),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-06")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(48),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(2136),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502136),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, interest rate change before and after suppression"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2022-01-01"),
            interestRequestedTo = "2022-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2020-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(51),
        interestDueCallTotal = Some(5706),
        amountIntTotal = Some(505706),
        unpaidAmountTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(128),
        interestDueDailyAccrual = Some(51),
        interestDueDutyTotal = Some(5706),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(505706),
        amountOnIntDueDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-01")),
          periodTo = Some(LocalDate.parse("2022-01-06")),
          numberOfDays = Some(5),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(178),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500178),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(45),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-03-05")),
          numberOfDays = Some(13),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-06")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(30),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(1232),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(501232),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(2181),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502181),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-07-04")),
          numberOfDays = Some(42),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(47),
          interestDueWindow = Some(2013),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502013),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, 1 debt, 2 overlapping suppressions that start on same day"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
            subTrans = None,
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-03-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2022-01-01"),
            interestRequestedTo = "2022-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2020-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(51),
        interestDueCallTotal = Some(5090),
        amountIntTotal = Some(505090),
        unpaidAmountTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(113),
        interestDueDailyAccrual = Some(51),
        interestDueDutyTotal = Some(5090),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(505090),
        amountOnIntDueDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-01")),
          periodTo = Some(LocalDate.parse("2022-01-06")),
          numberOfDays = Some(5),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(178),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500178),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(45),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE; LEGISLATIVE"),
              description = Some("COVID; COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-03-05")),
          numberOfDays = Some(13),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE; LEGISLATIVE"),
              description = Some("COVID; COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-06")),
          periodTo = Some(LocalDate.parse("2022-03-20")),
          numberOfDays = Some(15),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-21")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(15),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(616),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500616),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(2181),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502181),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-07-04")),
          numberOfDays = Some(42),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(47),
          interestDueWindow = Some(2013),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502013),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-07-05")),
          periodTo = Some(LocalDate.parse("2022-07-06")),
          numberOfDays = Some(2),
          interestRate = Some(3.75),
          interestDueDailyAccrual = Some(51),
          interestDueWindow = Some(102),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500102),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, 1 debt, 2 overlapping suppressions - starting on different dates"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-03-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2022-01-06",
            dateTo = Some("2022-03-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = None,
            subTrans = Some("1000"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2022-01-01"),
            interestRequestedTo = "2022-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2020-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(51),
        interestDueCallTotal = Some(5054),
        amountIntTotal = Some(505054),
        unpaidAmountTotal = Some(500000),
        amountOnIntDueTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(112),
        interestDueDailyAccrual = Some(51),
        interestDueDutyTotal = Some(5054),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(505054),
        amountOnIntDueDuty = Some(500000),
        interestOnlyIndicator = Some(false)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-01")),
          periodTo = Some(LocalDate.parse("2022-01-05")),
          numberOfDays = Some(4),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(142),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500142),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-06")),
          periodTo = Some(LocalDate.parse("2022-01-06")),
          numberOfDays = Some(1),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(45),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE; LEGISLATIVE"),
              description = Some("COVID; COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-03-05")),
          numberOfDays = Some(13),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE; LEGISLATIVE"),
              description = Some("COVID; COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-06")),
          periodTo = Some(LocalDate.parse("2022-03-20")),
          numberOfDays = Some(15),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-21")),
          periodTo = Some(LocalDate.parse("2022-04-04")),
          numberOfDays = Some(15),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(616),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500616),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(49),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(2181),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502181),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-07-04")),
          numberOfDays = Some(42),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(47),
          interestDueWindow = Some(2013),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(502013),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-07-05")),
          periodTo = Some(LocalDate.parse("2022-07-06")),
          numberOfDays = Some(2),
          interestRate = Some(3.75),
          interestDueDailyAccrual = Some(51),
          interestDueWindow = Some(102),
          amountOnIntDueWindow = Some(500000),
          unpaidAmountWindow = Some(500102),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression period starts on same day as interest start date"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = Some("2024-04-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2024-03-01"),
            interestRequestedTo = "2024-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2019-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(88),
        interestDueCallTotal = Some(6837),
        amountIntTotal = Some(506837),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(77),
        interestDueDailyAccrual = Some(88),
        interestDueDutyTotal = Some(6837),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(506837),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-04-20")),
          numberOfDays = Some(50),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-04-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(77),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(88),
          unpaidAmountWindow = Some(506837),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression period starts before interest start date"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = Some("2024-04-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2024-04-01"),
            interestRequestedTo = "2024-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2019-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(88),
        interestDueCallTotal = Some(6837),
        amountIntTotal = Some(506837),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(77),
        interestDueDailyAccrual = Some(88),
        interestDueDutyTotal = Some(6837),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(506837),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-04-01")),
          periodTo = Some(LocalDate.parse("2024-04-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-04-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(77),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(88),
          unpaidAmountWindow = Some(506837),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, interest rate change before suppression"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2020-04-03",
            dateTo = Some("2020-04-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = None,
            mainTrans = Some("1535"),
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
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2020-03-01"),
            interestRequestedTo = "2020-04-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M", postCodeDate = "2019-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(37),
        interestDueCallTotal = Some(1468),
        amountIntTotal = Some(501468),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(34),
        interestDueDailyAccrual = Some(37),
        interestDueDutyTotal = Some(1468),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(501468),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-01")),
          periodTo = Some(LocalDate.parse("2020-03-29")),
          numberOfDays = Some(28),
          interestRate = Some(3.25),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-03-30")),
          periodTo = Some(LocalDate.parse("2020-04-02")),
          numberOfDays = Some(4),
          interestRate = Some(2.75),
          amountOnIntDueWindow = Some(500000)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-03")),
          periodTo = Some(LocalDate.parse("2020-04-04")),
          numberOfDays = Some(2),
          interestRate = Some(0.0),
          amountOnIntDueWindow = Some(500000),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2020-04-05")),
          periodTo = Some(LocalDate.parse("2020-04-06")),
          numberOfDays = Some(2),
          interestRate = Some(2.75),
          amountOnIntDueWindow = Some(500000)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression on non interest bearing debt"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            postcode = Some("EC2M 2LS"),
            mainTrans = None,
            subTrans = Some("1090"),
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
            subTrans = "1090",
            mainTrans = "1520",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(postCode = "EC2M", postCodeDate = "2018-07-06"),
          CustomerPostCode(postCode = "EC2M 4QQ", postCodeDate = "2019-07-06"),
          CustomerPostCode(postCode = "EC2M 4QR", postCodeDate = "2020-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(0),
        interestDueCallTotal = Some(0),
        amountIntTotal = Some(500000),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(false),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(500000),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }

  }
}
