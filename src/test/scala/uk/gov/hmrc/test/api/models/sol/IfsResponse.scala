/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.models.sol

import play.api.libs.json._

import java.time.LocalDate

final case class DebtCalculationWindow(
  periodFrom: LocalDate,
  periodTo: LocalDate,
  numberOfDays: Long,
  interestRate: Double,
  interestDueDailyAccrual: Int,
  interestDueWindow: Int,
  amountOnIntDueWindow: Int,
  unpaidAmountWindow: Int,
  breathingSpaceApplied: Boolean
)

object DebtCalculationWindow {
  implicit val debtCalculationWindowWrite: OFormat[DebtCalculationWindow] = Json.format[DebtCalculationWindow]
}

final case class DebtCalculationItem(
  debtID: String,
  interestBearing: Boolean,
  numberOfChargeableDays: Long,
  interestDueDailyAccrual: Int,
  interestDueDutyTotal: Int,
  amountOnIntDueDuty: Int,
  totalAmountIntDuty: Int,
  unpaidAmountDuty: Int,
  interestOnlyIndicator: Boolean,
  calculationWindows: Seq[DebtCalculationWindow]
)

object DebtCalculationItem {
  implicit val debtCalculationItemWrite: OFormat[DebtCalculationItem] = Json.format[DebtCalculationItem]
}

final case class IfsResponse(
  combinedDailyAccrual: Int,
  unpaidAmountTotal: Int,
  interestDueCallTotal: Int,
  amountIntTotal: Int,
  amountOnIntDueTotal: Int,
  debtCalculations: Seq[DebtCalculationItem]
)

object IfsResponse {}
