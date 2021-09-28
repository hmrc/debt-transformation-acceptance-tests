package uk.gov.hmrc.test.api.requests

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.models.{Frequency, InstalmentCalculation}
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

import java.time.LocalDate
import java.util.Date

object IFSInstalmentCalculationRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def addDebtItemChargesToInstalmentCalculation(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var debtItemCharges = ""

    asMapTransposed.zipWithIndex.foreach { case (debtItemCharge, index) =>
      debtItemCharges = debtItemCharges.concat(
        getBodyAsString("debtItemCharge")
          .replaceAll("<REPLACE_debtId>", debtItemCharge.get("debtId"))
          .replaceAll("<REPLACE_debtAmount>", debtItemCharge.get("debtAmount"))
          .replaceAll("<REPLACE_mainTrans>", debtItemCharge.get("mainTrans"))
          .replaceAll("<REPLACE_subTrans>", debtItemCharge.get("subTrans"))
      )

      if (index + 1 < asMapTransposed.size) debtItemCharges = debtItemCharges.concat(",")
    }
    val jsonWithDebtItemCharges =
      ScenarioContext.get("paymentPlan").toString.replaceAll("<REPLACE_debtItemCharges>", debtItemCharges)
    ScenarioContext.set("paymentPlan", jsonWithDebtItemCharges)
    print("debt Items Charges ::::::::::::::::::::::::::::::" + jsonWithDebtItemCharges)
  }

  def createInstalmentCalculationRequestBody(dataTable: DataTable): Unit = {
    val asmapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem           = false
    var paymentPlan: String = null
    try ScenarioContext.get("paymentPlan")
    catch {
      case e: Exception => firstItem = true
    }

    val dateTime = new DateTime(new Date()).withZone(DateTimeZone.UTC)

    var addNumberOfDays = ""
    var instalmentPaymentDate = ""
    if (asmapTransposed.toString.contains("instalmentPaymentDay")) {
      addNumberOfDays = asmapTransposed.get("instalmentPaymentDay")
      val localDate               = LocalDate.now()
      instalmentPaymentDate   = localDate.plusDays(addNumberOfDays.toInt).toString
    }

    var quoteDate = dateTime.toString("yyyy-MM-dd")
    if (asmapTransposed.toString.contains("quoteDate")) quoteDate = asmapTransposed.get("quoteDate")

    paymentPlan = getBodyAsString("instalmentCalculation")
      .replaceAll("<REPLACE_quoteDate>", quoteDate)
      .replaceAll("<REPLACE_instalmentPaymentDate>", instalmentPaymentDate)
      .replaceAll("<REPLACE_instalmentAmount>", asmapTransposed.get("instalmentPaymentAmount"))
      .replaceAll("<REPLACE_paymentFrequency>", asmapTransposed.get("paymentFrequency"))
      .replaceAll("<REPLACE_interestCallDueTotal>", asmapTransposed.get("interestCallDueTotal"))

    if (firstItem == true) {
      paymentPlan = paymentPlan
    } else {
      paymentPlan = ScenarioContext.get("paymentPlan").toString.concat(",").concat(paymentPlan)
    }

    ScenarioContext.set("paymentPlan", paymentPlan)
    print("instalment-calculation request json ::::::::::::::::::::::::::::::" + paymentPlan)

  }

  def debtPlanDetailsWithInitialPaymentDatePlus129Request(dataTable: DataTable): Unit = {
    val asmapTransposed     = dataTable.transpose().asMap(classOf[String], classOf[String])
    var firstItem           = false
    var paymentPlan: String = null
    try ScenarioContext.get("paymentPlan")
    catch {
      case e: Exception => firstItem = true
    }
    val dateTime              = new DateTime(new Date()).withZone(DateTimeZone.UTC)
    val quoteDate             = dateTime.toString("yyyy-MM-dd")
    val instalmentPaymentDate = dateTime.plusDays(129).toString("yyyy-MM-dd")
    val initialPaymentDate    = dateTime.plusDays(129).toString("yyyy-MM-dd")

    paymentPlan = getBodyAsString("DebtCalculationWithInitialPayment")
      .replaceAll("<REPLACE_quoteDate>", quoteDate)
      .replaceAll("<REPLACE_instalmentPaymentDate>", instalmentPaymentDate)
      .replaceAll("<REPLACE_instalmentAmount>", asmapTransposed.get("instalmentPaymentAmount"))
      .replaceAll("<REPLACE_paymentFrequency>", asmapTransposed.get("paymentFrequency"))
      .replaceAll("<REPLACE_interestCallDueTotal>", asmapTransposed.get("interestCallDueTotal"))
      .replaceAll("<REPLACE_initialPaymentDate>", initialPaymentDate)
      .replaceAll("<REPLACE_initialPaymentAmount>", asmapTransposed.get("initialPaymentAmount"))

    if (firstItem == true) {
      paymentPlan = paymentPlan
    } else {
      paymentPlan = ScenarioContext.get("paymentPlan").toString.concat(",").concat(paymentPlan)
    }

    ScenarioContext.set(
      "paymentPlan",
      paymentPlan
    )
    print("plan with initail payment request json ::::::::::::::::::::::::::::::" + paymentPlan)
  }

  def getNextInstalmentDateByFrequency(paymentPlan: InstalmentCalculation, iterateVal: Int): LocalDate = {
    val frequency = paymentPlan.paymentFrequency.entryName
    frequency match {
      case Frequency.Single.entryName     => paymentPlan.instalmentPaymentDate.plusDays(iterateVal)
      case Frequency.Weekly.entryName     => paymentPlan.instalmentPaymentDate.plusWeeks(iterateVal)
      case Frequency.BiWeekly.entryName   => paymentPlan.instalmentPaymentDate.plusWeeks(iterateVal * 2)
      case Frequency.FourWeekly.entryName => paymentPlan.instalmentPaymentDate.plusWeeks(iterateVal * 4)
      case Frequency.Monthly.entryName    => paymentPlan.instalmentPaymentDate.plusMonths(iterateVal)
      case Frequency.Quarterly.entryName  => paymentPlan.instalmentPaymentDate.plusMonths(iterateVal * 3)
      case Frequency.HalfYearly.entryName => paymentPlan.instalmentPaymentDate.plusMonths(iterateVal * 6)
      case Frequency.Annually.entryName   => paymentPlan.instalmentPaymentDate.plusYears(iterateVal)
    }
  }

  def getInstalmentCalculation(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:interest-forecasting"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$interestForecostingApiUrl/instalment-calculation"

    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    print("instalment-calculation baseUri ********************" + baseUri)
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def noInitialPayment() {
    ScenarioContext.set(
      "paymentPlan",
      ScenarioContext.get("paymentPlan").toString.replaceAll("<REPLACE_initialPayment>", "")
    )
  }

  def addInitialPayment(dataTable: DataTable): Unit = {
    val asmapTransposed = dataTable.transpose().asMap(classOf[String], classOf[String])
    val dateTime        = new DateTime(new Date()).withZone(DateTimeZone.UTC)

    var initialPaymentDate      = ""
    var initialPaymentAmount    = "\"\""
    if (asmapTransposed.toString.contains("initialPaymentDays")) {
      initialPaymentDate = dateTime.plusDays(129).toString("yyyy-MM-dd")
    }
    if (asmapTransposed.toString.contains("initialPaymentAmount")) {
      initialPaymentAmount = asmapTransposed.get("initialPaymentAmount")
    }
    val planWithInitialPayments = getBodyAsString("initialPayment")
      .replaceAll("<REPLACE_initialPaymentDate>", initialPaymentDate)
      .replaceAll("<REPLACE_initialPaymentAmount>", initialPaymentAmount)

    val paymentPlan =
      ScenarioContext.get("paymentPlan").toString.replaceAll("<REPLACE_initialPayment>", planWithInitialPayments)
    ScenarioContext.set("paymentPlan", paymentPlan)
    print(s"Plan with initial payment **********************  $paymentPlan")
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedIFSInstalmentCalculationFiles(variant)

}
