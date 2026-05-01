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

package uk.gov.hmrc.test.api.scalatest.specs.sol

import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.steps.context.FCStatementOfLiabilityContext
import uk.gov.hmrc.test.api.scalatest.steps.helpers.sol.{FCStatementOfLiabilityStepHelpers, StatementOfLiabilityStepHelpers}

class SolDebtDetailsUnhappyPathFeatureSpec
  extends FixtureAnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with FCStatementOfLiabilityStepHelpers
    with StatementOfLiabilityStepHelpers {

  override type FixtureParam = FCStatementOfLiabilityContext

  override def withFixture(test: OneArgTest) = {
    val context = FCStatementOfLiabilityContext()
    try test(context)
    finally ()
  }

  Feature("Statement of liability Unhappy Path (Service Errors)") {

    ignore("Send error message where no debt items are provided when SoL is called - DTD-545") { context =>
      Given("a request to sol with no debt items provided")
      // TODO: Helper 'aRequestToSolWithNoDebtItemsProvided' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aRequestToSolWithNoDebtItemsProvided(context)

      When("a debt statement of liability is requested")
      // TODO: Helper 'aDebtStatementOfLiabilityIsRequested' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // aDebtStatementOfLiabilityIsRequested(context)

      Then("the sol response code should be 400")
      // TODO: Helper 'theSolResponseCodeShouldBeInt' expects context 'StatementOfLiabilityContext' but this spec uses 'FCStatementOfLiabilityContext'.
      // Validate whether this scenario should use a different context or whether the helper should be aligned to this spec context.
      // theSolResponseCodeShouldBeInt(context)

      And("the sol service will respond with {reason:Could not parse body due to requirement failed: Debts which are mandatory, are missing,message:Invalid Json}")
      // TODO: No matching helper method found for this step. Validate and call the correct helper.

    }
  }
}
