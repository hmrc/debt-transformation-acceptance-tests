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

object InterestForecastingBuilder extends BaseRequests with RandomValues {

  final case class DebtInterestTypeResponseExpected(
    debts: Option[List[DebtInterestTypeExpected]] = None
  )

  final case class DebtInterestTypeExpected(
    mainTrans: Option[String] = None,
    subTrans: Option[String] = None,
    interestBearing: Option[Boolean] = None,
    useChargeReference: Option[Boolean] = None
  )

  final case class DebtCalculationsSummaryExpected(
    combinedDailyAccrual: Option[BigDecimal] = None,
    interestDueCallTotal: Option[BigDecimal] = None,
    amountIntTotal: Option[BigDecimal] = None,
    amountOnIntDueTotal: Option[BigDecimal] = None,
    unpaidAmountTotal: Option[BigDecimal] = None,
    debtCalculations: Option[List[DebtCalculationExpected]] = None
  )

  final case class DebtCalculationExpected(
    debtItemChargeId: Option[String] = None,
    debtID: Option[String] = None,
    interestBearing: Option[Boolean] = None,
    numberOfChargeableDays: Option[Long] = None,
    interestDueDailyAccrual: Option[BigDecimal] = None,
    interestDueDutyTotal: Option[BigDecimal] = None,
    amountOnIntDueDuty: Option[BigDecimal] = None,
    totalAmountIntDuty: Option[BigDecimal] = None,
    unpaidAmountDuty: Option[BigDecimal] = None,
    interestOnlyIndicator: Option[Boolean] = None,
    calculationWindows: Option[List[CalculationWindowExpected]] = None
  )

  final case class CalculationWindowExpected(
    periodFrom: Option[LocalDate] = None,
    periodTo: Option[LocalDate] = None,
    numberOfDays: Option[Long] = None,
    interestRate: Option[Double] = None,
    interestDueWindow: Option[BigDecimal] = None,
    interestDueDailyAccrual: Option[BigDecimal] = None,
    amountOnIntDueWindow: Option[BigDecimal] = None,
    breathingSpaceApplied: Option[Boolean] = None,
    unpaidAmountWindow: Option[BigDecimal] = None,
    suppressionApplied: Option[SuppressionAppliedExpected] = None,
    suppressionsApplied: Option[List[SuppressionsAppliedExpected]] = None
  )

  final case class SuppressionAppliedExpected(
    reason: Option[String] = None,
    description: Option[String] = None,
    code: Option[String] = None
  )

  final case class SuppressionsAppliedExpected(
    dateFrom: Option[String] = None,
    dateTo: Option[String] = None,
    reason: Option[String] = None,
    reasonDesc: Option[String] = None,
    postcode: Option[String] = None,
    mainTrans: Option[String] = None,
    subTrans: Option[String] = None,
    periodEnd: Option[String] = None
  )

  def getDebtCalculation(jsonRequest: JsValue): StandaloneWSResponse = {
    val bearerToken =
      createBearerToken(enrolments = Seq("read:interest-forecasting"), userType = getRandomAffinityGroup)
    val baseUri     = s"$interestForecastingApiUrl/debt-calculation"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-calculation baseUri ************************" + baseUri)
    WsClient.post(baseUri, headers = headers, jsonRequest)
  }

  def getDebtInterestTypeRequestBody(json: JsValue): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup
    )
    val baseUri     = s"$interestForecastingApiUrl/debt-interest-type"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("IFS debt-interest type baseUri ************************" + baseUri)
    print("IFS debt-interest Type json********************" + json)

    WsClient.post(baseUri, headers = headers, json)
  }

}
