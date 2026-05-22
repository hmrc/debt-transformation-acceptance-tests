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
import uk.gov.hmrc.test.api.models.sol.SolMultipleDebtsRequest
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests.{bearerToken, statementOfLiabilityApiUrl}
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, TestData}

object FCStatementOfLiabilityBuilder extends BaseRequests with RandomValues {

  final case class FCDebtsInput(
    debtDetails: Option[String] = None,
    debtId: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None,
    solDescription: Option[String] = None
  )
  def getFCStatementOfLiability(maybeRequest: Option[SolMultipleDebtsRequest]): StandaloneWSResponse = {
    val jsonRequest: JsValue = maybeRequest.fold(fail("Missing request for API call"))(Json.toJson(_))

    val baseUri = s"$statementOfLiabilityApiUrl/fc-sol"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }
}
