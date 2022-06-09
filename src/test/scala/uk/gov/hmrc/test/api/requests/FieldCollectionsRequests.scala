/*
 * Copyright 2020 HM Revenue & Customs
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
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object FieldCollectionsRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def getDebtCalculation(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/fc-debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-calculation baseUri ************************" + baseUri)
    print("IFS debt-calculation request json********************" + Json.parse(json))

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def postNewInterestRatesTable(json: String): StandaloneWSResponse =
    WsClient.post(
      dataForIFSApis("test-only/rates")._1,
      headers = dataForIFSApis("test-only/rates")._2,
      Json.parse(json)
    )

  def getAllRules =
    WsClient.get(dataForIFSApis("rules")._1, headers = dataForIFSApis("rules")._2)

  def postNewInterestRate(json: String, referenceId: String): StandaloneWSResponse =
    WsClient.put(
      dataForIFSApis(s"test-only/rates/$referenceId/interestRate")._1,
      headers = dataForIFSApis(s"test-only/rates/$referenceId/interestRate")._2,
      Json.parse(json)
    )

  def postNewRulesTable(json: String): StandaloneWSResponse =
    WsClient.post(dataForIFSApis("test-only/rules")._1, headers = dataForIFSApis("rules")._2, Json.parse(json))

  private def dataForIFSApis(uri: String) = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
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

  def createInterestFocastingRequestBodyFC(dataTable: DataTable): Unit = {
    val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem         = false
    var debtItems: String = null
    try ScenarioContext.get("fcDebtItem")
    catch { case e: Exception => firstItem = true }

    var periodEnd = ""
    if (asmapTransposed.toString.contains("periodEnd")) {
      periodEnd = "\"periodEnd\": \"" + asmapTransposed.get("periodEnd") + "\","
    } else { periodEnd = "" }

    var dateCreated = ""
    if (asmapTransposed.toString.contains("dateCreated")) {
      periodEnd = "\"dateCreated\": \"" + asmapTransposed.get("dateCreated") + "\","
    } else { dateCreated = "" }

    var interestStartDate = ""
    if (asmapTransposed.toString.contains("interestStartDate")) {
      interestStartDate = "\"interestStartDate\": \"" + asmapTransposed.get("interestStartDate") + "\","
    } else { dateCreated = "" }

    val fcDebtItem = getBodyAsString("fcDebtItem")
      .replaceAll("<REPLACE_debtId>", asmapTransposed.get("debtId"))
      .replaceAll("<REPLACE_originalAmount>", asmapTransposed.get("originalAmount"))
      .replaceAll("<REPLACE_interestIndicator>", asmapTransposed.get("interestIndicator"))
      .replaceAll("<REPLACE_periodEnd>", asmapTransposed.get("periodEnd"))
      .replaceAll("<REPLACE_interestStartDate>", asmapTransposed.get("interestStartDate"))
      .replaceAll("<REPLACE_interestRequestedTo>", asmapTransposed.get("interestRequestedTo"))
      .replaceAll("<REPLACE_periodEnd>", periodEnd)

    if (firstItem == true) { debtItems = fcDebtItem }
    else { debtItems = ScenarioContext.get("fcDebtItem").toString.concat(",").concat(fcDebtItem) }

    ScenarioContext.set(
      "fcDebtItem",
      debtItems
    )

    print("IFS debt-calculation request::::::::::::::::::" + debtItems)
  }

  def createInterestForcastingRequestWithNoDebtItems(): Unit =
    ScenarioContext.set(
      "debtItems",
      "{\"debtItems\":[],\"breathingSpaces\": []}"
    )

  def addFCPaymentHistory(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var payments        = ""

    asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
      payments = payments.concat(
        getBodyAsString("payment")
          .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
          .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
      )

      if (index + 1 < asMapTransposed.size) payments = payments.concat(",")
    }
    val jsonWithPayments = ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("fcDebtItem", jsonWithPayments)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithPayments)

  }

  def fcCustomerWithNoPaymentHistory(): Unit =
    ScenarioContext.set("fcDebtItem", ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_payments>", ""))

  def addFCBreathingSpace(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var breathingSpaces = ""

    asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
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
      ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_breathingSpaces>", breathingSpaces)
    ScenarioContext.set("fcDebtItem", jsonWithbreathingSpaces)
  }

  def noFCBreathingSpace() {
    ScenarioContext.set(
      "fcDebtItem",
      ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_breathingSpaces>", "")
    )
  }

  def addFCCustomerPostCodes(dataTable: DataTable): Unit = {
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "fcDebtItem",
      getBodyAsString("fcdebtCalcRequest")
        .replaceAllLiterally("<REPLACE_fcDebtItem>", ScenarioContext.get("fcDebtItem"))
    )

    val asMapTransposed   = dataTable.asMaps(classOf[String], classOf[String])
    var customerPostCodes = ""

    asMapTransposed.zipWithIndex.foreach { case (postCode, index) =>
      customerPostCodes = customerPostCodes.concat(
        getBodyAsString("fcCustomerPostCodes")
          .replaceAll("<REPLACE_addressPostcode>", postCode.get("addressPostcode"))
          .replaceAll("<REPLACE_postcodeDate>", postCode.get("postcodeDate"))
      )

      if (index + 1 < asMapTransposed.size) customerPostCodes = customerPostCodes.concat(",")

    }

    val jsonWithCustomerPostCodes =
      ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_fcCustomerPostCodes>", customerPostCodes)
    ScenarioContext.set("fcDebtItem", jsonWithCustomerPostCodes)
  }

  def noFCCustomerPostCodes() {
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "fcDebtItem",
      getBodyAsString("fcdebtCalcRequest")
        .replaceAllLiterally("<REPLACE_fcDebtItem>", ScenarioContext.get("fcDebtItem"))
    )

    ScenarioContext.set(
      "fcDebtItem",
      ScenarioContext.get("fcDebtItem").toString.replaceAll("<REPLACE_fcCustomerPostCodes>", "")
    )
  }
}
