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

package uk.gov.hmrc.test.api.cucumber.stepdefs.ifs

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models._
import uk.gov.hmrc.test.api.requests.FieldCollectionsRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

import scala.jdk.CollectionConverters.CollectionHasAsScala

class FCInterestForecastingSteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("a fc debt item") { (dataTable: DataTable) =>
    createInterestFocastingRequestBodyFC(dataTable)
  }

  Given("fc debt item with cotax charge interest") { (dataTable: DataTable) =>
    createFcCotaxChargeInterestRequest(dataTable)
  }

  Given("the debt item has fc payment history") { (dataTable: DataTable) =>
    addFCPaymentHistory(dataTable)
  }

  Given("the fc debt item has no payment history") { () =>
    fcCustomerWithNoPaymentHistory()
  }

  When("the debt item(s) is sent to the fc ifs service") { () =>
    val request  = ScenarioContext.get("fcDebtItem").toString
    println(s"IFS REQUEST --> $request")
    val response = getDebtCalculation(request)
    println(s"IFS RESPONSE --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  Then("the fc ifs service wilL return a total debts summary of") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")

    val responseBody = Json.parse(response.body).as[FCDebtCalculationsSummary]

    locally {
      val fieldName = "combinedDailyAccrual"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.combinedDailyAccrual.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "unpaidAmountTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.unpaidAmountTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "interestDueCallTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.interestDueCallTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "totalAmountIntTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.totalAmountIntTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }
    locally {
      val fieldName = "amountOnIntDueTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.amountOnIntDueTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) fc debt summary will contain") { (index: Int, dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status should be(200)

    val responseBody: FCDebtCalculation =
      Json.parse(response.body).as[FCDebtCalculationsSummary].debtCalculations(index - 1)

    locally {
      val fieldName = "debtItemChargeId"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.debtItemChargeId shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "interestDueDailyAccrual"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.interestDueDailyAccrual.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "interestDueDutyTotal"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.interestDueDutyTotal.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "amountOnIntDueDuty"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.amountOnIntDueDuty.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "totalAmountIntDuty"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.totalAmountIntDuty.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "unpaidAmountDuty"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.unpaidAmountDuty.toString shouldBe asMapTransposed.get(fieldName).toString
        }
      }
    }
  }

  Then("the ([0-9])(?:st|nd|rd|th) fc debt summary will have calculation windows") {
    (summaryIndex: Int, dataTable: DataTable) =>
      val asMapTransposed                = dataTable.asMaps(classOf[String], classOf[String]).asScala
      val response: StandaloneWSResponse = ScenarioContext.get("response")

      asMapTransposed.zipWithIndex.foreach { case (window, index) =>
        val responseBody =
          Json
            .parse(response.body)
            .as[FCDebtCalculationsSummary]
            .debtCalculations(summaryIndex - 1)
            .calculationWindows(index)

        locally {
          val fieldName = "periodFrom"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.periodFrom.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "periodTo"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.periodTo.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "numberOfDays"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.numberOfDays.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestRate"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.interestRate.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestDueDailyAccrual"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.interestDueDailyAccrual.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestDueWindow"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.interestDueWindow.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "interestDueWindow"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.interestDueWindow.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "unpaidAmountWindow"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.unpaidAmountWindow.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "amountOnIntDueWindow"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.amountOnIntDueWindow.toString shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "reason"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.suppressionApplied.head.reason shouldBe window.get(fieldName).toString
            }
          }
        }

        locally {
          val fieldName = "code"
          if (window.containsKey(fieldName)) {
            withClue(s"$fieldName: ") {
              responseBody.suppressionApplied.head.code shouldBe window.get(fieldName).toString
            }
          }
        }
      }
  }
  And("the fc customer has breathing spaces applied") { (dataTable: DataTable) =>
    addFCBreathingSpace(dataTable)
  }

  Given("no breathing spaces have been applied to the fc debt item") { () =>
    noFCBreathingSpace()
  }

  Given("the fc customer has post codes") { (dataTable: DataTable) =>
    addFCCustomerPostCodes(dataTable)
  }

  Given("add charge interest cotax") { (dataTable: DataTable) =>
    addChargedInterestCotax(dataTable)
  }

  Given("the fc customer has no post codes") { () =>
    noFCCustomerPostCodes()
  }

  Then("the ([0-9])(?:st|nd|rd|th) fc debt summary will not have any calculation windows") { (summaryIndex: Int) =>
    getFCCountOfCalculationWindows(summaryIndex) shouldBe 0
  }

  Then("""the fc ifs service will respond with (.*)""") { (expectedMessage: String) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.body   should include(expectedMessage)
    response.status should be(400)
  }

  Then("the ([0-9])(?:st|nd|rd|th) fc debt summary will have ([0-9]) calculation windows") {
    (summaryIndex: Int, numberOfWindows: Int) =>
      getFCCountOfCalculationWindows(summaryIndex) shouldBe numberOfWindows
  }

  def getFCCountOfCalculationWindows(summaryIndex: Int): Int = {
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    Json.parse(response.body).as[FCDebtCalculationsSummary].debtCalculations(summaryIndex - 1).calculationWindows.size
  }
}
