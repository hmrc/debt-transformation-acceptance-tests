package uk.gov.hmrc.test.api.requests

import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.time.{Milliseconds, Span}
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.conf.TestConfiguration

object HelloWorldRequests {

  def getSolService(endpoint: String): StandaloneWSResponse = {

    val baseUri = TestConfiguration.url("statement-of-liability") + endpoint

    WsClient.get(baseUri)
  }

}
