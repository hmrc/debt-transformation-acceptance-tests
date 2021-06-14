package uk.gov.hmrc.test.api.utils

import play.api.libs.json.Json
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

  def getQaStatementOfLiabilityAccessToken:Map[String, Seq[String]] = {
    val redirectUri = "https://www.example.com/redirect"
    val json = Json.obj(
      "grant_type" -> Seq("client_credentials"),
      "client_secret" -> Seq("6c2fc716-b9c6-4bb8-a57e-4908d32b9b27"),
      "client_id" -> "reRg5ZSks9hGLpzxS5RRnYHjHYtW",
      //"redirect_uri" -> Seq(redirectUri),
      "scope" -> Seq("read:statement-of-liability"))
    val response = WsClient.post(s"https://api.qa.tax.service.gov.uk/oauth/token", Map("Content-Type" -> "application/x-www-form-urlencoded"), json)
    val authHeader: Map[String, Seq[String]] = response.headers
      .filter(header => header._1.equalsIgnoreCase("Authorization"))
    authHeader

  }

}
