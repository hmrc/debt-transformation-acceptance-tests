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

package uk.gov.hmrc.test.api.models

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

import scala.collection.immutable

sealed trait ChargeType extends EnumEntry

object ChargeType extends Enum[ChargeType] with PlayJsonEnum[ChargeType] {
  val values: immutable.IndexedSeq[ChargeType] = findValues

  case object IT extends ChargeType

  case object NI extends ChargeType

  case object HIPG extends ChargeType // Health in pregnancy grant

  case object INT_IT extends ChargeType

  case object TG_PEN extends ChargeType

  case object ChildBenefitDebt extends ChargeType

  case object GuardiansAllowanceGBDebt extends ChargeType

  case object GuardiansAllowanceNIDebt extends ChargeType

  case object ChildBenefitMigratedDebt extends ChargeType

  case object GuardiansAllowanceMigratedGBDebt extends ChargeType

  case object GuardiansAllowanceMigratedNIDebt extends ChargeType

}
