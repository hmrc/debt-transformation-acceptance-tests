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
import uk.gov.hmrc.test.api.requests.StatementOfLiabilityRequests.bearerToken
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues}

object StatementOfLiabilityBuilder extends BaseRequests with RandomValues {

  def getStatementOfLiability(maybeRequest: Option[SolDebtsRequest]): StandaloneWSResponse = {
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
