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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions

import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.SuppressionRequest
import uk.gov.hmrc.test.api.models.sol.{SolCalculationSummaryResponse, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext

trait SuppressionStepHelpers {
  this: Matchers =>

  // ^suppression configuration data is created$
  def suppressionConfigurationDataIsCreated(context: SuppressionRulesContext, request: SuppressionRequest): Unit =
    context.suppressionRequest = Some(request)

  // ^suppression configuration is sent to ifs service$
  def suppressionConfigurationIsSentToIfsService(context: SuppressionRulesContext): Unit = {
    val requestJson         = Json.toJson(context.suppressionRequest.getOrElse(fail("Missing request in context")))
    val suppressionResponse = SuppressionRulesBuilder.putSuppressionData(requestJson)
    val suppressionStatus   = suppressionResponse.status

    suppressionStatus shouldBe 200
    context.status = suppressionStatus
    context.headers = suppressionResponse.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== SUPPRESSION REQUEST BODY ====")
    println(Json.stringify(requestJson))

    println("\n==== SUPPRESSION RESPONSE STATUS ====")
    println(context.status)
  }

  // ^a request is sent to ifs service to get suppression$
  def aRequestIsSentToSolServiceToGetSolCalculation(context: SuppressionRulesContext): Unit = {
    val solResponse  = SuppressionRulesBuilder.getStatementOfLiability(context.solRequest)
    val jsonResponse = solResponse.body[JsValue]
    context.solResponseBody = Some(jsonResponse.as[SolCalculationSummaryResponse])
    context.status = solResponse.status
    context.headers = solResponse.headers.view.mapValues(_.mkString(", ")).toMap
  }

  // ^debt details$
  def debtDetails(
    context: SuppressionRulesContext,
    request: SolDebtsRequest
  ): Unit =
    context.solRequest = Some(request)

  def serviceReturnsDebtStatementOfLiabilityDataWithSuppresion(
    context: SuppressionRulesContext,
    expectedResponse: SolCalculationSummaryResponse
  ): Unit = {
    val actual = context.solResponseBody
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

}
