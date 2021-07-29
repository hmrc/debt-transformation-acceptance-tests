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
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests.{getBodyAsString, _}
import uk.gov.hmrc.test.api.utils.ScenarioContext

import java.time.LocalDate

class InterestForecastingSteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("a debt item") { (dataTable: DataTable) =>
    createInterestFocastingRequestBody(dataTable)

  }

  Given("a new interest rate table") { () =>
    val newIntTable = InterestRates(
      22,
      Seq(
        InterestRate(LocalDate.of(2010, 1, 1), 1),
        InterestRate(LocalDate.of(2020, 1, 1), 10),
        InterestRate(LocalDate.of(2021, 1, 1), 20)
      )
    )
    postNewInterestRatesTable(Json.toJson(newIntTable).toString())
  }

  When("a rule has been updated") { (dataTable: DataTable) =>
    val asmapTransposed        = dataTable.transpose().asMap(classOf[String], classOf[String])
    val newRule                = asmapTransposed.get("rule")
    val responseGEtRules       = getAllRules
    val collection             = Json.parse(responseGEtRules.body).as[GetRulesResponse]
    val newRules: List[String] = collection.rules.find(_.enabled) match {
      case Some(activeRules) =>
        val rules = activeRules.rules.filterNot(vl =>
          (vl.contains(asmapTransposed.get("mainTrans")) && vl.contains(asmapTransposed.get("subTrans")))
        )
        rules ++ List(s"IF mainTrans == '${asmapTransposed.get("mainTrans")}' AND subTrans == '${asmapTransposed
          .get("subTrans")}' -> intRate = ${asmapTransposed.get("intRate")} AND interestOnlyDebt = false")

    }

    postNewRulesTable(Json.toJson(CreateRuleRequest(newRules)).toString())
  }

  Given("the current set of rules") { () =>
    val responseGEtRules = getAllRules

    val collection        = Json.parse(responseGEtRules.body).as[GetRulesResponse]
    val existingProdRules = collection.rules.find(_.version == 1)
    existingProdRules match {
      case Some(rules) => postNewRulesTable(Json.toJson(CreateRuleRequest(rules.rules)).toString())
      case _           => println("Error. No rules with version 1 found")
    }
  }

  When("a new interest rate is added") { (dataTable: DataTable) =>
    val asmapTransposed = dataTable.transpose().asMap(classOf[String], classOf[String])
    postNewInterestRate(
      Json
        .toJson(
          InterestRate(
            LocalDate.parse(asmapTransposed.get("date")),
            asmapTransposed.get("interestRate").toString.toDouble
          )
        )
        .toString(),
      "22"
    )
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
        .replaceAll("<REPLACE_dateCreated>", "")
        .replaceAll("<REPLACE_interestStartDate>", "2021-12-16")
        .replaceAll("<REPLACE_interestRequestedTo>", "2022-04-14")
        .replaceAll("<REPLACE_periodEnd>", "")

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
        .replaceAll("<REPLACE_debtId>", "123")
        .replaceAll("<REPLACE_originalAmount>", "500000")
        .replaceAll("<REPLACE_subTrans>", "1000")
        .replaceAll("<REPLACE_mainTrans>", "1525")
        .replaceAll("<REPLACE_dateCreated>", "")
        .replaceAll("<REPLACE_interestStartDate>", "2018-01-01")
        .replaceAll("<REPLACE_interestRequestedTo>", "2018-10-30")
        .replaceAll("<REPLACE_periodEnd>", "")

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
    addPaymentHistory(dataTable)
  }

  Given("the debt item has no payment history") { () =>
    customerWithNoPaymentHistory()
  }

  When("the debt item(s) is sent to the ifs service") { () =>
    val request  = ScenarioContext.get("debtItems").toString
    println(s"IFS REQUST --> $request")
    val response = getDebtCalculation(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("the ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[DebtCalculation]

    if (asMapTransposed.containsKey("combinedDailyAccrual")) {
      responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString
    }
    if (asMapTransposed.containsKey("interestDueCallTotal")) {
      responseBody.interestDueCallTotal.toString shouldBe asMapTransposed.get("interestDueCallTotal").toString
    }

    if (asMapTransposed.containsKey("unpaidAmountTotal")) {
      responseBody.unpaidAmountTotal.toString shouldBe asMapTransposed.get("unpaidAmountTotal").toString
    }
    if (asMapTransposed.containsKey("amountOnIntDueTotal")) {
      responseBody.amountOnIntDueTotal.toString shouldBe asMapTransposed.get("amountOnIntDueTotal").toString
    }
    if (asMapTransposed.containsKey("amountIntTotal")) {
      responseBody.amountIntTotal.toString shouldBe asMapTransposed.get("amountIntTotal").toString
    }
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody: DebtItemCalculation = Json.parse(response.body).as[DebtCalculation].debtCalculations(index - 1)

    if (asMapTransposed.containsKey("interestBearing")) {
      responseBody.interestBearing.toString shouldBe asMapTransposed.get("interestBearing").toString
    }
    if (asMapTransposed.containsKey("numberChargeableDays")) {
      responseBody.numberOfChargeableDays.toString shouldBe asMapTransposed.get("numberChargeableDays").toString
    }
    if (asMapTransposed.containsKey("interestDueDailyAccrual")) {
      responseBody.interestDueDailyAccrual.toString shouldBe asMapTransposed.get("interestDueDailyAccrual").toString
    }
    if (asMapTransposed.containsKey("interestDueDutyTotal")) {
      responseBody.interestDueDutyTotal.toString shouldBe asMapTransposed.get("interestDueDutyTotal").toString
    }
    if (asMapTransposed.containsKey("amountOnIntDueDuty")) {
      responseBody.amountOnIntDueDuty.toString shouldBe asMapTransposed.get("amountOnIntDueDuty").toString
    }
    if (asMapTransposed.containsKey("totalAmountIntDuty")) {
      responseBody.totalAmountIntDuty.toString shouldBe asMapTransposed.get("totalAmountIntDuty").toString
    }
    if (asMapTransposed.containsKey("unpaidAmountDuty")) {
      responseBody.unpaidAmountDuty.toString shouldBe asMapTransposed.get("unpaidAmountDuty").toString
    }
  }

  Then("""the ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

  Then("the ifs service will respond with") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val errorResponse                  = Json.parse(response.body).as[Errors]

    if (asMapTransposed.containsKey("statusCode")) {
      errorResponse.statusCode.toString shouldBe asMapTransposed.get("statusCode").toString
    }
    if (asMapTransposed.containsKey("reason")) {
      errorResponse.reason shouldBe asMapTransposed.get("reason").toString
    }
    if (asMapTransposed.containsKey("message")) {
      errorResponse.message shouldBe asMapTransposed.get("message").toString
    }
    response.status should be(400)
  }

  Then("the ([0-9])(?:st|nd|rd|th) debt summary will have calculation windows") {
    (summaryIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (window, index) =>
        val responseBody =
          Json.parse(response.body).as[DebtCalculation].debtCalculations(summaryIndex - 1).calculationWindows(index)

        if (window.containsKey("periodFrom")) {
          responseBody.periodFrom.toString shouldBe window.get("periodFrom").toString
        }
        if (window.containsKey("periodTo")) {
          responseBody.periodTo.toString shouldBe window.get("periodTo").toString
        }
        if (window.containsKey("numberOfDays")) {
          responseBody.numberOfDays.toString shouldBe window.get("numberOfDays").toString
        }
        if (window.containsKey("interestRate")) {
          responseBody.interestRate.toString shouldBe window.get("interestRate").toString
        }
        if (window.containsKey("interestDueDailyAccrual")) {
          responseBody.interestDueDailyAccrual.toString shouldBe window.get("interestDueDailyAccrual").toString
        }
        if (window.containsKey("interestDueWindow")) {
          responseBody.interestDueWindow.toString shouldBe window.get("interestDueWindow").toString
        }
        if (window.containsKey("unpaidAmountWindow")) {
          responseBody.unpaidAmountWindow.toString shouldBe window.get("unpaidAmountWindow").toString
        }
        if (window.containsKey("amountOnIntDueWindow")) {
          responseBody.amountOnIntDueWindow.toString() shouldBe window.get("amountOnIntDueWindow").toString
        }
        if (window.containsKey("reason") && (window.get("reason") != "")) {
          responseBody.suppressionApplied.head.reason shouldBe window.get("reason").toString
        }
        if (window.containsKey("code") && (window.get("code") != "")) {
          responseBody.suppressionApplied.head.code shouldBe window.get("code").toString
        }
      }
  }
  When("the payment plan detail(s) is sent to the ifs service") { () =>
    val request  = ScenarioContext.get("paymentPlan").toString
    println(s"IFS REQUST --> $request")
    val response = getPaymentPlan(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)

  }

  Then("ifs returns payment frequency summary") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody = Json.parse(response.body).as[PaymentPlanSummary]
    if (asMapTransposed.containsKey("totalNumberOfInstalments")) {
      responseBody.totalNumberOfInstalments.toString shouldBe asMapTransposed.get("totalNumberOfInstalments").toString
    }
    if (asMapTransposed.containsKey("totalDebtAmount")) {
      responseBody.expectedPayment.toString shouldBe asMapTransposed.get("expectedPayment").toString
    }
    if (asMapTransposed.containsKey("totalPlanInt")) {
      responseBody.totalPlanInt.toString shouldBe asMapTransposed.get("totalPlanInt").toString
    }
    if (asMapTransposed.containsKey("interestAccrued")) {
      responseBody.interestAccrued.toString() shouldBe asMapTransposed.get("interestAccrued").toString
    }
  }

  Then("ifs service return the following payment plan calculation instalment") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    asMapTransposed.zipWithIndex.foreach { case (window, index) =>
      val responseBody =
        Json.parse(response.body).as[PaymentPlanSummary].paymentPlanCalculationResponse(index)

      print("Body coming back here ************************************** " + responseBody)
      if (window.containsKey("serialNo")) {
        responseBody.serialNo.toString shouldBe window.get("serialNo").toString
      }
      if (window.containsKey("paymentDueDate")) {
        responseBody.paymentDueDate.toString shouldBe window.get("paymentDueDate").toString
      }
      if (window.containsKey("amountDue")) {
        responseBody.amountDue.toString shouldBe window.get("amountDue").toString
      }
      if (window.containsKey("uniqueDebtId")) {
        responseBody.uniqueDebtId.toString shouldBe window.get("uniqueDebtId").toString
      }
      if (window.containsKey("balance")) {
        responseBody.balance.toString shouldBe window.get("balance").toString
      }
      if (window.containsKey("interestDue")) {
        responseBody.interestDue.toString shouldBe window.get("interestDue").toString
      }
      if (window.containsKey("totalPaidAmount")) {
        responseBody.totalPaidAmount.toString() shouldBe window.get("totalPaidAmount").toString
      }
      if (window.containsKey("intRate") && (window.get("intRate") != "")) {
        responseBody.intRate.toString shouldBe window.get("intRate").toString
      }
    }
  }

  Given("the customer has breathing spaces applied") { (dataTable: DataTable) =>
    addBreathingSpace(dataTable)
  }

  Given("no breathing spaces have been applied to the customer") { () =>
    noBreathingSpace()
  }

  Given("the customer has post codes") { (dataTable: DataTable) =>
    addCustomerPostCodes(dataTable)
  }

  Given("no post codes have been provided for the customer") { () =>
    noCustomerPostCodes()
  }

  Given("debt payment plan details") { (dataTable: DataTable) =>
    createPaymentPlanRequestBody(dataTable)
  }
}
