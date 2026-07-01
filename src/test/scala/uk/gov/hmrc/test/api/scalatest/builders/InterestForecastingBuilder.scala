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

object InterestForecastingBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestFocastingRequestBody(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestFocastingRequestBodyInput(
    dateCreated: Option[String] = None,
    debtID: Option[String] = None,
    debtItems: Option[String] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    mainTrans: Option[String] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInterestFocastingRequestBody' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem         = false
  //   var debtItems: String = null
  //   try ScenarioContext.get("debtItems")
  //   catch { case _: Exception => firstItem = true }
  //   var periodEnd = ""
  //   if (asmapTransposed.toString.contains("periodEnd")) {
  //   periodEnd = "\"periodEnd\": \"" + asmapTransposed.get("periodEnd") + "\","
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addPaymentHistory(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class PaymentHistoryInput(
    debtItems: Option[String] = None,
    paymentAmount: Option[BigDecimal] = None,
    paymentDate: Option[String] = None,
    payments: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addPaymentHistory' looked like template/string-body setup.
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

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class BreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtItems: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addBreathingSpace' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "debtItems",
  //   getBodyAsString("debtCalcRequest").replaceAll("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
  //   )
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var breathingSpaces = ""
  //   asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
  //   if (breathingSpace.get("debtRespiteTo").toString.contains("-")) {
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addDebtBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class DebtBreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtItems: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addDebtBreathingSpace' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var breathingSpaces = ""
  //   asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
  //   breathingSpaces = breathingSpaces.concat(
  //   getBodyAsString("breathingSpace")
  //   .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
  //   .replaceAll("<REPLACE_debtRespiteTo>", breathingSpace.get("debtRespiteTo"))
  //   )
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addCustomerPostCodes(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class CustomerPostCodesInput(
    customerPostCodes: Option[String] = None,
    debtItems: Option[String] = None,
    postCode: Option[String] = None,
    postCodeDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addCustomerPostCodes' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "debtItems",
  //   getBodyAsString("debtCalcRequest").replaceAll("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
  //   )
  //   val asMapTransposed   = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var customerPostCodes = ""
  //   asMapTransposed.zipWithIndex.foreach { case (postCode, index) =>
  //   customerPostCodes = customerPostCodes.concat(
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestTypeRequestBody(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestTypeRequestBodyInput(
    debtInterestTypes: Option[BigDecimal] = None,
    mainTrans: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getDebtCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken =
      createBearerToken(enrolments = Seq("read:interest-forecasting"), userType = getRandomAffinityGroup)
    val baseUri     = s"$interestForecastingApiUrl/debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-calculation baseUri ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

  def getDebtInterestTypeRequestBody(json: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/debt-interest-type"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-interest type baseUri ************************" + baseUri)
    print("IFS debt-interest Type json********************" + json)

    WsClient.post(baseUri, headers = headers, json)
  }

}
