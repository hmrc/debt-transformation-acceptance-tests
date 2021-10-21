package uk.gov.hmrc.test.api.models.ttpp
import play.api.libs.json.Json

case class GenerateQuoteResponse(
  quoteReference: String,
  customerReference: String,
  quoteType: String,
  quoteDate: String,
  numberOfInstalments: Int,
  totalDebtIncInt: BigDecimal,
  interestAccrued: BigDecimal,
  planInterest: BigDecimal,
  instalments: List[Instalment]
)

object GenerateQuoteResponse {
  implicit val format = Json.format[GenerateQuoteResponse]
}
