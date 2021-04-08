/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class DebtItemCalculationWindow(dateFrom: LocalDate,
                                     dateTo: LocalDate,
                                     numberOfChargeableDays: Long,
                                     interestRateApplied: Long,
                                     dailyInterestAccrued: BigDecimal,
                                     totalInterestAccrued: BigDecimal)

object DebtItemCalculationWindow {
  implicit val formatDebtItemCalculationWindow: OFormat[DebtItemCalculationWindow] = Json.format[DebtItemCalculationWindow]
}
