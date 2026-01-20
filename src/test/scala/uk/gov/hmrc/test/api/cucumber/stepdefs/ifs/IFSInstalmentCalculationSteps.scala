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
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.requests.IFSInstalmentCalculationRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.CollectionHasAsScala

class IFSInstalmentCalculationSteps extends ScalaDsl with EN with Eventually with Matchers {

  var quoteDateString       = "2022-03-13"
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val quoteDate: LocalDate = LocalDate.parse(quoteDateString, formatter)
  val instalmentPaymentDate: LocalDate = quoteDate.plusDays(1)

  Given("debt instalment calculation with details") { (dataTable: DataTable) =>
    createInstalmentCalculationRequestBody(dataTable)
  }

  Given("debt instalment calculation with 129 details") { (dataTable: DataTable) =>
    createInstalmentCalculationRequestBody(dataTable)
  }

  Given("the instalment calculation has debt item charges") { (dataTable: DataTable) =>
    addDebtItemChargesToInstalmentCalculation(dataTable)
  }

  Given("the instalment calculation has postcode (.*) with postcode date a year in the past") { (postCode: String) =>
    addPostCodeToInstalmentCalculation(postCode, LocalDate.now().minusYears(1).toString)
  }

  Given("the instalment calculation has no postcodes") { () =>
    addEmptyPostCodeArrayToInstalmentCalculation()
  }

  Given("debt plan details with initial payment") { (dataTable: DataTable) =>
    addInitialPayment(dataTable)
  }

  Given("no initial payment for the debt item charge") { () =>
    noInitialPayment()
  }

  When("the instalment calculation detail(s) is sent to the ifs service") { () =>
    val request  = ScenarioContext.get("paymentPlan").toString
    println(s"IFS REQUST --> $request")
    val response = getInstalmentCalculation(request)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  When("the instalment calculation is sent to the ifs service with query parameters") { (dataTable: DataTable) =>
    val map                    = dataTable.transpose().asMap(classOf[String], classOf[String])
    val combineLastInstalments = map.get("combineLastInstalments").toString
    val request                = ScenarioContext.get("paymentPlan").toString
    println(s"IFS REQUST --> $request")
    val response               = getInstalmentCalculationWithQueryParams(request, combineLastInstalments)
    println(s"RESP --> ${response.body}")
    ScenarioContext.set("response", response)
  }

  And("add initial payment for the debt item charge") { (dataTable: DataTable) =>
    addInitialPayment(dataTable)
  }

  Then("ifs service returns weekly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      39,
      1423,
      1423 + 39,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1), 10000, 90000, 6, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2), 10000, 80000, 5, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3), 10000, 70000, 4, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4), 10000, 60000, 4, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5), 10000, 50000, 3, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6), 10000, 40000, 2, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7), 10000, 30000, 2, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8), 10000, 20000, 1, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9), 10000, 10000, 0, 100000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusWeeks(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs returns payment frequency summary") { (dataTable: DataTable) =>
    val asMapTransposed                = dataTable.transpose().asMap(classOf[String], classOf[String])
    val response: StandaloneWSResponse = ScenarioContext.get("paymentPlan")
    response.status should be(200)
    val paymentPlanSummary = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]
    paymentPlanSummary.numberOfInstalments.toString shouldBe (asMapTransposed
      .get("numberOfInstalments")
      .toString)

    locally {
      val fieldName = "totalPlanInt"
      if (asMapTransposed.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          paymentPlanSummary.planInterest.toString contains asMapTransposed.get(fieldName).toString
        }
      }
    }
    locally {
      val fieldName = "interestAccrued"
      if (asMapTransposed.containsKey("interestCallDueTotal")) {
        withClue(s"$fieldName: ") {
          paymentPlanSummary.interestAccrued.toString contains asMapTransposed.get(fieldName).toString
        }
      }
    }
  }

  Then("ifs service returns an interest bearing payment instalment plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      0,
      1423,
      1423 + 0,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusDays(1), 10000, 90000, 6, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusDays(2), 10000, 80000, 5, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusDays(3), 10000, 70000, 4, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusDays(4), 10000, 60000, 4, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusDays(5), 10000, 50000, 3, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusDays(6), 10000, 40000, 2, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusDays(7), 10000, 30000, 2, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusDays(8), 10000, 20000, 1, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusDays(9), 10000, 10000, 0, 100000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusDays(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )

  }

  Then("ifs service returns an non-interest bearing payment instalment plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      218,
      1423,
      1423 + 218,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusDays(1), 10000, 90000, 6, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusDays(2), 10000, 80000, 5, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusDays(3), 10000, 70000, 4, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusDays(4), 10000, 60000, 4, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusDays(5), 10000, 50000, 3, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusDays(6), 10000, 40000, 2, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusDays(7), 10000, 30000, 2, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusDays(8), 10000, 20000, 1, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusDays(9), 10000, 10000, 0, 100000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusDays(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )

  }

  Then("ifs service returns single payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      39,
      1423,
      1423 + 39,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusDays(1), 10000, 90000, 6, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusDays(2), 10000, 80000, 5, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusDays(3), 10000, 70000, 4, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusDays(4), 10000, 60000, 4, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusDays(5), 10000, 50000, 3, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusDays(6), 10000, 40000, 2, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusDays(7), 10000, 30000, 2, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusDays(8), 10000, 20000, 1, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusDays(9), 10000, 10000, 0, 100000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusDays(10), 1462, 0, 0, 100000 + 1462, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns 2-Weekly frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      455,
      1423,
      1423 + 455,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1 * 2), 10000, 90000, 89, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2 * 2), 10000, 80000, 79, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3 * 2), 10000, 70000, 69, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4 * 2), 10000, 60000, 59, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5 * 2), 10000, 50000, 49, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6 * 2), 10000, 40000, 39, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7 * 2), 10000, 30000, 29, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8 * 2), 10000, 20000, 19, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9 * 2), 10000, 10000, 9, 100000, 2.6),
        InstalmentResponse(
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

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns monthly payment frequency instalment plan with (.*) instalments") {
    (noOfInstalments: Int) =>
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      response.status shouldBe 200
      val responseBody = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]

      responseBody.numberOfInstalments shouldBe noOfInstalments
      responseBody.instalments.size    shouldBe noOfInstalments
  }

  Then("the IFS request should return status (.*)") { (status: Int) =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe status
  }

  Then("the ([0-9]\\d*)(?:st|nd|rd|th) instalment should have an interest accrued of (.*)") {
    (index: Int, interestAccrued: Int) =>
      val response: StandaloneWSResponse = ScenarioContext.get("response")
      val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]

      responseBody.instalments(index - 1).instalmentInterestAccrued shouldBe interestAccrued

  }

  Then("ifs service returns monthly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      12,
      983,
      9542,
      9542 + 983,
      12,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusMonths(1), 10000, 90000, 198, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusMonths(2), 10000, 80000, 170, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusMonths(3), 10000, 70000, 154, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusMonths(4), 10000, 60000, 128, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusMonths(5), 10000, 50000, 110, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusMonths(6), 10000, 40000, 88, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusMonths(7), 10000, 30000, 59, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusMonths(8), 10000, 20000, 44, 90000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(9), 10000, 10000, 21, 100000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(10), 10000, 0, 0, 110000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusMonths(11), 525, 0, 0, 100000 + 525, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns 4-Weekly frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      904,
      1423,
      1423 + 904,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1 * 4), 10000, 90000, 179, 10000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2 * 4), 10000, 80000, 159, 30000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3 * 4), 10000, 70000, 139, 40000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4 * 4), 10000, 60000, 119, 50000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5 * 4), 10000, 50000, 99, 60000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6 * 4), 10000, 40000, 79, 70000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7 * 4), 10000, 30000, 59, 80000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8 * 4), 10000, 20000, 39, 90000, 2.6),
        InstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusWeeks(9 * 4),
          10000,
          10000,
          19,
          100000,
          2.6
        ),
        InstalmentResponse(
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
    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns Quarterly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      2934,
      1423,
      1423 + 2934,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(
          debtId,
          2,
          instalmentPaymentDate.plusMonths(1 * 3),
          10000,
          90000,
          589,
          10000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          3,
          instalmentPaymentDate.plusMonths(2 * 3),
          10000,
          80000,
          524,
          30000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          4,
          instalmentPaymentDate.plusMonths(3 * 3),
          10000,
          70000,
          443,
          40000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          5,
          instalmentPaymentDate.plusMonths(4 * 3),
          10000,
          60000,
          393,
          50000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          6,
          instalmentPaymentDate.plusMonths(5 * 3),
          10000,
          50000,
          327,
          60000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          7,
          instalmentPaymentDate.plusMonths(6 * 3),
          10000,
          40000,
          262,
          70000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          8,
          instalmentPaymentDate.plusMonths(7 * 3),
          10000,
          30000,
          190,
          80000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          9,
          instalmentPaymentDate.plusMonths(8 * 3),
          10000,
          20000,
          131,
          90000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusMonths(9 * 3),
          10000,
          10000,
          65,
          100000,
          2.6
        ),
        InstalmentResponse(
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

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns 6Monthly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      12,
      5860,
      3538,
      3538 + 5860,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 7, 10000, 2.6),
        InstalmentResponse(
          debtId,
          2,
          instalmentPaymentDate.plusMonths(1 * 6),
          10000,
          90000,
          1179,
          10000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          3,
          instalmentPaymentDate.plusMonths(2 * 6),
          10000,
          80000,
          1031,
          30000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          4,
          instalmentPaymentDate.plusMonths(3 * 6),
          10000,
          70000,
          917,
          40000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          5,
          instalmentPaymentDate.plusMonths(4 * 6),
          10000,
          60000,
          773,
          50000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          6,
          instalmentPaymentDate.plusMonths(5 * 6),
          10000,
          50000,
          653,
          60000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          7,
          instalmentPaymentDate.plusMonths(6 * 6),
          10000,
          40000,
          517,
          70000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          8,
          instalmentPaymentDate.plusMonths(7 * 6),
          10000,
          30000,
          392,
          80000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          9,
          instalmentPaymentDate.plusMonths(8 * 6),
          10000,
          20000,
          257,
          90000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          10,
          instalmentPaymentDate.plusMonths(9 * 6),
          10000,
          10000,
          131,
          100000,
          2.6
        ),
        InstalmentResponse(
          debtId,
          11,
          instalmentPaymentDate.plusMonths(10 * 6),
          9398,
          0,
          0,
          100000 + 9398,
          2.6
        ),
        InstalmentResponse(
          debtId,
          12,
          instalmentPaymentDate.plusMonths(11 * 6),
          292,
          0,
          0,
          100000 + 10292,
          3
        )
      )
    )
    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns Annually payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId       = "debtId"
    val responseBody = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments

    quoteDateString = "2011-03-13"
    val formatter             = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val quoteDate             = LocalDate.parse(quoteDateString, formatter)
    val instalmentPaymentDate = quoteDate.plusDays(1)

    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      12,
      13458,
      1423,
      1423 + 13458,
      12,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 16, 10000, 3),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusYears(1), 10000, 90000, 2705, 10000, 3),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusYears(2), 10000, 80000, 2080, 30000, 3),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusYears(3), 10000, 70000, 1820, 40000, 3),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusYears(4), 10000, 60000, 1555, 50000, 3),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusYears(5), 10000, 50000, 1300, 60000, 3),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusYears(6), 10000, 40000, 1040, 70000, 3),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusYears(7), 10000, 30000, 780, 80000, 3),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusYears(8), 10000, 20000, 518, 90000, 3),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusYears(9), 10000, 10000, 260, 100000, 3.25),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusYears(10), 10000, 0, 0, 110000, 3.25),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusYears(11), 4881, 0, 0, 100000 + 4881, 2.6)
      )
    )
    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )
  }

  Then("ifs service returns monthly instalment calculation plan with initial payment") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      11,
      955,
      1423,
      955 + 1423,
      11,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10100, 100000, 7, 10100, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusMonths(1), 10000, 89900, 198, 20100, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusMonths(2), 10000, 79900, 170, 30100, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusMonths(3), 10000, 69900, 154, 40100, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusMonths(4), 10000, 59900, 132, 50100, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusMonths(5), 10000, 49900, 99, 60100, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusMonths(6), 10000, 39900, 88, 70100, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusMonths(7), 10000, 29900, 63, 80100, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusMonths(8), 10000, 19900, 43, 90100, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusMonths(9), 10000, 9900, 21, 100100, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusMonths(10), 2298, 0, 0, 102398, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )

  }

  Then("the instalment calculation summary contains values") { (dataTable: DataTable) =>
    val map = dataTable.transpose().asMap(classOf[String], classOf[String])

    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]

    response.status.shouldBe(200)

    locally {
      val fieldName = "numberOfInstalments"
      if (map.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.numberOfInstalments.toString shouldBe map.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "interestAccrued"
      if (map.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.interestAccrued.toString shouldBe map.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "planInterest"
      if (map.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.planInterest.toString shouldBe map.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "totalInterest"
      if (map.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.totalInterest.toString shouldBe map.get(fieldName).toString
        }
      }
    }

    locally {
      val fieldName = "duration"
      if (map.containsKey(fieldName)) {
        withClue(s"$fieldName: ") {
          responseBody.duration.toString shouldBe map.get(fieldName).toString
        }
      }
    }
  }

  Then("IFS response contains expected values") { (dataTable: DataTable) =>
    val map = dataTable.asMaps(classOf[String], classOf[String]).asScala

    val response: StandaloneWSResponse = ScenarioContext.get("response")
    val responseBody                   = Json.parse(response.body).as[InstalmentCalculationSummaryResponse]

    response.status.shouldBe(200)

    map.zipWithIndex.foreach { case (expectedInstalment, _) =>
      val responseIndex: Int = expectedInstalment.get("instalmentNumber").toString.toInt - 1

      if (map.toString.contains("expectedNumberOfInstalments")) {
        responseBody.numberOfInstalments.toString shouldBe expectedInstalment
          .get("expectedNumberOfInstalments")
          .toString
      }

      if (map.toString.contains("debtId")) {
        responseBody.instalments(responseIndex).debtId shouldBe expectedInstalment.get("debtId").toString
      }

      if (map.toString.contains("dueDate")) {
        responseBody.instalments(responseIndex).dueDate.toString shouldBe expectedInstalment.get("dueDate").toString
      }

      if (map.toString.contains("amountDue")) {
        responseBody.instalments(responseIndex).amountDue.toString shouldBe expectedInstalment.get("amountDue").toString
      }

      if (map.toString.contains("instalmentBalance")) {
        responseBody.instalments(responseIndex).instalmentBalance.toString shouldBe expectedInstalment
          .get("instalmentBalance")
          .toString
      }

      if (map.toString.contains("instalmentNumber")) {
        responseBody.instalments(responseIndex).instalmentNumber.toString shouldBe expectedInstalment
          .get("instalmentNumber")
          .toString
      }

      if (map.toString.contains("interestRate")) {
        responseBody.instalments(responseIndex).intRate.toString shouldBe expectedInstalment
          .get("interestRate")
          .toString
      }

      if (map.toString.contains("instalmentInterestAccrued")) {
        responseBody.instalments(responseIndex).instalmentInterestAccrued.toString shouldBe expectedInstalment
          .get("instalmentInterestAccrued")
          .toString
      }
    }

  }

  Then("ifs service returns weekly frequency instalment calculation plan with initial payment") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200

    val instalmentPaymentDate     = quoteDate.plusDays(129)
    val debtId                    = "debtId"
    val responseBody              = Json.parse(response.body).as[InstalmentCalculationSummaryResponse].instalments
    val actualnumberOfInstalments =
      Json.parse(response.body).as[InstalmentCalculationSummaryResponse].numberOfInstalments

    val expectedInstalmentCalculationResponse = InstalmentCalculationSummaryResponse(
      quoteDate,
      20,
      1345,
      2051,
      1345 + 2051,
      20,
      Vector(
        InstalmentResponse(debtId, 1, instalmentPaymentDate, 10000, 100000, 918, 10000, 2.6),
        InstalmentResponse(debtId, 2, instalmentPaymentDate.plusWeeks(1), 5000, 90000, 44, 15000, 2.6),
        InstalmentResponse(debtId, 3, instalmentPaymentDate.plusWeeks(2), 5000, 85000, 42, 20000, 2.6),
        InstalmentResponse(debtId, 4, instalmentPaymentDate.plusWeeks(3), 5000, 80000, 39, 25000, 2.6),
        InstalmentResponse(debtId, 5, instalmentPaymentDate.plusWeeks(4), 5000, 75000, 37, 30000, 2.6),
        InstalmentResponse(debtId, 6, instalmentPaymentDate.plusWeeks(5), 5000, 70000, 34, 35000, 2.6),
        InstalmentResponse(debtId, 7, instalmentPaymentDate.plusWeeks(6), 5000, 65000, 32, 40000, 2.6),
        InstalmentResponse(debtId, 8, instalmentPaymentDate.plusWeeks(7), 5000, 60000, 29, 45000, 2.6),
        InstalmentResponse(debtId, 9, instalmentPaymentDate.plusWeeks(8), 5000, 55000, 27, 50000, 2.6),
        InstalmentResponse(debtId, 10, instalmentPaymentDate.plusWeeks(9), 5000, 50000, 24, 55000, 2.6),
        InstalmentResponse(debtId, 11, instalmentPaymentDate.plusWeeks(10), 5000, 45000, 22, 60000, 2.6),
        InstalmentResponse(debtId, 12, instalmentPaymentDate.plusWeeks(11), 5000, 40000, 19, 65000, 2.6),
        InstalmentResponse(debtId, 13, instalmentPaymentDate.plusWeeks(12), 5000, 35000, 17, 70000, 2.6),
        InstalmentResponse(debtId, 14, instalmentPaymentDate.plusWeeks(13), 5000, 30000, 14, 75000, 2.6),
        InstalmentResponse(debtId, 15, instalmentPaymentDate.plusWeeks(14), 5000, 25000, 12, 80000, 2.6),
        InstalmentResponse(debtId, 16, instalmentPaymentDate.plusWeeks(15), 5000, 20000, 9, 85000, 2.6),
        InstalmentResponse(debtId, 17, instalmentPaymentDate.plusWeeks(16), 5000, 15000, 7, 90000, 2.6),
        InstalmentResponse(debtId, 18, instalmentPaymentDate.plusWeeks(17), 5000, 10000, 4, 95000, 2.6),
        InstalmentResponse(debtId, 19, instalmentPaymentDate.plusWeeks(18), 5000, 5000, 2, 100000, 2.6),
        InstalmentResponse(debtId, 20, instalmentPaymentDate.plusWeeks(19), 3396, 0, 0, 103396, 2.6)
      )
    )

    actualnumberOfInstalments             shouldBe expectedInstalmentCalculationResponse.numberOfInstalments
    responseBody.map(_.dueDate)           shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.dueDate
    )
    responseBody.map(_.instalmentBalance) shouldBe expectedInstalmentCalculationResponse.instalments.map(
      _.instalmentBalance
    )

  }

}
