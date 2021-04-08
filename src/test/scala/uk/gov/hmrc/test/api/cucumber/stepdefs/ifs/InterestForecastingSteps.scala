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

import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.cucumber.stepdefs.BaseStepDef
import uk.gov.hmrc.test.api.models.{DebtCalculation, DebtItemCalculationWindow}
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests.getBodyAsString
import uk.gov.hmrc.test.api.utils.ScenarioContext

class InterestForecastingSteps extends BaseStepDef {

  Given("a debt item") { (dataTable: DataTable) =>
    val asMapTransposed = dataTable.transpose().asMap(classOf[String], classOf[String])

    ScenarioContext.set(
      "debtItem",
      getBodyAsString("debtCalculation")
        .replaceAll("<REPLACE_uniqueItemReference>", "123")
        .replaceAll("<REPLACE_amount>", asMapTransposed.get("amount"))
        .replaceAll("<REPLACE_chargeType>", asMapTransposed.get("chargeType"))
        .replaceAll("<REPLACE_regime>", asMapTransposed.get("regime"))
        .replaceAll("<REPLACE_dateAmount>", asMapTransposed.get("dateAmount"))
        .replaceAll("<REPLACE_dateCalculationTo>", asMapTransposed.get("dateCalculationTo"))
    )
  }

  When("the debt item is sent to the ifs service") { () =>
    val response =
      InterestForecastingRequests.getDebtCalculation(ScenarioContext.get("debtItem"))
    ScenarioContext.set("response", response)

  }

  Then("the ifs service wilL return a debt summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody = Json.parse(response.body).as[DebtCalculation]

    responseBody.totalInterestAccrued.toString    shouldBe asMapTransposed.get("totalInterest").toString
    responseBody.totalAmountToPay.toString        shouldBe asMapTransposed.get("totalAmountToPay").toString
    responseBody.totalAmountWithInterest.toString shouldBe asMapTransposed.get("totalAmountWithInterest").toString
    responseBody.numberOfChargeableDays.toString  shouldBe asMapTransposed.get("numberChargeableDays").toString
    responseBody.dailyInterestAccrued.toString    shouldBe asMapTransposed.get("dailyInterest").toString
  }

  Then("""the ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

  Then("the debt summary will have calculation windows") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[DebtCalculation].debtCalculations.head.calculationWindow

    responseBody.head.dateFrom.toString               shouldBe asMapTransposed.get("dateFrom").toString
    responseBody.head.dateTo.toString                 shouldBe asMapTransposed.get("dateTo").toString
    responseBody.head.numberOfChargeableDays.toString shouldBe asMapTransposed.get("numberDays").toString
    responseBody.head.interestRateApplied.toString    shouldBe asMapTransposed.get("intRate").toString
    responseBody.head.dailyInterestAccrued.toString   shouldBe asMapTransposed.get("dailyInterest").toString
    responseBody.head.totalInterestAccrued.toString   shouldBe asMapTransposed.get("totalInterest").toString
  }
}
