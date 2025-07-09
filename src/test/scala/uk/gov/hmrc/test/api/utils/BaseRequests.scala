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

package uk.gov.hmrc.test.api.utils

import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.conf.TestConfiguration

trait BaseRequests extends RandomValues with BaseUris {

  def createBearerToken(
    enrolments: Seq[String] = Seq(),
    userType: String = "userType",
    utr: String = "123456789012"
  ) = {

    def createLocalBearerToken(enrolments: Seq[String], userType: String, utr: String) = {
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

      val response                                     = WsClient.post(s"$authLoginApiUri/session/login", Map("Content-Type" -> "application/json"), json)
      val authHeader: (String, collection.Seq[String]) =
        response.headers.filter(header => header._1.equalsIgnoreCase("Authorization")).head
      val authBearerToken                              = authHeader._2.head.replace("Bearer ", "")
      authBearerToken
    }

    def createQaBearerToken(scope: String): String = {
      print("scope to create token:::::::::::::::::::::::::::::::::::::: " + scope)
      val accessToken: StandaloneWSResponse = {
        val json                = Json.obj(
          "grant_type"    -> "client_credentials",
          "client_secret" -> "6c2fc716-b9c6-4bb8-a57e-4908d32b9b27",
          "client_id"     -> "reRg5ZSks9hGLpzxS5RRnYHjHYtW",
          "scope"         -> scope
        )
        val accessTokenResponse = WsClient.post(
          s"$oauthUri/token",
          Map("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json"),
          json
        )
        println(s"Bearer Token: ${accessTokenResponse.body}")
        accessTokenResponse.status shouldBe 200
        accessTokenResponse
      }

      (Json.parse(accessToken.body) \ "access_token").as[String]
    }

    val bearerToken: String = TestConfiguration.env match {
      case "local" =>
        createLocalBearerToken(enrolments, userType, utr)
      case _       =>
        createQaBearerToken(enrolments.head)
    }
    bearerToken
  }

}
