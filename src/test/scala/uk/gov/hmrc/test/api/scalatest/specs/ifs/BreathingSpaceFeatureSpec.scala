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
import uk.gov.hmrc.test.api.models.{DebtCalculation, FCDebtCalculation, FCDebtCalculationsSummary}
import uk.gov.hmrc.test.api.models.ifs.{BreathingSpaces, DebtItems, FCDebtCalculationRequest, PaymentHistory}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, FieldCollectionsContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags._

class BreathingSpaceFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = FieldCollectionsContext

  override def withFixture(test: OneArgTest) = {
    val context = FieldCollectionsContext()
    try test(context)
    finally ()
  }

  Feature("Breathing Space") {

    Scenario("Interest Bearing. Single debt with breathing space and no payment history (SA)", DTD_2244) { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          DebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 500000,
            interestIndicator = "Y",
            periodEnd = "2019-04-14",
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
            paymentHistory = Some(List.empty),
            customerPostcodes = Some(List.empty)
          )
        )
      )

      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the IFS service will return a total debts summary")
      val result: FCDebtCalculationsSummary =
        context.ifsResponseBody.get

      result.combinedDailyAccrual shouldBe BigDecimal(44)
      result.interestDueCallTotal shouldBe BigDecimal(3872)
      result.unpaidAmountTotal    shouldBe BigDecimal(500000)

      And("the 1st debt summary will contain")
      val debt: FCDebtCalculation =
        result.debtCalculations.head

      debt.interestDueDailyAccrual shouldBe 44
      debt.interestDueDutyTotal    shouldBe 3872
      debt.unpaidAmountDuty        shouldBe 500000

      And("the 1st debt summary will have calculation windows")
      debt.calculationWindows.map(_.numberOfDays) shouldBe List(17L, 32L, 70L)
      debt.calculationWindows
        .map(_.interestDueDailyAccrual)           shouldBe List(BigDecimal(44), BigDecimal(0), BigDecimal(44))
    }

    Scenario("2 debts with breathing space. No payment history (Scenario 1 - step 6) (SA)", DTD_2140, DTD_2243) {
      context =>
        Given("a fc debt calculation")
        val ifsRequest = FCDebtCalculationRequest(
          debtItems = List(
            DebtItems(
              debtItemChargeId = Some("123"),
              originalAmount = 50000,
              interestIndicator = "Y",
              periodEnd = "2022-05-15",
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
              paymentHistory = Some(List.empty),
              customerPostcodes = Some(List.empty)
            ),
            DebtItems(
              debtItemChargeId = Some("456"),
              originalAmount = 50000,
              interestIndicator = "Y",
              periodEnd = "2022-05-15",
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
              paymentHistory = Some(List.empty),
              customerPostcodes = Some(List.empty)
            )
          )
        )

        aFcDebtCalculation(context, ifsRequest)

        When("the debt item is sent to the IFS service")
        theDebtItemIsSentToTheFcIfsService(context)

        Then("the ifs service wilL return a total debts summary of")
        val result: FCDebtCalculationsSummary =
          context.ifsResponseBody.get

        result.combinedDailyAccrual shouldBe 8
        result.totalAmountIntTotal  shouldBe 100356

        And("the 1st debt summary will contain")
        val firstDebt = result.debtCalculations.head

        firstDebt.interestDueDailyAccrual shouldBe 4
        firstDebt.totalAmountIntDuty      shouldBe 50178

        And("the 1st debt summary will have calculation windows")
        firstDebt.calculationWindows.map(_.numberOfDays)            shouldBe
          List(20, 8, 35, 25, 16)
        firstDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
          List(3, 4, 0, 0, 4)
        firstDebt.calculationWindows.map(_.interestRate)            shouldBe
          List(2.75, 3.0, 0.0, 0.0, 3.25)
        firstDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
          List(50075, 50032, 50000, 50000, 50071)

        And("the 2nd debt summary will contain")
        val secondDebt = result.debtCalculations(1)

        secondDebt.interestDueDailyAccrual shouldBe 4
        secondDebt.totalAmountIntDuty      shouldBe 50178

        And("the 2nd debt summary will have calculation windows")

        secondDebt.calculationWindows.map(_.numberOfDays)            shouldBe
          List(20, 8, 35, 25, 16)
        secondDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
          List(3, 4, 0, 0, 4)
        secondDebt.calculationWindows.map(_.interestRate)            shouldBe
          List(2.75, 3.0, 0.0, 0.0, 3.25)
        secondDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
          List(50075, 50032, 50000, 50000, 50071)

    }

    Scenario("Single debt with breathing space AND payment history (SA)\", DTD_2140, DTD_2243", DTD_2244) { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          DebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 50000,
            interestIndicator = "Y",
            periodEnd = "2019-04-14",
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
            ),
            customerPostcodes = Some(List.empty)
          )
        )
      )

      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the IFS service will return a total debts summary")
      val result: FCDebtCalculationsSummary =
        context.ifsResponseBody.get
      result.combinedDailyAccrual shouldBe 2
      result.totalAmountIntTotal  shouldBe 30044

      And("the 1st debt summary will contain")
      val debt: FCDebtCalculation =
        result.debtCalculations.head
      debt.interestDueDailyAccrual shouldBe 2
      debt.totalAmountIntDuty      shouldBe 30044

      And("the 1st debt summary will have calculation windows")
      debt.calculationWindows.map(_.numberOfDays)            shouldBe
        List(3, 11, 4, 3, 11, 9)
      debt.calculationWindows.map(_.interestRate)            shouldBe
        List(3.25, 0.0, 3.25, 3.25, 0.0, 3.25)
      debt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
        List(1, 0, 1, 2, 0, 2)
      debt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
        List(20005, 20000, 20007, 30008, 30000, 30024)
    }

    Scenario(
      "2 debts one with a breathing space and payment history plus a late payment debt (Scenario 1, Step 7) (SA)",
      DTD_2140,
      DTD_2243
    ) { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          DebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 50000,
            interestIndicator = "Y",
            periodEnd = "2022-05-15",
            interestStartDate = Some("2022-01-31"),
            interestRequestedTo = "2022-06-10",
            breathingSpaces = Some(
              List(
                BreathingSpaces(
                  debtRespiteFrom = "2022-03-01",
                  debtRespiteTo = "2022-04-29"
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
            ),
            customerPostcodes = Some(List.empty)
          ),
          DebtItems(
            debtItemChargeId = Some("456"),
            originalAmount = 1500,
            interestIndicator = "Y",
            periodEnd = "2022-05-15",
            interestStartDate = Some("2034-11-12"),
            interestRequestedTo = "2022-06-10",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty),
            customerPostcodes = Some(List.empty)
          )
        )
      )

      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val result: FCDebtCalculationsSummary =
        context.ifsResponseBody.get

      result.combinedDailyAccrual shouldBe 2
      result.totalAmountIntTotal  shouldBe 26771

      And("the 1st debt summary will contain")
      val firstDebt = result.debtCalculations.head

      firstDebt.interestDueDailyAccrual shouldBe 2
      firstDebt.totalAmountIntDuty      shouldBe 25271

      And("the 1st debt summary will have calculation windows")
      firstDebt.calculationWindows.map(_.numberOfDays)            shouldBe
        List(20, 8, 35, 25, 24, 7, 20, 8, 35, 25, 24, 18)
      firstDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
        List(1, 2, 0, 0, 2, 2, 1, 2, 0, 0, 2, 2)
      firstDebt.calculationWindows.map(_.interestRate)            shouldBe
        List(2.75, 3.0, 0.0, 0.0, 3.25, 3.5, 2.75, 3.0, 0.0, 0.0, 3.25, 3.5)
      firstDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
        List(25037, 25016, 25000, 25000, 25053, 25016, 25037, 25016, 25000, 25000, 25053, 25043)

      And("the 2nd debt summary will contain")
      val secondDebt = result.debtCalculations(1)

      secondDebt.interestDueDailyAccrual shouldBe 0
      secondDebt.totalAmountIntDuty      shouldBe 1500

      And("the 2nd debt summary will have calculation windows")

      secondDebt.calculationWindows.map(_.numberOfDays)            shouldBe List.empty
      secondDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe List.empty
      secondDebt.totalAmountIntDuty                                shouldBe 1500

    }

    Scenario(
      "1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)",
      DTD_2140
    ) { context =>
      Given("a fc debt calculation")
      val ifsRequest = FCDebtCalculationRequest(
        debtItems = List(
          DebtItems(
            debtItemChargeId = Some("123"),
            originalAmount = 50000,
            interestIndicator = "Y",
            periodEnd = "2022-05-15",
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
            ),
            customerPostcodes = Some(List.empty)
          ),
          DebtItems(
            debtItemChargeId = Some("456"),
            originalAmount = 1500,
            interestIndicator = "Y",
            periodEnd = "2022-05-15",
            interestStartDate = Some("2034-11-12"),
            interestRequestedTo = "2022-06-10",
            breathingSpaces = Some(List.empty),
            paymentHistory = Some(List.empty),
            customerPostcodes = Some(List.empty)
          ),
          DebtItems(
            debtItemChargeId = Some("789"),
            originalAmount = 50000,
            interestIndicator = "Y",
            periodEnd = "2022-05-15",
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
            paymentHistory = Some(List.empty),
            customerPostcodes = Some(List.empty)
          )
        )
      )

      aFcDebtCalculation(context, ifsRequest)

      When("the debt item is sent to the IFS service")
      theDebtItemIsSentToTheFcIfsService(context)

      Then("the ifs service wilL return a total debts summary of")
      val result: FCDebtCalculationsSummary =
        context.ifsResponseBody.get

      result.combinedDailyAccrual shouldBe 0
      result.totalAmountIntTotal  shouldBe 76752

      And("the 1st debt summary will contain")
      val firstDebt = result.debtCalculations.head

      firstDebt.interestDueDailyAccrual shouldBe 0
      firstDebt.totalAmountIntDuty      shouldBe 25247

      And("the 1st debt summary will have calculation windows")
      firstDebt.calculationWindows.map(_.numberOfDays)            shouldBe
        List(20, 8, 35, 25, 24, 7, 20, 8, 35, 25, 24, 8, 19)
      firstDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
        List(1, 2, 0, 0, 2, 2, 1, 2, 0, 0, 2, 2, 0)
      firstDebt.calculationWindows.map(_.interestRate)            shouldBe
        List(2.75, 3.0, 0.0, 0.0, 3.25, 3.5, 2.75, 3.0, 0.0, 0.0, 3.25, 3.5, 0.0)
      firstDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
        List(25037, 25016, 25000, 25000, 25053, 25016, 25037, 25016, 25000, 25000, 25053, 25019, 25000)

      And("the 2nd debt summary will contain")
      val secondDebt = result.debtCalculations(1)

      secondDebt.interestDueDailyAccrual shouldBe 0
      secondDebt.totalAmountIntDuty      shouldBe 1500

      And("the 3nd debt summary will contain")
      val thirdDebt = result.debtCalculations(2)

      thirdDebt.interestDueDailyAccrual shouldBe 0
      thirdDebt.totalAmountIntDuty      shouldBe 50005

      And("the 3st debt summary will have calculation windows")

      thirdDebt.calculationWindows.map(_.numberOfDays)            shouldBe
        List(1, 10)
      thirdDebt.calculationWindows.map(_.interestDueDailyAccrual) shouldBe
        List(5, 0)
      thirdDebt.calculationWindows.map(_.interestRate)            shouldBe
        List(3.75, 0)
      thirdDebt.calculationWindows.map(_.unpaidAmountWindow)      shouldBe
        List(50005, 50000)

    }

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
