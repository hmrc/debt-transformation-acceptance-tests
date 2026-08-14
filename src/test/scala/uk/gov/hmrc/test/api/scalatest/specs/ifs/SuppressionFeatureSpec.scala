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
import uk.gov.hmrc.test.api.models.*
import uk.gov.hmrc.test.api.models.ifs.{CustomerPostCode, DebtCalculationRequest, DebtItem, PaymentHistory}
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder.{CalculationWindowExpected, DebtCalculationExpected, DebtCalculationsSummaryExpected, SuppressionAppliedExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.{InterestForecastingContext, SuppressionRulesContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags.*

import java.time.LocalDate

class SuppressionFeatureSpec
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2024-03-10"),
                PaymentHistory(paymentAmount = 50000, paymentDate = "2024-03-15")
              )
            )
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
        combinedDailyAccrual = Some(62),
        interestDueCallTotal = Some(6713),
        amountIntTotal = Some(356713),
        unpaidAmountTotal = Some(350000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(108),
        interestDueDailyAccrual = Some(62),
        interestDueDutyTotal = Some(6713),
        unpaidAmountDuty = Some(350000),
        totalAmountIntDuty = Some(356713),
        amountOnIntDueDuty = Some(350000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-15")),
          numberOfDays = Some(14),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(50000),
          amountOnIntDueWindow = Some(50000),
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
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-10")),
          numberOfDays = Some(9),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(100000),
          amountOnIntDueWindow = Some(100000),
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
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(350000),
          amountOnIntDueWindow = Some(350000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(108),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(62),
          interestDueWindow = Some(6713),
          unpaidAmountWindow = Some(356713),
          amountOnIntDueWindow = Some(350000),
          breathingSpaceApplied = Some(false)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2024-03-20"),
                PaymentHistory(paymentAmount = 50000, paymentDate = "2024-03-20")
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
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2019-07-06")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(133),
        interestDueCallTotal = Some(14385),
        amountIntTotal = Some(764385),
        unpaidAmountTotal = Some(750000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(108),
        interestDueDailyAccrual = Some(62),
        interestDueDutyTotal = Some(6713),
        unpaidAmountDuty = Some(350000),
        totalAmountIntDuty = Some(356713),
        amountOnIntDueDuty = Some(350000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(150000),
          amountOnIntDueWindow = Some(150000),
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
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(350000),
          amountOnIntDueWindow = Some(350000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(108),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(62),
          interestDueWindow = Some(6713),
          unpaidAmountWindow = Some(356713),
          amountOnIntDueWindow = Some(350000),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(108),
        interestDueDailyAccrual = Some(71),
        interestDueDutyTotal = Some(7672),
        unpaidAmountDuty = Some(400000),
        totalAmountIntDuty = Some(407672),
        amountOnIntDueDuty = Some(400000)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(400000),
          amountOnIntDueWindow = Some(400000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(108),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(71),
          interestDueWindow = Some(7672),
          unpaidAmountWindow = Some(407672),
          amountOnIntDueWindow = Some(400000),
          breathingSpaceApplied = Some(false)
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2024-05-01"),
                PaymentHistory(paymentAmount = 100000, paymentDate = "2024-05-20")
              )
            )
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
        combinedDailyAccrual = Some(53),
        interestDueCallTotal = Some(7582),
        amountIntTotal = Some(307582),
        unpaidAmountTotal = Some(300000),
        amountOnIntDueTotal = Some(300000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(211),
        interestDueDailyAccrual = Some(53),
        interestDueDutyTotal = Some(7582),
        unpaidAmountDuty = Some(300000),
        totalAmountIntDuty = Some(307582),
        amountOnIntDueDuty = Some(300000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(100000),
          amountOnIntDueWindow = Some(100000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-05-01")),
          numberOfDays = Some(42),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(745),
          unpaidAmountWindow = Some(100745),
          amountOnIntDueWindow = Some(100000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(100000),
          amountOnIntDueWindow = Some(100000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-05-20")),
          numberOfDays = Some(61),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(17),
          interestDueWindow = Some(1083),
          unpaidAmountWindow = Some(101083),
          amountOnIntDueWindow = Some(100000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-20")),
          numberOfDays = Some(19),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(300000),
          amountOnIntDueWindow = Some(300000),
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
          periodFrom = Some(LocalDate.parse("2024-03-21")),
          periodTo = Some(LocalDate.parse("2024-07-06")),
          numberOfDays = Some(108),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(53),
          interestDueWindow = Some(5754),
          unpaidAmountWindow = Some(305754),
          amountOnIntDueWindow = Some(300000),
          breathingSpaceApplied = Some(false)
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
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2019-07-06")
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
        interestBearing = Some(true),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(500000),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-03-01")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(127),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
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
                PaymentHistory(paymentAmount = 200000, paymentDate = "2021-04-20")
              )
            )
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
        combinedDailyAccrual = Some(0),
        interestDueCallTotal = Some(0),
        amountIntTotal = Some(300000),
        unpaidAmountTotal = Some(300000),
        amountOnIntDueTotal = Some(300000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(0),
        interestDueDailyAccrual = Some(0),
        interestDueDutyTotal = Some(0),
        unpaidAmountDuty = Some(300000),
        totalAmountIntDuty = Some(300000),
        amountOnIntDueDuty = Some(300000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-03-01")),
          periodTo = Some(LocalDate.parse("2021-04-20")),
          numberOfDays = Some(50),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          amountOnIntDueWindow = Some(200000),
          unpaidAmountWindow = Some(200000),
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
          periodFrom = Some(LocalDate.parse("2021-03-01")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(127),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          amountOnIntDueWindow = Some(300000),
          unpaidAmountWindow = Some(300000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
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
                PaymentHistory(paymentAmount = 100000, paymentDate = "2021-03-20"),
                PaymentHistory(paymentAmount = 50000, paymentDate = "2021-04-20")
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(59),
        interestDueCallTotal = Some(8056),
        amountIntTotal = Some(858056),
        unpaidAmountTotal = Some(850000),
        amountOnIntDueTotal = Some(850000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(232),
        interestDueDailyAccrual = Some(24),
        interestDueDutyTotal = Some(3641),
        unpaidAmountDuty = Some(350000),
        totalAmountIntDuty = Some(353641),
        amountOnIntDueDuty = Some(350000)
      )
      theDebtSummaryWillContain(context, 1, expectedDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expectedCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-03-20")),
          numberOfDays = Some(47),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(7),
          interestDueWindow = Some(334),
          unpaidAmountWindow = Some(100334),
          amountOnIntDueWindow = Some(100000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-04-03")),
          numberOfDays = Some(61),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(3),
          interestDueWindow = Some(217),
          unpaidAmountWindow = Some(50217),
          amountOnIntDueWindow = Some(50000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-04-04")),
          periodTo = Some(LocalDate.parse("2021-04-20")),
          numberOfDays = Some(17),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(50000),
          amountOnIntDueWindow = Some(50000),
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
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-04-03")),
          numberOfDays = Some(61),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(24),
          interestDueWindow = Some(1520),
          unpaidAmountWindow = Some(351520),
          amountOnIntDueWindow = Some(350000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-04-04")),
          periodTo = Some(LocalDate.parse("2021-05-04")),
          numberOfDays = Some(31),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(350000),
          amountOnIntDueWindow = Some(350000),
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
          periodFrom = Some(LocalDate.parse("2021-05-05")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(63),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(24),
          interestDueWindow = Some(1570),
          unpaidAmountWindow = Some(351570),
          amountOnIntDueWindow = Some(350000),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(124),
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(4415),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(504415),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-04-03")),
          numberOfDays = Some(61),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(2172),
          unpaidAmountWindow = Some(502172),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-04-04")),
          periodTo = Some(LocalDate.parse("2021-05-04")),
          numberOfDays = Some(31),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000),
          amountOnIntDueWindow = Some(500000),
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
          periodFrom = Some(LocalDate.parse("2021-05-05")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(63),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(2243),
          unpaidAmountWindow = Some(502243),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
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
            debtID = Some("123"),
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
          CustomerPostCode(postCode = "EC2M 2LS", postCodeDate = "2022-01-01")
        )
      )
      aDebtCalculationIsCreated(context, request)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(122),
        interestDueCallTotal = Some(12033),
        amountIntTotal = Some(912033),
        unpaidAmountTotal = Some(900000),
        amountOnIntDueTotal = Some(900000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expectedDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(144),
        interestDueDailyAccrual = Some(51),
        interestDueDutyTotal = Some(6209),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(506209),
        amountOnIntDueDuty = Some(500000)
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
          unpaidAmountWindow = Some(500178),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-07")),
          periodTo = Some(LocalDate.parse("2022-01-20")),
          numberOfDays = Some(14),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("SUBTRANS"),
              description = Some("COVID"),
              code = Some("Converted from new suppression style")
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-01-21")),
          periodTo = Some(LocalDate.parse("2022-02-20")),
          numberOfDays = Some(31),
          interestRate = Some(2.75),
          interestDueDailyAccrual = Some(37),
          interestDueWindow = Some(1167),
          unpaidAmountWindow = Some(501167),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-02-21")),
          periodTo = Some(LocalDate.parse("2022-03-06")),
          numberOfDays = Some(14),
          interestRate = Some(3.0),
          interestDueDailyAccrual = Some(41),
          interestDueWindow = Some(575),
          unpaidAmountWindow = Some(500575),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-03-07")),
          periodTo = Some(LocalDate.parse("2022-03-20")),
          numberOfDays = Some(14),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("PERIODEND"),
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
          unpaidAmountWindow = Some(500616),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-05")),
          periodTo = Some(LocalDate.parse("2022-04-06")),
          numberOfDays = Some(2),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(89),
          unpaidAmountWindow = Some(500089),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-04-07")),
          periodTo = Some(LocalDate.parse("2022-04-20")),
          numberOfDays = Some(14),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          interestDueWindow = Some(0),
          unpaidAmountWindow = Some(500000),
          amountOnIntDueWindow = Some(500000),
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
          periodFrom = Some(LocalDate.parse("2022-04-21")),
          periodTo = Some(LocalDate.parse("2022-05-23")),
          numberOfDays = Some(33),
          interestRate = Some(3.25),
          interestDueDailyAccrual = Some(44),
          interestDueWindow = Some(1469),
          unpaidAmountWindow = Some(501469),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-05-24")),
          periodTo = Some(LocalDate.parse("2022-07-04")),
          numberOfDays = Some(42),
          interestRate = Some(3.5),
          interestDueDailyAccrual = Some(47),
          interestDueWindow = Some(2013),
          unpaidAmountWindow = Some(502013),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2022-07-05")),
          periodTo = Some(LocalDate.parse("2022-07-06")),
          numberOfDays = Some(2),
          interestRate = Some(3.75),
          interestDueDailyAccrual = Some(51),
          interestDueWindow = Some(102),
          unpaidAmountWindow = Some(500102),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expectedCalculationWindows)

      And("the 2nd debt summary will contain")
      val expected2ndDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(82),
        interestDueDailyAccrual = Some(71),
        interestDueDutyTotal = Some(5824),
        unpaidAmountDuty = Some(400000),
        totalAmountIntDuty = Some(405824),
        amountOnIntDueDuty = Some(400000)
      )
      theDebtSummaryWillContain(context, 2, expected2ndDebtSummary)

      And("the 2nd debt summary will have calculation windows")
      val expected2ndCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2024-03-01")),
          periodTo = Some(LocalDate.parse("2024-03-06")),
          numberOfDays = Some(5),
          interestRate = Some(6.5),
          interestDueDailyAccrual = Some(71),
          interestDueWindow = Some(355),
          unpaidAmountWindow = Some(400355),
          amountOnIntDueWindow = Some(400000),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 2, expected2ndCalculationWindows)
    }

  }
}
