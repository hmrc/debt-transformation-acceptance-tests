/*
 * Copyright 2021 HM Revenue & Customs
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
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.models.sol.{HelloWorld, SolCalculation, SolCalculationSummaryResponse}
import uk.gov.hmrc.test.api.requests.StatementOfLiabilityRequests
import uk.gov.hmrc.test.api.utils.{ScenarioContext, TestData}
class StatementOfLiabilitySteps extends ScalaDsl with EN with Eventually with Matchers {

  When("a request is made to get response from sol hello world endpoint") { () =>
    val response = StatementOfLiabilityRequests.getStatementLiabilityHelloWorld("/hello-world")
    ScenarioContext.set("response", response)
  }

  When("a request is made to an invalid sol endpoint") { () =>
    val response =
      StatementOfLiabilityRequests.getStatementLiabilityHelloWorld("/helloo-world")
    ScenarioContext.set("response", response)
  }

  And("""the sol hello world response body should be (.*)""") { message: String =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = Json.parse(response.body).as[HelloWorld]
    responseBody.message should be(message)
  }

  Then("the sol response code should be {int}") { expectedCode: Int =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }

  Given("""debt details""") { (dataTable: DataTable) =>
    val asMapTransposed             = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem                   = false
    var debtDetails: String         = null
    var customerReferenceId: String = null

    if (asMapTransposed.containsKey("customerUniqueRef")) {
      customerReferenceId = asMapTransposed.get("customerUniqueRef")
    } else { customerReferenceId = "customer-1" }

    try ScenarioContext.get("debtDetails")
    catch { case e: Exception => firstItem = true }

    val debtDetailsTestfile = getBodyAsString("debtDetailsTestfile")
      .replaceAll("<REPLACE_solType>", asMapTransposed.get("solType"))
      .replaceAll("<REPLACE_debtId>", asMapTransposed.get("debtId"))
      .replaceAll("<REPLACE_solRequestedDate>", asMapTransposed.get("solRequestedDate"))
      .replaceAll("<REPLACE_customerReference>", asMapTransposed.get("customerUniqueRef"))
      .replaceAll("REPLACE_interestRequestedTo", asMapTransposed.get("interestRequestedTo"))
      .replaceAll("<REPLACE_mainTrans>", asMapTransposed.get("mainTrans"))
      .replaceAll("<REPLACE_subTrans>", asMapTransposed.get("subTrans"))

    if (firstItem == true) { debtDetails = debtDetailsTestfile }
    else { debtDetails = ScenarioContext.get("debtDetails").toString.concat(",").concat(debtDetailsTestfile) }

    ScenarioContext.set("debtDetails", debtDetails)
  }

  Given("""statement of liability multiple debt requests""") { (dataTable: DataTable) =>
    val asMapTransposed             = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem                   = false
    var multipleDebtDetails: String = null

    try ScenarioContext.get("debtDetails")
    catch { case e: Exception => firstItem = true }

    val SolMultipleDebts = getBodyAsString("SolMultipleDebts")
      .replaceAll("<REPLACE_solType>", asMapTransposed.get("solType"))
      .replaceAll("REPLACE_solRequestedDate", asMapTransposed.get("solRequestedDate"))
      .replaceAll("REPLACE_debtId", asMapTransposed.get("debtId"))
      .replaceAll("REPLACE_ID", asMapTransposed.get("debtId2"))
      .replaceAll("REPLACE_interestRequestedTo", asMapTransposed.get("interestRequestedTo"))
      .replaceAll("REPLACE_interestRequestedTo2", asMapTransposed.get("interestRequestedTo"))

    if (firstItem == true) { multipleDebtDetails = SolMultipleDebts }
    else { multipleDebtDetails = ScenarioContext.get("debtDetails").toString.concat(",").concat(SolMultipleDebts) }

    ScenarioContext.set("debtDetails", multipleDebtDetails)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  And("""add debt item chargeIDs to the debt""") { (dataTable: DataTable) =>
    StatementOfLiabilityRequests.addDutyIds(dataTable)
  }

  When("""a debt statement of liability is requested""") {
    val request = ScenarioContext.get("debtDetails").toString

    val response =
      StatementOfLiabilityRequests.getStatementOfLiability(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("service returns debt statement of liability data") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)
    val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse]
    responseBody.amountIntTotal.toString       shouldBe asMapTransposed.get("amountIntTotal").toString
    responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString

  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) sol debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val debt: SolCalculation = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(index - 1)
    debt.debtId                        shouldBe asMapTransposed.get("debtId").toString
    debt.mainTrans                     shouldBe asMapTransposed.get("mainTrans").toString
    debt.debtTypeDescription           shouldBe asMapTransposed.get("debtTypeDescription").toString
    debt.interestDueDebtTotal.toString shouldBe asMapTransposed.get("interestDueDebtTotal").toString
    debt.totalAmountIntDebt.toString   shouldBe asMapTransposed.get("totalAmountIntDebt").toString
    debt.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString
  }

  Then("the ([0-9])(?:st|nd|rd|th) sol debt summary will contain duties") { (debtIndex: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
      val responseBody = Json
        .parse(response.body)
        .as[SolCalculationSummaryResponse]
        .debts(debtIndex - 1)
        .duties(index)

      responseBody.dutyId                         shouldBe duty.get("dutyId").toString
      responseBody.subTrans                       shouldBe duty.get("subTrans").toString
      responseBody.dutyTypeDescription            shouldBe duty.get("dutyTypeDescription").toString
      responseBody.unpaidAmountDuty.toString      shouldBe duty.get("unpaidAmountDuty").toString
      responseBody.combinedDailyAccrual.toString  shouldBe duty.get("combinedDailyAccrual").toString
      responseBody.interestBearing.toString       shouldBe duty.get("interestBearing").toString
      responseBody.interestOnlyIndicator.toString shouldBe duty.get("interestOnlyIndicator").toString
    }
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) multiple statement of liability debt summary will contain duties") {
    (debtIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
        val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(0)
        responseBody.debtId                                     shouldBe duty.get("debtId").toString
        responseBody.mainTrans                                  shouldBe duty.get("mainTrans").toString
        responseBody.debtTypeDescription                        shouldBe duty.get("debtTypeDescription").toString
        responseBody.interestDueDebtTotal.toString              shouldBe duty.get("interestDueDebtTotal").toString
        responseBody.totalAmountIntDebt.toString                shouldBe duty.get("totalAmountIntDebt").toString
        responseBody.combinedDailyAccrual.toString              shouldBe duty.get("combinedDailyAccrualDebt").toString
        responseBody.duties.head.dutyId                         shouldBe duty.get("dutyId").toString
        responseBody.duties.head.subTrans                       shouldBe duty.get("subTrans").toString
        responseBody.duties.head.unpaidAmountDuty.toString      shouldBe duty.get("unpaidAmountDuty").toString
        responseBody.duties.head.combinedDailyAccrual.toString  shouldBe duty.get("combinedDailyAccrual").toString
        responseBody.duties.head.interestBearing.toString       shouldBe duty.get("interestBearing").toString
        responseBody.duties.head.interestOnlyIndicator.toString shouldBe duty.get("interestOnlyIndicator").toString

        print(" new array duty id **********  " + responseBody)

      }
  }

  Then("""the statement of liability debt summary response""") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    asMapTransposed.zipWithIndex.foreach { case (debt, index) =>
      val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(1)
      responseBody.debtId                                     shouldBe debt.get("debtId").toString
      responseBody.mainTrans                                  shouldBe debt.get("mainTrans").toString
      responseBody.debtTypeDescription                        shouldBe debt.get("debtTypeDescription").toString
      responseBody.interestDueDebtTotal.toString              shouldBe debt.get("interestDueDebtTotal").toString
      responseBody.totalAmountIntDebt.toString                shouldBe debt.get("totalAmountIntDebt").toString
      responseBody.combinedDailyAccrual.toString              shouldBe debt.get("combinedDailyAccrualDebt").toString
      responseBody.duties.head.dutyId                         shouldBe debt.get("dutyId").toString
      responseBody.duties.head.subTrans                       shouldBe debt.get("subTrans").toString
      responseBody.duties.head.unpaidAmountDuty.toString      shouldBe debt.get("unpaidAmountDuty").toString
      responseBody.duties.head.combinedDailyAccrual.toString  shouldBe debt.get("combinedDailyAccrual").toString
      responseBody.duties.head.interestBearing.toString       shouldBe debt.get("interestBearing").toString
      responseBody.duties.head.interestOnlyIndicator.toString shouldBe debt.get("interestOnlyIndicator").toString

      print(" new array duty id **********  " + responseBody)

    }
  }

  Then("""the sol service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body should include(expectedMessage)
  }

}
