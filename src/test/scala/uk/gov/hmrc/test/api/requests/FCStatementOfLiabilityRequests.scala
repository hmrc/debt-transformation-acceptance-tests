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

import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, ScenarioContext, TestData}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object FCStatementOfLiabilityRequests extends BaseRequests with RandomValues {

  val bearerToken = createBearerToken(
    enrolments = Seq("read:statement-of-liability"),
    userType = getRandomAffinityGroup
  )

  def getFCStatementOfLiability(json: String): StandaloneWSResponse = {
    val baseUri = s"$statementOfLiabilityApiUrl/fc-sol"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def fcSolRequest(dataTable: DataTable): Unit = {
    val asMapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem           = false
    var debtDetails: String = null

    try ScenarioContext.get("debtDetails")
    catch {
      case e: Exception => firstItem = true
    }

    val FCSolMultipleDebts = getBodyAsString("FCSolDebt")
      .replaceAll("REPLACE_customerUniqueRef", asMapTransposed.get("customerUniqueRef"))
      .replaceAll("REPLACE_solRequestedDate", asMapTransposed.get("solRequestedDate"))
    if (firstItem) {
      debtDetails = FCSolMultipleDebts
    } else {
      debtDetails = ScenarioContext.get("debtDetails").toString.concat(",").concat(FCSolMultipleDebts)
    }

    ScenarioContext.set("debtDetails", debtDetails)
    println(debtDetails)
  }

  def addFCDebts(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
    var debtIds         = ""
    asMapTransposed.zipWithIndex.foreach { case (debtId, index) =>
      debtIds = debtIds.concat(
        getBodyAsString("debtId")
          .replaceAll("<REPLACE_debtId>", debtId.get("debtId"))
          .replaceAll("<REPLACE_originalAmount>", debtId.get("originalAmount"))
          .replaceAll("<REPLACE_solDescription>", "solDescription")
          .replaceAll("<REPLACE_interestStartDate>", debtId.get("interestStartDate"))
          .replaceAll("<REPLACE_interestRequestedTo>", debtId.get("interestRequestedTo"))
          .replaceAll("<REPLACE_interestIndicator>", debtId.get("interestIndicator"))
          .replaceAll("<REPLACE_periodEnd>", debtId.get("periodEnd"))
      )
      if (index + 1 < asMapTransposed.size) debtIds = debtIds.concat(",")
    }
    val jsonWithdebtIds = ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_debtId>", debtIds)
    ScenarioContext.set("debtDetails", jsonWithdebtIds)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithdebtIds)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  def fcSolWithCotaxInterestChargeRequest(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
    var debtIds         = ""
    asMapTransposed.zipWithIndex.foreach { case (debtId, index) =>
      debtIds = debtIds.concat(
        getBodyAsString("chargeInterestDebtItem")
          .replaceAll("<REPLACE_debtId>", debtId.get("debtId"))
          .replaceAll("<REPLACE_originalAmount>", debtId.get("originalAmount"))
          .replaceAll("<REPLACE_solDescription>", "solDescription")
          .replaceAll("<REPLACE_interestStartDate>", debtId.get("interestStartDate"))
          .replaceAll("<REPLACE_interestRequestedTo>", debtId.get("interestRequestedTo"))
          .replaceAll("<REPLACE_interestIndicator>", debtId.get("interestIndicator"))
          .replaceAll("<REPLACE_periodEnd>", debtId.get("periodEnd"))
          .replaceAll("<REPLACE_chargedInterest>", debtId.get("chargedInterest"))
      )
      if (index + 1 < asMapTransposed.size) debtIds = debtIds.concat(",")
    }
    val jsonWithdebtIds = ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_debtId>", debtIds)
    ScenarioContext.set("debtDetails", jsonWithdebtIds)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithdebtIds)
  }

  def addFCSOLPaymentHistory(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
    var payments        = ""

    asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
      payments = payments.concat(
        getBodyAsString("payment")
          .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
          .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
      )

      if (index + 1 < asMapTransposed.size) payments = payments.concat(",")
    }
    val jsonWithPayments = ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("debtDetails", jsonWithPayments)
    print("debt with payment history ::::::::::::::::::::::::::::::" + jsonWithPayments)
  }

  def FCSolWithNoPaymentHistory(): Unit =
    ScenarioContext.set("debtDetails", ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_payments>", ""))

  def FCSolWithNoDebts(): Unit =
    ScenarioContext.set("debtDetails", ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_debtId>", ""))

}
