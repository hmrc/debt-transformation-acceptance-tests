package uk.gov.hmrc.test.api.models.ifs

import play.api.libs.json.{Json, OFormat}

final case class FCVATDebtItems(
  debtItemChargeId: Option[String],
  originalAmount: Int,
  interestIndicator: String,
  periodEnd: String,
  interestRequestedTo: String,
  breathingSpaces: Option[List[BreathingSpaces]],
  paymentHistory: Option[List[PaymentHistory]],
)

object FCVATDebtItems {
  implicit val formatDebtItems: OFormat[FCVATDebtItems] = Json.format[FCVATDebtItems]
}

final case class FCVATDebtCalculationRequest(
  debtItems: List[FCVATDebtItems]
)

object FCVATDebtCalculationRequest {
  implicit val format: OFormat[FCVATDebtCalculationRequest] = Json.format[FCVATDebtCalculationRequest]
}
