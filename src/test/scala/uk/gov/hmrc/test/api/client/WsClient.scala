package uk.gov.hmrc.test.api.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws._
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import scala.concurrent.Await
import scala.concurrent.duration._

object WsClient extends LazyLogging {
  val timeout: FiniteDuration = 60 seconds

  implicit val bodyWrites: BodyWritable[JsValue] =
    BodyWritable(a => InMemoryBody(ByteString.fromArrayUnsafe(Json.toBytes(a))), "application/json")
  private val asyncClient: StandaloneAhcWSClient = {
    implicit val system: ActorSystem             = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    StandaloneAhcWSClient()
  }

  def get(
    uri: String,
    queryParameters: Map[String, String] = Map.empty[String, String],
    headers: Map[String, String] = Map.empty[String, String],
    cookies: Seq[WSCookie] = Seq.empty[WSCookie],
    followRedirects: Boolean = false
  ): StandaloneWSResponse = {
    println("")
    logger.debug("*********** NEW REQUEST ***********")
    logger.debug(s"GET request URI: $uri")
    if (!queryParameters.isEmpty) {
      logger.debug(s"GET request query parameters: $queryParameters")
    }
    if (!headers.isEmpty) {
      logger.debug(s"GET request headers: $headers")
    }
    if (!cookies.isEmpty) {
      logger.debug(s"GET request cookies: $cookies")
    }

    val client   = asyncClient
    val request  = client.url(uri)
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

  def post(uri: String, headers: Map[String, String], json: JsValue): StandaloneWSResponse = {
    logger.info(s"POST request URI: $uri")
    logger.debug(s"POST request headers: $headers")
    logger.debug(s"POST request body: $json")

    val client   = asyncClient
    val request  = client.url(uri)
    val response = Await.result(
      request
        .withHttpHeaders(headers.toSeq: _*)
        .withFollowRedirects(false)
        .post(json),
      timeout
    )

    println("")
    logger.debug(s"POST response status: ${response.status}")
    logger.debug(s"POST response headers: ${response.headers}")
    logger.debug(s"POST response body: ${response.body}")

    response
  }

  def delete(uri: String, headers: Map[String, String]): StandaloneWSResponse = {
    println("")
    logger.info(s"DELETE request URI: $uri")
    logger.debug(s"DELETE request headers: $headers")

    val client   = asyncClient
    val request  = client.url(uri)
    val response = Await.result(
      request
        .withHttpHeaders(headers.toSeq: _*)
        .withFollowRedirects(false)
        .delete(),
      timeout
    )

    println("")
    logger.debug(s"DELETE response status: ${response.status}")
    logger.debug(s"DELETE response headers: ${response.headers}")
    logger.debug(s"DELETE response body: ${response.body}")

    response
  }
  

}
