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
import uk.gov.hmrc.test.api.models.sol.{HelloWorld, SolCalculation, SolCalculationSummaryResponse}
import uk.gov.hmrc.test.api.requests.StatementOfLiabilityRequests
import uk.gov.hmrc.test.api.utils.{ScenarioContext, TestData}

import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala
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

  Given("a request to sol with no debt items provided") {
    ScenarioContext.set(
      "debtDetails",
      "{" + "\"solType\":\"UI\"," +
        "\"customerUniqueRef\":\"XZ0000100351724\"," +
        "\"debts\":[ ]}"
    )
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
      .replaceAll("REPLACE_debtId", asMapTransposed.get("debtId"))
      .replaceAll("REPLACE_ID", asMapTransposed.get("debtId2"))
      .replaceAll("REPLACE_interestRequestedTo", asMapTransposed.get("interestRequestedTo"))

    if (firstItem == true) { multipleDebtDetails = SolMultipleDebts }
    else { multipleDebtDetails = ScenarioContext.get("debtDetails").toString.concat(",").concat(SolMultipleDebts) }

    ScenarioContext.set("debtDetails", multipleDebtDetails)
    println(s"request:::::::::::::::$multipleDebtDetails")
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

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

    locally {
      val fieldName = "amountIntTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName") {
          responseBody.amountIntTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "combinedDailyAccrual"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName") {
          responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }
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
    val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String]).asScala
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
      val responseBody = Json
        .parse(response.body)
        .as[SolCalculationSummaryResponse]
        .debts(debtIndex - 1)
        .duties(index)

      responseBody.subTrans shouldBe duty.get("subTrans").toString

      locally {
        val fieldName = "dutyTypeDescription"
        if (duty.containsKey(fieldName)) {
          withClue(s"$fieldName") {
            responseBody.dutyTypeDescription.toString contains duty.get(fieldName).toString

          }
        }
      }
      responseBody.unpaidAmountDuty.toString      shouldBe duty.get("unpaidAmountDuty").toString
      responseBody.combinedDailyAccrual.toString  shouldBe duty.get("combinedDailyAccrual").toString
      responseBody.interestBearing.toString       shouldBe duty.get("interestBearing").toString
      responseBody.interestOnlyIndicator.toString shouldBe duty.get("interestOnlyIndicator").toString
    }
  }

  And("the {int}(st|nd|rd|th) customer statement of liability contains duty values as") {
    (debtIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String]).asScala
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      val responseDebts                  = Json.parse(response.body).as[SolCalculationSummaryResponse].debts
      val duties                         = responseDebts(debtIndex - 1).duties

      asMapTransposed.zipWithIndex.foreach { case (row, index) =>
        val duty = duties(index)

        locally {
          val fieldName = "subTrans"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.subTrans shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "dutyTypeDescription"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.dutyTypeDescription shouldBe Some(row.get(fieldName))
            }
          }
        }

        locally {
          val fieldName = "unpaidAmountDuty"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.unpaidAmountDuty.toString shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "combinedDailyAccrual"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.combinedDailyAccrual.toString shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestBearing"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.interestBearing.toString shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestOnlyIndicator"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              duty.interestOnlyIndicator.toString shouldBe row.get(fieldName).toString
            }
          }
        }
      }
  }

  And("""the {int}(st|nd|rd|th) customer statement of liability contains debt values as""") {
    (debtIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String]).asScala
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (row, index) =>
        val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(debtIndex - 1)

        locally {
          val fieldName = "debtId"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.debtId shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "mainTrans"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.mainTrans shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "debtTypeDescription"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.debtTypeDescription shouldBe row.get(fieldName).toString
            }
          }
        }
        locally {
          val fieldName = "interestDueDebtTotal"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.interestDueDebtTotal.toString shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "totalAmountIntDebt"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.totalAmountIntDebt.toString() shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "combinedDailyAccrual"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.debtTypeDescription shouldBe row.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "parentMainTrans"
          if (row.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.parentMainTrans.toString shouldBe Some(row.get(fieldName)).toString
            }
          }
        }

      }

  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) multiple statement of liability duties summary will contain") {
    (debtIndex: Int, dataTable: DataTable) =>
      val asMapTransposed: Iterable[util.Map[Nothing, Nothing]] =
        dataTable.asMaps(classOf[String], classOf[String]).asScala
      val response: StandaloneWSResponse                        = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (duty, index) =>
        val responseBody = Json.parse(response.body).as[SolCalculationSummaryResponse].debts(debtIndex - 1)

        locally {
          val fieldName = "unpaidAmountDuty"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.unpaidAmountDuty.toString shouldBe duty.get("unpaidAmountDuty").toString
            }
          }
        }

        locally {
          val fieldName = "combinedDailyAccrual"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.combinedDailyAccrual.toString shouldBe duty.get("combinedDailyAccrual").toString
            }
          }
        }

        locally {
          val fieldName = "interestBearing"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.interestBearing.toString shouldBe duty.get("interestBearing").toString
            }
          }
        }

        locally {
          val fieldName = "unpaidAmountDuty"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.interestOnlyIndicator.toString shouldBe duty
                .get("interestOnlyIndicator")
                .toString
            }
          }
        }

        locally {
          val fieldName = "unpaidAmountDuty"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.subTrans shouldBe duty.get("subTrans").toString
            }
          }
        }

        locally {
          val fieldName = "dutyTypeDescription"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.dutyTypeDescription shouldBe duty.get("dutyTypeDescription").toString
            }
          }
        }

        locally {
          val fieldName = "combinedDailyAccrual"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.combinedDailyAccrual shouldBe duty.get("combinedDailyAccrual").toString
            }
          }
        }
        locally {
          val fieldName = "interestBearing"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.interestBearing shouldBe duty.get("interestBearing").toString
            }
          }
        }

        locally {
          val fieldName = "interestOnlyIndicator"
          if (duty.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.duties.head.interestOnlyIndicator shouldBe duty.get("interestOnlyIndicator").toString
            }
          }
        }

      }
  }

  Then("""the sol service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body shouldBe expectedMessage
  }

}
