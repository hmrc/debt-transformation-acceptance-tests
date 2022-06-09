package uk.gov.hmrc.test.api.requests

import cucumber.api.scala.{EN, ScalaDsl}
import io.cucumber.datatable.DataTable
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.models.{Frequency, InstalmentCalculation, InstalmentCalculationSummaryResponse}
import uk.gov.hmrc.test.api.utils.{BaseRequests, ScenarioContext, TestData}

import java.time.LocalDate
import java.util.Date
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object IFSInstalmentCalculationRequests extends ScalaDsl with EN with Eventually with Matchers with BaseRequests {

  def addPostCodeToInstalmentCalculation(postCode: String, postCodeDate: String): Unit = {
    val postCodeJson    =
      s"""
        |{
        | "postCode": "$postCode",
        | "postCodeDate":"$postCodeDate"
        |}
        |""".stripMargin
    val paymentPlanJson = ScenarioContext.get("paymentPlan").toString.replaceAll("<REPLACE_postCodes>", postCodeJson)
    ScenarioContext.set("paymentPlan", paymentPlanJson)
  }

  def addEmptyPostCodeArrayToInstalmentCalculation(): Unit = {
    val postCodeJson = ScenarioContext.get("paymentPlan").toString.replaceAll("<REPLACE_postCodes>", "")
    ScenarioContext.set("paymentPlan", postCodeJson)
  }

  def addDebtItemChargesToInstalmentCalculation(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var debtItemCharges = ""

    asMapTransposed.zipWithIndex.foreach { case (debtItemCharge, index) =>
      val periodEnd = if (debtItemCharge.containsKey("periodEnd")) {
        s"""
           |,"periodEnd": "${debtItemCharge.get("periodEnd")}"
           |""".stripMargin
      } else ""
      debtItemCharges = debtItemCharges.concat(
        getBodyAsString("debtItemCharge")
          .replaceAll("<REPLACE_debtId>", debtItemCharge.get("debtId"))
          .replaceAll("<REPLACE_debtAmount>", debtItemCharge.get("debtAmount"))
          .replaceAll("<REPLACE_mainTrans>", debtItemCharge.get("mainTrans"))
          .replaceAll("<REPLACE_subTrans>", debtItemCharge.get("subTrans"))
          .replaceAll("<REPLACE_periodEnd>", periodEnd)
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

    var addNumberOfDays       = ""
    var instalmentPaymentDate = ""

    if (asmapTransposed.toString.contains("instalmentPaymentDay")) {
      addNumberOfDays = asmapTransposed.get("instalmentPaymentDay")
      val localDate = LocalDate.now()
      instalmentPaymentDate = localDate.plusDays(addNumberOfDays.toInt).toString
    }

    if (asmapTransposed.toString.contains("instalmentPaymentDate")) {
      instalmentPaymentDate = asmapTransposed.get("instalmentPaymentDate")
    }

    var quoteDate                  = dateTime.toString("yyyy-MM-dd")
    if (asmapTransposed.toString.contains("quoteDate")) quoteDate = asmapTransposed.get("quoteDate")
    val durationOrInstalmentAmount = if (asmapTransposed.get("quoteType").equals("instalmentAmount")) {
      s""" "duration":${asmapTransposed.get("duration")} """
    } else {
      s""" "instalmentPaymentAmount":${asmapTransposed.get("instalmentPaymentAmount")} """
    }

    paymentPlan = getBodyAsString("instalmentCalculation")
      .replaceAll("<REPLACE_quoteDate>", quoteDate)
      .replaceAll("<REPLACE_quoteType>", asmapTransposed.get("quoteType"))
      .replaceAll("<REPLACE_instalmentPaymentDate>", instalmentPaymentDate)
      .replaceAll("<REPLACE_durationOrInstalmentAmount>", durationOrInstalmentAmount)
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

    var initialPaymentDate   = ""
    var initialPaymentAmount = "\"\""
    if (asmapTransposed.toString.contains("initialPaymentDays")) {
      var addNumberOfDays = ""
      addNumberOfDays = asmapTransposed.get("initialPaymentDays")
      val localDate       = LocalDate.now()
      initialPaymentDate = localDate.plusDays(addNumberOfDays.toInt).toString
    }

    if (asmapTransposed.toString.contains("initialPaymentDate")) {
      initialPaymentDate = asmapTransposed.get("initialPaymentDate")
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
