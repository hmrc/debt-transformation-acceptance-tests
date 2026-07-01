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
import uk.gov.hmrc.test.api.models.sol.{Debt, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder.{SolCalculationExpected, SolCalculationSummaryResponseExpected, SolDutyExpected}
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
          Debt(debtId = "debtSA001", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(907817)),
        combinedDailyAccrual = Some(BigInt(63))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA001"),
        mainTrans = Some("4920"),
        debtTypeDescription = Some("SA 1st Payment on Account"),
        interestDueDebtTotal = Some(BigInt(7817)),
        totalAmountIntDebt = Some(BigInt(907817)),
        combinedDailyAccrual = Some(BigInt(63))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1553"),
          dutyTypeDescription = Some("SA 1st Payment on Account"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        ),
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("SA Pship Late Filing Penalty"),
          unpaidAmountDuty = Some(BigInt(400000)),
          combinedDailyAccrual = Some(BigInt(28)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario("2. SA debt statement of liability. Single duty non interest bearing.", DTD_1959) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA002", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA002"),
        mainTrans = Some("5073"),
        debtTypeDescription = Some("SA Transfer to OAS"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1553"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "3. SA debt statement of liability - 2 duties Multiple breathing space and payment history.",
      DTD_2166
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA003", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(605264)),
        combinedDailyAccrual = Some(BigInt(41))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA003"),
        mainTrans = Some("4920"),
        debtTypeDescription = Some("SA 1st Payment on Account"),
        interestDueDebtTotal = Some(BigInt(5264)),
        totalAmountIntDebt = Some(BigInt(605264)),
        combinedDailyAccrual = Some(BigInt(41))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1553"),
          dutyTypeDescription = Some("SA 1st Payment on Account"),
          unpaidAmountDuty = Some(BigInt(350000)),
          combinedDailyAccrual = Some(BigInt(24)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        ),
        SolDutyExpected(
          subTrans = Some("1090"),
          dutyTypeDescription = Some("SA Pship Late Filing Penalty"),
          unpaidAmountDuty = Some(BigInt(250000)),
          combinedDailyAccrual = Some(BigInt(17)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "4. Statement of liability for customer with ETMP parentMainTrans - Single Non Interest bearing debt",
      DTD_2940
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0014", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0014"),
        mainTrans = Some("6010"),
        debtTypeDescription = Some("SA Late Payment Interest"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0)),
        parentMainTrans = Some("33")
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1554"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(true)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "5. Statement of liability for customer with parentMainTrans - Single SA Non Interest bearing debt",
      DTD_2940
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0015", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0015"),
        mainTrans = Some("6010"),
        debtTypeDescription = Some("SA Balancing Charge Interest"),
        interestDueDebtTotal = Some(BigInt(0)),
        totalAmountIntDebt = Some(BigInt(500000)),
        combinedDailyAccrual = Some(BigInt(0)),
        parentMainTrans = Some("25")
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1554"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(0)),
          interestBearing = Some(false),
          interestOnlyIndicator = Some(true)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "6. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0017, mainTrans=4027, subTrans=1080, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0017", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0017"),
        mainTrans = Some("4027"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1080"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "7. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0018, mainTrans=4028, subTrans=1085, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0018", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0018"),
        mainTrans = Some("4028"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1085"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
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
          Debt(debtId = "debtSA0019", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0019"),
        mainTrans = Some("4029"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "9. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0020, mainTrans=4031, subTrans=1095, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0020", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0020"),
        mainTrans = Some("4031"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1095"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "10. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt [debtId=debtSA0021, mainTrans=4032, subTrans=1090, interestBearing=true, interestOnlyIndicator=false]",
      DTD_3523
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0021", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0021"),
        mainTrans = Some("4032"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1090"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

    Scenario(
      "11. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt [debtId=debtSA0022, mainTrans=4033, subTrans=1095, interestBearing=false, interestOnlyIndicator=true]"
    ) { context =>
      Given("debt details")
      val request = SolDebtsRequest(
        solType = "UI",
        customerUniqueRef = "NEHA1234",
        debts = List(
          Debt(debtId = "debtSA0022", interestRequestedTo = "2021-08-10")
        )
      )
      debtDetails(context, request)

      When("a debt statement of liability is requested")
      aDebtStatementOfLiabilityIsRequested(context)

      Then("service returns debt statement of liability data")
      val expectedSummary = SolCalculationSummaryResponseExpected(
        amountIntTotal = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      serviceReturnsDebtStatementOfLiabilityData(context, expectedSummary)

      And("the 1st sol debt summary will contain")
      val expected1stDebt = SolCalculationExpected(
        debtId = Some("debtSA0022"),
        mainTrans = Some("4033"),
        debtTypeDescription = Some("Penalty reform charge"),
        interestDueDebtTotal = Some(BigInt(4629)),
        totalAmountIntDebt = Some(BigInt(504629)),
        combinedDailyAccrual = Some(BigInt(35))
      )
      theCustomerStatementOfLiabilityContainsDebtValuesAs(context, 1, expected1stDebt)

      And("the 1st sol debt summary will contain duties")
      val expected1stDuties = List(
        SolDutyExpected(
          subTrans = Some("1095"),
          unpaidAmountDuty = Some(BigInt(500000)),
          combinedDailyAccrual = Some(BigInt(35)),
          interestBearing = Some(true),
          interestOnlyIndicator = Some(false)
        )
      )
      theSolDebtSummaryWillContainDuties(context, 1, expected1stDuties)
    }

  }
}
