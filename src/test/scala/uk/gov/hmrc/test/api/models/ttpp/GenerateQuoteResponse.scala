package uk.gov.hmrc.test.api.models.ttpp
import java.time.LocalDate

import play.api.libs.json.Json

case class GenerateQuoteResponse(quoteReference: String,
                                 customerReference: String,
                                 quoteType: String,
                                 instalments: List[Instalment],
                                 numberOfInstalments: String,
                                 totalDebtAmount: BigDecimal,
                                 totalInterest: Double)

object GenerateQuoteResponse {
  implicit val format = Json.format[GenerateQuoteResponse]
}
