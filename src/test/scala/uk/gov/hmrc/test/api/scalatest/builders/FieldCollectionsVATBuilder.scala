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

object FieldCollectionsVATBuilder extends BaseRequests with RandomValues {

  final case class FCVATDebtCalculationsSummaryExpected(
    dateOfCalculation: Option[LocalDate] = None,
    combinedDailyAccrual: Option[Int] = None,
    unpaidAmountTotal: Option[Int] = None,
    debtCalculations: Option[List[FCVATDebtCalculationExpected]] = None
  )

  final case class FCVATDebtCalculationExpected(
    debtItemChargeId: Option[String] = None,
    interestDueDailyAccrual: Option[BigDecimal] = None,
    interestRate: Option[Double] = None
  )

  def getDebtCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/fc-vat-debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS FC VAT debt-calculation baseUri ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

}
