/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}

case class Payment(amountPaid: BigDecimal, dateOfPayment: LocalDate)

object Payment {
  implicit val formatPayment: OFormat[Payment] = Json.format[Payment]
}
