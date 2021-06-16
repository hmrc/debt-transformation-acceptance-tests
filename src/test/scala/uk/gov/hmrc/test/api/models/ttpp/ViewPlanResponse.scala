package uk.gov.hmrc.test.api.models.ttpp

import java.time.LocalDate

import play.api.libs.json.Json

case class PlanInstalment(dutyId: String,
                      debtId: String,
                      dueDate: LocalDate,
                      amountDue: BigDecimal,
                      balance: BigDecimal,
                      interest: BigDecimal,
                      interestRate: Double,
                      instalmentNumber: Int)

object PlanInstalment {
  implicit val format = Json.format[PlanInstalment]
}

case class ViewPlanResponse(customerReference: String,
                            planId: String,
                            quoteType: String,
                            paymentMethod: String,
                            paymentReference: String,
                            instalments: List[PlanInstalment],
                            numberOfInstalments: String,
                            totalDebtAmount: BigDecimal,
                            totalInterest: Double)

object ViewPlanResponse {
  implicit val format = Json.format[ViewPlanResponse]
}
