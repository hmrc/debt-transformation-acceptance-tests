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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.sol.{SolCalculationSummaryResponse, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext

trait StatementOfLiabilityStepHelpers { this: Matchers =>

  def theSolServiceRespondWith(statusCode: Int, message: String, context: StatementOfLiabilityContext): Unit = {
    context.status       shouldBe statusCode
    context.errorMessage shouldBe Some(message)
  }

  // ^statement of liability multiple debt requests$
  def statementOfLiabilityMultipleDebtRequests(
    context: StatementOfLiabilityContext,
    request: SolDebtsRequest
  ): Unit = {

    println("SolDebtsRequest : " + request)
    context.request = Some(request)
  }

  // ^a debt statement of liability is requested$
  def aDebtStatementOfLiabilityIsRequested(context: StatementOfLiabilityContext): Unit = {
    val response         = StatementOfLiabilityBuilder.getStatementOfLiability(context.request)
    val jsonResponseBody = response.body[JsValue]
    context.status = response.status
    context.responseBody = Some(jsonResponseBody.as[SolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def statementOfLiabilityIsRequestedWithoutDebt(context: StatementOfLiabilityContext): Unit = {
    val response = StatementOfLiabilityBuilder.getStatementOfLiability(context.request)
    context.status = response.status
    context.errorMessage = Some(response.body)
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  // ^service returns debt statement of liability data$
  def serviceReturnsDebtStatementOfLiabilityData(
    context: StatementOfLiabilityContext,
    expectedResponse: SolCalculationSummaryResponse
  ): Unit = {
    val actual = context.responseBody
    println(s"actualResponseBody : " + actual)
    println(s"expectedResponse : " + Some(expectedResponse))

    context.status shouldBe 200

    actual match {
      case Some(actual) =>
        withClue("amountIntTotal: ") {
          actual.amountIntTotal shouldBe expectedResponse.amountIntTotal
        }
        withClue("combinedDailyAccrual: ") {
          actual.combinedDailyAccrual shouldBe expectedResponse.combinedDailyAccrual
        }
        withClue("debts list length: ") {
          actual.debts.length shouldBe expectedResponse.debts.length
        }

        // Verify each SolCalculation in debts list
        actual.debts.zip(expectedResponse.debts).zipWithIndex.foreach { case ((actualDebt, expectedDebt), debtIndex) =>
          withClue(s"debts[$debtIndex].debtId: ") {
            actualDebt.debtId shouldBe expectedDebt.debtId
          }
          withClue(s"debts[$debtIndex].mainTrans: ") {
            actualDebt.mainTrans shouldBe expectedDebt.mainTrans
          }
          withClue(s"debts[$debtIndex].debtTypeDescription: ") {
            actualDebt.debtTypeDescription shouldBe expectedDebt.debtTypeDescription
          }
          withClue(s"debts[$debtIndex].interestDueDebtTotal: ") {
            actualDebt.interestDueDebtTotal shouldBe expectedDebt.interestDueDebtTotal
          }
          withClue(s"debts[$debtIndex].totalAmountIntDebt: ") {
            actualDebt.totalAmountIntDebt shouldBe expectedDebt.totalAmountIntDebt
          }
          withClue(s"debts[$debtIndex].combinedDailyAccrual: ") {
            actualDebt.combinedDailyAccrual shouldBe expectedDebt.combinedDailyAccrual
          }
          withClue(s"debts[$debtIndex].parentMainTrans: ") {
            actualDebt.parentMainTrans shouldBe expectedDebt.parentMainTrans
          }
          withClue(s"debts[$debtIndex].duties list length: ") {
            actualDebt.duties.length shouldBe expectedDebt.duties.length
          }

          // Verify each SolDuty in duties list
          actualDebt.duties.zip(expectedDebt.duties).zipWithIndex.foreach {
            case ((actualDuty, expectedDuty), dutyIndex) =>
              withClue(s"debts[$debtIndex].duties[$dutyIndex].subTrans: ") {
                actualDuty.subTrans shouldBe expectedDuty.subTrans
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].dutyTypeDescription: ") {
                actualDuty.dutyTypeDescription shouldBe expectedDuty.dutyTypeDescription
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].unpaidAmountDuty: ") {
                actualDuty.unpaidAmountDuty shouldBe expectedDuty.unpaidAmountDuty
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].combinedDailyAccrual: ") {
                actualDuty.combinedDailyAccrual shouldBe expectedDuty.combinedDailyAccrual
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].interestBearing: ") {
                actualDuty.interestBearing shouldBe expectedDuty.interestBearing
              }
              withClue(s"debts[$debtIndex].duties[$dutyIndex].interestOnlyIndicator: ") {
                actualDuty.interestOnlyIndicator shouldBe expectedDuty.interestOnlyIndicator
              }
          }
        }
      case None         => fail("Response body is empty")
    }
  }

  def checkAmountIntTotalAndCombinedDailyAccrual(
    amountIntTotal: BigInt,
    combinedDailyAccrual: BigInt,
    context: StatementOfLiabilityContext
  ): Unit = {
    withClue("amountIntTotal: ") {
      context.responseBody.map(_.amountIntTotal) shouldBe Some(amountIntTotal)
    }
    withClue("combinedDailyAccrual: ") {
      context.responseBody.map(_.combinedDailyAccrual) shouldBe Some(combinedDailyAccrual)
    }
  }

  // ^the ([0-9]\\d*)(?:st|nd|rd|th) sol debt summary will contain$
  def theSolDebtSummaryWillContain(
    debtSummaryEntry: Int,
    debtId: String,
    mainTrans: String,
    debtTypeDescription: String,
    interestDueDebtTotal: BigInt,
    totalAmountIntDebt: BigInt,
    combinedDailyAccrual: BigInt,
    parentMainTrans: Option[String],
    context: StatementOfLiabilityContext
  ): Unit = {
    val maybeDebtSummary = context.responseBody.map(_.debts(debtSummaryEntry - 1))

    withClue("debtId: ") {
      maybeDebtSummary.map(_.debtId) shouldBe Some(debtId)
    }
    withClue("mainTrans: ") {
      maybeDebtSummary.map(_.mainTrans) shouldBe Some(mainTrans)
    }
    withClue("debtTypeDescription: ") {
      maybeDebtSummary.map(_.debtTypeDescription) shouldBe Some(debtTypeDescription)
    }
    withClue("interestDueDebtTotal: ") {
      maybeDebtSummary.map(_.interestDueDebtTotal) shouldBe Some(interestDueDebtTotal)
    }
    withClue("totalAmountIntDebt: ") {
      maybeDebtSummary.map(_.totalAmountIntDebt) shouldBe Some(totalAmountIntDebt)
    }
    withClue("combinedDailyAccrual: ") {
      maybeDebtSummary.map(_.combinedDailyAccrual) shouldBe Some(combinedDailyAccrual)
    }
    withClue("parentMainTrans: ") {
      if (parentMainTrans.isDefined)
        maybeDebtSummary.map(_.parentMainTrans) shouldBe Some(parentMainTrans)
    }
  }

  // ^the ([0-9])(?:st|nd|rd|th) sol debt summary will contain duties$
  def theSolDebtSummaryWillContainDuties(
    solDutyEntry: Int,
    subTrans: String,
    dutyTypeDescription: Option[String] = None,
    unpaidAmountDuty: BigInt,
    combinedDailyAccrual: BigInt,
    interestBearing: Boolean,
    interestOnlyIndicator: Boolean,
    context: StatementOfLiabilityContext
  ): Unit = {
    val maybeDebtSummary =
      context.responseBody
        .map(_.debts.headOption.getOrElse(fail("Missing Debts from Sol Calculation Summary")))
        .map(_.duties(solDutyEntry - 1))

    withClue("subTrans: ") {
      maybeDebtSummary.map(_.subTrans) shouldBe Some(subTrans)
    }
    withClue("dutyTypeDescription: ") {
      if (dutyTypeDescription.isDefined)
        maybeDebtSummary.map(_.dutyTypeDescription) shouldBe Some(dutyTypeDescription)
    }
    withClue("unpaidAmountDuty: ") {
      maybeDebtSummary.map(_.unpaidAmountDuty) shouldBe Some(unpaidAmountDuty)
    }
    withClue("combinedDailyAccrual: ") {
      maybeDebtSummary.map(_.combinedDailyAccrual) shouldBe Some(combinedDailyAccrual)
    }
    withClue("interestBearing: ") {
      maybeDebtSummary.map(_.interestBearing) shouldBe Some(interestBearing)
    }
    withClue("interestOnlyIndicator: ") {
      maybeDebtSummary.map(_.interestOnlyIndicator) shouldBe Some(interestOnlyIndicator)
    }
  }
}
