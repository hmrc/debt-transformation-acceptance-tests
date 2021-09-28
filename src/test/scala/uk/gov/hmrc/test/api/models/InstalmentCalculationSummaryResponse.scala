package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

/*
 * Copyright 2021 HM Revenue & Customs
 *
 */


final case class InstalmentCalculationSummaryResponse(
                                                       dateOfCalculation: LocalDate,
                                                       numberOfInstalments: Long,
                                                       planInterest: Int,
                                                       interestAccrued: Int,
                                                       totalInterest: Int,
                                                       duration: Long,
                                                       instalments: Seq[InstalmentResponse]
                                                     )

object InstalmentCalculationSummaryResponse {
  implicit val instalmentCalculationSummaryResponseFormat: OFormat[InstalmentCalculationSummaryResponse] = Json.format[InstalmentCalculationSummaryResponse]

}


