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

object FieldCollectionsBuilder extends BaseRequests with RandomValues {

  final case class FCDebtCalculationsSummaryExpected(
    dateOfCalculation: Option[LocalDate] = None,
    combinedDailyAccrual: Option[Int] = None,
    unpaidAmountTotal: Option[Int] = None,
    interestDueCallTotal: Option[Int] = None,
    totalAmountIntTotal: Option[Int] = None,
    amountOnIntDueTotal: Option[Int] = None,
    debtCalculations: Option[List[FCDebtCalculationExpected]] = None
  )

  final case class FCDebtCalculationExpected(
    debtItemChargeId: Option[String] = None,
    interestDueDailyAccrual: Option[Int] = None,
    interestDueDutyTotal: Option[Int] = None,
    amountOnIntDueDuty: Option[Int] = None,
    totalAmountIntDuty: Option[Int] = None,
    unpaidAmountDuty: Option[Int] = None,
    calculationWindows: Option[List[FCCalculationWindowExpected]] = None
  )

  final case class FCCalculationWindowExpected(
    periodFrom: Option[LocalDate] = None,
    periodTo: Option[LocalDate] = None,
    numberOfDays: Option[Int] = None,
    interestRate: Option[Double] = None,
    interestDueDailyAccrual: Option[Int] = None,
    interestDueWindow: Option[Int] = None,
    amountOnIntDueWindow: Option[Int] = None,
    unpaidAmountWindow: Option[Int] = None,
    suppressionApplied: Option[SuppressionAppliedExpected] = None
  )

  final case class SuppressionAppliedExpected(
    reason: Option[String] = None,
    description: Option[String] = None,
    code: Option[String] = None
  )

  def getDebtCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/fc-debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-calculation baseUri ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

}
