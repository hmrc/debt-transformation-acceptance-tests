package uk.gov.hmrc.test.api.models.ttpp

import java.time.LocalDate

import play.api.libs.json.Json

case class UpdateQuoteResponse(customerReference: String,
                                 pegaPlanId: String,
                                 quoteStatus: String,
                                 quoteUpdatedDate: LocalDate)

object UpdateQuoteResponse {
  implicit val format = Json.format[UpdateQuoteResponse]
}

