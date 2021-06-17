package uk.gov.hmrc.test.api.models.ttpp

import play.api.libs.json.Json

case class CreatePlanResponse(customerReference: String,
                              planId: String,
                              planStatus: String)

object CreatePlanResponse {
  implicit val format = Json.format[CreatePlanResponse]
}
