package uk.gov.hmrc.test.api.scalatest.steps.context

import uk.gov.hmrc.test.api.models.DebtSubmission

 case class UpdatedDebtContext (
  var request: Option[DebtSubmission] = None,
  var responseBody: String = "",
  var status: Int = 0,
  var headers: Map[String, String] = Map.empty
)
