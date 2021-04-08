/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtItemCalculation(
                                calculationWindow: List[DebtItemCalculationWindow] = Nil
                              )

object DebtItemCalculation {
  implicit val formatDebtCalculation: OFormat[DebtItemCalculation] = Json.format[DebtItemCalculation]
}
