/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class SolCalculationSummaryResponse(
  totalAmountIntDuty: Int,
  combinedDailyAccrual: Int,
  debts: List[SolCalculation]
) {}

object SolCalculationSummaryResponse {
  implicit val solResponseFormat: OFormat[SolCalculationSummaryResponse] = Json.format[SolCalculationSummaryResponse]

  def apply(solCalculationSummary: SolCalculationSummary): SolCalculationSummaryResponse =
    SolCalculationSummaryResponse(
      totalAmountIntDuty = solCalculationSummary.totalAmountIntDuty,
      combinedDailyAccrual = solCalculationSummary.combinedDailyAccrual,
      debts = solCalculationSummary.debts
    )
}
