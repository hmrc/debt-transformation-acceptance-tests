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

import play.api.libs.json.JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues}

object FieldCollectionsVATBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestForecastingRequestBodyFCVAT(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestForecastingRequestBodyFCVATInput(
    debtItemChargeId: Option[String] = None,
    fcVatDebtItem: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInterestForecastingRequestBodyFCVAT' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem         = false
  //   var debtItems: String = null
  //   try ScenarioContext.get("fcVatDebtItem")
  //   catch { case _: Exception => firstItem = true }
  //   val fcVatDebtItem = getBodyAsString("fcVatDebtItem")
  //   .replaceAll("<REPLACE_debtItemChargeId>", asmapTransposed.get("debtItemChargeId"))
  //   .replaceAll("<REPLACE_originalAmount>", asmapTransposed.get("originalAmount"))
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCVATPaymentHistory(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCVATPaymentHistoryInput(
    fcVatDebtItem: Option[String] = None,
    paymentAmount: Option[BigDecimal] = None,
    paymentDate: Option[String] = None,
    payments: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCVATPaymentHistory' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
  //   var payments        = ""
  //   asMapTransposed.asScala.zipWithIndex.foreach { case (payment, index) =>
  //   payments = payments.concat(
  //   getBodyAsString("payment")
  //   .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
  //   .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
  //   )
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCVATBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCVATBreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None,
    fcVatDebtItem: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getDebtCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/fc-vat-debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS FC VAT debt-calculation baseUri ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

}
