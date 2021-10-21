package uk.gov.hmrc.test.api.models.ttpp

import play.api.libs.json.Json
import uk.gov.hmrc.test.api.models.{DebtItem, Frequency}

import java.time.LocalDate

final case class PlanToCreatePlan(
  quoteId: QuoteId,
  quoteType: QuoteType,
  quoteDate: LocalDate,
  instalmentStartDate: LocalDate,
  instalmentPaymentAmount: BigDecimal,
  paymentPlanType: InstalmentCalculationType,
  thirdPartyBank: Boolean,
  numberOfInstalments: Int,
  frequency: Frequency,
  duration: Duration,
  initialPaymentDate: LocalDate,
  initialPaymentAmount: BigDecimal,
  totalDebtIncInt: BigDecimal,
  totalInterest: BigDecimal,
  interestAccrued: BigDecimal,
  planInterest: BigDecimal
)

object PlanToCreatePlan {
  implicit val format = Json.format[PlanToCreatePlan]
}

final case class PaymentInformation(paymentMethod: PaymentMethod, paymentReference: PaymentReference)

object PaymentInformation {
  implicit val format = Json.format[PaymentInformation]
}

final case class CreatePlanRequest(
  customerReference: CustomerReference,
  quoteReference: QuoteReference,
  channelIdentifier: ChannelIdentifier,
  plan: PlanToCreatePlan,
  debtItems: Seq[DebtItem],
  payments: Seq[PaymentInformation],
  customerPostCodes: Seq[CustomerPostCode],
  instalments: Seq[Instalment]
)

object CreatePlanRequest {
  implicit val format = Json.format[CreatePlanRequest]
}
