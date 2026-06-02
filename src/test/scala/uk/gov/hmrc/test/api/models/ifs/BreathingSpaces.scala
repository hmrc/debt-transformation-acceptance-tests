package uk.gov.hmrc.test.api.models.ifs

import play.api.libs.json.{Json, OFormat}

final case class BreathingSpaces(
  debtRespiteFrom: String,
  debtRespiteTo: String
)

object BreathingSpaces {
  implicit val format: OFormat[BreathingSpaces] = Json.format[BreathingSpaces]
}
