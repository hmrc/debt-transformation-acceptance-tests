package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class HelloWorld(message: String)

object HelloWorld {
  implicit val format: OFormat[HelloWorld] = Json.format
}
