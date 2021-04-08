/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtItemCalculation(
                                uniqueItemReference: String,
                                dailyInterestAccrued: BigDecimal,
                                totalInterestAccrued: BigDecimal,
                                totalAmountOnWhichInterestDue: BigDecimal,
                                totalAmountWithInterest: BigDecimal,
                                totalAmountToPay: BigDecimal,
                                calculationWindows: List[DebtItemCalculationWindow] = Nil
                              )

object DebtItemCalculation {
  implicit val formatDebtCalculation: OFormat[DebtItemCalculation] = Json.format[DebtItemCalculation]
}
