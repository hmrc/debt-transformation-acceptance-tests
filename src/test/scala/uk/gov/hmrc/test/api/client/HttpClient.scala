/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.client
import akka.actor.ActorSystem
import play.api.Play.{current, materializer}
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

class HttpClient {

  implicit val actorSystem: ActorSystem = ActorSystem()
  val wsClient                          = StandaloneAhcWSClient()
  implicit val ec: ExecutionContext     = ExecutionContext.global

  def GET(url: String): Future[ServiceResponse] =
    wsClient
      .url(url)
      .get
      .map(response => ServiceResponse(response.status, Option(response.headers), Option(response.body)))
}

case class ServiceResponse(status: Int, headers: Option[Map[String, Seq[String]]], body: Option[String])
