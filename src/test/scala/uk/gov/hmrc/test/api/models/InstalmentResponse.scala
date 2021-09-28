/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

final case class InstalmentResponse(
  debtId: String,
  instalmentNumber: Int,
  dueDate: LocalDate,
  amountDue: Int,
  instalmentBalance: Int,
  instalmentInterestAccrued: Int,
  expectedPayment: Int,
  intRate: Double
)

object InstalmentResponse {
  implicit val instalmentResponseFormat: OFormat[InstalmentResponse] = Json.format
}
