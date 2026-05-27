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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.sol.{SolCalculationSummaryResponse, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext

trait StatementOfLiabilityStepHelpers { this: Matchers =>

  def theSolServiceRespondWith(statusCode: Int, message: String, context: StatementOfLiabilityContext): Unit = {
    context.status       shouldBe statusCode
    context.errorMessage shouldBe Some(message)
  }

  // ^statement of liability multiple debt requests$
  def statementOfLiabilityMultipleDebtRequests(
    context: StatementOfLiabilityContext,
    request: SolDebtsRequest
  ): Unit = {

    println("SolDebtsRequest : " + request)
    context.request = Some(request)
  }

  // ^a debt statement of liability is requested$
  def aDebtStatementOfLiabilityIsRequested(context: StatementOfLiabilityContext): Unit = {
    val response         = StatementOfLiabilityBuilder.getStatementOfLiability(context.request)
    val jsonResponseBody = response.body[JsValue]
    context.status = response.status
    context.responseBody = Some(jsonResponseBody.as[SolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def statementOfLiabilityIsRequestedWithoutDebt(context: StatementOfLiabilityContext): Unit = {
    val response = StatementOfLiabilityBuilder.getStatementOfLiability(context.request)
    context.status = response.status
    context.errorMessage = Some(response.body)
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }
}
