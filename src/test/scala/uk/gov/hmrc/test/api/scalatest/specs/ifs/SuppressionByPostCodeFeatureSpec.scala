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
import uk.gov.hmrc.test.api.scalatest.builders.InterestForecastingBuilder._
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
        val expectedResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(35),
          interestDueCallTotal = Some(2314),
          amountIntTotal = Some(502314),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculationExpected(
          interestBearing = Some(true),
          numberOfChargeableDays = Some(65),
          interestDueDailyAccrual = Some(35),
          interestDueDutyTotal = Some(2314),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(502314),
          amountOnIntDueDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have suppression applied calculation windows")
        val expected1stSuppressionsApplied = SuppressionsAppliedExpected(
          dateFrom = Some("2020-12-04"),
          dateTo = Some("2021-03-01"),
          reason = Some("LEGISLATIVE"),
          reasonDesc = Some("COVID"),
          postcode = Some("TW3 4PR")
        )
        theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindows = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-02-01")),
            periodTo = Some(LocalDate.parse("2021-02-03")),
            numberOfDays = Some(2),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            unpaidAmountWindow = Some(500071),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-02-04")),
            periodTo = Some(LocalDate.parse("2021-05-04")),
            numberOfDays = Some(90),
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
            periodFrom = Some(LocalDate.parse("2021-05-05")),
            periodTo = Some(LocalDate.parse("2021-07-06")),
            numberOfDays = Some(63),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            unpaidAmountWindow = Some(502243),
            breathingSpaceApplied = Some(false)
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(2314),
        amountIntTotal = Some(502314)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(65),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(502314)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have suppression applied calculation windows")
      val expected1stSuppressionsApplied = SuppressionsAppliedExpected(
        dateFrom = Some("2021-02-04"),
        dateTo = Some("2021-05-04"),
        reason = Some("LEGISLATIVE"),
        reasonDesc = Some("COVID"),
        postcode = Some("EC2M 2LS"),
        mainTrans = Some("1535")
      )
      theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-02-03")),
          numberOfDays = Some(2),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          interestDueWindow = Some(71),
          unpaidAmountWindow = Some(500071),
          amountOnIntDueWindow = Some(500000),
          breathingSpaceApplied = Some(false)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-04")),
          periodTo = Some(LocalDate.parse("2021-05-04")),
          numberOfDays = Some(90),
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
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)
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
      val expectedResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(2243),
        amountIntTotal = Some(502243),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, expectedResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(63),
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(2243),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(502243),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have suppression applied calculation windows")
      val expected1stSuppressionsApplied = SuppressionsAppliedExpected(
        dateFrom = Some("2020-12-04"),
        dateTo = Some("2021-03-01"),
        reason = Some("LEGISLATIVE"),
        reasonDesc = Some("COVID"),
        postcode = Some("TW3 4PR")
      )
      theDebtSummaryWillHaveSuppressionAppliedCalculationWindows(context, 1, 1, expected1stSuppressionsApplied)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindows = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-02-03")),
          numberOfDays = Some(2),
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
          periodFrom = Some(LocalDate.parse("2021-02-04")),
          periodTo = Some(LocalDate.parse("2021-03-01")),
          numberOfDays = Some(26),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0),
          unpaidAmountWindow = Some(500000),
          breathingSpaceApplied = Some(false),
          suppressionApplied = Some(
            SuppressionAppliedExpected(
              reason = Some("LEGISLATIVE; LEGISLATIVE"),
              description = Some("COVID; COVID"),
              code = Some("Converted from new suppression style")
            )
          ),
          suppressionsApplied = Some(
            List(
              SuppressionsAppliedExpected(
                dateFrom = Some("2020-12-04"),
                dateTo = Some("2021-03-01"),
                reason = Some("LEGISLATIVE"),
                reasonDesc = Some("COVID"),
                postcode = Some("TW3 4PR")
              )
            )
          )
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-03-02")),
          periodTo = Some(LocalDate.parse("2021-05-04")),
          numberOfDays = Some(64),
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
          periodFrom = Some(LocalDate.parse("2021-05-05")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(63),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          unpaidAmountWindow = Some(502243),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindows)

    }

    //  TODO Fails Suppression not applied when customer has 2 or more matching postcodes
    ignore("Suppression applied to customer latest postcode 3 postcodes", WIP, DTD_400) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
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
            postCodeDate = "2021-02-06"
          ),
          CustomerPostCode(
            postCode = "TW3 4QQ",
            postCodeDate = "2019-07-06"
          ),
          CustomerPostCode(
            postCode = "TW3 4TW",
            postCodeDate = "2018-07-06"
          )
        )
      )
      aDebtCalculationIsCreated(context, ifsRequest)

      When("the debt item is sent to the ifs service")
      theDebtItemIsSentToTheIfsService(context)

      Then("the ifs service will return a total debts summary of")
      val debtCalculationSummaryResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(2243)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(155),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(502243)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(155),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)
    }

    Scenario("Suppression Start Date for a Postcode before interest start date") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(
        List(
          SuppressionInformation(
            dateFrom = "2021-01-04",
            dateTo = Some("2021-05-04"),
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
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
      val debtCalculationSummaryResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(2243)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        numberOfChargeableDays = Some(63),
        interestDueDailyAccrual = Some(35),
        totalAmountIntDuty = Some(502243)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-05-04")),
          numberOfDays = Some(92),
          interestRate = Some(0.0),
          interestDueDailyAccrual = Some(0)
        ),
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-05-05")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(63),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35)
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
        val debtCalculationSummaryResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(35),
          interestDueCallTotal = Some(5520)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculationExpected(
          numberOfChargeableDays = Some(155),
          interestDueDailyAccrual = Some(35),
          totalAmountIntDuty = Some(505520)
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindow = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-02-01")),
            periodTo = Some(LocalDate.parse("2021-07-06")),
            numberOfDays = Some(155),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35)
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
        val debtCalculationSummaryResponse = DebtCalculationsSummaryExpected(
          combinedDailyAccrual = Some(35),
          interestDueCallTotal = Some(2314),
          amountIntTotal = Some(502314),
          unpaidAmountTotal = Some(500000)
        )
        theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

        And("the 1st debt summary will contain")
        val expected1stDebtSummary = DebtCalculationExpected(
          interestBearing = Some(true),
          numberOfChargeableDays = Some(65),
          interestDueDailyAccrual = Some(35),
          interestDueDutyTotal = Some(2314),
          unpaidAmountDuty = Some(500000),
          totalAmountIntDuty = Some(502314),
          amountOnIntDueDuty = Some(500000)
        )
        theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

        And("the 1st debt summary will have calculation windows")
        val expected1stCalculationWindow = List(
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-02-01")),
            periodTo = Some(LocalDate.parse("2021-02-03")),
            numberOfDays = Some(2),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            unpaidAmountWindow = Some(500071),
            breathingSpaceApplied = Some(false)
          ),
          CalculationWindowExpected(
            periodFrom = Some(LocalDate.parse("2021-02-04")),
            periodTo = Some(LocalDate.parse("2021-05-04")),
            numberOfDays = Some(90),
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
            periodFrom = Some(LocalDate.parse("2021-05-05")),
            periodTo = Some(LocalDate.parse("2021-07-06")),
            numberOfDays = Some(63),
            interestRate = Some(2.6),
            interestDueDailyAccrual = Some(35),
            unpaidAmountWindow = Some(502243),
            breathingSpaceApplied = Some(false)
          )
        )
        theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)
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
      val debtCalculationSummaryResponse = DebtCalculationsSummaryExpected(
        combinedDailyAccrual = Some(35),
        interestDueCallTotal = Some(5520),
        amountIntTotal = Some(505520),
        unpaidAmountTotal = Some(500000)
      )
      theIfsServiceWillReturnATotalDebtsSummaryOf(context, debtCalculationSummaryResponse)

      And("the 1st debt summary will contain")
      val expected1stDebtSummary = DebtCalculationExpected(
        interestBearing = Some(true),
        numberOfChargeableDays = Some(155),
        interestDueDailyAccrual = Some(35),
        interestDueDutyTotal = Some(5520),
        unpaidAmountDuty = Some(500000),
        totalAmountIntDuty = Some(505520),
        amountOnIntDueDuty = Some(500000)
      )
      theDebtSummaryWillContain(context, 1, expected1stDebtSummary)

      And("the 1st debt summary will have calculation windows")
      val expected1stCalculationWindow = List(
        CalculationWindowExpected(
          periodFrom = Some(LocalDate.parse("2021-02-01")),
          periodTo = Some(LocalDate.parse("2021-07-06")),
          numberOfDays = Some(155),
          interestRate = Some(2.6),
          interestDueDailyAccrual = Some(35),
          unpaidAmountWindow = Some(505520),
          breathingSpaceApplied = Some(false)
        )
      )
      theDebtSummaryWillHaveCalculationWindows(context, 1, expected1stCalculationWindow)
    }
  }
}
