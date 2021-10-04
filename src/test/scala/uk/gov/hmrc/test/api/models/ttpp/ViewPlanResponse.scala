package uk.gov.hmrc.test.api.models.ttpp

import play.api.libs.json.Json
import uk.gov.hmrc.test.api.models.DebtItem

case class ViewPlanResponse(
  customerReference: String,
  channelIdentifier: String,
  plan: Plan,
  debtItemCharges: Seq[DebtItem],
  payments: Seq[PaymentInformation],
  customerPostcodes: Seq[CustomerPostCode],
  instalments: List[Instalment]
)

object ViewPlanResponse {
  implicit val format = Json.format[ViewPlanResponse]
}