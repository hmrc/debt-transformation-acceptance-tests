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
import uk.gov.hmrc.test.api.models.{SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues}

object SuppressionRulesBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addSuppressions(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class SuppressionsInput(
    code: Option[String] = None,
    description: Option[String] = None,
    enabled: Option[String] = None,
    fromDate: Option[String] = None,
    reason: Option[String] = None,
    suppressionId: Option[String] = None,
    suppressions: Option[String] = None,
    toDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addSuppressions' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps[String, String](classOf[String], classOf[String]).asScala
  //   var suppressions    = ""
  //   var id: String      = null
  //   asMapTransposed.zipWithIndex.foreach { case (suppression, index) =>
  //   val parsedFromDate = suppression.get("fromDate").toString match {
  //   case "yesterday"         => LocalDate.now().minusDays(1).toString
  //   case "2 months from now" => LocalDate.now().plusMonths(2).toString
  //   case other               => other
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addSuppressionRules(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class SuppressionRulesInput(
    mainTrans: Option[String] = None,
    periodEnd: Option[String] = None,
    postCode: Option[String] = None,
    rule: Option[String] = None,
    ruleId: Option[String] = None,
    suppressionIds: Option[String] = None,
    suppressionRules: Option[String] = None
  )

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

  // Best-effort typed model builder from legacy constructor usage.
  def toSuppressionInformation(in: SuppressionCriteriaInput): SuppressionInformation = {
    // TODO: review inferred defaults below before relying on this method.
    SuppressionInformation(
      dateFrom = /* TODO: supply dateFrom: String */ "",
      dateTo = in.dateTo,
      reason = /* TODO: supply reason: String */ "",
      reasonDesc = /* TODO: supply reasonDesc: String */ "",
      suppressionChargeDescription = /* TODO: supply suppressionChargeDescription: String */ "",
      postcode = in.postcode,
      mainTrans = in.mainTrans,
      subTrans = in.subTrans,
      checkPeriodEnd = in.checkPeriodEnd
    )
  }

  def toSuppressionInformationSeq(inputs: Seq[SuppressionCriteriaInput]): Seq[SuppressionInformation] =
    inputs.map(toSuppressionInformation)

  // Best-effort typed model builder from legacy constructor usage.
  def toSuppressionRequest(in: SuppressionCriteriaInput): SuppressionRequest = {
    // TODO: review inferred defaults below before relying on this method.
    SuppressionRequest(
      suppressions = /* TODO: supply suppressions: Seq[SuppressionInformation] */ Seq.empty[SuppressionInformation]
    )
  }

  def toSuppressionRequestSeq(inputs: Seq[SuppressionCriteriaInput]): Seq[SuppressionRequest] =
    inputs.map(toSuppressionRequest)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def postSuppressionData(context: SuppressionRulesContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:suppression-data"),
          userType = getRandomAffinityGroup
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


  def deleteNewSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
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


  def deleteSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
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


  def postSuppressionRules(context: SuppressionRulesContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:suppression-rule"),
          userType = getRandomAffinityGroup
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


  def deleteSuppressionRules(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:suppression-rule"),
          userType = getRandomAffinityGroup
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


  def updateSuppressionData(context: SuppressionRulesContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:suppression-data"),
          userType = getRandomAffinityGroup
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


  def getSuppressionData(context: SuppressionRulesContext): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
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


}
