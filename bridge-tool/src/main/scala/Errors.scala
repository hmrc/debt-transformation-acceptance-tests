package errors

import requests.Response
import main.RequestDetail
import io.circe.{ Error => CirceError }

sealed abstract class BridgeToolError {
  import BridgeToolError._

  def errorMessage: String =
    this match {

      case Token(error: Response) =>
        s"Failed get token; got: $error"

      case Connectivity(url: String, message: String) =>
        s"Failed to connect to $url; $message"

      case BadResponse(error: Response) =>
        s"Bad Response got: $error"

      case BadResponseWithDetails(error: Response, details: RequestDetail) =>
        s"Bad Response with details; got: $error"

      case MissingURL(badDetails: RequestDetail) =>
        s"Missing URL from request details; details: $badDetails"

      case MissingQAToken =>
        s"Missing QAToken from local environment"

      case BadMethod(badDetails: RequestDetail) =>
        badDetails.method match {
          case Some(badMethod) =>
            s"Bad method from details: $badDetails; method $badMethod is not POST, PUT, or GET"
          case None =>
            s"Bad method from details: $badDetails; missing http method"
        }

      case Decode(badObject: String, error: CirceError) =>
        s"failed to decode $badObject; got: $error"

      case NoRequestsToProcess =>
        s"No available requests."

    }

}

object BridgeToolError {

  type Result[A] = Either[BridgeToolError, A]

  final case class Token(error: Response) extends BridgeToolError
  final case class Connectivity(url: String, message: String) extends BridgeToolError
  final case class BadResponse(error: Response) extends BridgeToolError
  final case class BadResponseWithDetails(error: Response, details: RequestDetail) extends BridgeToolError
  final case class MissingURL(badDetails: RequestDetail) extends BridgeToolError
  case object MissingQAToken extends BridgeToolError
  final case class BadMethod(badDetails: RequestDetail) extends BridgeToolError
  final case class Decode(badObject: String, error: CirceError) extends BridgeToolError
  case object NoRequestsToProcess extends BridgeToolError

}
