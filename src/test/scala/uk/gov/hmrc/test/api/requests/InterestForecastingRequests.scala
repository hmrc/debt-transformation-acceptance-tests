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
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

object InterestForecastingRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def getDebtCalculation(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:interest-forecasting"))
    print("New bearerToken ::::::::::::::::::" + bearerToken)

    val baseUri     = s"$interestForecostingApiUrl/debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def postSuppressionData(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:suppression-data"))
    val baseUri     = s"$interestForecostingApiUrl/suppressions/1/suppression"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def deleteSuppressionData(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:suppression-data"))
    val baseUri     = s"$interestForecostingApiUrl/suppressions"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.delete(baseUri, headers = headers)
  }

  def postSuppressionRules(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:suppression-rule"))
    val baseUri     = s"$interestForecostingApiUrl/suppression-rules/1/suppression-rule"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def deleteSuppressionRules(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:suppression-rule"))
    val baseUri     = s"$interestForecostingApiUrl/suppression-rules"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.delete(baseUri, headers = headers)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  def getSuppressionBodyAsString(variant: String): String =
    TestData.loadedSuppressionFiles(variant)

  def createInterestFocastingRequestBody(dataTable: DataTable): Unit = {
    val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem         = false
    var debtItems: String = null
    try ScenarioContext.get("debtItems")
    catch { case e: Exception => firstItem = true }

    val debtItem = getBodyAsString("debtItem")
      .replaceAll("<REPLACE_debtID>", "123")
      .replaceAll("<REPLACE_originalAmount>", asmapTransposed.get("originalAmount"))
      .replaceAll("<REPLACE_subTrans>", asmapTransposed.get("subTrans"))
      .replaceAll("<REPLACE_mainTrans>", asmapTransposed.get("mainTrans"))
      .replaceAll("<REPLACE_dateCreated>", asmapTransposed.get("dateCreated"))
      .replaceAll("<REPLACE_interestStartDate>", asmapTransposed.get("interestStartDate"))
      .replaceAll("<REPLACE_interestRequestedTo>", asmapTransposed.get("interestRequestedTo"))

    if (firstItem == true) { debtItems = debtItem }
    else { debtItems = ScenarioContext.get("debtItems").toString.concat(",").concat(debtItem) }

    ScenarioContext.set(
      "debtItems",
      debtItems
    )
    print("request json ::::::::::::::::::::::::::::::::::::" + debtItems)
  }

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
      getBodyAsString("debtCalcRequest")
        .replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
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

  def addSuppressions(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var suppressions    = ""

    asMapTransposed.zipWithIndex.foreach { case (suppression, index) =>
      suppressions = suppressions.concat(
        getSuppressionBodyAsString("suppressionData")
          .replaceAll("<REPLACE_code>", "1")
          .replaceAll("<REPLACE_reason>", suppression.get("reason").toString)
          .replaceAll("<REPLACE_enabled>", suppression.get("enabled"))
          .replaceAll("<REPLACE_fromDate>", suppression.get("fromDate").toString)
          .replaceAll("<REPLACE_toDate>", suppression.get("toDate").toString)
      )

      if (index + 1 < asMapTransposed.size) suppressions = suppressions.concat(",")
    }
    val request  = getSuppressionBodyAsString("suppressionsData").replaceAll("<REPLACE_suppressions>", suppressions)
    println(s"SUPPRESSION DATA REQUEST --> $request")
    val response = InterestForecastingRequests.postSuppressionData(request)
    response.status should be(200)
  }

  def addSuppressionRules(dataTable: DataTable): Unit = {
    val asMapTransposed  = dataTable.asMaps(classOf[String], classOf[String])
    var suppressionRules = ""

    asMapTransposed.zipWithIndex.foreach { case (rule, index) =>
      suppressionRules = suppressionRules.concat(
        getSuppressionBodyAsString("suppressionRule")
          .replaceAll("<REPLACE_ruleId>", rule.get("ruleId").toString)
          .replaceAll("<REPLACE_postCode>", rule.get("postCode").toString)
          .replaceAll("<REPLACE_suppressionIds>", rule.get("suppressionIds"))
      )

      if (index + 1 < asMapTransposed.size) suppressionRules = suppressionRules.concat(",")
    }
    val request  =
      getSuppressionBodyAsString("suppressionRules").replaceAll("<REPLACE_suppressionRules>", suppressionRules)
    println(s"SUPPRESSION RULES REQUEST --> $request")
    val response = InterestForecastingRequests.postSuppressionRules(request)
    response.status should be(200)
  }
}
