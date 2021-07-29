package uk.gov.hmrc.test.api.models

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class PaymentPlanInstalment(
  serialNo: Int,
  paymentDueDate: LocalDate,
  amountDue: BigDecimal,
  uniqueDebtId: String,
  balance: BigDecimal,
  interestDue: BigDecimal,
  totalPaidAmount: BigDecimal,
  intRate: Double
)

object PaymentPlanInstalment {
  implicit val paymentPlanInstalmentFormat: OFormat[PaymentPlanInstalment] = Json.format[PaymentPlanInstalment]
}
