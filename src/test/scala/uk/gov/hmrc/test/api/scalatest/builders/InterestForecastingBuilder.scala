package uk.gov.hmrc.test.api.scalatest.builders

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.scalatest.steps.context.InterestForecastingContext
import uk.gov.hmrc.test.api.utils.BaseRequests
import uk.gov.hmrc.test.api.utils.RandomValues
import uk.gov.hmrc.test.api.utils.TestData

object InterestForecastingBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestFocastingRequestBody(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestFocastingRequestBodyInput(
    dateCreated: Option[String] = None,
    debtID: Option[String] = None,
    debtItems: Option[String] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    mainTrans: Option[String] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInterestFocastingRequestBody' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem         = false
  //   var debtItems: String = null
  //   try ScenarioContext.get("debtItems")
  //   catch { case _: Exception => firstItem = true }
  //   var periodEnd = ""
  //   if (asmapTransposed.toString.contains("periodEnd")) {
  //   periodEnd = "\"periodEnd\": \"" + asmapTransposed.get("periodEnd") + "\","
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addPaymentHistory(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class PaymentHistoryInput(
    debtItems: Option[String] = None,
    paymentAmount: Option[BigDecimal] = None,
    paymentDate: Option[String] = None,
    payments: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addPaymentHistory' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var payments        = ""
  //   asMapTransposed.zipWithIndex.foreach { case (payment, index) =>
  //   payments = payments.concat(
  //   getBodyAsString("payment")
  //   .replaceAll("<REPLACE_paymentAmount>", payment.get("paymentAmount"))
  //   .replaceAll("<REPLACE_paymentDate>", payment.get("paymentDate"))
  //   )
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class BreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtItems: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addBreathingSpace' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "debtItems",
  //   getBodyAsString("debtCalcRequest").replaceAll("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
  //   )
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var breathingSpaces = ""
  //   asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
  //   if (breathingSpace.get("debtRespiteTo").toString.contains("-")) {
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addDebtBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class DebtBreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtItems: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addDebtBreathingSpace' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var breathingSpaces = ""
  //   asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
  //   breathingSpaces = breathingSpaces.concat(
  //   getBodyAsString("breathingSpace")
  //   .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
  //   .replaceAll("<REPLACE_debtRespiteTo>", breathingSpace.get("debtRespiteTo"))
  //   )
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addCustomerPostCodes(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class CustomerPostCodesInput(
    customerPostCodes: Option[String] = None,
    debtItems: Option[String] = None,
    postCode: Option[String] = None,
    postCodeDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addCustomerPostCodes' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "debtItems",
  //   getBodyAsString("debtCalcRequest").replaceAll("<REPLACE_debtItems>", ScenarioContext.get("debtItems"))
  //   )
  //   val asMapTransposed   = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var customerPostCodes = ""
  //   asMapTransposed.zipWithIndex.foreach { case (postCode, index) =>
  //   customerPostCodes = customerPostCodes.concat(
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestTypeRequestBody(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestTypeRequestBodyInput(
    debtInterestTypes: Option[BigDecimal] = None,
    mainTrans: Option[String] = None,
    subTrans: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInterestTypeRequestBody' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed           = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem                 = false
  //   var debtInterestTypes: String = null
  //   try ScenarioContext.get("debtInterestTypes")
  //   catch {
  //   case _: Exception => firstItem = true
  //   }
  //   val debtInterestType = getBodyAsString("debtInterestType")
  // -----------------------------------------------------------------------

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getDebtCalculation(context: InterestForecastingContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup)
        val baseUri     = s"$interestForecostingApiUrl/debt-calculation"
        val headers     = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )
        print("IFS debt-calculation baseUri ************************" + baseUri)
        print("IFS debt-calculation request json********************" + Json.parse(json))

        WsClient.post(baseUri, headers = headers, Json.parse(json))
  }


  def getDebtInterestTypeRequestBody(context: InterestForecastingContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
        )
        val baseUri     = s"$interestForecostingApiUrl/debt-interest-type"
        val headers     = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )
        print("IFS debt-interest type baseUri ************************" + baseUri)
        print("IFS debt-interest Type json********************" + Json.parse(json))

        WsClient.post(baseUri, headers = headers, Json.parse(json))
  }


}
