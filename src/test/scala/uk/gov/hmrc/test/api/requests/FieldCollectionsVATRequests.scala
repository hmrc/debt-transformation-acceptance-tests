/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.requests

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object FieldCollectionsVATRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def getDebtCalculation(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecostingApiUrl/fc-vat-debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS FC VAT debt-calculation baseUri ************************" + baseUri)
    print("IFS FC VAT debt-calculation request json********************" + Json.parse(json))

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def getAllRules =
    WsClient.get(dataForIFSApis("rules")._1, headers = dataForIFSApis("rules")._2)

  private def dataForIFSApis(uri: String) = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecostingApiUrl/$uri"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    (baseUri, headers)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  def createInterestForecastingRequestBodyFCVAT(dataTable: DataTable): Unit = {
    val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem         = false
    var debtItems: String = null
    try ScenarioContext.get("fcVatDebtItem")
    catch { case e: Exception => firstItem = true }

    val fcVatDebtItem = getBodyAsString("fcVatDebtItem")
      .replaceAll("<REPLACE_debtItemChargeId>", asmapTransposed.get("debtItemChargeId"))
      .replaceAll("<REPLACE_originalAmount>", asmapTransposed.get("originalAmount"))
      .replaceAll("<REPLACE_interestIndicator>", asmapTransposed.get("interestIndicator"))
      .replaceAll("<REPLACE_interestRequestedTo>", asmapTransposed.get("interestRequestedTo"))
      .replaceAll("<REPLACE_periodEnd>", asmapTransposed.get("periodEnd"))

    if (firstItem) { debtItems = fcVatDebtItem }
    else { debtItems = ScenarioContext.get("fcVatDebtItem").toString.concat(",").concat(fcVatDebtItem) }

    ScenarioContext.set(
      "fcVatDebtItem",
      debtItems
    )
    print("IFS FC VAT debt-calculation request::::::::::::::::::" + debtItems)
  }

  def addFCVATPaymentHistory(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var payments        = ""

    asMapTransposed.asScala.zipWithIndex.foreach { case (payment, index) =>
      payments = payments.concat(
        getBodyAsString("payment")
          .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
          .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
      )

      if (index + 1 < asMapTransposed.size) payments = payments.concat(",")
    }
    val jsonWithPayments = ScenarioContext.get("fcVatDebtItem").toString.replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("fcVatDebtItem", jsonWithPayments)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithPayments)

  }

  def fcVatCustomerWithNoPaymentHistory(): Unit =
    ScenarioContext.set(
      "fcVatDebtItem",
      ScenarioContext.get("fcVatDebtItem").toString.replaceAll("<REPLACE_payments>", "")
    )

  def addFCVATBreathingSpace(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var breathingSpaces = ""

    asMapTransposed.asScala.zipWithIndex.foreach { case (breathingSpace, index) =>
      if (breathingSpace.get("debtRespiteTo").toString.contains("-")) {
        breathingSpaces = breathingSpaces.concat(
          getBodyAsString("breathingSpace")
            .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
            .replaceAll("<REPLACE_debtRespiteTo>", breathingSpace.get("debtRespiteTo"))
        )
      } else {
        breathingSpaces = breathingSpaces.concat(
          getBodyAsString("breathingSpace")
            .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
            .replaceAll(",\"debtRespiteTo\" :\"<REPLACE_debtRespiteTo>\"", "")
        )
      }

      if (index + 1 < asMapTransposed.size) breathingSpaces = breathingSpaces.concat(",")

    }

    val jsonWithbreathingSpaces =
      ScenarioContext.get("fcVatDebtItem").toString.replaceAll("<REPLACE_breathingSpaces>", breathingSpaces)
    ScenarioContext.set("fcVatDebtItem", jsonWithbreathingSpaces)
  }

  def noFCVatBreathingSpace(): Unit =
    ScenarioContext.set(
      "fcVatDebtItem",
      ScenarioContext.get("fcVatDebtItem").toString.replaceAll("<REPLACE_breathingSpaces>", "")
    )

  def addFcVatDebtItemRequest(): Unit =
    ScenarioContext.set(
      "fcVatDebtItem",
      getBodyAsString("fcVatDebtCalcRequest")
        .replaceAll("<REPLACE_fcVatDebtItem>", ScenarioContext.get("fcVatDebtItem"))
    )

}
