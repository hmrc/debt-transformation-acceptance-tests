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
import uk.gov.hmrc.test.api.models.{SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.utils.ScenarioContext._
import uk.gov.hmrc.test.api.utils.{BaseRequests, TestData}

import java.time.LocalDate
import scala.jdk.CollectionConverters._

object SuppressionRulesRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def postSuppressionData(json: String, id: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-data"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def deleteNewSuppressionData(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.delete(baseUri, headers = headers)
  }

  def deleteSuppressionData(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppressions/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.delete(baseUri, headers = headers)
  }

  def postSuppressionRules(json: String, rulesID: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-rule"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppression-rules/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }
  def deleteSuppressionRules(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-rule"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppression-rules/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.delete(baseUri, headers = headers)
  }

  def updateSuppressionData(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-data"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.put(baseUri, headers = headers, Json.parse(json))
  }

  def getSuppressionData(): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/test-only/suppressions"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.get(baseUri, headers = headers)
  }

  def getSuppressionBodyAsString(variant: String): String =
    TestData.loadedSuppressionFiles(variant)

  def addSuppressionToInstalmentCalculation(
    id: String,
    code: String,
    reason: String,
    description: String,
    from: LocalDate,
    durationMonths: Int
  ): Unit = {
    val suppressions = getSuppressionBodyAsString("suppressionData")
      .replaceAll("<REPLACE_code>", code)
      .replaceAll("<REPLACE_reason>", reason)
      .replaceAll("<REPLACE_description>", description)
      .replaceAll("<REPLACE_enabled>", "true")
      .replaceAll("<REPLACE_fromDate>", from.toString())
      .replaceAll("<REPLACE_toDate>", from.plusMonths(durationMonths).toString())

    val request  = getSuppressionBodyAsString("suppressionsData").replaceAll("<REPLACE_suppressions>", suppressions)
    val response = SuppressionRulesRequests.postSuppressionData(request, id)
    response.status should be(200)
  }

  def addSuppressions(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps[String, String](classOf[String], classOf[String]).asScala
    var suppressions    = ""
    var id: String      = null

    asMapTransposed.zipWithIndex.foreach { case (suppression, index) =>
      val parsedFromDate = suppression.get("fromDate").toString match {
        case "yesterday"         => LocalDate.now().minusDays(1).toString()
        case "2 months from now" => LocalDate.now().plusMonths(2).toString()
        case other               => other
      }

      val parsedToDate = suppression.get("toDate").toString match {
        case "2 months from now" => LocalDate.now().plusMonths(2).toString
        case "4 months from now" => LocalDate.now().plusMonths(4).toString()
        case other               => other
      }
      val code         = if (suppression.containsKey("code")) suppression.get("code") else "1"
      suppressions = suppressions.concat(
        getSuppressionBodyAsString("suppressionData")
          .replaceAll("<REPLACE_code>", code)
          .replaceAll("<REPLACE_reason>", suppression.get("reason"))
          .replaceAll("<REPLACE_description>", suppression.get("description"))
          .replaceAll("<REPLACE_enabled>", suppression.get("enabled"))
          .replaceAll("<REPLACE_fromDate>", parsedFromDate)
          .replaceAll("<REPLACE_toDate>", parsedToDate)
      )

      if (asMapTransposed.toString.contains("suppressionId")) {
        id = suppression.get("suppressionId")
      } else (id = "1")

      if (index + 1 < asMapTransposed.size) suppressions = suppressions.concat(",")
    }

    val request  = getSuppressionBodyAsString("suppressionsData").replaceAll("<REPLACE_suppressions>", suppressions)
    println(s"SUPPRESSION DATA REQUEST --> $request")
    val response = SuppressionRulesRequests.postSuppressionData(request, id)
    response.status should be(200)
  }

  def addSuppressionRules(dataTable: DataTable): Unit = {
    val asMapTransposed  = dataTable.asMaps(classOf[String], classOf[String]).asScala
    var suppressionRules = ""
    var rulesID          = ""

    if (asMapTransposed.toString.contains("postCode")) {
      asMapTransposed.zipWithIndex.foreach { case (rule, index) =>
        val postCodeRule =
          "IF postCode LIKE '<REPLACE_postCode>'".replaceAll("<REPLACE_postCode>", rule.get("postCode").toString)
        suppressionRules = suppressionRules.concat(
          getSuppressionBodyAsString("suppressionRule")
            .replaceAll("<REPLACE_ruleId>", rule.get("ruleId").toString)
            .replaceAll("<REPLACE_rule>", postCodeRule)
            .replaceAll("<REPLACE_suppressionIds>", rule.get("suppressionIds"))
        )
        if (index + 1 < asMapTransposed.size) suppressionRules = suppressionRules.concat(",")
      }
      rulesID = "1"
    }

    if (asMapTransposed.toString.contains("mainTrans")) {
      asMapTransposed.zipWithIndex.foreach { case (rule, index) =>
        val postCodeRule =
          "IF mainTrans LIKE '<REPLACE_mainTrans>'".replaceAll("<REPLACE_mainTrans>", rule.get("mainTrans").toString)
        suppressionRules = suppressionRules.concat(
          getSuppressionBodyAsString("suppressionRule")
            .replaceAll("<REPLACE_ruleId>", rule.get("ruleId").toString)
            .replaceAll("<REPLACE_rule>", postCodeRule)
            .replaceAll("<REPLACE_suppressionIds>", rule.get("suppressionIds"))
        )
        if (index + 1 < asMapTransposed.size) suppressionRules = suppressionRules.concat(",")
      }
      rulesID = "2"
    }

    if (asMapTransposed.toString.contains("periodEnd")) {
      asMapTransposed.zipWithIndex.foreach { case (rule, index) =>
        val periodEndRule =
          "IF periodEnd LIKE '<REPLACE_periodEnd>'".replaceAll("<REPLACE_periodEnd>", rule.get("periodEnd").toString)
        suppressionRules = suppressionRules.concat(
          getSuppressionBodyAsString("suppressionRule")
            .replaceAll("<REPLACE_ruleId>", rule.get("ruleId").toString)
            .replaceAll("<REPLACE_rule>", periodEndRule)
            .replaceAll("<REPLACE_suppressionIds>", rule.get("suppressionIds"))
        )
        if (index + 1 < asMapTransposed.size) suppressionRules = suppressionRules.concat(",")
      }
      rulesID = "3"
    }

    val request =
      getSuppressionBodyAsString("suppressionRules").replaceAll("<REPLACE_suppressionRules>", suppressionRules)

    println(s"SUPPRESSION RULES REQUEST --> $request")

    val response = SuppressionRulesRequests.postSuppressionRules(request, rulesID)
    println(response.body)
    response.status should be(200)
  }

  def addSuppressionCriteria(dataTable: DataTable): Unit = {
    val rows            = dataTable.asMaps[String, String](classOf[String], classOf[String]).asScala.map(_.asScala)
    var suppressionInfo = List[SuppressionInformation]()

    rows.foreach { supInfo =>
      val dateFrom                     = supInfo("dateFrom")
      val dateTo                       = supInfo.get("dateTo")
      val reason                       = supInfo("reason")
      val reasonDesc                   = supInfo("reasonDesc")
      val suppressionChargeDescription = supInfo("suppressionChargeDescription")
      val mainTrans                    = supInfo.get("mainTrans").filter(_.nonEmpty)
      val subTrans                     = supInfo.get("subTrans").filter(_.nonEmpty)
      val postcode                     = supInfo.get("postcode").filter(_.nonEmpty)
      val checkPeriodEnd               = supInfo.get("checkPeriodEnd").filter(_.nonEmpty).map(_.toBoolean)

      val suppressionApplied = SuppressionInformation(
        dateFrom = dateFrom,
        dateTo = dateTo,
        reason = reason,
        reasonDesc = reasonDesc,
        suppressionChargeDescription = suppressionChargeDescription,
        mainTrans = mainTrans,
        subTrans = subTrans,
        postcode = postcode,
        checkPeriodEnd = checkPeriodEnd
      )

      suppressionInfo ::= suppressionApplied
    }

    val suppressionRequest     = SuppressionRequest(suppressions = suppressionInfo)
    val suppressionRequestJson = Json.toJson(suppressionRequest)
    setSuppression("suppressionsData", suppressionInfo)
    set("suppressionsJson", Json.stringify(suppressionRequestJson))
    println(s"suppression request json body ************************ $suppressionRequestJson")
  }
}
