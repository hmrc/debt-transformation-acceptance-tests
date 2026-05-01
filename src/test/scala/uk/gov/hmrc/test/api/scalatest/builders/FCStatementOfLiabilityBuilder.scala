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

package uk.gov.hmrc.test.api.scalatest.builders

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests.bearerToken
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, TestData}

object FCStatementOfLiabilityBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: fcSolRequest(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FcSolRequestInput(
    customerUniqueRef: Option[String] = None,
    debtDetails: Option[String] = None,
    solRequestedDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'fcSolRequest' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem           = false
  //   var debtDetails: String = null
  //   try ScenarioContext.get("debtDetails")
  //   catch {
  //   case _: Exception => firstItem = true
  //   }
  //   val FCSolMultipleDebts = getBodyAsString("FCSolDebt")
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCDebts(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCDebtsInput(
    debtDetails: Option[String] = None,
    debtId: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None,
    solDescription: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCDebts' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var debtIds         = ""
  //   asMapTransposed.zipWithIndex.foreach { case (debtId, index) =>
  //   debtIds = debtIds.concat(
  //   getBodyAsString("debtId")
  //   .replaceAll("<REPLACE_debtId>", debtId.get("debtId"))
  //   .replaceAll("<REPLACE_originalAmount>", debtId.get("originalAmount"))
  //   .replaceAll("<REPLACE_solDescription>", "solDescription")
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: fcSolWithCotaxInterestChargeRequest(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FcSolWithCotaxInterestChargeRequestInput(
    chargedInterest: Option[BigDecimal] = None,
    debtDetails: Option[String] = None,
    debtId: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None,
    solDescription: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'fcSolWithCotaxInterestChargeRequest' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var debtIds         = ""
  //   asMapTransposed.zipWithIndex.foreach { case (debtId, index) =>
  //   debtIds = debtIds.concat(
  //   getBodyAsString("chargeInterestDebtItem")
  //   .replaceAll("<REPLACE_debtId>", debtId.get("debtId"))
  //   .replaceAll("<REPLACE_originalAmount>", debtId.get("originalAmount"))
  //   .replaceAll("<REPLACE_solDescription>", "solDescription")
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCSOLPaymentHistory(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCSOLPaymentHistoryInput(
    debtDetails: Option[String] = None,
    paymentAmount: Option[BigDecimal] = None,
    paymentDate: Option[String] = None,
    payments: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCSOLPaymentHistory' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var payments        = ""
  //   asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
  //   payments = payments.concat(
  //   getBodyAsString("payment")
  //   .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
  //   .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
  //   )
  // -----------------------------------------------------------------------

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getFCStatementOfLiability(context: FCStatementOfLiabilityContext, json: String): StandaloneWSResponse = {
    val baseUri = s"$statementOfLiabilityApiUrl/fc-sol"
        val headers = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )
        WsClient.post(baseUri, headers = headers, Json.parse(json))
  }


}
