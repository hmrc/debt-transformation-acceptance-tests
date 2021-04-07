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

package uk.gov.hmrc.test.api.cucumber.stepdefs.ifs

import com.google.gson.{JsonParser}
import org.scalatest.Assertions._
import scala.collection.JavaConversions._
import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.cucumber.stepdefs.BaseStepDef
import uk.gov.hmrc.test.api.models.DebtItem
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests.getBodyAsString
import uk.gov.hmrc.test.api.utils.ScenarioContext

class InterestForecastingSteps extends BaseStepDef {

  Given("a debt item") { (dataTable: DataTable) =>
    val maps = dataTable.asMaps(classOf[String], classOf[String])

    var request:String = "{\"debtItems\":["
    for(value <- maps) {
      request += getBodyAsString("debtCalculation")
        .replaceAll("<REPLACE_uniqueItemReference>", value.get("uniqueItemReference"))
        .replaceAll("<REPLACE_amount>", value.get("amount"))
        .replaceAll("<REPLACE_chargeType>", value.get("chargeType"))
        .replaceAll("<REPLACE_regime>", value.get("regime"))
        .replaceAll("<REPLACE_dateAmount>", value.get("dateAmount"))
        .replaceAll("<REPLACE_dateCalculationTo>", value.get("dateCalculationTo"))
        .replaceAll("<REPLACE_paymentHistory>", value.get("paymentHistory"))
        .replaceAll("<REPLACE_numberOfDays>", value.get("numberOfDays"))
        .replaceAll("<REPLACE_interestRate>", value.get("interestRate"))
    }
    request += "]}"

    ScenarioContext.set(
      "debtItems", request
    )
  }

  When("the debt item is sent to the ifs service") { () =>
    val response =
      InterestForecastingRequests.getDebtCalculation(ScenarioContext.get("debtItems"))
    ScenarioContext.set("response", response)
  }

  Then("the ifs service will respond with") { (dataTable: DataTable) =>
    val asMap = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val parser = new JsonParser()
    val responseBody = parser.parse(response.body)
    val expectedJson = parser.parse(getBodyAsString(asMap.get("expectedResponse")))
    assert(responseBody == expectedJson)
  }

  Then("""the ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }
}
