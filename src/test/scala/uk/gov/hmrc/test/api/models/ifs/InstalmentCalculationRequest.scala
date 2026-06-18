package uk.gov.hmrc.test.api.models.ifs

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

final case class InstalmentCalculationRequest(
  debtItemCharges: Option[List[DebtItemCharge]],
  quoteDate: LocalDate,
  quoteType: String,
  isQuoteDateNonInclusive: Option[Boolean] = None,
  instalmentPaymentDate: LocalDate,
  paymentFrequency: String,
  instalmentPaymentAmount: Option[Int] = None,
  duration: Option[Int] = None,
  customerPostCodes: Option[List[InstallmentCalculationCustomerPostCode]],
  interestCallDueTotal: BigDecimal,
  initialPaymentDate: Option[LocalDate] = None,
  initialPaymentAmount: Option[BigDecimal] = None
)

object InstalmentCalculationRequest {
  implicit val format: OFormat[InstalmentCalculationRequest] = Json.format[InstalmentCalculationRequest]
}

final case class DebtItemCharge(
  debtId: String,
  debtAmount: BigDecimal,
  subTrans: String,
  mainTrans: String,
  interestStartDate: Option[LocalDate] = None,
  periodEnd: Option[LocalDate] = None
)

object DebtItemCharge {
  implicit val formatDebtItemCharge: OFormat[DebtItemCharge] = Json.format[DebtItemCharge]
}

final case class InstallmentCalculationCustomerPostCode(
  postCode: String,
  postCodeDate: String
)

object InstallmentCalculationCustomerPostCode {
  implicit val formatInstallmentCalculationCustomerPostCode: OFormat[InstallmentCalculationCustomerPostCode] =
    Json.format[InstallmentCalculationCustomerPostCode]
}
