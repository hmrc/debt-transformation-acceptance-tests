/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.cucumber.stepdefs.suppressions

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.requests.SuppressionRulesRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

class SuppresionStepDefs extends ScalaDsl with EN with Eventually with Matchers {

  Given("suppression configuration data is created") { (dataTable: DataTable) =>
    addSuppressionCriteria(dataTable)
  }

  When("suppression configuration is sent to ifs service") { () =>
    val requestJson = ScenarioContext.get[String]("suppressionsJson")
    println(s"suppression data REQUEST ---------> $requestJson")
    val response    = updateSuppressionData(requestJson)
    withClue(s"Incorrect status with body : ${response.body}\n\n") {
      response.status should be(200)
    }

    println(s"suppression data RESPONSE ---------> ${response.body}")
    ScenarioContext.set("response", response)
  }

  And("a request is sent to ifs service to get suppression") {
    ()
    val response = getSuppressionData()
    response.status should be(200)
    ScenarioContext.set("response", response)
    println(s"suppression data RESPONSE ---------> ${response.body}")

  }

}
