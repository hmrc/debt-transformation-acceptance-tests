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
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.cucumber.stepdefs.BaseStepDef
import uk.gov.hmrc.test.api.models.DebtCalculation
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests.getBodyAsString
import uk.gov.hmrc.test.api.utils.ScenarioContext

class InterestForecastingSteps extends BaseStepDef {

  Given("a debt item") { (dataTable: DataTable) =>
    val asMapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem         = false
    var debtItems: String = null

    try ScenarioContext.get("debtItems")
    catch { case e: Exception => firstItem = true }

    val debtItem = getBodyAsString("debtItem")
      .replaceAll("<REPLACE_uniqueItemReference>", "123")
      .replaceAll("<REPLACE_amount>", asMapTransposed.get("amount"))
      .replaceAll("<REPLACE_chargeType>", asMapTransposed.get("chargeType"))
      .replaceAll("<REPLACE_regime>", asMapTransposed.get("regime"))
      .replaceAll("<REPLACE_dateAmount>", asMapTransposed.get("dateAmount"))
      .replaceAll("<REPLACE_dateCalculationTo>", asMapTransposed.get("dateCalculationTo"))

    if (firstItem == true) { debtItems = debtItem }
    else { debtItems = ScenarioContext.get("debtItems").toString.concat(",").concat(debtItem) }

    ScenarioContext.set(
      "debtItems",
      debtItems
    )
  }

  Given("the debt item has payment history") { (dataTable: DataTable) =>
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var payments        = ""

    asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
      payments = payments.concat(
        getBodyAsString("payment")
          .replaceAll("<REPLACE_amountPaid>", payment.get("amountPaid"))
          .replaceAll("<REPLACE_dateOfPayment>", payment.get("dateOfPayment"))
      )

      if (index + 1 < asMapTransposed.size) payments = payments.concat(",")
    }

    val jsonWithPayments = ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("debtItems", jsonWithPayments)
  }

  Given("the debt item has no payment history") { () =>
    ScenarioContext.set(
      "debtItems",
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_payments>", "")
    )
  }

  When("the debt item is sent to the ifs service") { () =>
    val request = getBodyAsString("debtCalcRequest")
      .replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))

    val response =
      InterestForecastingRequests.getDebtCalculation(request)
    ScenarioContext.set("response", response)
  }

  Then("the ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[DebtCalculation]

    responseBody.dailyInterestAccrued.toString          shouldBe asMapTransposed.get("dailyInterest").toString
    responseBody.totalInterestAccrued.toString          shouldBe asMapTransposed.get("totalInterest").toString
    responseBody.totalAmountToPay.toString              shouldBe asMapTransposed.get("totalAmountToPay").toString
    responseBody.totalAmountOnWhichInterestDue.toString shouldBe asMapTransposed
      .get("totalAmountOnWhichInterestDue")
      .toString
    responseBody.totalAmountWithInterest.toString       shouldBe asMapTransposed.get("totalAmountWithInterest").toString
  }

  Then("the ([0-9])(?:st|nd|rd|th) debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody = Json.parse(response.body).as[DebtCalculation].debtCalculations(index - 1)
    responseBody.dailyInterestAccrued.toString          shouldBe asMapTransposed.get("dailyInterest").toString
    responseBody.totalInterestAccrued.toString          shouldBe asMapTransposed.get("totalInterest").toString
    responseBody.totalAmountOnWhichInterestDue.toString shouldBe asMapTransposed
      .get("totalAmountOnWhichInterestDue")
      .toString
    responseBody.totalAmountWithInterest.toString       shouldBe asMapTransposed.get("totalAmountWithInterest").toString
    responseBody.totalAmountToPay.toString              shouldBe asMapTransposed.get("totalAmountToPay").toString
  }

  Then("""the ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

  Then("the ([0-9])(?:st|nd|rd|th) debt summary will have calculation windows") {
    (summaryIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (window, index) =>
        val responseBody =
          Json.parse(response.body).as[DebtCalculation].debtCalculations(summaryIndex - 1).calculationWindows(index)

        responseBody.dateFrom.toString               shouldBe window.get("dateFrom").toString
        responseBody.dateTo.toString                 shouldBe window.get("dateTo").toString
        responseBody.numberOfChargeableDays.toString shouldBe window.get("numberDays").toString
        responseBody.interestRateApplied.toString    shouldBe window.get("intRate").toString
        responseBody.dailyInterestAccrued.toString   shouldBe window.get("dailyInterest").toString
        responseBody.totalInterestAccrued.toString   shouldBe window.get("totalInterest").toString
        responseBody.totalAmountOnWhichInterestDue
          .toString()                                shouldBe window.get("totalAmountOnWhichInterestDue").toString
      }
  }
}
