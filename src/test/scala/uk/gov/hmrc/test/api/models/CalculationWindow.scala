/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

case class CalculationWindow(
  periodFrom: LocalDate,
  periodTo: LocalDate,
  numberOfDays: Long,
  interestRate: Double,
  interestDueWindow: BigDecimal,
  interestDueDailyAccrual: BigDecimal,
  amountOnIntDueWindow: BigDecimal,
  unpaidAmountWindow: BigDecimal,
  suppressionApplied: Option[SuppressionApplied]
)

case class SuppressionApplied(reason: String, description: String, code: String)

object SuppressionApplied {
  implicit val formatCalculationWindow: OFormat[SuppressionApplied] = Json.format[SuppressionApplied]
}

object CalculationWindow {
  implicit val formatDebtItemCalculationWindow: OFormat[CalculationWindow] =
    Json.format[CalculationWindow]
}
