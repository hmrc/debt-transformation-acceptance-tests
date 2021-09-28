/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class InstalmentCalculation(
                                  debtId: String,
                                  debtAmount: BigDecimal,
                                  instalmentPaymentAmount: BigDecimal,
                                  paymentFrequency: Frequency,
                                  instalmentPaymentDate: LocalDate,
                                  quoteDate: LocalDate,
                                  mainTrans: MainTransType,
                                  subTrans: SubTransType,
                                  interestAccrued: Int,
                                  initialPaymentDate: Option[LocalDate] = None,
                                  initialPaymentAmount: Option[BigDecimal] = None
                                )

object InstalmentCalculation {
  implicit val instalmentCalculationFormat: OFormat[InstalmentCalculation] = Json.format[InstalmentCalculation]
}

