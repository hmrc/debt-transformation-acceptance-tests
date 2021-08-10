package uk.gov.hmrc.test.api.models

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */
import play.api.libs.json.{Json, OFormat}
case class PaymentPlanSummary(
                               totalNumberOfInstalments:Long,
                               totalPlanInt: BigDecimal,
                               interestAccrued: BigDecimal,
                               totalInterest: BigDecimal,
                               paymentPlanCalculationResponse: Seq[PaymentPlanInstalmentResponse]

)

object PaymentPlanSummary {
  implicit val paymentPlanInstalmentFormat: OFormat[PaymentPlanSummary] = Json.format[PaymentPlanSummary]
}
