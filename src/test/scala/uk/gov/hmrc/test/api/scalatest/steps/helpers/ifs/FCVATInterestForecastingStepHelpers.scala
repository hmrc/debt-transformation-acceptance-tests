/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs

import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.test.api.models.ifs.FCVATDebtCalculationRequest
import uk.gov.hmrc.test.api.models.FCVATDebtCalculationsSummary
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsVATBuilder.{FCVATDebtCalculationExpected, FCVATDebtCalculationsSummaryExpected}
import uk.gov.hmrc.test.api.scalatest.builders.FieldCollectionsVATBuilder
import uk.gov.hmrc.test.api.scalatest.steps.context.FieldCollectionsVATContext

trait FCVATInterestForecastingStepHelpers { this: Matchers =>

  def aFcVatDebtCalculation(context: FieldCollectionsVATContext, request: FCVATDebtCalculationRequest): Unit =
    context.ifsRequest = Some(request)

  def theDebtItemIsSentToTheFcVatIfsService(context: FieldCollectionsVATContext): Unit = {
    val requestJson                    = Json.toJson(context.ifsRequest.getOrElse(fail("Missing request in context")))
    val response: StandaloneWSResponse = FieldCollectionsVATBuilder.getDebtCalculation(requestJson)
    context.response = response
    context.status = response.status
    context.headers = response.headers.view.mapValues(_.mkString(", ")).toMap

    println("\n==== REQUEST BODY ====")
    println(requestJson)

    println("\n==== RESPONSE STATUS ====")
    println(context.status)

    if (response.status == 200) {
      val jsonResponseBody = response.body[JsValue]
      context.ifsResponseBody = Some(jsonResponseBody.as[FCVATDebtCalculationsSummary])

      println("\n==== RESPONSE BODY ====")
      println(jsonResponseBody)
    } else {
      println("\n==== ERROR RESPONSE BODY ====")
      println(response.body)
    }
  }

  def theFcVatIfsServiceWillReturnATotalDebtsSummaryOf(
    context: FieldCollectionsVATContext,
    expectedResponse: FCVATDebtCalculationsSummaryExpected
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    withClue("FCDebtCalculationsSummary") {
      expectedResponse.dateOfCalculation.foreach { e =>
        withClue("dateOfCalculation") {
          responseBody.dateOfCalculation shouldBe e
        }
      }

      expectedResponse.combinedDailyAccrual.foreach { e =>
        withClue("combinedDailyAccrual") {
          responseBody.combinedDailyAccrual shouldBe e
        }
      }

      expectedResponse.unpaidAmountTotal.foreach { e =>
        withClue("unpaidAmountTotal") {
          responseBody.unpaidAmountTotal shouldBe e
        }
      }
    }
  }

  def theFcVatDebtSummaryWillContain(
    context: FieldCollectionsVATContext,
    index: Int,
    expectedResponse: FCVATDebtCalculationExpected
  ): Unit = {
    val responseBody = context.ifsResponseBody.getOrElse(fail("Missing response body in context"))

    val FCVATDebtCalculations = responseBody.debtCalculations(index - 1)

    withClue("FCDebtCalculation") {
      expectedResponse.debtItemChargeId.foreach { e =>
        withClue("debtItemChargeId") {
          FCVATDebtCalculations.debtItemChargeId shouldBe e
        }
      }

      expectedResponse.interestDueDailyAccrual.foreach { e =>
        withClue("interestDueDailyAccrual") {
          FCVATDebtCalculations.interestDueDailyAccrual shouldBe e
        }
      }

      expectedResponse.interestRate.foreach { e =>
        withClue("interestRate") {
          FCVATDebtCalculations.interestRate shouldBe e
        }
      }
    }
  }

  def theFcVatIfsServiceWillRespondWith(context: FieldCollectionsVATContext, expectedMessage: String): Unit = {
    val response = Option(context.response).getOrElse(fail("Missing response in context"))

    withClue("response body should include expected message") {
      response.body should include(expectedMessage)
    }

    withClue("response status") {
      context.status shouldBe 400
    }
  }

}
