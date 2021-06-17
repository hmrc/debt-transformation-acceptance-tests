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
import uk.gov.hmrc.test.api.utils.{BaseRequests, TestData}

object SuppressionRulesRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

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

  def postSuppressionRules(json: String, rulesID: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(enrolments = Seq("read:suppression-rule"))
    val baseUri     = s"$interestForecostingApiUrl/suppression-rules/" + rulesID + "/suppression-rule"
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

  def getSuppressionBodyAsString(variant: String): String =
    TestData.loadedSuppressionFiles(variant)

  def addSuppressions(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var suppressions    = ""

    asMapTransposed.zipWithIndex.foreach { case (suppression, index) =>
      suppressions = suppressions.concat(
        getSuppressionBodyAsString("suppressionData")
          .replaceAll("<REPLACE_code>", "1")
          .replaceAll("<REPLACE_reason>", suppression.get("reason").toString)
          .replaceAll("<REPLACE_description>", suppression.get("description").toString)
          .replaceAll("<REPLACE_enabled>", suppression.get("enabled"))
          .replaceAll("<REPLACE_fromDate>", suppression.get("fromDate").toString)
          .replaceAll("<REPLACE_toDate>", suppression.get("toDate").toString)
      )

      if (index + 1 < asMapTransposed.size) suppressions = suppressions.concat(",")
    }
    val request  = getSuppressionBodyAsString("suppressionsData").replaceAll("<REPLACE_suppressions>", suppressions)
    println(s"SUPPRESSION DATA REQUEST --> $request")
    val response = SuppressionRulesRequests.postSuppressionData(request)
    response.status should be(200)
  }

  def addSuppressionRules(dataTable: DataTable): Unit = {
    val asMapTransposed          = dataTable.asMaps(classOf[String], classOf[String])
    var suppressionRules = ""
    var rulesID = ""

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
    response.status should be(200)
  }
}
