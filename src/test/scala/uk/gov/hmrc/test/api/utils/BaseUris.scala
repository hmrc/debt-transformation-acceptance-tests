package uk.gov.hmrc.test.api.utils

import uk.gov.hmrc.test.api.conf.TestConfiguration

trait BaseUris {
  val statementOfLiabilityApiUrl: String =
    TestConfiguration.url("statement-of-liability")
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
