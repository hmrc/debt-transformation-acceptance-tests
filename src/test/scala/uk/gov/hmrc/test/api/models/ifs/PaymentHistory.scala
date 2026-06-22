package uk.gov.hmrc.test.api.models.ifs

import play.api.libs.json.{Json, OFormat}

final case class PaymentHistory(
  paymentAmount: Int,
  paymentDate: String
)

object PaymentHistory {
  implicit val format: OFormat[PaymentHistory] = Json.format[PaymentHistory]
}
