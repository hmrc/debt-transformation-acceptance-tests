/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtCalculation(
  debtItemChargeId: String,
  interestBearing: Boolean,
  interestDueDailyAccrual: BigDecimal,
  interestDueDutyTotal: BigDecimal,
  amountOnIntDueDuty: BigDecimal,
  numberOfChargeableDays: Long,
  totalAmountIntDuty: BigDecimal,
  unpaidAmountDuty: BigDecimal,
  interestOnlyIndicator: Boolean,
  calculationWindows: List[CalculationWindow] = Nil
)

object DebtCalculation {
  implicit val formatDebtCalculation: OFormat[DebtCalculation] = Json.format[DebtCalculation]
}
