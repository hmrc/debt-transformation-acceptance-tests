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

final case class DebtItem(
  debtID: Option[String],
  originalAmount: Int,
  subTrans: String,
  mainTrans: String,
  interestStartDate: Option[String],
  interestRequestedTo: String,
  breathingSpaces: Option[List[BreathingSpaces]],
  paymentHistory: Option[List[PaymentHistory]],
  periodEnd: Option[String] = None,
  dateCreated: Option[String] = None
)

object DebtItem {
  implicit val formatDebtItem: OFormat[DebtItem] = Json.format[DebtItem]
}

final case class CustomerPostCode(
  postCode: String,
  postCodeDate: String
)

object CustomerPostCode {
  implicit val formatCustomerPostCode: OFormat[CustomerPostCode] = Json.format[CustomerPostCode]
}

final case class DebtCalculationRequest(
  debtItems: List[DebtItem],
  customerPostCodes: List[CustomerPostCode]
)

object DebtCalculationRequest {
  implicit val format: OFormat[DebtCalculationRequest] = Json.format[DebtCalculationRequest]
}
