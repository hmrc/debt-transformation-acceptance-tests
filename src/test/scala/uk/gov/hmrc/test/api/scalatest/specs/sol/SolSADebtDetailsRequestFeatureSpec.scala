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

package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.sol.{Debt, SolCalculation, SolCalculationSummaryResponse, SolDebtsRequest, SolDuty}
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.StatementOfLiabilityStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class SolSADebtDetailsRequestFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = StatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = StatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("Statement of liability Debt details for Self Assessment Debts") {

    Scenario(
      "1. SA debt statement of liability, 2 duties and multiple breathing space with no payment history.",
      DTD_1959,
      DTD_3003
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA001",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedResponse = SolCalculationSummaryResponse(
        amountIntTotal = BigInt(907817L),
        combinedDailyAccrual = BigInt(63L),
        debts = List(
          SolCalculation(
            // the 1st sol debt summary will contain
            debtId = "debtSA001",
            mainTrans = "4920",
            debtTypeDescription = "SA 1st Payment on Account",
            interestDueDebtTotal = BigInt(7817L),
            totalAmountIntDebt = BigInt(907817L),
            combinedDailyAccrual = BigInt(63L),
            parentMainTrans = None,
            duties = Seq(
              // the 1st sol debt summary will contain duties
              SolDuty(
                subTrans = "1553",
                dutyTypeDescription = Some("SA 1st Payment on Account"),
                unpaidAmountDuty = BigInt(500000L),
                combinedDailyAccrual = BigInt(35L),
                interestBearing = true,
                interestOnlyIndicator = false
              ),
              SolDuty(
                subTrans = "1090",
                dutyTypeDescription = Some("SA Pship Late Filing Penalty"),
                unpaidAmountDuty = BigInt(400000L),
                combinedDailyAccrual = BigInt(28L),
                interestBearing = true,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedResponse)
    }
    Scenario("2. SA debt statement of liability. Single duty non interest bearing.", DTD_1959) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA002",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedResponse = SolCalculationSummaryResponse(
        amountIntTotal = BigInt(500000L),
        combinedDailyAccrual = BigInt(0L),
        debts = List(
          SolCalculation(
            // the 1st sol debt summary will contain
            debtId = "debtSA002",
            mainTrans = "5073",
            debtTypeDescription = "SA Transfer to OAS",
            interestDueDebtTotal = BigInt(0L),
            totalAmountIntDebt = BigInt(500000L),
            combinedDailyAccrual = BigInt(0L),
            parentMainTrans = None,
            duties = Seq(
              SolDuty(
                // the 1st sol debt summary will contain duties
                subTrans = "1553",
                dutyTypeDescription = None,
                unpaidAmountDuty = BigInt(500000L),
                combinedDailyAccrual = BigInt(0L),
                interestBearing = false,
                interestOnlyIndicator = false
              )
            )
          )
        )
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedResponse)
    }

    Scenario("3. SA debt statement of liability - 2 duties Multiple breathing space and payment history.", DTD_2166) {
      context =>
        Given("debt details")
        val request = SolDebtsRequest(
          solType = "UI",
          customerUniqueRef = "NEHA1234",
          debts = List(
            Debt(
              debtId = "debtSA003",
              interestRequestedTo = "2021-08-10"
            )
          )
        )
        statementOfLiabilityMultipleDebtRequests(context, request)

        When("a debt statement of liability is requested")
        aDebtStatementOfLiabilityIsRequested(context)

        Then("service returns debt statement of liability data")
        val expectedResponse = SolCalculationSummaryResponse(
          amountIntTotal = BigInt(605264L),
          combinedDailyAccrual = BigInt(41L),
          debts = List(
            SolCalculation(
              // the 1st sol debt summary will contain
              debtId = "debtSA003",
              mainTrans = "4920",
              debtTypeDescription = "SA 1st Payment on Account",
              interestDueDebtTotal = BigInt(5264L),
              totalAmountIntDebt = BigInt(605264L),
              combinedDailyAccrual = BigInt(41L),
              parentMainTrans = None,
              duties = Seq(
                SolDuty(
                  // the 1st sol debt summary will contain 1st duties
                  subTrans = "1553",
                  dutyTypeDescription = Some("SA 1st Payment on Account"),
                  unpaidAmountDuty = BigInt(350000L),
                  combinedDailyAccrual = BigInt(24L),
                  interestBearing = true,
                  interestOnlyIndicator = false
                ),
                SolDuty(
                  // the 1st sol debt summary will contain 2nd duties
                  subTrans = "1090",
                  dutyTypeDescription = Some("SA Pship Late Filing Penalty"),
                  unpaidAmountDuty = BigInt(250000L),
                  combinedDailyAccrual = BigInt(17L),
                  interestBearing = true,
                  interestOnlyIndicator = false
                )
              )
            )
          )
        )
        serviceReturnsDebtStatementOfLiabilityData(context, expectedResponse)
    }

    Scenario(
      "6. Statement of liability for customer with ETMP parentMainTrans   - Single Non Interest bearing debt",
      DTD_2940
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0014",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0014",
        mainTrans = "6010",
        debtTypeDescription = "SA Late Payment Interest",
        interestDueDebtTotal = BigInt(0),
        totalAmountIntDebt = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        parentMainTrans = Option("33"),
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1554",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        interestBearing = false,
        interestOnlyIndicator = true,
        context
      )
    }

    Scenario(
      "7. Statement of liability for customer with parentMainTrans   - Single SA Non Interest bearing debt",
      DTD_2940
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0015",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0015",
        mainTrans = "6010",
        debtTypeDescription = "SA Balancing Charge Interest",
        interestDueDebtTotal = BigInt(0),
        totalAmountIntDebt = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        parentMainTrans = Option("25"),
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1554",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(0),
        interestBearing = false,
        interestOnlyIndicator = true,
        context
      )
    }

    Scenario(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0017, mainTrans=4027, subTrans=1080, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0017",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0017",
        mainTrans = "4027",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1080",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0018, mainTrans=4028, subTrans=1085, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0018",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0018",
        mainTrans = "4028",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1085",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0019, mainTrans=4029, subTrans=1090, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0019",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0019",
        mainTrans = "4029",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0020, mainTrans=4031, subTrans=1095, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0020",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0020",
        mainTrans = "4031",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1095",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario(
      "8. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0021, mainTrans=4032, subTrans=1090, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0021",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0021",
        mainTrans = "4032",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1090",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }

    Scenario(
      "9. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0022, mainTrans=4033, subTrans=1095, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(
            debtId = "debtSA0022",
            interestRequestedTo = "2021-08-10"
          )
        )
      )
      statementOfLiabilityMultipleDebtRequests(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      checkAmountIntTotalAndCombinedDailyAccrual(
        amountIntTotal = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        context
      )
      And("the 1st sol debt summary will contain")
      theSolDebtSummaryWillContain(
        debtSummaryEntry = 1,
        debtId = "debtSA0022",
        mainTrans = "4033",
        debtTypeDescription = "Penalty reform charge",
        interestDueDebtTotal = BigInt(4629),
        totalAmountIntDebt = BigInt(504629),
        combinedDailyAccrual = BigInt(35),
        parentMainTrans = None,
        context
      )
      And("the 1st sol debt summary will contain duties")
      theSolDebtSummaryWillContainDuties(
        solDutyEntry = 1,
        subTrans = "1095",
        dutyTypeDescription = None,
        unpaidAmountDuty = BigInt(500000),
        combinedDailyAccrual = BigInt(35),
        interestBearing = true,
        interestOnlyIndicator = false,
        context
      )
    }
  }
}
