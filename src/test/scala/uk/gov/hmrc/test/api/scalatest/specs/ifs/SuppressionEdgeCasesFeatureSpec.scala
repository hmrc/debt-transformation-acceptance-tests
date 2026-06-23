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
import uk.gov.hmrc.test.api.models.ifs.{CustomerPostCode, DebtCalculationRequest, DebtItem}
import uk.gov.hmrc.test.api.models._
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
      "Suppression, interest rate change during suppression",suppression, DTD_2790
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-04-05"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2020-01-05"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 51,
        interestDueCallTotal = 4251,
        amountIntTotal = 504251,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 92,
        interestDueDailyAccrual = 51,
        interestDueDutyTotal = 4251,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 504251,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-01"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 3,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-04-05"),
          numberOfDays = 1,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          LocalDate.parse("2022-04-06"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 48,
          interestRate = 3.25,
          interestDueWindow = 2136,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502136,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 51,
        interestDueCallTotal = 5706,
        amountIntTotal = 505706,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 128,
        interestDueDailyAccrual = 51,
        interestDueDutyTotal = 5706,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 505706,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-01"),
          periodTo = LocalDate.parse("2022-01-06"),
          numberOfDays = 5,
          interestRate = 2.6,
          interestDueWindow = 178,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500178,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-07"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 45,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-03-05"),
          numberOfDays = 13,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-06"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 30,
          interestRate = 3.0,
          interestDueWindow = 1232,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501232,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueWindow = 2181,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502181,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-07-04"),
          numberOfDays = 42,
          interestRate = 3.5,
          interestDueWindow = 2013,
          interestDueDailyAccrual = 47,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502013,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 51,
        interestDueCallTotal = 5090,
        amountIntTotal = 505090,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 113,
        interestDueDailyAccrual = 51,
        interestDueDutyTotal = 5090,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 505090,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val overlappingSuppressions    = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE; LEGISLATIVE",
          description = "COVID; COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-01"),
          periodTo = LocalDate.parse("2022-01-06"),
          numberOfDays = 5,
          interestRate = 2.6,
          interestDueWindow = 178,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500178,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-07"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 45,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = overlappingSuppressions,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-03-05"),
          numberOfDays = 13,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = overlappingSuppressions,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-06"),
          periodTo = LocalDate.parse("2022-03-20"),
          numberOfDays = 15,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-21"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 15,
          interestRate = 3.0,
          interestDueWindow = 616,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500616,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueWindow = 2181,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502181,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-07-04"),
          numberOfDays = 42,
          interestRate = 3.5,
          interestDueWindow = 2013,
          interestDueDailyAccrual = 47,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502013,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-07-05"),
          periodTo = LocalDate.parse("2022-07-06"),
          numberOfDays = 2,
          interestRate = 3.75,
          interestDueWindow = 102,
          interestDueDailyAccrual = 51,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500102,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 51,
        interestDueCallTotal = 5054,
        amountIntTotal = 505054,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 112,
        interestDueDailyAccrual = 51,
        interestDueDutyTotal = 5054,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 505054,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val overlappingSuppressions    = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE; LEGISLATIVE",
          description = "COVID; COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-01"),
          periodTo = LocalDate.parse("2022-01-05"),
          numberOfDays = 4,
          interestRate = 2.6,
          interestDueWindow = 142,
          interestDueDailyAccrual = 35,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500142,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-06"),
          periodTo = LocalDate.parse("2022-01-06"),
          numberOfDays = 1,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-07"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 45,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = overlappingSuppressions,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-03-05"),
          numberOfDays = 13,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = overlappingSuppressions,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-06"),
          periodTo = LocalDate.parse("2022-03-20"),
          numberOfDays = 15,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-21"),
          periodTo = LocalDate.parse("2022-04-04"),
          numberOfDays = 15,
          interestRate = 3.0,
          interestDueWindow = 616,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500616,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-05"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 49,
          interestRate = 3.25,
          interestDueWindow = 2181,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502181,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-05-24"),
          periodTo = LocalDate.parse("2022-07-04"),
          numberOfDays = 42,
          interestRate = 3.5,
          interestDueWindow = 2013,
          interestDueDailyAccrual = 47,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 502013,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-07-05"),
          periodTo = LocalDate.parse("2022-07-06"),
          numberOfDays = 2,
          interestRate = 3.75,
          interestDueWindow = 102,
          interestDueDailyAccrual = 51,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500102,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 88,
        interestDueCallTotal = 6837,
        amountIntTotal = 506837,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 77,
        interestDueDailyAccrual = 88,
        interestDueDutyTotal = 6837,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 506837,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-04-20"),
          numberOfDays = 50,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-04-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 77,
          interestRate = 6.5,
          interestDueWindow = 6837,
          interestDueDailyAccrual = 88,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 506837,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 88,
        interestDueCallTotal = 6837,
        amountIntTotal = 506837,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 77,
        interestDueDailyAccrual = 88,
        interestDueDutyTotal = 6837,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 506837,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-04-01"),
          periodTo = LocalDate.parse("2024-04-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-04-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 77,
          interestRate = 6.5,
          interestDueWindow = 6837,
          interestDueDailyAccrual = 88,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 506837,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
            interestStartDate = Some("2020-03-01"),
            interestRequestedTo = "2020-04-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 37,
        interestDueCallTotal = 1468,
        amountIntTotal = 501468,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 34,
        interestDueDailyAccrual = 37,
        interestDueDutyTotal = 1468,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 501468,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val singleSuppression          = Some(
        SuppressionApplied(
          reason = "LEGISLATIVE",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val expectedCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-01"),
          periodTo = LocalDate.parse("2020-03-29"),
          numberOfDays = 28,
          interestRate = 3.25,
          interestDueWindow = 1243,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501243,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-03-30"),
          periodTo = LocalDate.parse("2020-04-02"),
          numberOfDays = 4,
          interestRate = 2.75,
          interestDueWindow = 150,
          interestDueDailyAccrual = 37,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500150,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-03"),
          periodTo = LocalDate.parse("2020-04-04"),
          numberOfDays = 2,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2020-04-05"),
          periodTo = LocalDate.parse("2020-04-06"),
          numberOfDays = 2,
          interestRate = 2.75,
          interestDueWindow = 75,
          interestDueDailyAccrual = 37,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500075,
          suppressionApplied = None,
          suppressionsApplied = None
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
            suppressionChargeDescription = "SA-Suppression",
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
          CustomerPostCode(
            postCode = "EC2M",
            postCodeDate = "2018-07-06"
          ),
          CustomerPostCode(
            postCode = "EC2M 4QQ",
            postCodeDate = "2019-07-06"
          ),
          CustomerPostCode(
            postCode = "EC2M 4QR",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 500000,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = false,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 500000,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the debt summary will have no calculation windows")
      theDebtSummaryWillNotHaveAnyCalculationWindows(context, 1)
    }
  }
}
