package uk.gov.hmrc.test.api.models.ttpp

import play.api.libs.json.Json

case class ViewPlanResponse(customerReference: String,
                            planId: String,
                            quoteType: String,
                            paymentMethod: String,
                            paymentReference: String,
                            instalments: List[Instalment],
                            numberOfInstalments: String,
                            totalDebtAmount: BigDecimal,
                            totalInterest: Double)

object ViewPlanResponse {
  implicit val format = Json.format[ViewPlanResponse]
}
