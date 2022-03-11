package uk.gov.hmrc.test.api.models

final case class DebtItemId(value: String) extends AnyVal

object DebtItemId extends ValueTypeFormatter {
  implicit val format =
    valueTypeFormatter(DebtItemId.apply, DebtItemId.unapply)
}