/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtItemCalculation(
  debtID: String,
  interestBearing: Boolean,
  interestDueDailyAccrual: BigDecimal,
  interestDueDutyTotal: BigDecimal,
  amountOnIntDueDuty: BigDecimal,
  numberOfChargeableDays: Long,
  totalAmountIntDuty: BigDecimal,
  unpaidAmountDuty: BigDecimal,
  interestOnlyIndicator: Boolean,
  calculationWindows: List[DebtItemCalculationWindow] = Nil
)
object DebtItemCalculation {
  implicit val formatDebtCalculation: OFormat[DebtItemCalculation] = Json.format[DebtItemCalculation]
}
