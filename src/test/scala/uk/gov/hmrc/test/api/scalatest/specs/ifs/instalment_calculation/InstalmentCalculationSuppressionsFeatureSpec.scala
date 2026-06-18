/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.models.{SuppressionInformation, SuppressionRequest}
import uk.gov.hmrc.test.api.models.ifs.{DebtItemCharge, InstallmentCalculationCustomerPostCode, InstalmentCalculationRequest}
import uk.gov.hmrc.test.api.scalatest.steps.context.IFSInstalmentCalculationContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.{FCInterestForecastingStepHelpers, IFSInstalmentCalculationStepHelpers, InterestForecastingStepHelpers}
import uk.gov.hmrc.test.api.scalatest.tags.DTD_417

import java.time.LocalDate

class InstalmentCalculationSuppressionsFeatureSpec
    extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = IFSInstalmentCalculationContext

  override def withFixture(test: OneArgTest) = {
    val context = IFSInstalmentCalculationContext()
    try test(context)
    finally ()
  }

  Feature("Suppression Period ends after quote date") {
    val dateInFuture = Some(LocalDate.now().plusYears(1).toString)

    Scenario(
      "Instalment calculation has been requested where a postcode suppression period ends after the quote date"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = None,
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "BS39 5DP",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }

    Scenario(
      "Instalment calculation has been requested where a period end suppression period ends after the quote date"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = None,
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "TW3",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }

    Scenario(
      "Instalment calculation has been requested where a main trans suppression period ends after the quote date"
    ) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000",
              periodEnd = Some(LocalDate.parse("2021-08-16"))
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "duration",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = None,
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "TW3",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 1423,
        instalmentPaymentAmount = Some(10000)
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }

    Scenario("Should calculate instalment where suppression period ends after the quote date", DTD_417) { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "BS39 5DP",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 0,
        instalmentPaymentAmount = None
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }

    Scenario("Should calculate instalment where a period end suppression period ends after the quote date") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000"
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "TW3",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 0,
        instalmentPaymentAmount = None
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }

    Scenario("Should calculate instalment where a main trans suppression period ends after the quote date") { context =>
      Given("suppression configuration data is created")
      val suppressionRequest = SuppressionRequest(suppressions =
        Seq(
          SuppressionInformation(
            dateFrom = "2024-03-01",
            dateTo = dateInFuture,
            reason = "LEGISLATIVE",
            reasonDesc = "COVID",
            suppressionChargeDescription = "SA-Suppression",
            postcode = None,
            mainTrans = None,
            subTrans = Some("1000"),
            checkPeriodEnd = None
          )
        )
      )
      suppressionInformationDetails(context, suppressionRequest)

      When("suppression configuration is sent to ifs service")
      theSuppressionConfigurationIsSentToTheIfsService(context)

      And("instalment calculation details with postcode date a year in the past")
      val ifsRequest = InstalmentCalculationRequest(
        debtItemCharges = Some(
          List(
            DebtItemCharge(
              debtId = "debtId",
              debtAmount = 100000,
              mainTrans = "1525",
              subTrans = "1000",
              periodEnd = Some(LocalDate.parse("2021-08-16"))
            )
          )
        ),
        quoteDate = LocalDate.now(),
        quoteType = "instalmentAmount",
        isQuoteDateNonInclusive = None,
        instalmentPaymentDate = LocalDate.now.plusDays(1),
        paymentFrequency = "monthly",
        duration = Some(24),
        customerPostCodes = Some(
          List(
            InstallmentCalculationCustomerPostCode(
              postCode = "TW3",
              postCodeDate = LocalDate.now().minusYears(1).toString
            )
          )
        ),
        interestCallDueTotal = 0,
        instalmentPaymentAmount = None
      )
      instalmentCalculationDetails(context, ifsRequest)

      And("the instalment calculation detail is sent to the ifs service")
      theInstalmentCalculationDetailIsSentToTheIfsService(context)

      Then("the IFS request should return status 200")
      theIfsRequestShouldReturnStatus(context, 200)

      And("the 1st instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 1, 0)

      And("the 2nd instalment should have an interest accrued of 0")
      theInstalmentShouldHaveAnInterestAccruedOf(context, 2, 0)

    }
  }
}
