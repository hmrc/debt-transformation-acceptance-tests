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
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, TestData}

object IFSInstalmentCalculationBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addDebtItemChargesToInstalmentCalculation(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class DebtItemChargesToInstalmentCalculationInput(
    debtAmount: Option[BigDecimal] = None,
    debtId: Option[String] = None,
    debtItemCharges: Option[String] = None,
    interestStartDate: Option[BigDecimal] = None,
    mainTrans: Option[String] = None,
    paymentPlan: Option[String] = None,
    periodEnd: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addDebtItemChargesToInstalmentCalculation' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var debtItemCharges = ""
  //   asMapTransposed.zipWithIndex.foreach { case (debtItemCharge, index) =>
  //   val periodEnd         = if (debtItemCharge.containsKey("periodEnd")) {
  //   s"""
  //   |,"periodEnd": "${debtItemCharge.get("periodEnd")}"
  //   |""".stripMargin
  //   } else ""
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInstalmentCalculationRequestBody(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InstalmentCalculationRequestBodyInput(
    duration: Option[Int] = None,
    durationOrInstalmentAmount: Option[BigDecimal] = None,
    instalmentPaymentAmount: Option[BigDecimal] = None,
    instalmentPaymentDate: Option[String] = None,
    instalmentPaymentDay: Option[String] = None,
    interestCallDueTotal: Option[BigDecimal] = None,
    isQuoteDateNonInclusive: Option[Boolean] = None,
    paymentFrequency: Option[String] = None,
    paymentPlan: Option[String] = None,
    quoteDate: Option[String] = None,
    quoteType: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInstalmentCalculationRequestBody' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem           = false
  //   var paymentPlan: String = null
  //   try ScenarioContext.get("paymentPlan")
  //   catch {
  //   case _: Exception => firstItem = true
  //   }
  //   val dateTime                = LocalDateTime.now()
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: debtPlanDetailsWithInitialPaymentDatePlus129Request(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class DebtPlanDetailsWithInitialPaymentDatePlus129RequestInput(
    initialPaymentAmount: Option[BigDecimal] = None,
    initialPaymentDate: Option[String] = None,
    instalmentAmount: Option[BigDecimal] = None,
    instalmentPaymentAmount: Option[BigDecimal] = None,
    instalmentPaymentDate: Option[String] = None,
    interestCallDueTotal: Option[BigDecimal] = None,
    paymentFrequency: Option[String] = None,
    paymentPlan: Option[String] = None,
    quoteDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'debtPlanDetailsWithInitialPaymentDatePlus129Request' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem           = false
  //   var paymentPlan: String = null
  //   try ScenarioContext.get("paymentPlan")
  //   catch {
  //   case _: Exception => firstItem = true
  //   }
  //   val dateTime              = LocalDateTime.now()
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addInitialPayment(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InitialPaymentInput(
    initialPayment: Option[String] = None,
    initialPaymentAmount: Option[BigDecimal] = None,
    initialPaymentDate: Option[String] = None,
    initialPaymentDays: Option[String] = None,
    paymentPlan: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addInitialPayment' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed      = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var initialPaymentDate   = ""
  //   var initialPaymentAmount = "\"\""
  //   if (asmapTransposed.toString.contains("initialPaymentDays")) {
  //   var addNumberOfDays = ""
  //   addNumberOfDays = asmapTransposed.get("initialPaymentDays")
  //   val localDate       = LocalDate.now()
  //   initialPaymentDate = localDate.plusDays(addNumberOfDays.toInt).toString
  // -----------------------------------------------------------------------

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getInstalmentCalculation(context: IFSInstalmentCalculationContext, json: String): StandaloneWSResponse = {
    val bearerToken =
          createBearerToken(enrolments = Seq("read:interest-forecasting"), userType = getRandomAffinityGroup)
        val baseUri     = s"$interestForecostingApiUrl/instalment-calculation"

        val headers = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )
        print("instalment-calculation baseUri ********************" + baseUri)
        WsClient.post(baseUri, headers = headers, Json.parse(json))
  }


  def getInstalmentCalculationWithQueryParams(context: IFSInstalmentCalculationContext, json: String, combineLastInstalments: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
        )
        val baseUri     = s"$interestForecostingApiUrl/instalment-calculation"

        val headers = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )

        val queryParameters = Map(
          "combineLastInstalments" -> s"$combineLastInstalments"
        )

        println(s"query string parameters ******************** --> $queryParameters")
        println(s"instalment-calculation baseUri ******************** --> $baseUri")

        WsClient.postWithQueryParams(baseUri, headers = headers, queryParameters = queryParameters, Json.parse(json))
  }


}
