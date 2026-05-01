/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.suppressions

import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext

// TODO: Validate that FCStatementOfLiabilityContext is the correct context for helpers migrated from SuppresionStepDefs.scala.
trait SuppresionStepHelpers { this: Matchers =>

  // ^suppression configuration data is created$
  def suppressionConfigurationDataIsCreated(context: FCStatementOfLiabilityContext): Unit = {
    // addSuppressionCriteria(dataTable)
    // TODO: No matching generated builder input or existing model was found.
    // Add a typed parameter and wire it into context or request JSON.
  }

  // ^suppression configuration is sent to ifs service$
  def suppressionConfigurationIsSentToIfsService(context: FCStatementOfLiabilityContext): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // val requestJson = ScenarioContext.get[String]("suppressionsJson")
    // println(s"suppression data REQUEST ---------> $requestJson")
    // val response    = updateSuppressionData(requestJson)
    // withClue(s"Incorrect status with body : ${response.body}\n\n") {
    // TODO: Implement typed helper for this step.
  }

  // ^a request is sent to ifs service to get suppression$
  def aRequestIsSentToIfsServiceToGetSuppression(context: FCStatementOfLiabilityContext): Unit = {
    // Migration hint: legacy ScenarioContext usage, response assertion
    // ()
    // val response = getSuppressionData()
    // response.status should be(200)
    // ScenarioContext.set("response", response)
    // TODO: Implement typed helper for this step.
  }

}
