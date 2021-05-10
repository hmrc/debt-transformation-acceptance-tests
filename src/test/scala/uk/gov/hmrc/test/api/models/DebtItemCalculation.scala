/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtItemCalculation(
  uniqueItemReference: String,
  interestBearing: Boolean,
  interestDueDailyAccrual: BigDecimal,
  interestDueDebtTotal: BigDecimal,
  amountOnIntDueDebt: BigDecimal,
  numberOfChargeableDays: Long,
  totalAmountIntDebt: BigDecimal,
  unpaidAmountDebt: BigDecimal,
  calculationWindows: List[DebtItemCalculationWindow] = Nil
)
object DebtItemCalculation {
  implicit val formatDebtCalculation: OFormat[DebtItemCalculation] = Json.format[DebtItemCalculation]
}
