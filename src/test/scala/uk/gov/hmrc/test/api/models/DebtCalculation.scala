/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

case class DebtCalculation(
  debtItemChargeId: Option[String],
  debtID: Option[String],
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
