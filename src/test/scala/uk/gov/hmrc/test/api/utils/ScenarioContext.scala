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

package uk.gov.hmrc.test.api.utils

import uk.gov.hmrc.test.api.models.SuppressionInformation

object ScenarioContext {
  private var scenarioValues = Map.empty[String, Any]
  private var suppression    = Map.empty[String, List[SuppressionInformation]]

  def set(key: String, value: Any): Unit                                     =
    scenarioValues = scenarioValues + (key -> value)

  def setSuppression(key: String, value: List[SuppressionInformation]): Unit =
    suppression = suppression + (key -> value)
  def get[T: Manifest](key: String): T                                       =
    scenarioValues
      .get(key)
      .fold(throw new Exception(s"Key $key not found in scenario context"))(_.asInstanceOf[T])

  def remove(key: String): Unit = scenarioValues = scenarioValues - key
  def reset(): Unit = {
    scenarioValues = Map.empty[String, Any]
  }
}
