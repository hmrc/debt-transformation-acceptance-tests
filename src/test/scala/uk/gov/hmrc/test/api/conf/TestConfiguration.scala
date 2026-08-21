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

package uk.gov.hmrc.test.api.conf

import com.typesafe.config.{Config, ConfigFactory}
import uk.gov.hmrc.api.conf.TestEnvironment

object TestConfiguration extends TestEnvironment {
  val config: Config    = ConfigFactory.load()
  val envConfig: Config = config.getConfig(environment).withFallback(config.getConfig("local"))

  def clientId: String     = envConfig.getString("clientId")
  def clientSecret: String = envConfig.getString("clientSecret")
}
