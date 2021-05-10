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

package uk.gov.hmrc.test.api.cucumber.stepdefs.sol

import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.cucumber.stepdefs.BaseStepDef
import uk.gov.hmrc.test.api.models.{HelloWorld, SolCalculation, SolCalculationSummary, SolCalculationSummaryResponse}
import uk.gov.hmrc.test.api.requests.{HelloWorldRequests, RequestSolDetails, TestData}
import uk.gov.hmrc.test.api.utils.ScenarioContext

class StatementOfLiabilityStepDef extends BaseStepDef {

  When("a request is made to get response from sol hello world endpoint") { () =>
    val response =
      HelloWorldRequests.getStatementLiabilityService("/hello-world")
    ScenarioContext.set("response", response)
  }

  When("a request is made to an invalid sol endpoint") { () =>
    val response =
      HelloWorldRequests.getStatementLiabilityService("/helloo-world")
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
    val asMapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem           = false
    var debtDetails: String = null

    try ScenarioContext.get("debtDetails")
    catch { case e: Exception => firstItem = true }

    val debtDetailsTestfile = getBodyAsString("debtDetailsTestfile")
      .replaceAll("<REPLACE_solType>", asMapTransposed.get("solType"))
      .replaceAll("<REPLACE_debtID>", asMapTransposed.get("debtId"))
      .replaceAll("<REPLACE_mainTrans>", asMapTransposed.get("mainTrans"))
      .replaceAll("<REPLACE_subTrans>", asMapTransposed.get("subTrans"))

    if (firstItem == true) { debtDetails = debtDetailsTestfile }
    else { debtDetails = ScenarioContext.get("debtDetails").toString.concat(",").concat(debtDetailsTestfile) }

    ScenarioContext.set("debtDetails", debtDetails)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  And("""add debt item chargeIDs to the debt""") { (dataTable: DataTable) =>
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var dutyChargeIds   = ""

    asMapTransposed.zipWithIndex.foreach { case (dutyId, index) =>
      dutyChargeIds = dutyChargeIds.concat(dutyId.get("dutyId"))
      if (index + 1 < asMapTransposed.size) dutyChargeIds = dutyChargeIds.concat(",")
    }
    val jsonWithDutyChargeId =
      ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_debtItemChargeIDs>", dutyChargeIds)
    ScenarioContext.set("debtDetails", jsonWithDutyChargeId)
  }

  When("""a debt statement of liability is requested""") {
    val request = ScenarioContext.get("debtDetails").toString

    val response =
      RequestSolDetails.getStatementOfLiability(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("service returns debt statement of liability data") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse]

    responseBody.totalAmountIntDuty.toString   shouldBe asMapTransposed.get("totalAmountIntDuty").toString
    responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get("combinedDailyAccrual").toString

  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) sol debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val debt: SolCalculation = Json.parse(response.body).as[SolCalculationSummary].debts(index - 1)
    debt.debtID           shouldBe asMapTransposed.get("debtID").toString
    debt.mainTrans                     shouldBe asMapTransposed.get("mainTrans").toString
    debt.description                   shouldBe asMapTransposed.get("description").toString
    debt.periodEnd.toString            shouldBe asMapTransposed.get("periodEnd").toString
    debt.interestDueDutyTotal.toString shouldBe asMapTransposed.get("interestDueDutyTotal").toString
  }

  Then("the ([0-9])(?:st|nd|rd|th) sol debt summary will contain duties") { (debtIndex: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
      val responseBody = Json
        .parse(response.body)
        .as[SolCalculationSummary]
        .debts(debtIndex - 1)
        .duties(index)

      responseBody.debtItemChargeID              shouldBe duty.get("debtItemChargeID").toString
      responseBody.subTrans                      shouldBe duty.get("subTrans").toString
      responseBody.description                   shouldBe duty.get("description").toString
      responseBody.unpaidAmountDuty.toString     shouldBe duty.get("unpaidAmountDuty").toString
      responseBody.combinedDailyAccrual.toString shouldBe duty.get("combinedDailyAccrual").toString
    }
  }

  Then("""the sol service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
  }

}
