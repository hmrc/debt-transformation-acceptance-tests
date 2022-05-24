package main

import errors.BridgeToolError
import errors.BridgeToolError.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import requests.*
import requests.RequestAuth.Bearer

import java.net.ConnectException
import java.time.LocalDateTime

final case class RequestDetail(
  requestId: String,
  content: Json,
  isResponse: Boolean,
  uri: Option[String] = None,
  createdOn: Option[LocalDateTime] = None,
  method: Option[String] = None,
  status: Option[Int] = None,
  headers: Option[Map[String, String]] = None
) {
  def headersToApply = headers.getOrElse(Map.empty)
}

final case class TokenResponse(`access_token`: String) {
  def authBearerToken: requests.RequestAuth =
    requests.RequestAuth.Bearer(`access_token`)
}

val defaultHeaders = Map("Content-Type" -> "application/json")

val externalTestTokenURL = "https://test-api.service.hmrc.gov.uk/oauth/token"

val externalTestTokenParams = Map(
  "client_secret" -> "a3438df2-c78a-4926-92b5-bc50382e6d0c",
  "client_id"     -> "IRSzWvRL34nBwQqhKVV8ACzchThp",
  "grant_type"    -> "client_credentials",
  "scope"         -> "read:time-to-pay-proxy"
)

val externalTestTTPURL = "https://test-api.service.hmrc.gov.uk/individuals/time-to-pay-proxy/"
val qaAPIURL = "https://admin.qa.tax.service.gov.uk/ifs"

val externalTestsRequestsURL = s"${externalTestTTPURL}test-only/requests"
val externalTestsResponseURL = s"${externalTestTTPURL}test-only/response"
def externalTestDeleteURL(id: String): String =
  s"${externalTestTTPURL}test-only/request/$id"

def captureFailedRequests(requestAction: () => Response): Result[Response] =
  try
    Right(requestAction())
  catch {
    case ex: RequestFailedException => Left(BridgeToolError.BadResponse(ex.response))
    case ex: TimeoutException       => Left(BridgeToolError.Connectivity(ex.url, ex.message))
    case ex: UnknownHostException   => Left(BridgeToolError.Connectivity(ex.url, ex.message))
    case ex: ConnectException       => Left(BridgeToolError.Connectivity("unknown", ex.getMessage))
  }

def retrieveAccessToken(url: String, data: Map[String, String]): Result[TokenResponse] =
  for {
    response <- captureFailedRequests(() => requests.post(url, data = data))
    result <- decode[TokenResponse](response.text()).left.map { error =>
                BridgeToolError.Decode(response.text(), error)
              }
  } yield result

def retrieveExternalTestToken(): Result[TokenResponse] =
  retrieveAccessToken(externalTestTokenURL, externalTestTokenParams)

def retrieveQAToken(): Result[TokenResponse] =
  sys.env.get("ADMIN_QA_TOKEN") match {
    case Some(value) => Right(TokenResponse(value))
    case None        => Left(BridgeToolError.MissingQAToken)
  }

def retrieveAllUnprocessedRequests(token: TokenResponse): Result[List[RequestDetail]] =
  for {
    response <- captureFailedRequests { () =>
                  requests.get(
                    url = externalTestsRequestsURL,
                    auth = token.authBearerToken
                  )
                }
    result <- decode[List[RequestDetail]](response.text()).left.map { error =>
                BridgeToolError.Decode(response.text(), error)
              }
    _ = logging.info(s"Found ${result.length} requests to process")
  } yield result

def rewriteForLocal(url: String): String =
  if (url.contains("localhost"))
    url.replace("/individuals/field-collections", "")
  else
    url

def extractURL(details: RequestDetail): Result[String] =
  details.uri
    .toRight {
      BridgeToolError.MissingURL(details)
    }
    .map(path => s"$qaAPIURL$path")

def extractMethod(details: RequestDetail): Result[String] =
  details.method.map(_.trim.toUpperCase) match {
    case Some("POST") => Right("POST")
    case Some("PUT")  => Right("PUT")
    case Some("GET")  => Right("GET")
    case None =>
      logging.error(s"Missing method for request ${details.requestId}. Defaulting to POST.")
      Right("POST")
    case invalid => Left(BridgeToolError.BadMethod(details))
  }

def postRequestDetailsToQA(token: TokenResponse, details: RequestDetail): Result[requests.Response] =
  for {
    url    <- extractURL(details)
    method <- extractMethod(details)
    response <- captureFailedRequests { () =>
                  requests
                    .send(method)
                    .apply(
                      auth = token.authBearerToken,
                      url = url,
                      data = details.content.noSpaces,
                      headers = defaultHeaders ++ details.headersToApply
                    )
                }.left.map {
                  case BadResponse(response) => BadResponseWithDetails(response, details)
                  case other                 => other
                }
  } yield response

def postResponseToExternalTest(
  token: TokenResponse,
  details: RequestDetail,
  qaResponse: Json,
  status: Int
): Result[Int] = {
  val responseDetails = RequestDetail(
    isResponse = true,
    requestId = details.requestId,
    content = qaResponse,
    status = Some(status)
  )
  captureFailedRequests { () =>
    requests
      .post(
        auth = token.authBearerToken,
        url = externalTestsResponseURL,
        data = responseDetails.asJson.noSpaces,
        headers = defaultHeaders ++ details.headersToApply
      )
  } match {
    case Left(BadResponse(response)) => Left(BadResponseWithDetails(response, details))
    case Right(ok)                   => Right(ok.statusCode)
    case Left(other)                 => Left(other)
  }
}

def parseQAResponse(response: requests.Response): Result[Json] =
  parse(response.text()).left.map { error =>
    BridgeToolError.Decode(response.text(), error)
  }

def nextProcessableRequest(details: List[RequestDetail]): Result[RequestDetail] =
  details.headOption.toRight(BridgeToolError.NoRequestsToProcess)

def deleteRequest(token: TokenResponse, details: RequestDetail): Result[Unit] =
  captureFailedRequests { () =>
    requests.delete(
      url = externalTestDeleteURL(details.requestId),
      auth = token.authBearerToken
    )
  }.map(_ => ())

def process(qaToken: TokenResponse, externalTestToken: TokenResponse): Result[Unit] =
  for {
    requests        <- retrieveAllUnprocessedRequests(externalTestToken)
    details         <- nextProcessableRequest(requests)
    qaResponse      <- postRequestDetailsToQA(qaToken, details)
    responseContent <- parseQAResponse(qaResponse)
    _               <- postResponseToExternalTest(externalTestToken, details, responseContent, qaResponse.statusCode)
    _               <- deleteRequest(externalTestToken, details)
  } yield ()

@scala.annotation.tailrec
def pollForRequests(qaToken: TokenResponse, externalTestToken: TokenResponse): Unit =
  process(qaToken, externalTestToken) match {

    case Right(()) =>
      pollForRequests(qaToken, externalTestToken)

    case Left(BridgeToolError.NoRequestsToProcess) =>
      logging.info("Haven't found any requests to process right now. Sleeping..")
      Thread.sleep(2000)
      pollForRequests(qaToken, externalTestToken)

    case Left(BridgeToolError.BadResponseWithDetails(errorResponse, details)) =>
      logging.error(
        s"Bad response; got ${errorResponse.statusCode} from ${errorResponse.url}; posting response to ET",
        errorResponse
      )
      parseQAResponse(errorResponse).flatMap { json =>
        postResponseToExternalTest(externalTestToken, details, json, errorResponse.statusCode)
      }

      Thread.sleep(2000)
      pollForRequests(qaToken, externalTestToken)

    case Left(error) =>
      logging.error("Failed to process request;")
      logging.error(error.errorMessage)
      Thread.sleep(2000)
      pollForRequests(qaToken, externalTestToken)

  }

object Main extends App {

  for {
    externalTestToken <- retrieveExternalTestToken()
    qaToken           <- retrieveQAToken()
  } yield pollForRequests(qaToken, externalTestToken)

}
