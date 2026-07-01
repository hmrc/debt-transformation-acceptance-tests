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

package uk.gov.hmrc.test.api.models.ifs

import play.api.libs.json.{Json, OFormat}

final case class FCDebtItems(
  debtItemChargeId: Option[String],
  originalAmount: Int,
  interestIndicator: String,
  periodEnd: String,
  interestStartDate: Option[String],
  interestRequestedTo: String,
  chargedInterest: Option[Int] = None,
  breathingSpaces: Option[List[BreathingSpaces]],
  paymentHistory: Option[List[PaymentHistory]],
  customerPostcodes: Option[List[FCCustomerPostCode]]
)

object FCDebtItems {
  implicit val formatDebtItems: OFormat[FCDebtItems] = Json.format[FCDebtItems]
}

final case class FCCustomerPostCode(
  addressPostcode: String,
  postcodeDate: String
)

object FCCustomerPostCode {
  implicit val formatFCCustomerPostCode: OFormat[FCCustomerPostCode] = Json.format[FCCustomerPostCode]
}

final case class FCDebtCalculationRequest(
  debtItems: List[FCDebtItems]
)

object FCDebtCalculationRequest {
  implicit val format: OFormat[FCDebtCalculationRequest] = Json.format[FCDebtCalculationRequest]
}
