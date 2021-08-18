/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.utils

import uk.gov.hmrc.test.api.conf.TestConfiguration

trait BaseUris {
  val statementOfLiabilityApiUrl: String =
    TestConfiguration.url("statement-of-liability")
  val timeToPayProxyApiUrl: String       =
    TestConfiguration.url("time-to-pay-proxy")
  val interestForecostingApiUrl: String  =
    TestConfiguration.url("interest-forecasting")
  val authLoginApiUri: String            =
    TestConfiguration.url("auth-login-api")
  val authUri: String                    =
    TestConfiguration.url("auth")
  val authLoginStubUrl: String           =
    TestConfiguration.url("auth-login-stub")
  val oauthFrontEndUrl: String           =
    TestConfiguration.url("oauth-frontend")
  val oauthUri: String                   =
    TestConfiguration.url("oauth")
  val enrolmentStoreStartUri: String     =
    TestConfiguration.url("enrolment-store-start")
  val apiSubscriptionFieldsUri: String   =
    TestConfiguration.url("api-subscription-fields")

}
