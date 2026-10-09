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

import play.api.libs.json.Json
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.conf.TestConfiguration

trait BaseRequests extends RandomValues with BaseUris {

  def createBearerToken(
    enrolments: Seq[String] = Seq(),
    userType: String = "userType",
    utr: String = "123456789012"
  ): String = {

    def createLocalBearerToken(enrolments: Seq[String], userType: String, utr: String) = {
      val json =
        Json.obj(
          "affinityGroup"      -> userType,
          "excludeGnapToken"   -> true,
          "credentialStrength" -> "strong",
          "confidenceLevel"    -> 50,
          "credId"             -> "test",
          "enrolments" -> enrolments.map(key =>
            Json.obj(
              "key" -> key,
              "identifiers" -> Json.arr(
                Json.obj(
                  "key"   -> "UTRNumber",
                  "value" -> utr
                )
              ),
              "state" -> "Activated"
            )
          )
        )

      val response = WsClient.post(s"$authLoginApiUri/session/login", Map("Content-Type" -> "application/json"), json)
      val authHeader: (String, collection.Seq[String]) =
        response.headers.filter(header => header._1.equalsIgnoreCase("Authorization")).head
      val authBearerToken = authHeader._2.head.replace("Bearer ", "")
      authBearerToken
    }

    TestConfiguration.environment match {
      case "local" =>
        createLocalBearerToken(enrolments, userType, utr)
    }

  }

}
