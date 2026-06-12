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
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions.SuppressionStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

import java.time.LocalDate

class SuppressionByPostCodeFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with SuppressionStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = InterestForecastingContext

  override def withFixture(test: OneArgTest) = {
    val context = InterestForecastingContext()
    try test(context)
    finally ()
  }

  Feature("Suppression by Postcode") {
    val suppressionRulesContext = SuppressionRulesContext()

    Scenario("Suppression applied to customers latest postcode - 2 postcodes one before interest start date") {
      context =>
        Given("suppression configuration data is created")
        val suppressionRequest = SuppressionRequest(
          List(
            SuppressionInformation(
              dateFrom = "2021-02-04",
              dateTo = Some("2021-05-04"),
              reason = "LEGISLATIVE",
              reasonDesc = "COVID",
              suppressionChargeDescription = "SA-Suppression",
              postcode = Some("TW3 4QR"),
              mainTrans = None,
              subTrans = None,
              checkPeriodEnd = None
            ),
            SuppressionInformation(
              dateFrom = "2020-12-04",
              dateTo = Some("2021-03-01"),
              reason = "LEGISLATIVE",
              reasonDesc = "COVID",
              suppressionChargeDescription = "SA-Suppression",
              postcode = Some("TW3 4PR"),
              mainTrans = None,
              subTrans = None,
              checkPeriodEnd = None
            )
          )
        )
        suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

        When("suppression configuration is sent to ifs service")
        suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

        And("a debt calculation is created")
        val ifsRequest = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1000",
              mainTrans = "1535",
              interestStartDate = Some("2021-02-01"),
              interestRequestedTo = "2021-07-06",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List(
            CustomerPostCode(
              postCode = "TW3 4QR",
              postCodeDate = "2020-12-10"
            ),
            CustomerPostCode(
              postCode = "TW3 4PR",
              postCodeDate = "2021-04-10"
            )
          )
        )
        aDebtCalculationIsCreated(context, ifsRequest)

        When("the debt item is sent to the ifs service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val debtCalculationSummaryResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 35,
          interestDueCallTotal = 2314,
          amountIntTotal = 502314,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty[DebtCalculation]
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 65,
          interestDueDailyAccrual = 35,
          interestDueDutyTotal = 2314,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 502314,
          unpaidAmountDuty = 500000,
          interestOnlyIndicator = false,
          calculationWindows = List.empty[CalculationWindow]
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have suppression applied calculation windows")
        val expected1stSuppressionsApplied = SuppressionsApplied(
          dateFrom = "2021-02-04",
          dateTo = Some("2021-05-04"),
          reason = "LEGISLATIVE",
          reasonDesc = "COVID",
          postcode = Some("TW3 4QR"),
          mainTrans = None,
          subTrans = None,
          periodEnd = None
        )
        theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindow = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-02-01"),
            periodTo = LocalDate.parse("2021-02-03"),
            numberOfDays = 2,
            interestRate = 2.6,
            interestDueDailyAccrual = 35,
            interestDueWindow = 71,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500071,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-02-04"),
            periodTo = LocalDate.parse("2021-05-04"),
            numberOfDays = 90,
            interestRate = 0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500000,
            breathingSpaceApplied = false,
            suppressionApplied = Some(
              SuppressionApplied(
                reason = "LEGISLATIVE",
                description = "COVID",
                code = "Converted from new suppression style"
              )
            ),
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
        theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression not applied to customers previous postcode") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
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
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW2 4TW",
            postCodeDate = "2018-07-06"
          ),
          CustomerPostCode(
            postCode = "TW3 4QQ",
            postCodeDate = "2019-07-06"
          ),
          CustomerPostCode(
            postCode = "EC2M 2LS",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2314,
        amountIntTotal = 502314,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 65,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 2314,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 502314,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = List.empty[CalculationWindow]
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have suppression applied calculation windows")
      val expected1stSuppressionsApplied = SuppressionsApplied(
        dateFrom = "2021-02-04",
        dateTo = Some("2021-05-04"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        postcode = Some("EC2M 2LS"),
        mainTrans = Some("1535"),
        subTrans = None,
        periodEnd = None
      )
      theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-02-03"),
          numberOfDays = 2,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 71,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500071,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-04"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 90,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
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
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression applied to customers latest postcode - 2 postcodes", DTD_400) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2020-12-04",
            dateTo = Some("2021-03-01"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4PR"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4QR"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW3 4QR",
            postCodeDate = "2021-04-10"
          ),
          CustomerPostCode(
            postCode = "TW3 4PR",
            postCodeDate = "2020-12-10"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2243,
        amountIntTotal = 502243,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 63,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 2243,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 502243,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = List.empty[CalculationWindow]
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have suppression applied calculation windows")
      val expected1stSuppressionsApplied = SuppressionsApplied(
        dateFrom = "2020-12-04",
        dateTo = Some("2021-03-01"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        postcode = Some("TW3 4PR"),
        mainTrans = None,
        subTrans = None,
        periodEnd = None
      )
      theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-02-03"),
          numberOfDays = 2,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-04"),
          periodTo = LocalDate.parse("2021-03-01"),
          numberOfDays = 26,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE; LEGISLATIVE",
              description = "COVID; COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = Some(
            List(
              SuppressionsApplied(
                dateFrom = "2020-12-04",
                dateTo = Some("2021-03-01"),
                reason = "LEGISLATIVE",
                reasonDesc = "COVID",
                postcode = Some("TW3 4PR"),
                mainTrans = None,
                subTrans = None,
                periodEnd = None
              ),
              SuppressionsApplied(
                dateFrom = "2021-02-04",
                dateTo = Some("2021-05-04"),
                reason = "LEGISLATIVE",
                reasonDesc = "COVID",
                postcode = Some("TW3 4QR"),
                mainTrans = None,
                subTrans = None,
                periodEnd = None
              )
            )
          )
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-02"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 64,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = Some(
            List(
              SuppressionsApplied(
                dateFrom = "2021-02-04",
                dateTo = Some("2021-05-04"),
                reason = "LEGISLATIVE",
                reasonDesc = "COVID",
                postcode = Some("TW3 4QR"),
                mainTrans = None,
                subTrans = None,
                periodEnd = None
              )
            )
          )
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
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

//  TODO Fails Suppression not applied when customer has 2 or more matching postcodes
    ignore("Scenario applied to customer latest postcode 3 postcodes", WIP, DTD_400) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4QR"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2020-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4QQ"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          ),
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4TW"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW3 4QR",
            postCodeDate = "2021-04-10"
          ),
          CustomerPostCode(
            postCode = "TW3 4PR",
            postCodeDate = "2020-12-10"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2243,
        amountIntTotal = 502243,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 63,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 2243,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 502243,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = List.empty[CalculationWindow]
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have suppression applied calculation windows")
      val expected1stSuppressionsApplied = SuppressionsApplied(
        dateFrom = "2020-12-04",
        dateTo = Some("2021-03-01"),
        reason = "LEGISLATIVE",
        reasonDesc = "COVID",
        postcode = Some("TW3 4PR"),
        mainTrans = None,
        subTrans = None,
        periodEnd = None
      )
      theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-02-03"),
          numberOfDays = 2,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 71,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-04"),
          periodTo = LocalDate.parse("2021-03-01"),
          numberOfDays = 26,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE; LEGISLATIVE",
              description = "COVID; COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = None
        ),
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-03-02"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 64,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression Start Date for a Postcode before interest start date", WIP, DTD_400) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "TW3 4QQ",
            postCodeDate = "2019-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2243,
        amountIntTotal = 502243,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 63,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 2243,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 502243,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = List.empty[CalculationWindow]
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-05-04"),
          numberOfDays = 92,
          interestRate = 0,
          interestDueDailyAccrual = 0,
          interestDueWindow = 0,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 500000,
          breathingSpaceApplied = false,
          suppressionApplied = Some(
            SuppressionApplied(
              reason = "LEGISLATIVE",
              description = "COVID",
              code = "Converted from new suppression style"
            )
          ),
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
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression should not be applied where postcode date after suppression period - border case") {
      context =>
        Given("suppression configuration data is created")
        val suppressionRequest = SuppressionRequest(
          List(
            SuppressionInformation(
              dateFrom = "2021-02-04",
              dateTo = Some("2021-05-04"),
              reason = "LEGISLATIVE",
              reasonDesc = "COVID",
              suppressionChargeDescription = "SA-Suppression",
              postcode = Some("TW3"),
              mainTrans = None,
              subTrans = None,
              checkPeriodEnd = None
            )
          )
        )
        suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

        When("suppression configuration is sent to ifs service")
        suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

        And("a debt calculation is created")
        val ifsRequest = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1000",
              mainTrans = "1535",
              interestStartDate = Some("2021-02-01"),
              interestRequestedTo = "2021-07-06",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List(
            CustomerPostCode(
              postCode = "TW3 4QR",
              postCodeDate = "2021-05-05"
            )
          )
        )
        aDebtCalculationIsCreated(context, ifsRequest)

        When("the debt item is sent to the ifs service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val debtCalculationSummaryResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 35,
          interestDueCallTotal = 5520,
          amountIntTotal = 505520,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty[DebtCalculation]
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 155,
          interestDueDailyAccrual = 35,
          interestDueDutyTotal = 5520,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 505520,
          unpaidAmountDuty = 500000,
          interestOnlyIndicator = false,
          calculationWindows = List.empty[CalculationWindow]
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindow = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-02-01"),
            periodTo = LocalDate.parse("2021-07-06"),
            numberOfDays = 155,
            interestRate = 2.6,
            interestDueDailyAccrual = 35,
            interestDueWindow = 5520,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 505520,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression should be applied if customer moved in on last day of suppression period - border case") {
      context =>
        Given("suppression configuration data is created")
        val suppressionRequest = SuppressionRequest(
          List(
            SuppressionInformation(
              dateFrom = "2021-02-04",
              dateTo = Some("2021-05-04"),
              reason = "LEGISLATIVE",
              reasonDesc = "COVID",
              suppressionChargeDescription = "SA-Suppression",
              postcode = Some("TW3"),
              mainTrans = None,
              subTrans = None,
              checkPeriodEnd = None
            )
          )
        )
        suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

        When("suppression configuration is sent to ifs service")
        suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

        And("a debt calculation is created")
        val ifsRequest = DebtCalculationRequest(
          debtItems = List(
            DebtItem(
              debtID = Some("123"),
              originalAmount = 500000,
              subTrans = "1000",
              mainTrans = "1535",
              interestStartDate = Some("2021-02-01"),
              interestRequestedTo = "2021-07-06",
              breathingSpaces = Some(List.empty),
              paymentHistory = Some(List.empty)
            )
          ),
          customerPostCodes = List(
            CustomerPostCode(
              postCode = "TW3 4QR",
              postCodeDate = "2021-05-04"
            )
          )
        )
        aDebtCalculationIsCreated(context, ifsRequest)

        When("the debt item is sent to the ifs service")
        theDebtItemIsSentToTheIfsService(context)

        Then("the ifs service will return a total debts summary of")
        val debtCalculationSummaryResponse = DebtCalculationsSummary(
          combinedDailyAccrual = 35,
          interestDueCallTotal = 2314,
          amountIntTotal = 502314,
          amountOnIntDueTotal = 500000,
          unpaidAmountTotal = 500000,
          debtCalculations = List.empty[DebtCalculation]
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculation(
          debtItemChargeId = None,
          debtID = Some("123"),
          interestBearing = true,
          numberOfChargeableDays = 65,
          interestDueDailyAccrual = 35,
          interestDueDutyTotal = 2314,
          amountOnIntDueDuty = 500000,
          totalAmountIntDuty = 502314,
          unpaidAmountDuty = 500000,
          interestOnlyIndicator = false,
          calculationWindows = List.empty[CalculationWindow]
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindow = List(
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-02-01"),
            periodTo = LocalDate.parse("2021-02-03"),
            numberOfDays = 2,
            interestRate = 2.6,
            interestDueDailyAccrual = 35,
            interestDueWindow = 71,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500071,
            breathingSpaceApplied = false,
            suppressionApplied = None,
            suppressionsApplied = None
          ),
          CalculationWindow(
            periodFrom = LocalDate.parse("2021-02-04"),
            periodTo = LocalDate.parse("2021-05-04"),
            numberOfDays = 90,
            interestRate = 0,
            interestDueDailyAccrual = 0,
            interestDueWindow = 0,
            amountOnIntDueWindow = 500000,
            unpaidAmountWindow = 500000,
            breathingSpaceApplied = false,
            suppressionApplied = Some(
              SuppressionApplied(
                reason = "LEGISLATIVE",
                description = "COVID",
                code = "Converted from new suppression style"
              )
            ),
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
        theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }

    Scenario("Suppression should be applied to customer sub district [postCode=AA9A 9AA]") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("AA9A 9AA"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "AA9A 9AA",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2314,
        amountIntTotal = 502314,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

    }

    Scenario("Suppression should be applied to customer sub district [subDistrict=A99, postCode=A99 9AA]") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("A99 9AA"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "A99 9AA",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2314,
        amountIntTotal = 502314,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

    }

    Scenario("Suppression should be applied to customer sub district [postCode=AA9 9AA]") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("AA9 9AA"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "AA9 9AA",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2314,
        amountIntTotal = 502314,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

    }

    Scenario("Suppression should be applied to customer sub district [ostCode=AA99 9AA]") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("AA99 9AA"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "AA99 9AA",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 2314,
        amountIntTotal = 502314,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

    }

    Scenario("Suppression should not be applied for non matching postcodes") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-02-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = Some("TW3 4PR"),
            mainTrans = None,
            subTrans = None,
            checkPeriodEnd = None
          )
        )
      )
      suppressionConfigurationDataIsCreated(suppressionRulesContext, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      suppressionConfigurationIsSentToIfsService(suppressionRulesContext)

      And("a debt calculation is created")
      val ifsRequest = DebtCalculationRequest(
        debtItems = List(
          DebtItem(
            debtID = Some("123"),
            originalAmount = 500000,
            subTrans = "1000",
            mainTrans = "1535",
            interestStartDate = Some("2021-02-01"),
            interestRequestedTo = "2021-07-06",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty)
          )
        ),
        customerPostCodes = List(
          CustomerPostCode(
            postCode = "AA9 9AA",
            postCodeDate = "2020-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummary(
        combinedDailyAccrual = 35,
        interestDueCallTotal = 5520,
        amountIntTotal = 505520,
        amountOnIntDueTotal = 500000,
        unpaidAmountTotal = 500000,
        debtCalculations = List.empty[DebtCalculation]
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculation(
        debtItemChargeId = None,
        debtID = Some("123"),
        interestBearing = true,
        numberOfChargeableDays = 155,
        interestDueDailyAccrual = 35,
        interestDueDutyTotal = 5520,
        amountOnIntDueDuty = 500000,
        totalAmountIntDuty = 505520,
        unpaidAmountDuty = 500000,
        interestOnlyIndicator = false,
        calculationWindows = List.empty[CalculationWindow]
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindow(
          periodFrom = LocalDate.parse("2021-02-01"),
          periodTo = LocalDate.parse("2021-07-06"),
          numberOfDays = 155,
          interestRate = 2.6,
          interestDueDailyAccrual = 35,
          interestDueWindow = 5520,
          amountOnIntDueWindow = 500000,
          unpaidAmountWindow = 505520,
          breathingSpaceApplied = false,
          suppressionApplied = None,
          suppressionsApplied = None
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)

    }
  }
}
