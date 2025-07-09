/*
 * Copyright 2023 HM Revenue & Customs
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

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.sol.FCSolCalculationSummaryResponse
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests
import uk.gov.hmrc.test.api.requests.FCStatementOfLiabilityRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

import scala.jdk.CollectionConverters.CollectionHasAsScala

class FCStatementOfLiabilitySteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("""fc sol request""") { (dataTable: DataTable) =>
    FCStatementOfLiabilityRequests.fcSolRequest(dataTable)
  }

  Given("fc sol debt item has multiple debts with charge interest") { (dataTable: DataTable) =>
    FCStatementOfLiabilityRequests.fcSolWithCotaxInterestChargeRequest(dataTable)
  }

  And("""the fc sol debt item has multiple debts""") { (dataTable: DataTable) =>
    FCStatementOfLiabilityRequests.addFCDebts(dataTable)
  }

  And("""the fc sol debt item has no debts""") {
    FCStatementOfLiabilityRequests.FCSolWithNoDebts()
  }

  Given("the debt item has fc sol payment history") { (dataTable: DataTable) =>
    addFCSOLPaymentHistory(dataTable)
  }

  Given("the fc sol debt item has no payment history") {
    FCSolWithNoPaymentHistory()
  }

  When("""a debt fc statement of liability is requested""") {
    val request = ScenarioContext.get("debtDetails").toString

    val response =
      FCStatementOfLiabilityRequests.getFCStatementOfLiability(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("service returns fc debt statement of liability data") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)
    val responseBody = Json.parse(response.body).as[FCSolCalculationSummaryResponse]
    responseBody.amountIntTotal.toString       shouldBe asMapTransposed.get("amountIntTotal").toString
    responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString

  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) multiple fc statement of liability debt summary will contain duties") {
    (debtIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String]).asScala
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
        val responseBody = Json.parse(response.body).as[FCSolCalculationSummaryResponse].debts(index)
        responseBody.debtId                        shouldBe duty.get("debtId").toString
        responseBody.interestDueDebtTotal.toString shouldBe duty.get("interestDueDebtTotal").toString
        responseBody.totalAmountIntDebt.toString   shouldBe duty.get("totalAmountIntDebt").toString
      }
  }

  Then("""the fc sol service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

}
