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
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

import java.time.LocalDate
import java.util.Date
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object InterestForecastingRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def getDebtCalculation(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-calculation baseUri ************************" + baseUri)
    print("IFS debt-calculation request json********************" + Json.parse(json))

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }
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

  def createInterestFocastingRequestBody(dataTable: DataTable): Unit = {
    val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem         = false
    var debtItems: String = null
    try ScenarioContext.get("debtItems")
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

    val debtItem = getBodyAsString("debtItem")
      .replaceAll("<REPLACE_debtID>", "123")
      .replaceAll("<REPLACE_originalAmount>", asmapTransposed.get("originalAmount"))
      .replaceAll("<REPLACE_subTrans>", asmapTransposed.get("subTrans"))
      .replaceAll("<REPLACE_mainTrans>", asmapTransposed.get("mainTrans"))
      .replaceAll("<REPLACE_dateCreated>", dateCreated)
      .replaceAll("<REPLACE_interestStartDate>", interestStartDate)
      .replaceAll("<REPLACE_interestRequestedTo>", asmapTransposed.get("interestRequestedTo"))
      .replaceAll("<REPLACE_periodEnd>", periodEnd)

    if (firstItem == true) { debtItems = debtItem }
    else { debtItems = ScenarioContext.get("debtItems").toString.concat(",").concat(debtItem) }

    ScenarioContext.set(
      "debtItems",
      debtItems
    )
    print("IFS debt-calculation request::::::::::::::::::" + debtItems)
  }

  def createInterestForcastingRequestWithNoDebtItems(): Unit =
    ScenarioContext.set(
      "debtItems",
      "{\"debtItems\":[],\"breathingSpaces\": []}"
    )

  def addPaymentHistory(dataTable: DataTable): Unit = {
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
    val jsonWithPayments = ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("debtItems", jsonWithPayments)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithPayments)

  }

  def customerWithNoPaymentHistory(): Unit =
    ScenarioContext.set("debtItems", ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_payments>", ""))

  def addBreathingSpace(dataTable: DataTable): Unit = {
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "debtItems",
      getBodyAsString("debtCalcRequest")
        .replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
    )

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
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_breathingSpaces>", breathingSpaces)
    ScenarioContext.set("debtItems", jsonWithbreathingSpaces)
  }

  def noBreathingSpace() {
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "debtItems",
      getBodyAsString("debtCalcRequest").replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
    )
    ScenarioContext.set(
      "debtItems",
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_breathingSpaces>", "")
    )
  }

  def addCustomerPostCodes(dataTable: DataTable): Unit = {
    val asMapTransposed   = dataTable.asMaps(classOf[String], classOf[String])
    var customerPostCodes = ""

    asMapTransposed.zipWithIndex.foreach { case (postCode, index) =>
      customerPostCodes = customerPostCodes.concat(
        getBodyAsString("customerPostCodes")
          .replaceAll("<REPLACE_postCode>", postCode.get("postCode"))
          .replaceAll("<REPLACE_postCodeDate>", postCode.get("postCodeDate"))
      )

      if (index + 1 < asMapTransposed.size) customerPostCodes = customerPostCodes.concat(",")

    }

    val jsonWithCustomerPostCodes =
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_customerPostCodes>", customerPostCodes)
    ScenarioContext.set("debtItems", jsonWithCustomerPostCodes)
  }

  def noCustomerPostCodes() {
    ScenarioContext.set(
      "debtItems",
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_customerPostCodes>", "")
    )
  }

}
