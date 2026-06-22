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
