package uk.gov.hmrc.test.api.scalatest.steps.context

// Minimal per-scenario context; extend fields as migration progresses.
final case class FCStatementOfLiabilityContext(
  var request: String = "",
  var responseBody: String = "",
  var status: Int = 0,
  var headers: Map[String, String] = Map.empty,
)
