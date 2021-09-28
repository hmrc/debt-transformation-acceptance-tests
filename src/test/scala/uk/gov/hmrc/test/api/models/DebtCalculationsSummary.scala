/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtCalculationsSummary(
  combinedDailyAccrual: BigDecimal,
  interestDueCallTotal: BigDecimal,
  amountIntTotal: BigDecimal,
  amountOnIntDueTotal: BigDecimal,
  unpaidAmountTotal: BigDecimal,
  debtCalculations: List[DebtCalculation]
)

object DebtCalculationsSummary {
  implicit val formatDebtCalculation: OFormat[DebtCalculationsSummary] = Json.format[DebtCalculationsSummary]
}
