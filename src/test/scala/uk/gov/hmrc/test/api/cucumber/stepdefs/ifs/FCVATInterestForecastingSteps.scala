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

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.requests.FieldCollectionsVATRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

import java.time.LocalDate

class FCVATInterestForecastingSteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("a fc vat debt item") { (dataTable: DataTable) =>
    createInterestForecastingRequestBodyFCVAT(dataTable)
  }

  Given("the fc vat debt item has payment history") { (dataTable: DataTable) =>
    addFCVATPaymentHistory(dataTable)
  }

  Given("the fc vat debt item has no payment history") { () =>
    fcVatCustomerWithNoPaymentHistory()
  }

  When("the debt item(s) is sent to the fc vat ifs service") { () =>
    addFcVatDebtItemRequest()
    val request  = ScenarioContext.get("fcVatDebtItem").toString
    println(s"IFS REQUEST --> $request")
    val response = getDebtCalculation(request)
    println(s"IFS RESPONSE --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("the fc vat ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val toDaysDate                     = LocalDate.now().toString

    val responseBody = Json.parse(response.body).as[FCVATDebtCalculationsSummary]

    responseBody.dateOfCalculation.toString shouldBe toDaysDate
    if (asMapTransposed.containsKey("combinedDailyAccrual")) {
      responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString
    }
    if (asMapTransposed.containsKey("unpaidAmountTotal")) {
      responseBody.unpaidAmountTotal.toString shouldBe asMapTransposed.get("unpaidAmountTotal").toString
    }
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) fc vat debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)
    val responseBody =
      Json
        .parse(response.body)
        .as[FCVATDebtCalculationsSummary]
        .debtCalculations(index - 1)

    if (asMapTransposed.containsKey("interestRate")) {
      responseBody.interestRate.toString shouldBe asMapTransposed.get("interestRate").toString
    }
    if (asMapTransposed.containsKey("debtItemChargeId")) {
      responseBody.debtItemChargeId.toString shouldBe asMapTransposed.get("debtItemChargeId").toString
    }
    if (asMapTransposed.containsKey("interestDueDailyAccrual")) {
      responseBody.interestDueDailyAccrual.toString shouldBe asMapTransposed.get("interestDueDailyAccrual").toString
    }

  }

  And("the fc vat customer has breathing spaces applied") { (dataTable: DataTable) =>
    addFCVATBreathingSpace(dataTable)
  }

  Given("no breathing spaces have been applied to the fc vat customer") { () =>
    noFCVatBreathingSpace()
  }

  Then("""the fc vat ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

}
