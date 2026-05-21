package uk.gov.hmrc.test.api.models.sol

import play.api.libs.json.{Json, OFormat}

final case class PaymentHistory(
  paymentAmount: BigDecimal,
  paymentDate: String
)

object PaymentHistory {
  implicit val format: OFormat[PaymentHistory] = Json.format[PaymentHistory]
}

final case class Debt(
  debtId: String,
  originalAmount: BigDecimal,
  solDescription: String,
  interestStartDate: String,
  interestRequestedTo: String,
  interestIndicator: String,
  chargedInterest: BigDecimal,
  periodEnd: String,
  paymentHistory: List[PaymentHistory]
)

object Debt {
  implicit val format: OFormat[Debt] = Json.format[Debt]
}

final case class SolMultipleDebtsRequest(
  customerUniqueRef: String,
  solRequestedDate: String,
  debts: List[Debt]
)

object SolMultipleDebtsRequest {
  implicit val format: OFormat[SolMultipleDebtsRequest] = Json.format[SolMultipleDebtsRequest]
}
