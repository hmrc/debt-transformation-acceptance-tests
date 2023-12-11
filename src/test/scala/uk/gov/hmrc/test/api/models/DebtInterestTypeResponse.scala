/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Format, JsValue, Json, OFormat, Reads, Writes}

case class DebtInterestTypeResponse(
                                     debts: List[DebtInterestType])
case class DebtInterestType(
                             mainTrans: String,
                             subTrans: String,
                             interestBearing: Boolean,
                             useChargeReference: Boolean
                           )

object DebtInterestType {
  implicit val formats: Format[DebtInterestType] = Json.format[DebtInterestType]
}


object DebtInterestTypeResponse {
  implicit val reads: Reads[DebtInterestTypeResponse] = (json: JsValue) =>
    json.validate[List[DebtInterestType]].map(identifiers => DebtInterestTypeResponse(identifiers))

  implicit val writes: Writes[DebtInterestTypeResponse] = (model: DebtInterestTypeResponse) => Json.toJson(model.debts)
}

