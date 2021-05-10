/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.test.api.models

import play.api.libs.json.Json

case class SolDuty(
  debtItemChargeID: String,
  subTrans: String,
  description: String,
  unpaidAmountDuty: Int,
  combinedDailyAccrual: Int
) {}

object SolDuty {
  implicit val format = Json.format[SolDuty]
}
