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

package uk.gov.hmrc.test.api.cucumber.stepdefs.ttpp

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.models.ttpp.{GenerateQuoteResponse, UpdatePlanResponse, ViewPlanResponse}
import uk.gov.hmrc.test.api.requests.TimeToPayProxyRequests
import uk.gov.hmrc.test.api.requests.TimeToPayProxyRequests._
import uk.gov.hmrc.test.api.utils.{ScenarioContext, TestData}

class TimeToPayProxySteps
    extends ScalaDsl
    with EN
    with Eventually
    with Matchers {
  Given("a generate quote request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createGenerateQuoteRequestBody(dataTable)

  }

  Given("an update plan request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createRequestParameters(dataTable)
    TimeToPayProxyRequests.createUpdatePlanRequestBody(dataTable)
  }

  Given("an view plan request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createRequestParameters(dataTable)
  }

  When("the view plan request is sent to the ttpp service") { () =>

    val customerReference = ScenarioContext.get("customerReference").toString
    val planId = ScenarioContext.get("planId").toString
    val response = TimeToPayProxyRequests.getPlan(customerReference, planId)

    ScenarioContext.set("response", response)
  }


  When("the update plan request is sent to the ttpp service") { () =>

    val request = ScenarioContext.get("updatePlanRequest").toString
    val customerReference = ScenarioContext.get("customerReference").toString
    val planId = ScenarioContext.get("planId").toString
    val response = TimeToPayProxyRequests.putPlan(request, customerReference, planId)

    ScenarioContext.set("response", response)
  }

  When("the generate quote request is sent to the ttpp service") { () =>
    addAdHocsToGenerateQuoteRequest()
    addCustomersToGenerateQuoteRequest()
    addDebtsToGenerateQuoteRequest()

    val request = ScenarioContext.get("generateQuoteRequest").toString

    val response = TimeToPayProxyRequests.postQuote(request)

    ScenarioContext.set("response", response)
  }

  And("customer values are") { dataTable: DataTable => addCustomer(dataTable)
  }

  And("adHoc values are") { dataTable: DataTable => addAdHocs(dataTable)
  }

  And("Debt is") { dataTable: DataTable => addDebt(dataTable)
  }

  And("Duty is") { dataTable: DataTable => addDuty(dataTable)
  }

  And("Breathing spaces are") { dataTable: DataTable =>
    addBreathingSpaces(dataTable)
  }

  And("the ([0-9]\\d*)(?:st|nd|rd|th) instalment will contain") {
    (index: Int, dataTable: DataTable) =>
      val asMapTransposed =
        dataTable.transpose().asMap(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      response.status should be(200)

      val generateQuoteResponse =
        ScenarioContext.get[GenerateQuoteResponse]("generateQuoteResponse")
      val nthInstalment = generateQuoteResponse.instalments(index - 1)
      if (asMapTransposed.containsKey("dutyId")) {
        nthInstalment.dutyId shouldBe asMapTransposed.get("dutyId").toString
      }

      if (asMapTransposed.containsKey("debtId")) {
        nthInstalment.debtId shouldBe asMapTransposed.get("debtId").toString
      }

      if (asMapTransposed.containsKey("dueDate")) {
        nthInstalment.dueDate.toString shouldBe asMapTransposed
          .get("dueDate")
          .toString
      }

      if (asMapTransposed.containsKey("interestRate")) {
        nthInstalment.interestRate.toString shouldBe asMapTransposed
          .get("interestRate")
          .toString
      }

      if (asMapTransposed.containsKey("instalmentNumber")) {
        nthInstalment.instalmentNumber.toString shouldBe asMapTransposed
          .get("instalmentNumber")
          .toString
      }
  }

  Then("the ttp service is going to return an update response with") {
    dataTable: DataTable =>
      val asMapTransposed =
        dataTable.transpose().asMap(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      val responseBody = Json.parse(response.body).as[UpdatePlanResponse]

      if (asMapTransposed.containsKey("customerReference")) {
        responseBody.customerReference shouldBe asMapTransposed
          .get("customerReference")
          .toString
      }

      if (asMapTransposed.containsKey("planId")) {
        responseBody.planId shouldBe asMapTransposed
          .get("planId")
          .toString
      }

      if (asMapTransposed.containsKey("quoteStatus")) {
        responseBody.quoteStatus shouldBe asMapTransposed
          .get("quoteStatus")
          .toString
      }

      if (asMapTransposed.containsKey("quoteUpdatedDate")) {
        responseBody.quoteUpdatedDate.toString shouldBe asMapTransposed
          .get("quoteUpdatedDate")
          .toString
      }
  }

  And("the ([0-9]\\d*)(?:st|nd|rd|th) view response instalment will contain") {
    (index: Int, dataTable: DataTable) =>
      val asMapTransposed =
        dataTable.transpose().asMap(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      response.status should be(200)

      val generateQuoteResponse =
        ScenarioContext.get[ViewPlanResponse]("viewPlanResponse")

      val nthInstalment = generateQuoteResponse.instalments(index - 1)
      if (asMapTransposed.containsKey("dutyId")) {
        nthInstalment.dutyId shouldBe asMapTransposed.get("dutyId").toString
      }

      if (asMapTransposed.containsKey("debtId")) {
        nthInstalment.debtId shouldBe asMapTransposed.get("debtId").toString
      }

      if (asMapTransposed.containsKey("dueDate")) {
        nthInstalment.dueDate.toString shouldBe asMapTransposed
          .get("dueDate")
          .toString
      }

      if (asMapTransposed.containsKey("balance")) {
        nthInstalment.balance.toString shouldBe asMapTransposed
          .get("balance")
          .toString
      }

      if (asMapTransposed.containsKey("interest")) {
        nthInstalment.interest.toString shouldBe asMapTransposed
          .get("interest")
          .toString
      }

      if (asMapTransposed.containsKey("interestRate")) {
        nthInstalment.interestRate.toString shouldBe asMapTransposed
          .get("interestRate")
          .toString
      }

      if (asMapTransposed.containsKey("instalmentNumber")) {
        nthInstalment.instalmentNumber.toString shouldBe asMapTransposed
          .get("instalmentNumber")
          .toString
      }
  }

  Then("the ttp service is going to return a generate quote response with") {
    dataTable: DataTable =>
      val asMapTransposed =
        dataTable.transpose().asMap(classOf[String], classOf[String])

      val response: StandaloneWSResponse = ScenarioContext.get("response")

      val responseBody = Json.parse(response.body).as[GenerateQuoteResponse]

      ScenarioContext.set("generateQuoteResponse", responseBody)

      if (asMapTransposed.containsKey("customerReference")) {
        responseBody.customerReference shouldBe asMapTransposed
          .get("customerReference")
          .toString
      }

      if (asMapTransposed.containsKey("quoteReference")) {
        responseBody.quoteReference shouldBe asMapTransposed
          .get("quoteReference")
          .toString
      }

      if (asMapTransposed.containsKey("quoteType")) {
        responseBody.quoteType shouldBe asMapTransposed
          .get("quoteType")
          .toString
      }
      if (asMapTransposed.containsKey("numberOfInstalments")) {
        responseBody.numberOfInstalments.toString shouldBe asMapTransposed
          .get("numberOfInstalments")
          .toString
      }
      if (asMapTransposed.containsKey("totalDebtAmount")) {
        responseBody.totalDebtAmount.toString shouldBe asMapTransposed
          .get("totalDebtAmount")
          .toString
      }
      if (asMapTransposed.containsKey("totalInterest")) {
        responseBody.totalInterest.toString shouldBe asMapTransposed
          .get("totalInterest")
          .toString
      }
  }

  Then("the ttp service is going to return an view response with") {
    dataTable: DataTable =>
      val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      val responseBody = Json.parse(response.body).as[ViewPlanResponse]

      ScenarioContext.set("viewPlanResponse", responseBody)

      if (asMapTransposed.containsKey("customerReference")) {
        responseBody.customerReference shouldBe asMapTransposed
          .get("customerReference")
          .toString
      }

      if (asMapTransposed.containsKey("planId")) {
        responseBody.planId shouldBe asMapTransposed
          .get("planId")
          .toString
      }

      if (asMapTransposed.containsKey("quoteType")) {
        responseBody.quoteType shouldBe asMapTransposed
          .get("quoteType")
          .toString
      }

      if (asMapTransposed.containsKey("paymentMethod")) {
        responseBody.paymentMethod shouldBe asMapTransposed
          .get("paymentMethod")
          .toString
      }

      if (asMapTransposed.containsKey("paymentReference")) {
        responseBody.paymentReference shouldBe asMapTransposed
          .get("paymentReference")
          .toString
      }

      if (asMapTransposed.containsKey("numberOfInstalments")) {
        responseBody.numberOfInstalments.toString shouldBe asMapTransposed
          .get("numberOfInstalments")
          .toString
      }
      if (asMapTransposed.containsKey("totalDebtAmount")) {
        responseBody.totalDebtAmount.toString shouldBe asMapTransposed
          .get("totalDebtAmount")
          .toString
      }
      if (asMapTransposed.containsKey("totalInterest")) {
        responseBody.totalInterest.toString shouldBe asMapTransposed
          .get("totalInterest")
          .toString
      }
  }

  When("a request is made to get response from ttpp hello world endpoint") {
    () =>
      val response =
        TimeToPayProxyRequests.getTimeToPayProxy("/hello-world")
      ScenarioContext.set("response", response)
  }

  When("a request is made to an invalid ttpp endpoint") { () =>
    val response =
      TimeToPayProxyRequests.getTimeToPayProxy("/helloo-world")
    ScenarioContext.set("response", response)
  }

  When(
    "a request is made to get response from ttpp hello world endpoint without bearer token"
  ) { () =>
    val response =
      TimeToPayProxyRequests.getTimeToPayProxyWithoutBearerToken("/hello-world")
    ScenarioContext.set("response", response)
  }

  And("""the ttpp hello world response body should be (.*)""") {
    message: String =>
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      val responseBody = response.body
      responseBody should be(message)
  }

  Then("the ttpp response code should be {int}") { expectedCode: Int =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }

}
