package uk.gov.hmrc.test.api.cucumber.stepdefs.ifs

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.{InstalmentCalculationSummaryResponse, InstalmentResponse}
import uk.gov.hmrc.test.api.requests.IFSInstalmentCalculationRequests._
import uk.gov.hmrc.test.api.utils.ScenarioContext

import java.time.LocalDate

class IFSInstalmentCalculationSteps extends ScalaDsl with EN with Eventually with Matchers {

  Given("debt instalment calculation with details") { (dataTable: DataTable) =>
    createInstalmentCalculationRequestBody(dataTable)
  }

  Given("debt instalment calculation with 129 details") { (dataTable: DataTable) =>
    createInstalmentCalculationRequestBody(dataTable)
  }

  Given("the instalment calculation has debt item charges") { (dataTable: DataTable) =>
    addDebtItemChargesToInstalmentCalculation(dataTable)
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

  And("add initial payment for the debt item charge") { (dataTable: DataTable) =>
    addInitialPayment(dataTable)
  }

  Then("ifs service returns weekly payment frequency instalment calculation plan") { () =>
    val response: StandaloneWSResponse = ScenarioContext.get("response")
    response.status shouldBe 200
    val quoteDate                 = LocalDate.now
    val instalmentPaymentDate     = quoteDate.plusDays(1)
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

}
