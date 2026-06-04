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
