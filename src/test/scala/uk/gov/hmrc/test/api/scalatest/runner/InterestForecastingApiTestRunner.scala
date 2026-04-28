package uk.gov.hmrc.test.api.scalatest.runner

import org.scalatest.Suites


// Mirrored from Cucumber feature path: src/test/resources/features/ifs
class InterestForecastingApiTestRunner
    extends Suites(
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.BreathingSpaceFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.DebtCalculationValidationFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.GetDebtForTPSSCasesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.InterestRateChangesEdgeCasesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.InterestRateChangesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.LeapYearFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.MultipeDebtItemsEdgeCasesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.MultipeDebtItemsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.ReferenceDataFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.SuppressionEdgeCasesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.SuppressionPeriodEndFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.SupressionByPostCodeFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.SupressionFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS.FCBreathingSpaceFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS.FCMultipeDebtItemsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS.VATFCBreathingSpaceFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.fieldCollectionsIFS.VATFCFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmemtCalculationSingleDebtFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationCombinedInstalmentsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationMultipleDebtsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationMultipleDebtsInput2FeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationMultipleDutiesFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationSingleDutyFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.instalment_calculation.InstalmentCalculationSuppressionsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.ifs.interestTypeDebt.DebtInterestTypeFeatureSpec
    )
