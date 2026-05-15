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
import uk.gov.hmrc.test.api.models.sol.{SolCalculationSummaryResponse, SolDebtsRequest}
import uk.gov.hmrc.test.api.models.{SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext

trait SuppresionStepHelpers { this: Matchers =>

  // ^suppression configuration data is created$
  def suppressionConfigurationDataIsCreated(context: SuppressionRulesContext, request: SuppressionInformation): Unit =
    context.ifsRequest = Some(SuppressionRequest(List(request)))

  // ^suppression configuration is sent to ifs service$
  def suppressionConfigurationIsSentToIfsService(context: SuppressionRulesContext): Unit = {
    val ifsResponse = SuppressionRulesBuilder.putSuppressionData(context.ifsRequest)
    val ifsStatus   = ifsResponse.status
    ifsStatus shouldBe 200
    context.status = ifsStatus
    context.headers = ifsResponse.headers.view.mapValues(_.mkString(", ")).toMap
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

  def checkAmountIntTotalAndCombinedDailyAccrual(
    amountIntTotal: BigInt,
    combinedDailyAccrual: BigInt,
    context: SuppressionRulesContext
  ): Unit = {
    withClue("amountIntTotal: ") {
      context.solResponseBody.map(_.amountIntTotal) shouldBe Some(amountIntTotal)
    }
    withClue("combinedDailyAccrual: ") {
      context.solResponseBody.map(_.combinedDailyAccrual) shouldBe Some(combinedDailyAccrual)
    }
  }

  def checkDebtSummaryContains(
    debtSummaryEntry: Int,
    debtId: String,
    mainTrans: String,
    debtTypeDescription: String,
    interestDueDebtTotal: BigInt,
    totalAmountIntDebt: BigInt,
    combinedDailyAccrual: BigInt,
    parentMainTrans: Option[String],
    context: SuppressionRulesContext
  ): Unit = {
    val maybeDebtSummary = context.solResponseBody.map(_.debts(debtSummaryEntry - 1))

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

  def checkSolDutyOfFirstSolCalculationContains(
    solDutyEntry: Int,
    subTrans: String,
    dutyTypeDescription: Option[String],
    unpaidAmountDuty: BigInt,
    combinedDailyAccrual: BigInt,
    interestBearing: Boolean,
    interestOnlyIndicator: Boolean,
    context: SuppressionRulesContext
  ): Unit = {
    val maybeDebtSummary =
      context.solResponseBody
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
