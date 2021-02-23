**This is a template README.md.  Be sure to update this with project specific content that describes your api test project.**

# debt-transformation-acceptance-tests
API test suite for the `<digital service name>` using Cucumber and [play-ws](https://github.com/playframework/play-ws) client.  

## Running the tests

Prior to executing the tests ensure you have:
 - Installed [MongoDB](https://docs.mongodb.com/manual/installation/) 
 - Installed/configured [service manager](https://github.com/hmrc/service-manager).  

Run the following command to start services locally:

    sudo mongod
    sm --start DIRECT_DEBIT_STUBS -r --wait 100

Using the `--wait 100` argument ensures a health check is run on all the services started as part of the profile. `100` refers to the given number of seconds to wait for services to pass health checks.    

Then execute the Cucumber tests:

    sbt clean test

The tests default to the `local` environment.  For a complete list of supported param values, see:
 - `src/test/resources/application.conf` for **environment** 

#### Running the tests against a test environment

To run the tests against an environment set the corresponding `host` environment property as specified under
 `<env>.host.services` in the [application.conf](/src/test/resources/application.conf). 

 ### Scalafmt
 This repository uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf).

 To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

 ```
 sbt scalafmtAll
 ```

 To check files have been formatted as expected execute:

 ```
 sbt scalafmtCheckAll scalafmtSbtCheck
 ```

[Visit the official Scalafmt documentation to view a complete list of tasks which can be run.](https://scalameta.org/scalafmt/docs/installation.html#task-keys)