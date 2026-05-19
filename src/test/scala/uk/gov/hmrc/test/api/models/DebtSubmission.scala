package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

final case class DebtRequest(
  debtId: String,
  interestRequestedTo: String
)

object DebtRequest {
  implicit val format: OFormat[DebtRequest] = Json.format[DebtRequest]
}
final case class DebtSubmission(
  solType: String,
  customerUniqueRef: String,
  debts: List[DebtRequest]
)

object DebtSubmission {
  implicit val format: OFormat[DebtSubmission] = Json.format[DebtSubmission]
}
