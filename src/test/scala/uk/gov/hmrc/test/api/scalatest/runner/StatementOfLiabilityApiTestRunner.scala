package uk.gov.hmrc.test.api.scalatest.runner

import org.scalatest.Suites


// Mirrored from Cucumber feature path: src/test/resources/features/sol
class StatementOfLiabilityApiTestRunner
    extends Suites(
      new uk.gov.hmrc.test.api.scalatest.specs.sol.RequestSoLForMultipleDebtsFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.sol.SolDebtDetailsRequestFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.sol.SolDebtDetailsUnhappyPathFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.sol.SolSADebtDetailsRequestFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.sol.SolWilthSuppressionFeatureSpec,
      new uk.gov.hmrc.test.api.scalatest.specs.sol.fcsol.FCSolrequestFeatureSpec
    )
