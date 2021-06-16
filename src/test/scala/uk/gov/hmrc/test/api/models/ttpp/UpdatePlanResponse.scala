package uk.gov.hmrc.test.api.models.ttpp

import java.time.LocalDate

import play.api.libs.json.Json

case class UpdatePlanResponse(customerReference: String,
                               planId: String,
                               quoteStatus: String,
                               quoteUpdatedDate: LocalDate)

object UpdatePlanResponse {
  implicit val format = Json.format[UpdatePlanResponse]
}
