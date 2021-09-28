package uk.gov.hmrc.test.api.models

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
case class PaymentPlanSummary(
  dateOfCalculation: LocalDate,
  numberOfInstalments: Long,
  interestAccrued: BigDecimal,
  planInterest: BigDecimal,
  totalInterest: BigDecimal,
  duration: Long,
  instalments: Seq[PaymentPlanInstalmentResponse]
)

object PaymentPlanSummary {
  implicit val paymentPlanInstalmentFormat: OFormat[PaymentPlanSummary] = Json.format[PaymentPlanSummary]
}
