package uk.gov.hmrc.test.api.models.ttpp
import java.time.LocalDate
import play.api.libs.json.Json

case class Instalment(dutyId: String,
                      debtId: String,
                      dueDate: LocalDate,
                      amountDue: BigDecimal,
                      expectedPayment: BigDecimal,
                      interestRate: Double,
                      instalmentNumber: Int)

object Instalment {
  implicit val format = Json.format[Instalment]
}
