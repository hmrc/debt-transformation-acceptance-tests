package uk.gov.hmrc.test.api.models.sol

import play.api.libs.json.{Json, OFormat}

final case class Debt(
  debtId: String,
  interestRequestedTo: String
)

object Debt {
  implicit val formatDebt: OFormat[Debt] = Json.format[Debt]
}

final case class SolMultipleDebtsRequest(
  solType: String,
  customerUniqueRef: String,
  debts: List[Debt]
)

object SolMultipleDebtsRequest {
  implicit val format: OFormat[SolMultipleDebtsRequest] = Json.format[SolMultipleDebtsRequest]
}
