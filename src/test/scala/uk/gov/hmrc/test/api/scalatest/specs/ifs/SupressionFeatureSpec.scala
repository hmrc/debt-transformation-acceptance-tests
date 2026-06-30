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
import uk.gov.hmrc.test.api.models.ifs.{CustomerPostCode, DebtCalculationRequest, DebtItem, PaymentHistory}
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.scalatest.steps.context.{InterestForecastingContext, SuppressionRulesContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class SupressionFeatureSpec
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

  Feature("Suppression") {
    val suppressionContext = SuppressionRulesContext()

    Scenario(
      "Suppression, 2 payments on different dates during suppression",
      suppression,
      DTD_2790
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = Some("2024-03-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("EC2M 2LS"),
            mainTrans = Some("1535"),
            subTrans = Some("1000"),
            checkPeriodEnd = Some(true)
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
            periodEnd = Some("2024-03-06"),
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2024-03-10"
                ),
                PaymentHistory(
                  paymentAmount = 50000,
                  paymentDate = "2024-03-15"
                )
              )
            )
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
        combinedDailyAccrual = 62,
        interestDueCallTotal = 6713,
        amountIntTotal = 356713,
        amountOnIntDueTotal = 350000,
        unpaidAmountTotal = 350000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 108,
        interestDueDailyAccrual = 62,
        interestDueDutyTotal = 6713,
        amountOnIntDueDuty = 350000,
        totalAmountIntDuty = 356713,
        unpaidAmountDuty = 350000,
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
          periodTo = LocalDate.parse("2024-03-15"),
          numberOfDays = 14,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 50000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 50000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-10"),
          numberOfDays = 9,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 350000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 108,
          interestRate = 6.5,
          interestDueWindow = 6713,
          interestDueDailyAccrual = 62,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 356713,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, 2 debts 2 payments on same day for one of the debts"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = Some("2024-03-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = Some("1535"),
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
            interestStartDate = Some("2024-03-01"),
            interestRequestedTo = "2024-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2024-03-20"
                ),
                PaymentHistory(
                  paymentAmount = 50000,
                  paymentDate = "2024-03-20"
                )
              )
            )
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 400000,
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
        combinedDailyAccrual = 133,
        interestDueCallTotal = 14385,
        amountIntTotal = 764385,
        amountOnIntDueTotal = 750000,
        unpaidAmountTotal = 750000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 108,
        interestDueDailyAccrual = 62,
        interestDueDutyTotal = 6713,
        amountOnIntDueDuty = 350000,
        totalAmountIntDuty = 356713,
        unpaidAmountDuty = 350000,
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
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 150000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 150000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 350000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 108,
          interestRate = 6.5,
          interestDueWindow = 6713,
          interestDueDailyAccrual = 62,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 356713,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = true,
        numberOfChargeableDays = 108,
        interestDueDailyAccrual = 71,
        interestDueDutyTotal = 7672,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 407672,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400000,
          breathingSpaceApplied = false,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 108,
          interestRate = 6.5,
          interestDueDailyAccrual = 71,
          interestDueWindow = 7672,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 407672,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario(
      "Suppression, 2 payments after suppression dates"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = Some("2024-03-20"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
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
            interestStartDate = Some("2024-03-01"),
            interestRequestedTo = "2024-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2024-05-01"
                ),
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2024-05-20"
                )
              )
            )
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
        combinedDailyAccrual = 53,
        interestDueCallTotal = 7582,
        amountIntTotal = 307582,
        amountOnIntDueTotal = 300000,
        unpaidAmountTotal = 300000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 211,
        interestDueDailyAccrual = 53,
        interestDueDutyTotal = 7582,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 307582,
        unpaidAmountDuty = 300000,
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
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-05-01"),
          numberOfDays = 42,
          interestRate = 6.5,
          interestDueWindow = 745,
          interestDueDailyAccrual = 17,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100745,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-05-20"),
          numberOfDays = 61,
          interestRate = 6.5,
          interestDueWindow = 1083,
          interestDueDailyAccrual = 17,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 101083,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-20"),
          numberOfDays = 19,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 300000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-21"),
          periodTo = LocalDate.parse("2024-07-06"),
          numberOfDays = 108,
          interestRate = 6.5,
          interestDueWindow = 5754,
          interestDueDailyAccrual = 53,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 305754,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, open ended suppression no payment history"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2020-04-04",
            dateTo = Some("9999-12-31"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
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
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-07-06",
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
        interestBearing = true,
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
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-07-06"),
          numberOfDays = 127,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, open ended suppression with payment history"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2020-04-04",
            dateTo = Some("9999-12-31"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
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
            interestStartDate = Some("2021-03-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 200000,
                  paymentDate = "2021-04-20"
                )
              )
            )
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
        combinedDailyAccrual = 0,
        interestDueCallTotal = 0,
        amountIntTotal = 300000,
        amountOnIntDueTotal = 300000,
        unpaidAmountTotal = 300000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 0,
        interestDueDailyAccrual = 0,
        interestDueDutyTotal = 0,
        amountOnIntDueDuty = 300000,
        totalAmountIntDuty = 300000,
        unpaidAmountDuty = 300000,
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
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-04-20"),
          numberOfDays = 50,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 200000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 200000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-01"),
          periodTo = LocalDate.parse("2021-07-06"),
          numberOfDays = 127,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 300000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 300000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)
    }

    Scenario(
      "Suppression, 2 debts, 1 matching on period end"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2021-04-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = Some("1535"),
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
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            periodEnd = Some("2021-04-04"),
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(
              List(
                PaymentHistory(
                  paymentAmount = 100000,
                  paymentDate = "2021-03-20"
                ),
                PaymentHistory(
                  paymentAmount = 50000,
                  paymentDate = "2021-04-20"
                )
              )
            )
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            periodEnd = Some("2021-12-21"),
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List.empty
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 59,
        interestDueCallTotal = 8056,
        amountIntTotal = 858056,
        amountOnIntDueTotal = 850000,
        unpaidAmountTotal = 850000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 232,
        interestDueDailyAccrual = 24,
        interestDueDutyTotal = 3641,
        amountOnIntDueDuty = 350000,
        totalAmountIntDuty = 353641,
        unpaidAmountDuty = 350000,
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
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-03-20"),
          numberOfDays = 47,
          interestRate = 2.6,
          interestDueWindow = 334,
          interestDueDailyAccrual = 7,
          amountOnIntDueWindow = 100000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 100334,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-04-03"),
          numberOfDays = 61,
          interestRate = 2.6,
          interestDueWindow = 217,
          interestDueDailyAccrual = 3,
          amountOnIntDueWindow = 50000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 50217,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-04-04"),
          periodTo = LocalDate.parse("2021-04-20"),
          numberOfDays = 17,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 50000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 50000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-04-03"),
          numberOfDays = 61,
          interestRate = 2.6,
          interestDueWindow = 1520,
          interestDueDailyAccrual = 24,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 351520,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-04-04"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 31,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 350000,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-05-05"),
          periodTo = LocalDate.parse("2021-07-06"),
          numberOfDays = 63,
          interestRate = 2.6,
          interestDueWindow = 1570,
          interestDueDailyAccrual = 24,
          amountOnIntDueWindow = 350000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 351570,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = true,
        numberOfChargeableDays = 124,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 4415,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 504415,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-04-03"),
          numberOfDays = 61,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 2172,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 502172,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-04-04"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 31,
          interestRate = 0.0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = singleSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-05-05"),
          periodTo = LocalDate.parse("2021-07-06"),
          numberOfDays = 63,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 2243,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 502243,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

    Scenario(
      "Suppression applied by all criteria on 2 debt items.",
      DTD_3325
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        suppressions = List(
          SuppressionInformation(
            dateFrom = "2022-01-07",
            dateTo = Some("2022-01-20"),
            reason = "SUBTRANS",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2024-03-07",
            dateTo = Some("2024-04-20"),
            reason = "MAINTRANS",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = Some("1535"),
            subTrans = None,
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2022-03-07",
            dateTo = Some("2022-03-20"),
            reason = "PERIODEND",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = Some(true)
          ),
          SuppressionInformation(
            dateFrom = "2022-04-07",
            dateTo = Some("2022-04-20"),
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
            interestStartDate = Some("2022-01-01"),
            interestRequestedTo = "2022-07-06",
            periodEnd = Some("2022-03-09"),
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          ),
          DebtItem(
            debtID = Some("456"),
            originalAmount = 400000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2024-03-01"),
            interestRequestedTo = "2024-07-06",
            periodEnd = None,
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2022-01-01"
          )
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 122,
        interestDueCallTotal = 12033,
        amountIntTotal = 912033,
        amountOnIntDueTotal = 900000,
        unpaidAmountTotal = 900000,
        debtCalculations = List.empty
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 144,
        interestDueDailyAccrual = 51,
        interestDueDutyTotal = 6209,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 506209,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val subTransSuppression        = Some(
        SuppressionApplied(
          reason = "SUBTRANS",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val periodendSuppression       = Some(
        SuppressionApplied(
          reason = "PERIODEND",
          description = "COVID",
          code = "Converted from new suppression style"
        )
      )
      val legislativeSuppression     = Some(
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
          periodTo = LocalDate.parse("2022-01-20"),
          numberOfDays = 14,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = subTransSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-01-21"),
          periodTo = LocalDate.parse("2022-02-20"),
          numberOfDays = 31,
          interestRate = 2.75,
          interestDueWindow = 1167,
          interestDueDailyAccrual = 37,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501167,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-02-21"),
          periodTo = LocalDate.parse("2022-03-06"),
          numberOfDays = 14,
          interestRate = 3.0,
          interestDueWindow = 575,
          interestDueDailyAccrual = 41,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500575,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-03-07"),
          periodTo = LocalDate.parse("2022-03-20"),
          numberOfDays = 14,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = periodendSuppression,
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
          periodTo = LocalDate.parse("2022-04-06"),
          numberOfDays = 2,
          interestRate = 3.25,
          interestDueWindow = 89,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500089,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-07"),
          periodTo = LocalDate.parse("2022-04-20"),
          numberOfDays = 14,
          interestRate = 0.0,
          interestDueWindow = 0,
          interestDueDailyAccrual = 0,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 500000,
          suppressionApplied = legislativeSuppression,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2022-04-21"),
          periodTo = LocalDate.parse("2022-05-23"),
          numberOfDays = 33,
          interestRate = 3.25,
          interestDueWindow = 1469,
          interestDueDailyAccrual = 44,
          amountOnIntDueWindow = 500000,
          breathingSpaceApplied = false,
          unpaidAmountWindow = 501469,
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

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("456"),
        interestBearing = true,
        numberOfChargeableDays = 82,
        interestDueDailyAccrual = 71,
        interestDueDutyTotal = 5824,
        amountOnIntDueDuty = 400000,
        totalAmountIntDuty = 405824,
        unpaidAmountDuty = 400000,
        interestOnlyIndicator = false,
        calculationWindows = Nil
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2024-03-01"),
          periodTo = LocalDate.parse("2024-03-06"),
          numberOfDays = 5,
          interestRate = 6.5,
          interestDueDailyAccrual = 71,
          interestDueWindow = 355,
          amountOnIntDueWindow = 400000,
          unpaidAmountWindow = 400355,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }
  }
}
