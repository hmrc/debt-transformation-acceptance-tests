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

package uk.gov.hmrc.test.api.models.sol

import play.api.libs.json.{Json, OFormat}

case class SolCalculation(
  debtId: String,
  mainTrans: String,
  debtTypeDescription: String,
  interestDueDebtTotal: BigInt,
  totalAmountIntDebt: BigInt,
  combinedDailyAccrual: BigInt,
  parentMainTrans: Option[String],
  duties: Seq[SolDuty]
)

object SolCalculation {
  implicit val formatOutputVariables: OFormat[SolCalculation] = Json.format[SolCalculation]
}
