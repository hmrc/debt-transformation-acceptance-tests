package uk.gov.hmrc.test.api.models.ttpp

import play.api.libs.json.Json

import java.time.LocalDate

final case class Plan(planId: PlanId,
                      quoteId: QuoteId,
                      quoteDate: LocalDate,
                      quoteType: QuoteType,
                      paymentPlanType: String,
                      thirdPartyBank: Boolean,
                      numberOfInstalments: Int,
                      totalDebtIncInt: BigDecimal,
                      totalInterest: BigDecimal,
                      interestAccrued: BigDecimal,
                      planInterest: BigDecimal)

object Plan {
  implicit val format = Json.format[Plan]
}