package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

final case class DebtInterestTypeRequest(
  subTrans: String,
  mainTrans: String
)

object DebtInterestTypeRequest {
  implicit val format: OFormat[DebtInterestTypeRequest] = Json.format[DebtInterestTypeRequest]
}
