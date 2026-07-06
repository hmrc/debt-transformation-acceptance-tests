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
import play.api.libs.json._
import uk.gov.hmrc.test.api.models.SuppressionRequest
import uk.gov.hmrc.test.api.scalatest.builders.SuppressionRulesBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.SuppressionRulesContext

trait SuppressionStepHelpers {
  this: Matchers =>

  def suppressionConfigurationDataIsCreated(context: SuppressionRulesContext, request: SuppressionRequest): Unit =
    context.suppressionRequest = Some(request)

  def suppressionConfigurationIsSentToIfsService(context: SuppressionRulesContext): Unit = {
    val requestJson         = Json.toJson(context.suppressionRequest.getOrElse(fail("Missing request in context")))
    val suppressionResponse = SuppressionRulesBuilder.putSuppressionData(requestJson)
    val suppressionStatus   = suppressionResponse.status

    suppressionStatus shouldBe 200
    context.status = suppressionStatus
    context.headers = suppressionResponse.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== SUPPRESSION REQUEST BODY ====")
    println(Json.stringify(requestJson))

    println("\n==== SUPPRESSION RESPONSE STATUS ====")
    println(context.status)
  }

}
