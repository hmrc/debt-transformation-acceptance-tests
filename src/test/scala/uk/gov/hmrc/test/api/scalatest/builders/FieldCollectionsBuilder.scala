package uk.gov.hmrc.test.api.scalatest.builders

import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsContext
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, TestData}

object FieldCollectionsBuilder extends BaseRequests with RandomValues {

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createInterestFocastingRequestBodyFC(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class InterestFocastingRequestBodyFCInput(
    dateCreated: Option[String] = None,
    debtId: Option[String] = None,
    fcDebtItem: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createInterestFocastingRequestBodyFC' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem         = false
  //   var debtItems: String = null
  //   try ScenarioContext.get("fcDebtItem")
  //   catch { case _: Exception => firstItem = true }
  //   var periodEnd = ""
  //   if (asmapTransposed.toString.contains("periodEnd")) {
  //   periodEnd = "\"periodEnd\": \"" + asmapTransposed.get("periodEnd") + "\","
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: createFcCotaxChargeInterestRequest(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FcCotaxChargeInterestRequestInput(
    chargedInterest: Option[BigDecimal] = None,
    dateCreated: Option[String] = None,
    debtItemChargeId: Option[String] = None,
    fcDebtItem: Option[String] = None,
    interestIndicator: Option[Boolean] = None,
    interestRequestedTo: Option[BigDecimal] = None,
    interestStartDate: Option[BigDecimal] = None,
    originalAmount: Option[BigDecimal] = None,
    periodEnd: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'createFcCotaxChargeInterestRequest' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asmapTransposed   = dataTable.transpose().asMap(classOf[String], classOf[String])
  //   var firstItem         = false
  //   var debtItems: String = null
  //   try ScenarioContext.get("fcDebtItem")
  //   catch { case _: Exception => firstItem = true }
  //   var periodEnd = ""
  //   if (asmapTransposed.toString.contains("periodEnd")) {
  //   periodEnd = "\"periodEnd\": \"" + asmapTransposed.get("periodEnd") + "\","
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCPaymentHistory(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCPaymentHistoryInput(
    fcDebtItem: Option[String] = None,
    paymentAmount: Option[BigDecimal] = None,
    paymentDate: Option[String] = None,
    payments: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCPaymentHistory' looked like template/string-body setup.
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
  // Typed input generated from legacy method: addFCBreathingSpace(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCBreathingSpaceInput(
    breathingSpaces: Option[String] = None,
    debtRespiteFrom: Option[String] = None,
    debtRespiteTo: Option[String] = None,
    fcDebtItem: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCBreathingSpace' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var breathingSpaces = ""
  //   asMapTransposed.zipWithIndex.foreach { case (breathingSpace, index) =>
  //   if (breathingSpace.get("debtRespiteTo").toString.contains("-")) {
  //   breathingSpaces = breathingSpaces.concat(
  //   getBodyAsString("breathingSpace")
  //   .replaceAll("<REPLACE_debtRespiteFrom>", breathingSpace.get("debtRespiteFrom"))
  //   .replaceAll("<REPLACE_debtRespiteTo>", breathingSpace.get("debtRespiteTo"))
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addFCCustomerPostCodes(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class FCCustomerPostCodesInput(
    addressPostcode: Option[String] = None,
    fcCustomerPostCodes: Option[String] = None,
    fcDebtItem: Option[String] = None,
    postcodeDate: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addFCCustomerPostCodes' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "fcDebtItem",
  //   getBodyAsString("fcdebtCalcRequest")
  //   .replaceAll("<REPLACE_fcDebtItem>", ScenarioContext.get("fcDebtItem"))
  //   )
  //   val asMapTransposed   = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var customerPostCodes = ""
  //   asMapTransposed.zipWithIndex.foreach { case (postCode, index) =>
  // -----------------------------------------------------------------------

  // -----------------------------------------------------------------------
  // Typed input generated from legacy method: addChargedInterestCotax(DataTable)
  // Legacy DataTable code is inference-only and is not emitted.
  // -----------------------------------------------------------------------
  final case class ChargedInterestCotaxInput(
    addressPostcode: Option[String] = None,
    chargedInterest: Option[BigDecimal] = None,
    fcCustomerPostCodes: Option[String] = None,
    fcDebtItem: Option[String] = None
  )

  // -----------------------------------------------------------------------
  // Legacy method 'addChargedInterestCotax' looked like template/string-body setup.
  // Add a typed builder method here if this step is still needed by ScalaTest specs.
  // Legacy preview:
  //   ScenarioContext.set(
  //   "fcDebtItem",
  //   getBodyAsString("fcChargeInterest")
  //   .replaceAll("<REPLACE_chargedInterest>", ScenarioContext.get("fcDebtItem"))
  //   )
  //   val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String]).asScala
  //   var chargedInterest = ""
  //   asMapTransposed.zipWithIndex.foreach { case (chargedInte, index) =>
  // -----------------------------------------------------------------------

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)

  // -----------------------------------------------------------------------
  // HTTP client methods lifted from legacy Requests with typed context access.
  // -----------------------------------------------------------------------

  def getDebtCalculation(context: FieldCollectionsContext, json: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
          enrolments = Seq("read:interest-forecasting"),
          userType = getRandomAffinityGroup
        )
        val baseUri     = s"$interestForecostingApiUrl/fc-debt-calculation"
        val headers     = Map(
          "Authorization" -> s"Bearer $bearerToken",
          "Content-Type"  -> "application/json",
          "Accept"        -> "application/vnd.hmrc.1.0+json"
        )
        print("IFS debt-calculation baseUri ************************" + baseUri)
        print("IFS debt-calculation request json********************" + Json.parse(json))

        WsClient.post(baseUri, headers = headers, Json.parse(json))
  }


}
