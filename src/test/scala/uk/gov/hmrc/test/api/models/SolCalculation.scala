/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class SolCalculation(
  uniqueItemReference: String,
  mainTrans: String,
  description: String,
  periodEnd: LocalDate,
  interestDueDebtTotal: Int,
  interestRequestedTo: LocalDate,
  combinedDailyAccrual: Int,
  duties: Seq[SolDuty]
)

object SolCalculation {
  implicit val formatOutputVariables: OFormat[SolCalculation] = Json.format[SolCalculation]
}