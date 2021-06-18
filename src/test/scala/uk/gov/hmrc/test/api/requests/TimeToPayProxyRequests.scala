package uk.gov.hmrc.test.api.requests

import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{
  BaseRequests,
  BaseUris,
  ScenarioContext,
  TestData
}
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala

import scala.util.Try

object TimeToPayProxyRequests extends BaseRequests with BaseUris {
  val bearerToken = createBearerToken(
    enrolments = Seq("read:time-to-pay-proxy")
  )

  def baseCall(endpoint: String, maybeBearerToken: Option[String]) = {
    val baseUri = s"$timeToPayProxyApiUrl$endpoint"
    val headers =
      maybeBearerToken.fold[Map[String, String]](Map())(
        bearerToken => Map("Authorization" -> s"Bearer $bearerToken")
      )
    WsClient.get(baseUri, headers = headers)
  }

  private def getBodyAsString(variant: String): String =
    TestData.loadedTtppFiles(variant)

  def addCustomer(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])

    val customers =
      asMapTransposed.zipWithIndex.foldLeft[String]("")((acc, current) => {
        val (customer, index) = current

        val replaced = getBodyAsString("customer")
          .replaceAll("<REPLACE_quoteType>", customer.get("quoteType"))
          .replaceAll(
            "<REPLACE_instalmentStartDate>",
            customer.get("instalmentStartDate")
          )
          .replaceAll(
            "<REPLACE_instalmentAmount>",
            customer.get("instalmentAmount")
          )
          .replaceAll("<REPLACE_frequency>", customer.get("frequency"))
          .replaceAll("<REPLACE_duration>", customer.get("duration"))
          .replaceAll(
            "<REPLACE_initialPaymentAmount>",
            customer.get("initialPaymentAmount")
          )
          .replaceAll(
            "<REPLACE_initialPaymentDate>",
            customer.get("initialPaymentDate")
          )
          .replaceAll(
            "<REPLACE_paymentPlanType>",
            customer.get("paymentPlanType")
          )

        if (index + 1 < asMapTransposed.size)
          s"$acc$replaced,"
        else
          s"$acc$replaced"
      })

    Try(ScenarioContext.get[String]("customers")).fold(
      _ => ScenarioContext.set("customers", customers),
      cs => ScenarioContext.set("customers", s"$cs, $customers")
    )
  }

  def addAdHocs(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])

    val adHocs =
      asMapTransposed.zipWithIndex.foldLeft[String]("")((acc, current) => {
        val (adHoc, index) = current

        val replaced = getBodyAsString("adHoc")
          .replaceAll("<REPLACE_adHocDate>", adHoc.get("adHocDate"))
          .replaceAll("<REPLACE_adHocAmount>", adHoc.get("adHocAmount"))

        if (index + 1 < asMapTransposed.size)
          s"$acc$replaced,"
        else
          s"$acc$replaced"
      })

    Try(ScenarioContext.get[String]("adHocs")).fold(
      _ => ScenarioContext.set("adHocs", adHocs),
      ahs => ScenarioContext.set("adHocs", s"$ahs, $adHocs")
    )
  }

  def addInstalments(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])

    val instalments =
      asMapTransposed.zipWithIndex.foldLeft[String]("")((acc, current) => {
        val (instalment, index) = current

        val replaced = getBodyAsString("instalment")
          .replaceAll(
            "<REPLACE_dutyId>",
            instalment.get("dutyId")
          )
          .replaceAll(
            "<REPLACE_debtId>",
            instalment.get("debtId")
          )
          .replaceAll(
            "<REPLACE_dueDate>",
            instalment.get("dueDate")
          )
          .replaceAll(
            "<REPLACE_amountDue>",
            instalment.get("amountDue")
          )
          .replaceAll(
            "<REPLACE_expectedPayment>",
            instalment.get("expectedPayment")
          )
          .replaceAll(
            "<REPLACE_interestRate>",
            instalment.get("interestRate")
          )
          .replaceAll(
            "<REPLACE_instalmentNumber>",
            instalment.get("instalmentNumber")
          )

        if (index + 1 < asMapTransposed.size)
          s"$acc$replaced,"
        else
          s"$acc$replaced"
      })

    Try(ScenarioContext.get[String]("instalments")).fold(
      _ => ScenarioContext.set("instalments", instalments),
      is => ScenarioContext.set("instalments", s"$is, $instalments"))

  }

  def addBreathingSpaces(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])

    val breathingSpaces =
      asMapTransposed.zipWithIndex.foldLeft[String]("")((acc, current) => {
        val (breathingSpace, index) = current

        val replaced = getBodyAsString("breathingSpace")
          .replaceAll(
            "<REPLACE_debtRespiteFrom>",
            breathingSpace.get("debtRespiteFrom")
          )
          .replaceAll(
            "<REPLACE_debtRespiteTo>",
            breathingSpace.get("debtRespiteTo")
          )

        if (index + 1 < asMapTransposed.size)
          s"$acc$replaced,"
        else
          s"$acc$replaced"
      })

    val currentDuty = ScenarioContext
      .get("currentDuty")
      .toString
      .replaceAll("<REPLACE_breathingSpaces>", breathingSpaces)
    ScenarioContext.set("currentDuty", currentDuty)

  }

  def addPayments(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])

    val payments =
      asMapTransposed.zipWithIndex.foldLeft[String]("")((acc, current) => {
        val (payment, index) = current

        val replaced = getBodyAsString("payment")
          .replaceAll(
            "<REPLACE_paymentDate>",
            payment.get("paymentDate")
          )
          .replaceAll(
            "<REPLACE_paymentAmount>",
            payment.get("paymentAmount")
          )

        if (index + 1 < asMapTransposed.size)
          s"$acc$replaced,"
        else
          s"$acc$replaced"
      })

    val currentDuty = ScenarioContext
      .get("currentDuty")
      .toString
      .replaceAll("<REPLACE_payments>", payments)
    ScenarioContext.set("currentDuty", currentDuty)

  }

  def addInstalmentToCreatePlanRequest(): Unit = {

    val currentCreatePlanRequest =
      ScenarioContext.get[String]("createPlanRequest")
    val currentInstalments =
      Try(ScenarioContext.get[String]("instalments"))
        .fold(_ => "", identity)
    val createPlanRequest =
      currentCreatePlanRequest.replace("<REPLACE_instalments>", currentInstalments)
    ScenarioContext.set("createPlanRequest", createPlanRequest)
  }

  def addAdHocsToGenerateQuoteRequest(): Unit = {

    val currentGenerateQuoteRequest =
      ScenarioContext.get[String]("generateQuoteRequest")
    val currentAdHocs =
      Try(ScenarioContext.get[String]("adHocs"))
        .fold(_ => "", identity)
    val generateQuoteRequest =
      currentGenerateQuoteRequest.replace("<REPLACE_adHocs>", currentAdHocs)
    ScenarioContext.set("generateQuoteRequest", generateQuoteRequest)
  }

  def addCustomersToGenerateQuoteRequest(): Unit = {

    val currentGenerateQuoteRequest =
      ScenarioContext.get[String]("generateQuoteRequest")
    val currentCustomers =
      Try(ScenarioContext.get[String]("customers"))
        .fold(_ => "", identity)
    val generateQuoteRequest =
      currentGenerateQuoteRequest.replace(
        "<REPLACE_customer>",
        currentCustomers
      )
    ScenarioContext.set("generateQuoteRequest", generateQuoteRequest)
  }

  private def addPreviousDutyToDuties(): Unit = {
    val maybeCurrentDuties: Option[String] =
      Try(ScenarioContext.get[String]("currentDuties")).fold(_ => None, Some(_))

    val maybePreviousDuty: Option[String] =
      Try(ScenarioContext.get[String]("currentDuty")).fold(_ => None, Some(_))

    ScenarioContext.remove("currentDuty")

    addLastToList(maybePreviousDuty, maybeCurrentDuties, "currentDuties")
  }

  def addDuty(dataTable: DataTable): Unit = {
    addPreviousDutyToDuties()
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])

    val duty = getBodyAsString("duty")
      .replaceAll("<REPLACE_dutyId>", asMapTransposed.get("dutyId"))
      .replaceAll("<REPLACE_subTrans>", asMapTransposed.get("subTrans"))
      .replaceAll(
        "<REPLACE_originalDebtAmount>",
        asMapTransposed.get("originalDebtAmount")
      )
      .replaceAll(
        "<REPLACE_interestStartDate>",
        asMapTransposed.get("interestStartDate")
      )

    ScenarioContext.set("currentDuty", duty)

  }

  def addDebt(dataTable: DataTable): Unit = {
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])

    addPreviousDebtToDebts()

    val debt = getBodyAsString("debt")
      .replaceAll("<REPLACE_debtId>", asMapTransposed.get("debtId"))
      .replaceAll("<REPLACE_mainTrans>", asMapTransposed.get("mainTrans"))

    ScenarioContext.set("currentDebt", debt)
  }

  def addDebtsToGenerateQuoteRequest(): Unit = {
    addPreviousDebtToDebts()

    val currentGenerateQuoteRequest =
      ScenarioContext.get[String]("generateQuoteRequest")
    val currentDebts =
      Try(ScenarioContext.get[String]("currentDebts")).fold(_ => "", identity)
    val generateQuoteRequest =
      currentGenerateQuoteRequest.replace("<REPLACE_debts>", currentDebts)
    ScenarioContext.set("generateQuoteRequest", generateQuoteRequest)
  }

  private def addPreviousDebtToDebts(): Unit = {
    addPreviousDutyToDuties()

    val dutiesOfPreviousDebt: String =
      Try(ScenarioContext.get[String]("currentDuties")).fold(_ => "", identity)

    val maybePreviousDebt: Option[String] =
      Try(ScenarioContext.get[String]("currentDebt")).fold(
        _ => None,
        debt => Some(debt.replace("<REPLACE_duties>", dutiesOfPreviousDebt))
      )
    val maybeCurrentDebts: Option[String] =
      Try(ScenarioContext.get[String]("currentDebts")).fold(_ => None, Some(_))

    ScenarioContext.remove("currentDuties")
    ScenarioContext.remove("currentDebt")

    addLastToList(maybePreviousDebt, maybeCurrentDebts, "currentDebts")
  }

  private def addLastToList(maybePrevious: Option[String],
                            maybeCurrentBucket: Option[String],
                            key: String) = {
    (maybePrevious, maybeCurrentBucket) match {
      case (None, None)        => ()
      case (Some(d), Some(ds)) => ScenarioContext.set(key, s"$ds,$d")
      case (None, Some(ds))    => ScenarioContext.set(key, ds)
      case (Some(d), None)     => ScenarioContext.set(key, d)
    }
  }

  def createGenerateQuoteRequestBody(dataTable: DataTable): Unit = {
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])

    val generateQuoteRequest = getBodyAsString("generateQuoteRequest")
      .replaceAll(
        "<REPLACE_customerReference>",
        asMapTransposed.get("customerReference")
      )
      .replaceAll("<REPLACE_debtAmount>", asMapTransposed.get("debtAmount"))
      .replaceAll("<REPLACE_adHoc>", "")

    ScenarioContext.set("generateQuoteRequest", generateQuoteRequest)

  }

  def createRequestParameters(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.transpose().asMap(classOf[String], classOf[String])

    ScenarioContext.set("customerReference", asMapTransposed.get("customerReference"))
    ScenarioContext.set("planId", asMapTransposed.get("planId"))
  }

  def createCreatePlanRequestBody(dataTable: DataTable): Unit = {
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])

    val updatePlanRequest = getBodyAsString("createPlanRequest")
      .replaceAll(
        "<REPLACE_customerReference>",
        asMapTransposed.get("customerReference")
      )
      .replaceAll("<REPLACE_planId>", asMapTransposed.get("planId"))
      .replaceAll(
        "<REPLACE_paymentMethod>",
        asMapTransposed.get("paymentMethod")
      )
      .replaceAll(
        "<REPLACE_paymentReference>",
        asMapTransposed.get("paymentReference")
      )
      .replaceAll(
        "<REPLACE_thirdPartyBank>",
        asMapTransposed.get("thirdPartyBank")
      )
      .replaceAll(
        "<REPLACE_numberOfInstalments>",
        asMapTransposed.get("numberOfInstalments")
      )
      .replaceAll(
        "<REPLACE_totalDebtAmount>",
        asMapTransposed.get("totalDebtAmount")
      )      .replaceAll(
      "<REPLACE_totalInterest>",
      asMapTransposed.get("totalInterest")
    )

    ScenarioContext.set("createPlanRequest", updatePlanRequest)
  }

  def createUpdatePlanRequestBody(dataTable: DataTable): Unit = {
    val asMapTransposed =
      dataTable.transpose().asMap(classOf[String], classOf[String])

    val updatePlanRequest = getBodyAsString("updatePlanRequest")
      .replaceAll(
        "<REPLACE_customerReference>",
        asMapTransposed.get("customerReference")
      )
      .replaceAll("<REPLACE_planId>", asMapTransposed.get("planId"))
      .replaceAll("<REPLACE_updateType>", asMapTransposed.get("updateType"))
      .replaceAll(
        "<REPLACE_cancellationReason>",
        asMapTransposed.get("cancellationReason")
      )
      .replaceAll(
        "<REPLACE_paymentMethod>",
        asMapTransposed.get("paymentMethod")
      )
      .replaceAll(
        "<REPLACE_paymentReference>",
        asMapTransposed.get("paymentReference")
      )
      .replaceAll(
        "<REPLACE_thirdPartyBank>",
        asMapTransposed.get("thirdPartyBank")
      )

    ScenarioContext.set("updatePlanRequest", updatePlanRequest)
  }

  def getPlan(customerReference: String,
                       planId: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:time-to-pay-proxy")
    )

    val baseUri =
      s"$timeToPayProxyApiUrl/quote/$customerReference/$planId"

    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type" -> "application/json",
      "Accept" -> "application/vnd.hmrc.1.0+json"
    )

    WsClient.get(baseUri, headers = headers)
  }

  def putPlan(json: String,
                       customerReference: String,
                       planId: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:time-to-pay-proxy")
    )

    val baseUri =
      s"$timeToPayProxyApiUrl/quote/$customerReference/$planId"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type" -> "application/json",
      "Accept" -> "application/vnd.hmrc.1.0+json"
    )

    WsClient.put(baseUri, headers = headers, Json.parse(json))
  }

  def postQuote(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:time-to-pay-proxy")
    )

    val baseUri = s"$timeToPayProxyApiUrl/quote"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type" -> "application/json",
      "Accept" -> "application/vnd.hmrc.1.0+json"
    )

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def postPlan(json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:time-to-pay-proxy")
    )

    val baseUri = s"$timeToPayProxyApiUrl/quote/arrangement"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type" -> "application/json",
      "Accept" -> "application/vnd.hmrc.1.0+json"
    )

    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  def getTimeToPayProxy(endpoint: String): StandaloneWSResponse =
    baseCall(endpoint, Some(bearerToken))

  def getTimeToPayProxyWithoutBearerToken(
    endpoint: String
  ): StandaloneWSResponse =
    baseCall(endpoint, None)

}
