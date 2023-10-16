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

