package uk.gov.hmrc.test.api.requests

import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.client.WsClient
import uk.gov.hmrc.test.api.conf.TestConfiguration

object HelloWorldRequests {

  def getStatementLiabilityService(endpoint: String): StandaloneWSResponse = {

    val baseUri = TestConfiguration.url("statement-of-liability") + endpoint

    WsClient.get(baseUri)
  }

  def getInterestForecastingService(endpoint: String): StandaloneWSResponse = {

    val baseUri = TestConfiguration.url("interest-forecasting") + endpoint

    WsClient.get(baseUri)
  }



}
