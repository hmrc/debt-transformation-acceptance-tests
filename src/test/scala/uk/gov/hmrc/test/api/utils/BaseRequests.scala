package uk.gov.hmrc.test.api.utils

import org.scalatest.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient

trait BaseRequests extends RandomValues with BaseUris {

  def createBearerToken(
    enrolments: Seq[String] = Seq(),
    userType: String = getRandomAffinityGroup,
    utr: String = "123456789012"): String = {
    val json =
      Json.obj(
        "affinityGroup"      -> userType,
        "credentialStrength" -> "strong",
        "confidenceLevel"    -> 50,
        "credId"             -> "test",
        "enrolments"         -> enrolments.map(key =>
          Json.obj(
            "key"         -> key,
            "identifiers" -> Json.arr(
              Json.obj(
                "key"   -> "UTRNumber",
                "value" -> utr
              )
            ),
            "state"       -> "Activated"
          )
        )
      )

    val response                          = WsClient.post(s"$authLoginApiUri/session/login", Map("Content-Type" -> "application/json"), json)
    val authHeader: (String, Seq[String]) = response.headers
      .filter(header => header._1.equalsIgnoreCase("Authorization"))
      .head

    val authBearerToken = authHeader._2.head.replace("Bearer ", "")
    authBearerToken

  }

  def getQaStatementOfLiabilityAccessToken:String = {
    val accessToken: StandaloneWSResponse = {  val json = Json.obj(
      "grant_type" -> "client_credentials",
      "client_secret" -> "6c2fc716-b9c6-4bb8-a57e-4908d32b9b27",
      "client_id" -> "reRg5ZSks9hGLpzxS5RRnYHjHYtW",
      "scope" -> "read:statement-of-liability")
    val accessTokenResponse = WsClient.post(s"https://api.qa.tax.service.gov.uk/oauth/token",
      Map("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json"),
      json)
    println(s"Bearer Token: ${accessTokenResponse.body}")
      accessTokenResponse.status shouldBe 200
      accessTokenResponse
    }

    (Json.parse(accessToken.body) \ "access_token").as[String]
  }
}
