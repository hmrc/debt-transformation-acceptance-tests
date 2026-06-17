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

package uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.models.ifs.{DebtItemCharge, InstallmentCalculationCustomerPostCode, InstalmentCalculationRequest}
import uk.gov.hmrc.test.api.scalatest.steps.context.{FCStatementOfLiabilityContext, IFSInstalmentCalculationContext}
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}

import java.time.LocalDate

class InstalmentCalculationSingleDutyFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = IFSInstalmentCalculationContext

  override def withFixture(test: OneArgTest) = {
    val context = IFSInstalmentCalculationContext()
    try test(context)
    finally ()
  }

  Feature("Instalment calculation for 1 debt 1 duty") {

    Scenario("Payment plan calculation instalment - Single payment frequency") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "single",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns single payment frequency instalment calculation plan")
      ifsServiceReturnsSinglePaymentFrequencyInstalmentCalculationPlan(context)

    }

    Scenario("Payment plan calculation instalment - weekly payment frequency") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "weekly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns weekly payment frequency instalment calculation plan")
      ifsServiceReturnsWeeklyPaymentFrequencyInstalmentCalculationPlan(context)

    }

    Scenario("Payment plan calculation instalment - 2Weekly payment frequency") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "2Weekly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      And("ifs service returns 2-Weekly frequency instalment calculation plan")
      ifsServiceReturns2WeeklyFrequencyInstalmentCalculationPlan(context)

    }

    Scenario(
      "Payment plan calculation instalment - 4Weekly payment frequency with end of month instalment start Date"
    ) { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "4Weekly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns 4-Weekly frequency instalment calculation plan")
      ifsServiceReturns4WeeklyFrequencyInstalmentCalculationPlan(context)

    }

    Scenario(
      "Payment plan calculation instalment - Quarterly payment frequency with end of Leap year instalment Date"
    ) { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "quarterly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      And("ifs service returns Quarterly payment frequency instalment calculation plan")
      ifsServiceReturnsQuarterlyPaymentFrequencyInstalmentCalculationPlan(context)

    }

    Scenario(
      "Payment plan calculation instalment - 6Monthly payment frequency instalment Date starts in non leap year to Leap year"
    ) { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-03-14"),
        paymentFrequency = "6Monthly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 3538,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      And("ifs service returns 6Monthly payment frequency instalment calculation plan")
      ifsServiceReturns6monthlyPaymentFrequencyInstalmentCalculationPlan(context)

    }

    Scenario("Payment plan calculation instalment - Annually payment frequency") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2011-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2011-03-14"),
        paymentFrequency = "annually",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns Annually payment frequency instalment calculation plan")
      ifsServiceReturnsAnnuallyPaymentFrequencyInstalmentCalculationPlan(context)

    }

    Scenario("Single debt payment instalment calculation plan - Weekly payments with initial payment 129") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-03-13"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2022-07-20"),
        paymentFrequency = "weekly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(5000),
        initialPaymentAmount = Some(5000),
        initialPaymentDate = Some(LocalDate.parse("2022-07-20"))
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("ifs service returns weekly frequency instalment calculation plan with initial payment")
      ifsServiceReturnsWeeklyFrequencyInstalmentCalculationPlanWithInitialPayment(context)

    }

    Scenario("Initial payment on same day as instalment start date") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2021-06-10"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.parse("2021-07-01"),
        paymentFrequency = "monthly",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 5000,
        instalmentPaymentAmount = Some(15000),
        initialPaymentAmount = Some(45000),
        initialPaymentDate = Some(LocalDate.parse("2021-07-01"))
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("IFS response contains expected values")
      val expectedInstalmentResponse = InstalmentCalculationSummaryResponse(
        dateOfCalculation = LocalDate.parse("2021-06-10"),
        numberOfInstalments = 5,
        planInterest = 320,
        interestAccrued = 5000,
        totalInterest = 5320,
        duration = 5,
        instalments = Seq(
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 1,
            dueDate = LocalDate.parse("2021-07-01"),
            amountDue = 60000,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 17,
            expectedPayment = 10680,
            intRate = 3.25
          ),
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 2,
            dueDate = LocalDate.parse("2021-08-01"),
            amountDue = 15000,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 17,
            expectedPayment = 10680,
            intRate = 3.25
          ),
          InstalmentResponse(
            debtId = "Debt1",
            instalmentNumber = 5,
            dueDate = LocalDate.parse("2021-11-01"),
            amountDue = 320,
            instalmentBalance = 100000,
            instalmentInterestAccrued = 17,
            expectedPayment = 10680,
            intRate = 3.25
          )
        )
      )
      ifsResponseContainsExpectedValues(context, expectedInstalmentResponse)

    }

    Scenario("Payment plan calculation request -initialPaymentDate can be today") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-02-14"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now().plusDays(129),
        paymentFrequency = "single",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000),
        initialPaymentAmount = Some(5000),
        initialPaymentDate = Some(LocalDate.now())
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("Ifs service returns response code 200")
      ifsServiceReturnsResponseCode(context, 200)

    }

    Scenario("Payment plan calculation request -instalmentDate can be today") { context =>
      Given("instalment calculation details")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1530",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.parse("2022-02-14"),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now(),
        paymentFrequency = "single",
        duration = None,
        customerPostCodes = Some(List.empty[InstallmentCalculationCustomerPostCode]),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000),
        initialPaymentAmount = Some(5000),
        initialPaymentDate = Some(LocalDate.now())
      )
      instalmentCalculationDetails(context, ifsRequest)

      When("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("Ifs service returns response code 200")
      ifsServiceReturnsResponseCode(context, 200)

    }

  }
}
