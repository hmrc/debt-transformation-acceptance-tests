/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.cucumber.stepdefs.sol

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.cucumber.stepdefs.BaseStepDef
import uk.gov.hmrc.test.api.models.HelloWorld
import uk.gov.hmrc.test.api.requests.HelloWorldRequests
import uk.gov.hmrc.test.api.utils.ScenarioContext

class StatementOfLiabilityHelloWorldSteps extends BaseStepDef {
  When("a request is made to get response from sol hello world endpoint") { () =>
    val response = HelloWorldRequests.getStatementLiabilityService("/hello-world")
    ScenarioContext.set("response", response)
  }

  When("a request is made to an invalid sol endpoint") { () =>
    val response = HelloWorldRequests.getStatementLiabilityService("/helloo-world")
    ScenarioContext.set("response", response)
  }

  And("""the sol hello world response body should be (.*)""") { message: String =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody = Json.parse(response.body).as[HelloWorld]
    responseBody.message should be(message)
  }

  Then("the sol response code should be {int}") { expectedCode: Int =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }
}