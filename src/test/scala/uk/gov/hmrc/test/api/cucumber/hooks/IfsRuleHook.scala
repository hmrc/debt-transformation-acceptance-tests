package uk.gov.hmrc.test.api.cucumber.hooks

import com.typesafe.scalalogging.LazyLogging
import cucumber.api.scala.ScalaDsl
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}
import uk.gov.hmrc.test.api.requests.SuppressionRulesRequests
import uk.gov.hmrc.test.api.utils.ScenarioContext

class IfsRuleHook extends ScalaDsl with LazyLogging {

  Before("@suppression") { _ =>
  }

  Before() { _ =>
    ScenarioContext.reset()
  }

  After() { scenario =>
    val deleteResponse = SuppressionRulesRequests.deleteSuppressionData()
    deleteResponse.status should be(200)

    val deleteRuleResponse = SuppressionRulesRequests.deleteSuppressionRules()
    deleteRuleResponse.status should be(200)

    if (scenario.isFailed) {
      println("")
      logger.info(s"*********** rules in database after running ${scenario.getName} ***********")

    }
  }
}
