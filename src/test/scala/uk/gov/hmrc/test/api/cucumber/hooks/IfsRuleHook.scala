/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.test.api.cucumber.hooks

import com.typesafe.scalalogging.LazyLogging
import cucumber.api.scala.ScalaDsl
import org.scalatest.matchers.should.Matchers._
import uk.gov.hmrc.test.api.requests.SuppressionRulesRequests
import uk.gov.hmrc.test.api.utils.ScenarioContext

class IfsRuleHook extends ScalaDsl with LazyLogging {

  Before() { _ =>
    println("before block starts running")
    ScenarioContext.reset()

    println("before block finished running")
  }

  After() { scenario =>
    println("After block starts running")
    val deleteNewSuppressionResponseData = SuppressionRulesRequests.deleteNewSuppressionData()
    deleteNewSuppressionResponseData.status should be(200)
    val deleteResponse = SuppressionRulesRequests.deleteSuppressionData()
    deleteResponse.status should be(200)

    val deleteRuleResponse = SuppressionRulesRequests.deleteSuppressionRules()
    deleteRuleResponse.status should be(200)
    if (scenario.isFailed) {
      println("")
      logger.info(s"*********** rules in database after running ${scenario.getName} ***********")

    }
    println("After block finished running")
  }
}
