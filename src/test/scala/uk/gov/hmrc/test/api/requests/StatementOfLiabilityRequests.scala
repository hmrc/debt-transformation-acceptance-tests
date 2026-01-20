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

object StatementOfLiabilityRequests extends BaseRequests with RandomValues {

  val bearerToken: String = createBearerToken(
    enrolments = Seq("read:statement-of-liability"),
    userType = getRandomAffinityGroup)

  def getStatementOfLiability(json: String): StandaloneWSResponse = {
    val baseUri = s"$statementOfLiabilityApiUrl/sol"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )

    println(s"request headers :::::::::::::::::::  ${headers.toString()}")

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  //Used by hello world only
  def getStatementLiabilityHelloWorld(endpoint: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:statement-of-liability"),
      userType = getRandomAffinityGroup)
    val baseUri     = s"$statementOfLiabilityApiUrl$endpoint"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.get(baseUri, headers = headers)
  }

  def addDutyIds(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
    var dutyIds         = ""

    asMapTransposed.zipWithIndex.foreach { case (dutyId, index) =>
      dutyIds = dutyIds.concat(
        getBodyAsString("dutyItemChargeId")
          .replaceAll("<REPLACE_dutyId>", dutyId.get("dutyId"))
      )

      if (index + 1 < asMapTransposed.size) dutyIds = dutyIds.concat(",")

    }

    val jsonWithCustomerPostCodes =
      ScenarioContext.get("debtDetails").toString
    ScenarioContext.set("debtDetails", jsonWithCustomerPostCodes)
    print(jsonWithCustomerPostCodes)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)
}
