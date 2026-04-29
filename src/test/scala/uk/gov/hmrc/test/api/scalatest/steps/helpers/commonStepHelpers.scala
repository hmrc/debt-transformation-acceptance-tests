package uk.gov.hmrc.test.api.scalatest.steps.helpers

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext

// TODO: Validate that FCStatementOfLiabilityContext is the correct context for helpers migrated from commonSteps.scala.
trait commonStepHelpers { this: Matchers =>

  // ^suppression data has been created$
  def suppressionDataHasBeenCreated(context: FCStatementOfLiabilityContext): Unit = {
    // addSuppressions(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^suppression rules have been created$
  def suppressionRulesHaveBeenCreated(context: FCStatementOfLiabilityContext, inputs: Seq[SuppressionRulesBuilder.SuppressionsInput]): Unit = {
    // TODO: Wire inputs into context or request JSON using SuppressionRulesBuilder.
    // Suggested type: SuppressionRulesBuilder.SuppressionsInput
  }

  // ^service returns response code (.*)$
  def serviceReturnsResponseCode(context: FCStatementOfLiabilityContext, expectedCode: Int): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // val response: StandaloneWSResponse = ScenarioContext.get("response")
    // response.status should be(expectedCode)
    // TODO: Implement typed helper for this step.
  }

  // ^service returns error message (.*)$
  def serviceReturnsErrorMessage(context: FCStatementOfLiabilityContext, expectedMessage: String): Unit = {
    // Migration hint: legacy ScenarioContext usage
    // val response: StandaloneWSResponse = ScenarioContext.get("response")
    // val responseBody                   = response.body.stripMargin
    // print("response message*****************************" + responseBody)
    // responseBody should be(expectedMessage)
    // TODO: Implement typed helper for this step.
  }

}
