# debt-transformation-acceptance-tests
API test suite for the `Debt Transformation` using ScalaTest and [play-ws](https://github.com/playframework/play-ws) client.

## Running the tests

Prior to executing the tests ensure you have:
- Installed [Docker Desktop](https://docs.docker.com/desktop/setup/install/mac-install/)
- Installed [MongoDB](https://docs.mongodb.com/manual/installation/)
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

Run the following commands to start services locally:

You will also need a local mongodb replicaset which can be run in a docker container, to support the services' persistence.
```
    ./start-mongodb-replicaset.sh
```

```
    sm2 --start DTD_ALL -r --wait 100
```

Using the `--wait 100` argument ensures a health check is run on all the services started as part of the profile. `100` refers to the given number of seconds to wait for services to pass health checks.

Then execute the `run_tests.sh` script:

To run all debt transformation acceptance test run the below `.sh` script

`run_interest_forecasting_api_tests.sh`
`run_statement_of_liability_api_tests.sh`

Available Endpoints

```
statementOfLiabilityApiUrl/fc-sol
interestForecostingApiUrl/fc-debt-calculation
interestForecostingApiUrl/fc-vat-debt-calculation
interestForecostingApiUrl/instalment-calculation
interestForecostingApiUrl/debt-calculation
interestForecostingApiUrl/debt-interest-type
statementOfLiabilityApiUrl/sol
interestForecostingApiUrl/test-only/suppressions/overrides
interestForecostingApiUrl/test-only/suppressions/old
interestForecostingApiUrl/test-only/suppression-rules/old
interestForecostingApiUrl/test-only/suppressions
```

The tests default to the `local` environment.  For a complete list of supported param values, see:
- `src/test/resources/application.conf` for **environment**

#### Running the tests against a test environment

To run the tests against an environment set the corresponding `host` environment property as specified under
`<env>.host.services` in the [application.conf](src/test/resources/application.conf).

### Scalafmt
This repository uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf).

To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

 ```
 sbt scalafmtAll scalafmtSbt
 ```

To check files have been formatted as expected execute:

 ```
 sbt scalafmtCheckAll scalafmtSbtCheck
 ```