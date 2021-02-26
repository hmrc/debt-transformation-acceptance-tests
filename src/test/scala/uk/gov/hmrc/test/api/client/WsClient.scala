package uk.gov.hmrc.test.api.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.libs.ws._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Await
import scala.concurrent.duration._

object WsClient extends LazyLogging {
  val timeout: FiniteDuration = 60 seconds

  implicit val bodyWrites: BodyWritable[JsValue] = {
    BodyWritable(a => InMemoryBody(ByteString.fromArrayUnsafe(Json.toBytes(a))), "application/json")
  }
  private val asyncClient: StandaloneAhcWSClient = {
      implicit val system:       ActorSystem       = ActorSystem()
      implicit val materializer: ActorMaterializer = ActorMaterializer()

    StandaloneAhcWSClient()
  }

  def get(uri: String): StandaloneWSResponse = {
    println("")
    logger.info("*********** NEW REQUEST ***********")
    logger.info(s"GET request URI: $uri")

    val client  = asyncClient
    val request = client.url(uri)
    val response = Await.result(
      request
        .get(),
      timeout
    )

    println("")
    logger.info(s"GET response status: ${response.status}")
    logger.info(s"GET response headers: ${response.headers}")
    logger.info(s"GET response body: ${response.body}")

    response
  }

  def get(
    uri:             String,
    queryParameters: Map[String, String] = Map.empty[String, String],
    headers:         Map[String, String] = Map.empty[String, String],
    cookies:         Seq[WSCookie] = Seq.empty[WSCookie],
    followRedirects: Boolean = false): StandaloneWSResponse = {
    println("")
    logger.debug("*********** NEW REQUEST ***********")
    logger.debug(s"GET request URI: $uri")
    logger.debug(s"GET request query parameters: $queryParameters")
    logger.debug(s"GET request headers: $headers")
    logger.debug(s"GET request cookies: $cookies")

    val client  = asyncClient
    val request = client.url(uri)
    val response = Await.result(
      request
        .withHttpHeaders(headers.toSeq: _*)
        .addCookies(cookies: _*)
        .withQueryStringParameters(queryParameters.toSeq: _*)
        .withFollowRedirects(followRedirects)
        .get(),
      timeout
    )

    println("")
    logger.debug(s"GET response status: ${response.status}")
    logger.debug(s"GET response headers: ${response.headers}")
    logger.debug(s"GET response body: ${response.body}")

    response
  }

}
