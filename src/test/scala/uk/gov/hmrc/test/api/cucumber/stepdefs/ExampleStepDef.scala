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

package uk.gov.hmrc.test.api.cucumber.stepdefs

import uk.gov.hmrc.test.api.client.ServiceResponse
import uk.gov.hmrc.test.api.utils.ScenarioContext

class ExampleStepDef extends BaseStepDef {
  When("a request is made to get customer contact information") { () =>
    val response = exampleService.getInformation(validRegime, validIdType, validId)
    ScenarioContext.set("response", response)
  }

  When("an invalid request is made to get customer contact information") { () =>
    val response = exampleService.getInformation(validRegime, validIdType, invalidId)
    ScenarioContext.set("response", response)
  }

  Then("the response code should be {int}") { expectedCode: Int =>
    val response: ServiceResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }

  And("I am returned an invalid VRN response") { () =>
    val response: ServiceResponse = ScenarioContext.get("response")
    response.body shouldBe Some(invalidVRNResponse)
  }

  val validRegime        = "VATC"
  val validIdType        = "VRN"
  val validId            = "919951000"
  val invalidId          = "<ID>"
  val invalidVRNResponse = "VRN contained a non digit character: <ID>"

}
