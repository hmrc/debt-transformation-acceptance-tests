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
import uk.gov.hmrc.test.api.models.Errors
import uk.gov.hmrc.test.api.models.ttpp.{CreatePlanResponse, GenerateQuoteResponse, UpdatePlanResponse, ViewPlanResponse}
import uk.gov.hmrc.test.api.requests.TimeToPayProxyRequests
import uk.gov.hmrc.test.api.requests.TimeToPayProxyRequests.{addDebtItem, addPlan, addPostCodeDetails, _}
import uk.gov.hmrc.test.api.utils.ScenarioContext

class TimeToPayProxySteps extends ScalaDsl with EN with Eventually with Matchers {
  Given("a generate quote request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createGenerateQuoteRequestBody(dataTable)
  }

  Given("a create plan request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createCreatePlanRequestBody(dataTable)
  }
  Given("a create plan") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createPlanRequestBody(dataTable)
  }

  And("create payment plan details") { dataTable: DataTable =>
    planDetails(dataTable)
  }

  And("payment plan details") { dataTable: DataTable =>
    addPlan(dataTable)
  }

  Given("an update plan request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createRequestParameters(dataTable)
    TimeToPayProxyRequests.createUpdatePlanRequestBody(dataTable)
  }

  Given("a view plan request") { (dataTable: DataTable) =>
    TimeToPayProxyRequests.createRequestParameters(dataTable)
  }

  When("the view plan request is sent to the ttpp service") { () =>
    val customerReference = ScenarioContext.get("customerReference").toString
    val planId            = ScenarioContext.get("planId").toString
    val response          = TimeToPayProxyRequests.getPlan(customerReference, planId)

    ScenarioContext.set("response", response)
  }

  When("the update plan request is sent to the ttpp service") { () =>
    val request           = ScenarioContext.get("updatePlanRequest").toString
    val customerReference = ScenarioContext.get("customerReference").toString
    val planId            = ScenarioContext.get("planId").toString
    val response          = TimeToPayProxyRequests.putPlan(request, customerReference, planId)

    ScenarioContext.set("response", response)
  }

  When("the generate quote request is sent to the ttpp service") { () =>
    val request  = ScenarioContext.get("debtItems").toString
    println(s"TTP REQUEST ---------> $request")
    val response = TimeToPayProxyRequests.postQuote(request)
    println(s"TTP STUB RESPONSE ---------> ${response.body}")
    ScenarioContext.set("response", response)
  }

  When("the create plan request is sent to the ttpp service") { () =>
    val request  = ScenarioContext.get("debtItems").toString
    println(s"TTP REQUEST ---------> $request")
    val response = TimeToPayProxyRequests.createPlan(request)
    println(s"TTP STUB RESPONSE ---------> ${response.body}")
    ScenarioContext.set("response", response)

  }

  And("create instalment calculation details") { dataTable: DataTable =>
    planDetails(dataTable)
  }

  And("instalment calculation details") { dataTable: DataTable =>
    addPlan(dataTable)
  }
  And("customer address details") { dataTable: DataTable =>
    addAddressDetails(dataTable)
  }
  And("post codes details") { dataTable: DataTable =>
    addPostCodeDetails(dataTable)
  }
  And("debt payment method details") { dataTable: DataTable =>
    addPaymentMethod(dataTable)
  }
  And("customer debtItem details") { dataTable: DataTable => addDebtItem(dataTable) }

  And("payment history for the debt Item") { (dataTable: DataTable) =>
    addDebtPaymentHistory(dataTable)
  }

  And("debt instalment repayment details") { dataTable: DataTable => addInstalments(dataTable) }

  And("Debt is") { dataTable: DataTable => addDebt(dataTable) }

  And("Duty is") { dataTable: DataTable => addDuty(dataTable) }

  And("Breathing spaces are") { dataTable: DataTable =>
    addBreathingSpaces(dataTable)
  }

  And("Payments are") { dataTable: DataTable =>
    addPayments(dataTable)
  }

  And("the ([0-9]\\d*)(?:st|nd|rd|th) instalment will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)
    val generateQuoteResponse = ScenarioContext.get[GenerateQuoteResponse]("generateQuoteResponse")
    val nthInstalment         = generateQuoteResponse.instalments.head
    if (asMapTransposed.containsKey("debtItemChargeId")) {
      nthInstalment.debtItemChargeId shouldBe asMapTransposed.get("debtItemChargeId").toString
    }

    if (asMapTransposed.containsKey("debtItemId")) {
      nthInstalment.debtItemId shouldBe asMapTransposed.get("debtItemId").toString
    }

    if (asMapTransposed.containsKey("dueDate")) {
      nthInstalment.dueDate.toString shouldBe asMapTransposed.get("dueDate").toString
    }

    if (asMapTransposed.containsKey("amountDue")) {
      nthInstalment.amountDue.toString shouldBe asMapTransposed.get("amountDue").toString
    }

    if (asMapTransposed.containsKey("expectedPayment")) {
      nthInstalment.expectedPayment.toString shouldBe asMapTransposed.get("expectedPayment").toString
    }

    if (asMapTransposed.containsKey("interestRate")) {
      nthInstalment.interestRate.toString shouldBe asMapTransposed.get("interestRate").toString
    }

    if (asMapTransposed.containsKey("instalmentNumber")) {
      nthInstalment.instalmentNumber.toString shouldBe asMapTransposed.get("instalmentNumber").toString
    }

    if (asMapTransposed.containsKey("instalmentInterestAccrued")) {
      nthInstalment.instalmentInterestAccrued.toString shouldBe asMapTransposed
        .get("instalmentInterestAccrued")
        .toString
    }

    if (asMapTransposed.containsKey("instalmentBalance")) {
      nthInstalment.instalmentBalance.toString shouldBe asMapTransposed.get("instalmentBalance").toString
    }
  }

  Then("the ttp service is going to return a create plan response with") { dataTable: DataTable =>
    val asMapTransposed                =
      dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val responseBody = Json.parse(response.body).as[CreatePlanResponse]

    if (asMapTransposed.containsKey("customerReference")) {
      responseBody.customerReference shouldBe asMapTransposed.get("customerReference").toString

    }

    if (asMapTransposed.containsKey("planId")) {
      responseBody.planId shouldBe asMapTransposed.get("planId").toString
    }

    if (asMapTransposed.containsKey("caseId")) {
      responseBody.caseId shouldBe asMapTransposed.get("caseId").toString
    }

    if (asMapTransposed.containsKey("planStatus")) {
      responseBody.planStatus shouldBe asMapTransposed.get("planStatus").toString
    }
  }

  Then("the ttp service is going to return an update response with") { dataTable: DataTable =>
    val asMapTransposed                =
      dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[UpdatePlanResponse]

    if (asMapTransposed.containsKey("customerReference")) {
      responseBody.customerReference shouldBe asMapTransposed.get("customerReference").toString
    }

    if (asMapTransposed.containsKey("planId")) {
      responseBody.planId shouldBe asMapTransposed.get("planId").toString
    }

    if (asMapTransposed.containsKey("quoteStatus")) {
      responseBody.quoteStatus shouldBe asMapTransposed.get("quoteStatus").toString
    }

    if (asMapTransposed.containsKey("quoteUpdatedDate")) {
      responseBody.quoteUpdatedDate.toString shouldBe asMapTransposed.get("quoteUpdatedDate").toString
    }
  }

  Then("the ttp service is going to return a generate quote response with") { dataTable: DataTable =>
    val asMapTransposed                =
      dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val responseBody = Json.parse(response.body).as[GenerateQuoteResponse]
    ScenarioContext.set("generateQuoteResponse", responseBody)
    if (asMapTransposed.containsKey("customerReference")) {
      responseBody.customerReference shouldBe asMapTransposed.get("customerReference").toString
    }

    if (asMapTransposed.containsKey("quoteReference")) {
      responseBody.quoteReference shouldBe asMapTransposed.get("quoteReference").toString
    }

    if (asMapTransposed.containsKey("quoteType")) {
      responseBody.quoteType shouldBe asMapTransposed.get("quoteType").toString
    }

    if (asMapTransposed.containsKey("quoteDate")) {
      responseBody.quoteDate shouldBe asMapTransposed.get("quoteDate").toString
    }

    if (asMapTransposed.containsKey("numberOfInstalments")) {
      responseBody.numberOfInstalments.toString shouldBe asMapTransposed.get("numberOfInstalments").toString
    }
    if (asMapTransposed.containsKey("totalDebtincInt")) {
      responseBody.totalDebtincInt.toString() shouldBe asMapTransposed.get("totalDebtincInt").toString
    }

    if (asMapTransposed.containsKey("interestAccrued")) {
      responseBody.interestAccrued.toString() shouldBe asMapTransposed.get("interestAccrued").toString
    }

    if (asMapTransposed.containsKey("planInterest")) {
      responseBody.planInterest.toString() shouldBe asMapTransposed.get("planInterest").toString
    }
  }

  Then("the ttp service is going to return a view response with") { dataTable: DataTable =>
    val asMapTransposed                =
      dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = Json.parse(response.body).as[ViewPlanResponse]

    ScenarioContext.set("viewPlanResponse", responseBody)

    if (asMapTransposed.containsKey("customerReference")) {
      responseBody.customerReference shouldBe asMapTransposed.get("customerReference").toString
    }

    if (asMapTransposed.containsKey("channelIdentifier")) {
      responseBody.channelIdentifier shouldBe asMapTransposed.get("channelIdentifier").toString
    }
  }

  And("the plan will contain") { dataTable: DataTable =>
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody = Json.parse(response.body).as[ViewPlanResponse].plan

    ScenarioContext.set("viewPlanResponse", responseBody)

    if (asMapTransposed.containsKey("planId")) {
      responseBody.planId.value shouldBe asMapTransposed.get("planId").toString
    }

    if (asMapTransposed.containsKey("quoteId")) {
      responseBody.quoteId.value shouldBe asMapTransposed.get("quoteId").toString
    }

    if (asMapTransposed.containsKey("quoteType")) {
      responseBody.quoteType.entryName shouldBe asMapTransposed.get("quoteType").toString
    }

    if (asMapTransposed.containsKey("paymentPlanType")) {
      responseBody.paymentPlanType shouldBe asMapTransposed.get("paymentPlanType").toString
    }

    if (asMapTransposed.containsKey("thirdPartyBank")) {
      responseBody.thirdPartyBank.toString shouldBe asMapTransposed.get("thirdPartyBank").toString
    }

    if (asMapTransposed.containsKey("numberOfInstalments")) {
      responseBody.numberOfInstalments.toString shouldBe asMapTransposed.get("numberOfInstalments").toString
    }

    if (asMapTransposed.containsKey("totalDebtIncInt")) {
      responseBody.totalDebtIncInt.toString shouldBe asMapTransposed.get("totalDebtIncInt").toString
    }

    if (asMapTransposed.containsKey("totalInterest")) {
      responseBody.totalInterest.toString shouldBe asMapTransposed.get("totalInterest").toString
    }

    if (asMapTransposed.containsKey("interestAccrued")) {
      responseBody.interestAccrued.toString shouldBe asMapTransposed.get("interestAccrued").toString
    }

    if (asMapTransposed.containsKey("planInterest")) {
      responseBody.planInterest.toString shouldBe asMapTransposed.get("planInterest").toString
    }
  }

  And("the ([0-9]\\d*)(?:st|nd|rd|th) view response instalment will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val generateQuoteResponse = Json.parse(response.body).as[ViewPlanResponse]

    val nthInstalment = generateQuoteResponse.instalments(index - 1)
    if (asMapTransposed.containsKey("debtItemChargeId")) {
      nthInstalment.debtItemChargeId shouldBe asMapTransposed.get("debtItemChargeId").toString
    }

    if (asMapTransposed.containsKey("dueDate")) {
      nthInstalment.dueDate.toString shouldBe asMapTransposed
        .get("dueDate")
        .toString
    }

    if (asMapTransposed.containsKey("amountDue")) {
      nthInstalment.amountDue.toString shouldBe asMapTransposed
        .get("amountDue")
        .toString
    }

    if (asMapTransposed.containsKey("expectedPayment")) {
      nthInstalment.expectedPayment.toString shouldBe asMapTransposed
        .get("expectedPayment")
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
    if (asMapTransposed.containsKey("instalmentInterestAccrued")) {
      nthInstalment.instalmentInterestAccrued.toString shouldBe asMapTransposed
        .get("instalmentInterestAccrued")
        .toString
    }

    if (asMapTransposed.containsKey("instalmentBalance")) {
      nthInstalment.instalmentBalance.toString shouldBe asMapTransposed
        .get("instalmentBalance")
        .toString
    }
  }

  Then("the ttpp response code should be {int}") { expectedCode: Int =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(expectedCode)
  }

  And("ttp service returns error message (.*)") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = response.body
    print("response message*****************************" + responseBody)
    responseBody should be(expectedMessage)
  }

  Then("the ttp service will respond with") { (dataTable: DataTable) =>
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
  }



}
