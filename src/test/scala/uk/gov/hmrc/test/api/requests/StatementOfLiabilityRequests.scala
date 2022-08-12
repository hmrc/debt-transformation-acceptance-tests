package uk.gov.hmrc.test.api.requests

import io.cucumber.datatable.DataTable
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.utils.{BaseRequests, RandomValues, ScenarioContext, TestData}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object StatementOfLiabilityRequests extends BaseRequests with RandomValues {

  val bearerToken = createBearerToken(
    enrolments = Seq("read:statement-of-liability"),
    userType = getRandomAffinityGroup,
    utr = "123456789012"
  )

  def getStatementOfLiability(json: String): StandaloneWSResponse = {
    val baseUri = s"$statementOfLiabilityApiUrl/sol"
    val headers = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.post(baseUri, headers = headers, Json.parse(json))
  }

  //Used by hello world only
  def getStatementLiabilityHelloWorld(endpoint: String): StandaloneWSResponse = {
    val bearerToken = createBearerToken(
      enrolments = Seq("read:statement-of-liability"),
      userType = getRandomAffinityGroup,
      utr = "123456789012"
    )
    val baseUri     = s"$statementOfLiabilityApiUrl$endpoint"
    val headers     = Map(
      "Authorization" -> s"Bearer $bearerToken",
      "Content-Type"  -> "application/json",
      "Accept"        -> "application/vnd.hmrc.1.0+json"
    )
    WsClient.get(baseUri, headers = headers)
  }

  def addDutyIds(dataTable: DataTable): Unit = {
    val asMapTransposed = dataTable.asMaps(classOf[String], classOf[String])
    var dutyIds         = ""

    asMapTransposed.zipWithIndex.foreach { case (dutyId, index) =>
      dutyIds = dutyIds.concat(
        getBodyAsString("dutyItemChargeId")
          .replaceAll("<REPLACE_dutyId>", dutyId.get("dutyId"))
      )

      if (index + 1 < asMapTransposed.size) dutyIds = dutyIds.concat(",")

    }

    val jsonWithCustomerPostCodes =
      ScenarioContext.get("debtDetails").toString.replaceAll("<REPLACE_dutyItemChargeId>", dutyIds)
    ScenarioContext.set("debtDetails", jsonWithCustomerPostCodes)
    print("duty id **********************   " + jsonWithCustomerPostCodes)
  }

  def getBodyAsString(variant: String): String =
    TestData.loadedFiles(variant)
}
