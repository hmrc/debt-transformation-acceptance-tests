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

import org.scalatest.Assertions.fail
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.models.sol.SolDebtsRequest
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues}

object SuppressionRulesBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Legacy method 'addSuppressionRules' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed  = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var suppressionRules = ""
  //   var rulesID          = ""
  //   if (asMapTransposed.toString.contains("postCode")) {
  //   asMapTransposed.zipWithIndex.foreach { case (rule, index) =>
  //   val postCodeRule =
  //   "IF postCode LIKE '<REPLACE_postCode>'".replaceAll("<REPLACE_postCode>", rule.get("postCode").toString)
  //   suppressionRules = suppressionRules.concat(
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addSuppressionCriteria(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class SuppressionCriteriaInput(
    checkPeriodEnd: Option[Boolean] = None,
    dateInFuture: Option[String] = None,
    dateTo: Option[String] = None,
    mainTrans: Option[String] = None,
    postcode: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getStatementOfLiability(maybeRequest: Option[SolDebtsRequest]): StandaloneWSResponse = {
    val bearerToken =
      createBearerToken(enrolments = Seq("read:statement-of-liability"), userType = getRandomAffinityGroup)

    val baseUri              = s"$statementOfLiabilityApiUrl/sol"
    val jsonRequest: JsValue = maybeRequest.fold(fail("Missing request for API call"))(Json.toJson(_))

    println("debt management baseUri ************************" + baseUri)
    println("debt management request json *******************" + jsonRequest)

    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )

    println(s"request headers :::::::::::::::::::  ${headers.toString()}")

    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

  def putSuppressionData(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-data"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.put(baseUri, headers = headers, jsonRequest)
  }

  def deleteNewSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.delete(baseUri, headers = headers)
  }

  def deleteSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppressions/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.delete(baseUri, headers = headers)
  }

  def postSuppressionRules(context: SuppressionRulesContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-rule"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppression-rules/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def deleteSuppressionRules(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-rule"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppression-rules/old"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.delete(baseUri, headers = headers)
  }

  def updateSuppressionData(context: SuppressionRulesContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:suppression-data"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppressions/overrides"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("url ************************" + baseUri)
    WsClient.put(baseUri, headers = headers, Json.parse(json))
  }

  def getSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/test-only/suppressions"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print(s"Suppression bearer token ************************  $bearerToken")
    print(s"url ************************  $baseUri")
    WsClient.get(baseUri, headers = headers)
  }

}
