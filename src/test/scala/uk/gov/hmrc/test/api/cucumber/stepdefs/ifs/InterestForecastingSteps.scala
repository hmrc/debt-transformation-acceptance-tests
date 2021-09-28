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
import uk.gov.hmrc.test.api.models.{PaymentPlanSummaryResponse, _}
import uk.gov.hmrc.test.api.requests.InterestForecastingRequests.{getBodyAsString, _}
import uk.gov.hmrc.test.api.utils.ScenarioContext

import java.time.LocalDate

class InterestForecastingSteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("a debt item") { (dataTable: DataTable) =>
    createInterestFocastingRequestBody(dataTable)
  }

  Given("no debt item") { () =>
    createInterestForcastingRequestWithNoDebtItems()
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
        .replaceAll("<REPLACE_interestStartDate>", "\"interestStartDate\": \"2021-12-16\",")
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
        .replaceAll("<REPLACE_interestStartDate>", "\"interestStartDate\": \"2018-01-01\",")
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

  Given("the debt item has no payment history") { () =>
    customerWithNoPaymentHistory()
  }

  When("the debt item(s) is sent to the ifs service") { () =>
    val request  = ScenarioContext.get("debtItems").toString
    println(s"IFS REQUEST --> $request")
    val response = getDebtCalculation(request)
    println(s"IFS RESPONSE --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("the ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[DebtCalculationsSummary]

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

    val responseBody: DebtCalculation =
      Json.parse(response.body).as[DebtCalculationsSummary].debtCalculations(index - 1)

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
          Json
            .parse(response.body)
            .as[DebtCalculationsSummary]
            .debtCalculations(summaryIndex - 1)
            .calculationWindows(index)

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
    val request  = ScenarioContext.get("debtPaymentPlan").toString
    println(s"IFS REQUST --> $request")
    val response = getPaymentPlan(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("Ifs service returns response code (.*)") { (expectedCode: Int) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }

  And("Ifs service returns error message (.*)") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = response.body.stripMargin
    print("response message*****************************" + responseBody)
    responseBody should be(expectedMessage)
  }

  Then("ifs returns payment frequency summary") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("paymentPlan")
    response.status should be(200)
    val paymentPlanSummary = Json.parse(response.body).as[PaymentPlanSummary]
    paymentPlanSummary.numberOfInstalments.toString shouldBe (asMapTransposed
      .get("numberOfInstalments")
      .toString)
    if (asMapTransposed.containsKey("totalPlanInt")) {
      paymentPlanSummary.planInterest.toString contains (asMapTransposed.get("totalPlanInt").toString)
    }
    if (asMapTransposed.containsKey("interestCallDueTotal")) {
      paymentPlanSummary.interestAccrued.toString contains (asMapTransposed.get("interestAccrued").toString)
    }
  }

  Then("ifs service returns single payment freqeuncy instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      39,
      1423,
      1423 + 39,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusDays(1), 10000, 90000, 6, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusDays(2), 10000, 80000, 5, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusDays(3), 10000, 70000, 4, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusDays(4), 10000, 60000, 4, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusDays(5), 10000, 50000, 3, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusDays(6), 10000, 40000, 2, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusDays(7), 10000, 30000, 2, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusDays(8), 10000, 20000, 1, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusDays(9), 10000, 10000, 0, 100000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 11, instalmentPaymentDate.plusDays(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns weekly payment freqeuncy instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      39,
      1423,
      1423 + 39,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1), 10000, 90000, 6, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2), 10000, 80000, 5, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3), 10000, 70000, 4, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4), 10000, 60000, 4, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5), 10000, 50000, 3, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6), 10000, 40000, 2, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7), 10000, 30000, 2, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8), 10000, 20000, 1, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9), 10000, 10000, 0, 100000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 11, instalmentPaymentDate.plusWeeks(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns 2-Weekly frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      455,
      1423,
      1423 + 455,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1 * 2), 10000, 90000, 89, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2 * 2), 10000, 80000, 79, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3 * 2), 10000, 70000, 69, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4 * 2), 10000, 60000, 59, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5 * 2), 10000, 50000, 49, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6 * 2), 10000, 40000, 39, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7 * 2), 10000, 30000, 29, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8 * 2), 10000, 20000, 19, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9 * 2), 10000, 10000, 9, 100000, 2.6),
        PaymentPlanInstalmentResponse(
          debtId,
          11,
          instalmentPaymentDate.plusWeeks(10 * 2),
          1878,
          0,
          0,
          100000 + 1878,
          2.6
        )
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns monthly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      12,
      983,
      9542,
      9542 + 983,
      12,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusMonths(1), 10000, 90000, 198, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusMonths(2), 10000, 80000, 170, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusMonths(3), 10000, 70000, 154, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusMonths(4), 10000, 60000, 128, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusMonths(5), 10000, 50000, 110, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusMonths(6), 10000, 40000, 88, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusMonths(7), 10000, 30000, 59, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusMonths(8), 10000, 20000, 44, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(9), 10000, 10000, 21, 100000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(10), 10000, 0, 0, 110000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 11, instalmentPaymentDate.plusMonths(11), 525, 0, 0, 100000 + 525, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns 4-Weekly frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      904,
      1423,
      1423 + 904,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1 * 4), 10000, 90000, 179, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2 * 4), 10000, 80000, 159, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3 * 4), 10000, 70000, 139, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4 * 4), 10000, 60000, 119, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5 * 4), 10000, 50000, 99, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6 * 4), 10000, 40000, 79, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7 * 4), 10000, 30000, 59, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8 * 4), 10000, 20000, 39, 90000, 2.6),
        PaymentPlanInstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusWeeks(9 * 4),
          10000,
          10000,
          19,
          100000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          11,
          instalmentPaymentDate.plusWeeks(10 * 4),
          2327,
          0,
          0,
          100000 + 2327,
          2.6
        )
      )
    )
    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns Quarterly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      2934,
      1423,
      1423 + 2934,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(
          debtId,
          2,
          instalmentPaymentDate.plusMonths(1 * 3),
          10000,
          90000,
          589,
          10000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          3,
          instalmentPaymentDate.plusMonths(2 * 3),
          10000,
          80000,
          524,
          30000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          4,
          instalmentPaymentDate.plusMonths(3 * 3),
          10000,
          70000,
          443,
          40000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          5,
          instalmentPaymentDate.plusMonths(4 * 3),
          10000,
          60000,
          393,
          50000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          6,
          instalmentPaymentDate.plusMonths(5 * 3),
          10000,
          50000,
          327,
          60000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          7,
          instalmentPaymentDate.plusMonths(6 * 3),
          10000,
          40000,
          262,
          70000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          8,
          instalmentPaymentDate.plusMonths(7 * 3),
          10000,
          30000,
          190,
          80000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          9,
          instalmentPaymentDate.plusMonths(8 * 3),
          10000,
          20000,
          131,
          90000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusMonths(9 * 3),
          10000,
          10000,
          65,
          100000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          11,
          instalmentPaymentDate.plusMonths(10 * 3),
          4357,
          0,
          0,
          100000 + 4357,
          2.6
        )
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns 6Monthly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      11,
      5860,
      3538,
      3538 + 5860,
      11,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(
          debtId,
          2,
          instalmentPaymentDate.plusMonths(1 * 6),
          10000,
          90000,
          1179,
          10000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          3,
          instalmentPaymentDate.plusMonths(2 * 6),
          10000,
          80000,
          1031,
          30000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          4,
          instalmentPaymentDate.plusMonths(3 * 6),
          10000,
          70000,
          917,
          40000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          5,
          instalmentPaymentDate.plusMonths(4 * 6),
          10000,
          60000,
          773,
          50000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          6,
          instalmentPaymentDate.plusMonths(5 * 6),
          10000,
          50000,
          653,
          60000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          7,
          instalmentPaymentDate.plusMonths(6 * 6),
          10000,
          40000,
          517,
          70000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          8,
          instalmentPaymentDate.plusMonths(7 * 6),
          10000,
          30000,
          392,
          80000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          9,
          instalmentPaymentDate.plusMonths(8 * 6),
          10000,
          20000,
          257,
          90000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusMonths(9 * 6),
          10000,
          10000,
          131,
          100000,
          2.6
        ),
        PaymentPlanInstalmentResponse(
          debtId,
          11,
          instalmentPaymentDate.plusMonths(10 * 6),
          9398,
          0,
          0,
          100000 + 9398,
          2.6
        )
      )
    )
    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns Annually payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      12,
      11701,
      1423,
      1423 + 13124,
      12,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusYears(1), 10000, 90000, 2340, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusYears(2), 10000, 80000, 2080, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusYears(3), 10000, 70000, 1820, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusYears(4), 10000, 60000, 1555, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusYears(5), 10000, 50000, 1300, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusYears(6), 10000, 40000, 1040, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusYears(7), 10000, 30000, 780, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusYears(8), 10000, 20000, 518, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusYears(9), 10000, 10000, 260, 100000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusYears(10), 10000, 0, 0, 110000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 11, instalmentPaymentDate.plusYears(11), 3124, 0, 0, 100000 + 3124, 2.6)
      )
    )
    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)
  }

  Then("ifs service returns monthly instalment calculation plan with initial payment") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
    val initialPaymentDate        = quoteDate.plusDays(1)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      10,
      955,
      1423,
      955 + 1423,
      10,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10100, 100000, 7, 10100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusMonths(1), 10000, 89900, 192, 20100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusMonths(2), 10000, 79900, 176, 30100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusMonths(3), 10000, 69900, 149, 40100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusMonths(4), 10000, 59900, 132, 50100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusMonths(5), 10000, 49900, 110, 60100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusMonths(6), 10000, 39900, 79, 70100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusMonths(7), 10000, 29900, 66, 80100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusMonths(8), 5000, 19900, 42, 90100, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(9), 2478, 100, 0, 92578, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)

  }

  Then("ifs service returns weekly frequency instalment calculation plan with initial payment") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(129)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[PaymentPlanSummary].instalments
    val actualnumberOfInstalments = Json.parse(response.body).as[PaymentPlanSummary].numberOfInstalments

    val expectedPaymentPlanResponse = PaymentPlanSummaryResponse(
      quoteDate,
      20,
      1345,
      2051,
      1345 + 2051,
      20,
      Vector(
        PaymentPlanInstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 918, 10000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1), 5000, 90000, 44, 15000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2), 5000, 85000, 42, 20000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3), 5000, 80000, 39, 25000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4), 5000, 75000, 37, 30000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5), 5000, 70000, 34, 35000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6), 5000, 65000, 32, 40000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7), 5000, 60000, 29, 45000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8), 5000, 55000, 27, 50000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9), 5000, 50000, 24, 55000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 11, instalmentPaymentDate.plusWeeks(10), 5000, 45000, 22, 60000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 12, instalmentPaymentDate.plusWeeks(11), 5000, 40000, 19, 65000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 13, instalmentPaymentDate.plusWeeks(12), 5000, 35000, 17, 70000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 14, instalmentPaymentDate.plusWeeks(13), 5000, 30000, 14, 75000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 15, instalmentPaymentDate.plusWeeks(14), 5000, 25000, 12, 80000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 16, instalmentPaymentDate.plusWeeks(15), 5000, 20000, 9, 85000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 17, instalmentPaymentDate.plusWeeks(16), 5000, 15000, 7, 90000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 18, instalmentPaymentDate.plusWeeks(17), 5000, 10000, 4, 95000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 19, instalmentPaymentDate.plusWeeks(18), 5000, 5000, 2, 100000, 2.6),
        PaymentPlanInstalmentResponse(debtId, 20, instalmentPaymentDate.plusWeeks(19), 3396, 0, 0, 103396, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedPaymentPlanResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedPaymentPlanResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedPaymentPlanResponse.instalments.map(_.instalmentBalance)

  }

  Given("the customer has breathing spaces applied") { (dataTable: DataTable) =>
    addBreathingSpace(dataTable)
  }

  Given("no breathing spaces have been applied to the customer") { () =>
    noBreathingSpace()
  }

  Given("no initial payment for the debtItem") { () =>
    noInitialPayment()
  }

  Given("no post codes have been provided for the customer") { () =>
    noCustomerPostCodes()
  }

  And("add initial payment for the debtItem"){(dataTable:DataTable) =>
    addInitialPayment(dataTable)
  }


  Given("the customer has post codes") { (dataTable: DataTable) =>
    addCustomerPostCodes(dataTable)
  }

  Given("debt payment plan frequency details") { (dataTable: DataTable) =>
    createPaymentPlanFrequency(dataTable)
  }
  Given("plan details with initialPaymentDate is after instalmentPaymentDate") { (dataTable: DataTable) =>
    initialPaymentDateAfterInstalmentDate(dataTable)
  }
  Given("plan details with no initial payment date") { (dataTable: DataTable) =>
    noInitialPaymentDate(dataTable)
  }

  Given("plan details with no initial payment amount") { (dataTable: DataTable) =>
    noInitialPaymentAmount(dataTable)
  }

  Given("plan details with quote date in past") { (dataTable: DataTable) =>
    frequencyPlanWithQuoteDateInPast(dataTable)
  }
  Given("plan details with no instalment date") { (dataTable: DataTable) =>
    noInstalmentDate(dataTable)
  }
  Given("plan details with no quote date") { (dataTable: DataTable) =>
    noQuoteDate(dataTable)
  }

  Given("debt payment plan details") { (dataTable: DataTable) =>
    createPaymentPlanRequestBody(dataTable)
  }

  Given("debt instalment payment plan request details") { (dataTable: DataTable) =>
    debtInstalmentPaymentPlanRequest(dataTable)
  }

  Then("the ([0-9])(?:st|nd|rd|th) debt summary will not have any calculation windows") { (summaryIndex: Int) =>
    getCountOfCalculationWindows(summaryIndex) shouldBe 0
  }

  def getCountOfCalculationWindows(summaryIndex: Int): Int = {
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    Json.parse(response.body).as[DebtCalculationsSummary].debtCalculations(summaryIndex - 1).calculationWindows.size
  }
}
