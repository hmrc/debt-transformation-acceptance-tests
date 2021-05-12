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
import uk.gov.hmrc.test.api.models.{DebtCalculation, DebtItemCalculation}
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
      .replaceAll("<REPLACE_debtID>", "123")
      .replaceAll("<REPLACE_originalAmount>", asMapTransposed.get("originalAmount"))
      .replaceAll("<REPLACE_subTrans>", asMapTransposed.get("subTrans"))
      .replaceAll("<REPLACE_mainTrans>", asMapTransposed.get("mainTrans"))
      .replaceAll("<REPLACE_dateCreated>", asMapTransposed.get("dateCreated"))
      .replaceAll("<REPLACE_interestStartDate>", asMapTransposed.get("interestStartDate"))
      .replaceAll("<REPLACE_interestRequestedTo>", asMapTransposed.get("interestRequestedTo"))

    if (firstItem == true) { debtItems = debtItem }
    else { debtItems = ScenarioContext.get("debtItems").toString.concat(",").concat(debtItem) }

    ScenarioContext.set(
      "debtItems",
      debtItems
    )
    print("requst json ::::::::::::::::::::::::::::::::::::" + debtItems)
  }

  Given("(.*) debt items") { (numberItems: Int) =>
    var debtItems: String = null
    var n                 = 0

    while (n < numberItems) {
      val debtItem = getBodyAsString("debtItem")
        .replaceAll("<REPLACE_debtID>", "123")
        .replaceAll("<REPLACE_originalAmount>", "500000")
        .replaceAll("<REPLACE_subTrans>", "1000")
        .replaceAll("<REPLACE_mainTrans>", "1525")
        .replaceAll("<REPLACE_dateCreated>", "2021-12-16")
        .replaceAll("<REPLACE_interestStartDate>", "2021-12-16")
        .replaceAll("<REPLACE_interestRequestedTo>", "2022-04-14")

      if (n == 0) {
        debtItems = debtItem
      } else {
        debtItems = debtItems.concat(",").concat(debtItem)
      }

      ScenarioContext.set("debtItems", debtItems.toString.replaceAll("<REPLACE_payments>", ""))
      n = n + 1
    }
  }

  Given("(.*) debt items where interest rate changes from 3\\.0 to 3\\.25") { (numberItems: Int) =>
    var debtItems: String = null
    var n                 = 0

    while (n < numberItems) {
      val debtItem = getBodyAsString("debtItem")
        .replaceAll("<REPLACE_debtID>", "123")
        .replaceAll("<REPLACE_originalAmount>", "500000")
        .replaceAll("<REPLACE_subTrans>", "1000")
        .replaceAll("<REPLACE_mainTrans>", "1525")
        .replaceAll("<REPLACE_dateCreated>", "2018-01-01")
        .replaceAll("<REPLACE_interestStartDate>", "2018-01-01")
        .replaceAll("<REPLACE_interestRequestedTo>", "2018-10-30")

      if (n == 0) {
        debtItems = debtItem
      } else {
        debtItems = debtItems.concat(",").concat(debtItem)
      }

      ScenarioContext.set("debtItems", debtItems.toString.replaceAll("<REPLACE_payments>", ""))
      n = n + 1
    }
  }

  Given("the debt item has payment history") { (dataTable: DataTable) =>
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var payments        = ""

    asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
      payments = payments.concat(
        getBodyAsString("payment")
          .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
          .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
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

  When("the debt item(s) is sent to the ifs service") { () =>
    val request = ScenarioContext.get("debtItems").toString
    println(s"REQ --> $request")

    val response =
      InterestForecastingRequests.getDebtCalculation(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)

  }

  Then("the ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[DebtCalculation]

    responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString
    responseBody.interestDueCallTotal.toString shouldBe asMapTransposed.get("interestDueCallTotal").toString
    responseBody.unpaidAmountTotal.toString    shouldBe asMapTransposed.get("unpaidAmountTotal").toString
    responseBody.amountOnIntDueTotal.toString  shouldBe asMapTransposed
      .get("amountOnIntDueTotal")
      .toString
    responseBody.amountIntTotal.toString  shouldBe asMapTransposed.get("amountIntTotal").toString
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody: DebtItemCalculation = Json.parse(response.body).as[DebtCalculation].debtCalculations(index - 1)
    responseBody.interestBearing.toString         shouldBe asMapTransposed.get("interestBearing").toString
    responseBody.numberOfChargeableDays.toString  shouldBe asMapTransposed.get("numberChargeableDays").toString
    responseBody.interestDueDailyAccrual.toString shouldBe asMapTransposed.get("interestDueDailyAccrual").toString
    responseBody.interestDueDutyTotal.toString    shouldBe asMapTransposed.get("interestDueDutyTotal").toString
    responseBody.amountOnIntDueDuty.toString      shouldBe asMapTransposed
      .get("amountOnIntDueDuty")
      .toString
    responseBody.totalAmountIntDuty.toString      shouldBe asMapTransposed.get("totalAmountIntDuty").toString
    responseBody.unpaidAmountDuty.toString        shouldBe asMapTransposed.get("unpaidAmountDuty").toString
    responseBody.interestOnlyIndicator.toString    shouldBe asMapTransposed.get("interestOnlyIndicator").toString

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

        responseBody.periodFrom.toString              shouldBe window.get("periodFrom").toString
        responseBody.periodTo.toString                shouldBe window.get("periodTo").toString
        responseBody.numberOfDays.toString            shouldBe window.get("numberOfDays").toString
        responseBody.interestRate.toString            shouldBe window.get("interestRate").toString
        responseBody.interestDueDailyAccrual.toString shouldBe window.get("interestDueDailyAccrual").toString
        responseBody.interestDueWindow.toString       shouldBe window.get("interestDueWindow").toString
        responseBody.unpaidAmountWindow.toString      shouldBe window.get("unpaidAmountWindow").toString
        responseBody.amountOnIntDueWindow
          .toString()                                 shouldBe window.get("amountOnIntDueWindow").toString
      }
  }

  Given("the customer has breathing spaces applied") { (dataTable: DataTable) =>
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "debtItems",
      getBodyAsString("debtCalcRequest")
        .replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
    )

    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var breathingSpaces = ""

    asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
      if (breathingSpace.get("debtRespiteTo").toString.contains("-")) {
        breathingSpaces = breathingSpaces.concat(
          getBodyAsString("breathingSpace")
            .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
            .replaceAll("<REPLACE_debtRespiteTo>", breathingSpace.get("debtRespiteTo"))
        )
      } else {
        breathingSpaces = breathingSpaces.concat(
          getBodyAsString("breathingSpace")
            .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
            .replaceAll(",\"debtRespiteTo\" :\"<REPLACE_debtRespiteTo>\"", "")
        )
      }

      if (index + 1 < asMapTransposed.size) breathingSpaces = breathingSpaces.concat(",")

    }

    val jsonWithbreathingSpaces =
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_breathingSpaces>", breathingSpaces)
    ScenarioContext.set("debtItems", jsonWithbreathingSpaces)
  }

  Given("no breathing spaces have been applied to the customer") { () =>
    // Set scenario Context to be all debt items with payments.
    ScenarioContext.set(
      "debtItems",
      getBodyAsString("debtCalcRequest")
        .replaceAllLiterally("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
    )

    ScenarioContext.set(
      "debtItems",
      ScenarioContext.get("debtItems").toString.replaceAll("<REPLACE_breathingSpaces>", "")
    )
  }
}
