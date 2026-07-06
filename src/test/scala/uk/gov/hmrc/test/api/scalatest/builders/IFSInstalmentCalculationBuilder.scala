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

import play.api.libs.json.JsValue
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues}

import java.time.LocalDate

object IFSInstalmentCalculationBuilder extends BaseRequests with RandomValues {

  final case class InstalmentCalculationSummaryResponseExpected(
    dateOfCalculation: Option[LocalDate] = None,
    numberOfInstalments: Option[Long] = None,
    planInterest: Option[Int] = None,
    interestAccrued: Option[Int] = None,
    totalInterest: Option[Int] = None,
    duration: Option[Long] = None,
    instalments: Option[Seq[InstalmentResponseExpected]] = None
  )

  final case class InstalmentResponseExpected(
    debtId: Option[String] = None,
    instalmentNumber: Option[Int] = None,
    dueDate: Option[LocalDate] = None,
    amountDue: Option[Int] = None,
    instalmentBalance: Option[Int] = None,
    instalmentInterestAccrued: Option[Int] = None,
    expectedPayment: Option[Int] = None,
    intRate: Option[Double] = None
  )

  def getInstalmentCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken =
      createBearerToken(enrolments = Seq("read:interest-forecasting"), userType = getRandomAffinityGroup)
    val baseUri     = s"$interestForecastingApiUrl/instalment-calculation"

    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("instalment-calculation baseUri ********************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

  def getInstalmentCalculationWithQueryParams(
    jsonRequest: JsValue,
    combineLastInstalments: String
  ): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/instalment-calculation"

    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )

    val queryParameters = Map(
      "combineLastInstalments" -> s"$combineLastInstalments"
    )

    println(s"query string parameters ******************** --> $queryParameters")
    println(s"instalment-calculation baseUri ******************** --> $baseUri")

    WsClient.postWithQueryParams(baseUri, headers = headers, queryParameters = queryParameters, jsonRequest)
  }

  def updateSuppressionData(json: JsValue): StandaloneWSResponse = {
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
    WsClient.put(baseUri, headers = headers, json)
  }

}
