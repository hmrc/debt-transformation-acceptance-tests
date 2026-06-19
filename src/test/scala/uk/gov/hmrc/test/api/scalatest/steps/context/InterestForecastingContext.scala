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

package uk.gov.hmrc.test.api.scalatest.steps.context

import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.{DebtCalculationsSummary, DebtInterestTypeRequest, DebtInterestTypeResponse}
import uk.gov.hmrc.test.api.models.ifs.DebtCalculationRequest

final case class InterestForecastingContext(
  var ifsRequest: Option[DebtCalculationRequest] = None,
  var ditRequest: Option[Seq[DebtInterestTypeRequest]] = None,
  var ifsResponseBody: Option[DebtCalculationsSummary] = None,
  var ditResponseBody: Option[DebtInterestTypeResponse] = None,
  var response: StandaloneWSResponse = null,
  var status: Int = 0,
  var headers: Map[String, String] = Map.empty
)
