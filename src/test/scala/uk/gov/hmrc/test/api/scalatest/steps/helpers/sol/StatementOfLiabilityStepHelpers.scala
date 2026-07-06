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

package uk.gov.hmrc.test.api.scalatest.steps.helpers.sol

import org.scalactic.source.Position
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.JsValue
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.test.api.models.sol.{SolCalculationSummaryResponse, SolDebtsRequest}
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder
import uk.gov.hmrc.test.api.scalatest.builders.StatementOfLiabilityBuilder.{SolCalculationExpected, SolCalculationSummaryResponseExpected, SolDutyExpected}
import uk.gov.hmrc.test.api.scalatest.steps.context.StatementOfLiabilityContext

trait StatementOfLiabilityStepHelpers { this: Matchers =>

  def theSolServiceRespondWith(context: StatementOfLiabilityContext, statusCode: Int, message: String): Unit = {
    context.status       shouldBe statusCode
    context.errorMessage shouldBe Some(message)
  }

  def debtDetails(
    context: StatementOfLiabilityContext,
    request: SolDebtsRequest
  ): Unit = {
    println("SolDebtsRequest : " + request)
    context.solRequest = Some(request)
  }

  def aDebtStatementOfLiabilityIsRequested(context: StatementOfLiabilityContext): Unit = {
    val response         = StatementOfLiabilityBuilder.getStatementOfLiability(context.solRequest)
    val jsonResponseBody = response.body[JsValue]
    context.status = response.status
    context.solResponseBody = Some(jsonResponseBody.as[SolCalculationSummaryResponse])
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def statementOfLiabilityIsRequestedWithoutDebt(context: StatementOfLiabilityContext): Unit = {
    val response = StatementOfLiabilityBuilder.getStatementOfLiability(context.solRequest)
    context.status = response.status
    context.errorMessage = Some(response.body)
    context.headers = response.headers.map { case (key, values) => key -> values.headOption.getOrElse("") }
  }

  def serviceReturnsDebtStatementOfLiabilityData(
    context: StatementOfLiabilityContext,
    expectedResponse: SolCalculationSummaryResponseExpected
  )(implicit pos: Position): Unit = {
    context.status shouldBe 200

    val actual = context.solResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    expectedResponse.amountIntTotal.foreach { v =>
      withClue("amountIntTotal: ") {
        actual.amountIntTotal shouldBe v
      }
    }

    expectedResponse.combinedDailyAccrual.foreach { v =>
      withClue("combinedDailyAccrual: ") {
        actual.combinedDailyAccrual shouldBe v
      }
    }
  }

  def theCustomerStatementOfLiabilityContainsDebtValuesAs(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    expectedDebt: SolCalculationExpected
  )(implicit pos: Position): Unit = {
    val actual = context.solResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val actualDebt = actual.debts
      .lift(debtIndex - 1)
      .getOrElse(fail(s"Missing debt at index [${debtIndex - 1}] in response"))

    withClue(s"debts[${debtIndex - 1}]") {

      expectedDebt.debtId.foreach { v =>
        withClue("debtId: ") {
          actualDebt.debtId shouldBe v
        }
      }

      expectedDebt.mainTrans.foreach { v =>
        withClue("mainTrans: ") {
          actualDebt.mainTrans shouldBe v
        }
      }

      expectedDebt.debtTypeDescription.foreach { v =>
        withClue("debtTypeDescription: ") {
          actualDebt.debtTypeDescription shouldBe v
        }
      }

      expectedDebt.interestDueDebtTotal.foreach { v =>
        withClue("interestDueDebtTotal: ") {
          actualDebt.interestDueDebtTotal shouldBe v
        }
      }

      expectedDebt.totalAmountIntDebt.foreach { v =>
        withClue("totalAmountIntDebt: ") {
          actualDebt.totalAmountIntDebt shouldBe v
        }
      }

      expectedDebt.combinedDailyAccrual.foreach { v =>
        withClue("combinedDailyAccrual: ") {
          actualDebt.combinedDailyAccrual shouldBe v
        }
      }

      expectedDebt.parentMainTrans.foreach { v =>
        withClue("parentMainTrans: ") {
          actualDebt.parentMainTrans shouldBe Some(v)
        }
      }
    }
  }

  def theCustomerStatementOfLiabilityContainsDutyValuesAs(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    expectedDuties: List[SolDutyExpected]
  )(implicit pos: Position): Unit = {

    val actual = context.solResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val actualDuties = actual.debts
      .lift(debtIndex - 1)
      .getOrElse(fail(s"Missing debt at index [${debtIndex - 1}] in response"))
      .duties

    withClue(s"debts[${debtIndex - 1}].duties") {

      actualDuties.zip(expectedDuties).zipWithIndex.foreach { case ((actualDuty, expectedDuty), dutyIndex) =>

        withClue(s"duties[$dutyIndex]") {

          expectedDuty.subTrans.foreach { v =>
            withClue("subTrans: ") {
              actualDuty.subTrans shouldBe v
            }
          }

          expectedDuty.dutyTypeDescription.foreach { v =>
            withClue("dutyTypeDescription: ") {
              actualDuty.dutyTypeDescription shouldBe Some(v)
            }
          }

          expectedDuty.unpaidAmountDuty.foreach { v =>
            withClue("unpaidAmountDuty: ") {
              actualDuty.unpaidAmountDuty shouldBe v
            }
          }

          expectedDuty.combinedDailyAccrual.foreach { v =>
            withClue("combinedDailyAccrual: ") {
              actualDuty.combinedDailyAccrual shouldBe v
            }
          }

          expectedDuty.interestBearing.foreach { v =>
            withClue("interestBearing: ") {
              actualDuty.interestBearing shouldBe v
            }
          }

          expectedDuty.interestOnlyIndicator.foreach { v =>
            withClue("interestOnlyIndicator: ") {
              actualDuty.interestOnlyIndicator shouldBe v
            }
          }
        }
      }
    }
  }

  def theSolDebtSummaryWillContainDuties(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    expectedDuties: List[SolDutyExpected]
  )(implicit pos: Position): Unit = {
    val actual = context.solResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val actualDuties = actual.debts
      .lift(debtIndex - 1)
      .getOrElse(fail(s"Missing debt at index [${debtIndex - 1}] in response"))
      .duties

    withClue(s"debts[${debtIndex - 1}].duties") {

      withClue("duties list length: ") {
        actualDuties.length shouldBe expectedDuties.length
      }

      actualDuties.zip(expectedDuties).zipWithIndex.foreach { case ((actualDuty, expectedDuty), dutyIndex) =>

        withClue(s"duties[$dutyIndex]") {

          expectedDuty.subTrans.foreach { v =>
            withClue("subTrans: ") {
              actualDuty.subTrans shouldBe v
            }
          }

          expectedDuty.dutyTypeDescription.foreach { v =>
            withClue("dutyTypeDescription: ") {
              actualDuty.dutyTypeDescription.toString should include(v)
            }
          }

          expectedDuty.unpaidAmountDuty.foreach { v =>
            withClue("unpaidAmountDuty: ") {
              actualDuty.unpaidAmountDuty shouldBe v
            }
          }

          expectedDuty.combinedDailyAccrual.foreach { v =>
            withClue("combinedDailyAccrual: ") {
              actualDuty.combinedDailyAccrual shouldBe v
            }
          }

          expectedDuty.interestBearing.foreach { v =>
            withClue("interestBearing: ") {
              actualDuty.interestBearing shouldBe v
            }
          }

          expectedDuty.interestOnlyIndicator.foreach { v =>
            withClue("interestOnlyIndicator: ") {
              actualDuty.interestOnlyIndicator shouldBe v
            }
          }
        }
      }
    }
  }

  def theMultipleStatementOfLiabilityDutiesSummaryWillContain(
    context: StatementOfLiabilityContext,
    debtIndex: Int,
    expectedDuty: SolDutyExpected
  )(implicit pos: Position): Unit = {
    val actual = context.solResponseBody.getOrElse(
      fail("Missing response body in context")
    )

    val actualDuty = actual.debts
      .lift(debtIndex - 1)
      .getOrElse(fail(s"Missing debt at index [${debtIndex - 1}] in response"))
      .duties
      .headOption
      .getOrElse(fail(s"No duties found for debt at index [${debtIndex - 1}]"))

    withClue(s"debts[${debtIndex - 1}].duties.head") {

      expectedDuty.subTrans.foreach { v =>
        withClue("subTrans: ") {
          actualDuty.subTrans shouldBe v
        }
      }

      expectedDuty.dutyTypeDescription.foreach { v =>
        withClue("dutyTypeDescription: ") {
          actualDuty.dutyTypeDescription shouldBe v
        }
      }

      expectedDuty.unpaidAmountDuty.foreach { v =>
        withClue("unpaidAmountDuty: ") {
          actualDuty.unpaidAmountDuty shouldBe v
        }
      }

      expectedDuty.combinedDailyAccrual.foreach { v =>
        withClue("combinedDailyAccrual: ") {
          actualDuty.combinedDailyAccrual shouldBe v
        }
      }

      expectedDuty.interestBearing.foreach { v =>
        withClue("interestBearing: ") {
          actualDuty.interestBearing shouldBe v
        }
      }

      expectedDuty.interestOnlyIndicator.foreach { v =>
        withClue("interestOnlyIndicator: ") {
          actualDuty.interestOnlyIndicator shouldBe v
        }
      }
    }
  }

}
