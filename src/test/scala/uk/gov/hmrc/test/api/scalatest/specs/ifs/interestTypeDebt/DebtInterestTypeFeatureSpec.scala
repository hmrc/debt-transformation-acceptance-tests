package uk.gov.hmrc.test.api.scalatest.specs.ifs.interestTypeDebt

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.FCInterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.IFSInstalmentCalculationStepHelpers
import uk.gov.hmrc.test.api.scalatest.steps.helpers.ifs.InterestForecastingStepHelpers
import uk.gov.hmrc.test.api.scalatest.tags._

class DebtInterestTypeFeatureSpec
  extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCInterestForecastingStepHelpers
    with IFSInstalmentCalculationStepHelpers
    with InterestForecastingStepHelpers {

  override type FixtureParam = FCStatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = FCStatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("IFS provide details on whether the charges - use the charge reference or ASN and interest bearing") {

    Scenario("Debt Interest type - use the charge reference or ASN and interest bearing") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the 1st debt interest type response summary will contain")
      // TODO: Helper 'theDebtInterestTypeResponseSummaryWillContain' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeResponseSummaryWillContain(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5330, subTrans=7006]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5330, subTrans=7010]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5330, subTrans=7011]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5350, subTrans=7012]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5350, subTrans=7014]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=5350, subTrans=7013]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1085, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1085, subTrans=1020]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1085, subTrans=1025]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1085, subTrans=1180]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1511, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1515, subTrans=1090]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1520, subTrans=1090]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1525, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1526, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1530, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1531, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1535, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1536, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1540, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1541, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1545, subTrans=1000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1545, subTrans=1090]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1545, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1546, subTrans=2000]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=2421, subTrans=1150]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=1441, subTrans=1150]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=4618, subTrans=1090]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=3996, subTrans=1091]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
    Scenario("Debt Interest type rule validation [mainTrans=3997, subTrans=2091]") { context =>
      Given("a debt interest type item")
      // TODO: Helper 'aDebtInterestTypeItem' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtInterestTypeItem(context)

      And("the debt interest type request is sent to the ifs service")
      // TODO: Helper 'theDebtInterestTypeRequestIsSentToTheIfsService' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theDebtInterestTypeRequestIsSentToTheIfsService(context)

      Then("the ifs service will respond with")
      // TODO: Helper 'theIfsServiceWillRespondWith' expects context 'InterestForecastingContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theIfsServiceWillRespondWith(context)

    }
  }
}
