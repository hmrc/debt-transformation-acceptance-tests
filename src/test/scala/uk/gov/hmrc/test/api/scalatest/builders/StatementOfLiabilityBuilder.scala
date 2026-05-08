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
import uk.gov.hmrc.test.api.requests.StatementOfLiabilityRequests.bearerToken
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, TestData}

object StatementOfLiabilityBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addDutyIds(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class DutyIdsInput(
    debtDetails: Option[String] = None,
    dutyId: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addDutyIds' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var dutyIds         = ""
  //   asMapTransposed.zipWithIndex.foreach { case (dutyId, index) =>
  //   dutyIds = dutyIds.concat(
  //   getBodyAsString("dutyItemChargeId")
  //   .replaceAll("<REPLACE_dutyId>", dutyId.get("dutyId"))
  //   )
  //   if (index + 1 < asMapTransposed.size) dutyIds = dutyIds.concat(",")
  // -----------------------------------------------------------------------

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getStatementOfLiability(context: StatementOfLiabilityContext, json: String): StandaloneWSResponse = {
    val baseUri = s"$statementOfLiabilityApiUrl/sol"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )

    println(s"request headers :::::::::::::::::::  ${headers.toString()}")

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def getStatementLiabilityHelloWorld(context: StatementOfLiabilityContext, endpoint: String): StandaloneWSResponse = {
    val bearerToken =
      createBearerToken(enrolments = Seq("read:statement-of-liability"), userType = getRandomAffinityGroup)
    val baseUri     = s"$statementOfLiabilityApiUrl$endpoint"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.get(baseUri, headers = headers)
  }

}
