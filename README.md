# debt-transformation-acceptance-tests

### Source code formatting
We use [Scalafmt](https://scalameta.org/scalafmt/) to format our code base.

In case of contribution and you are an IntelliJ user, you should install the [scalafmt plugin](https://plugins.jetbrains.com/plugin/8236-scalafmt), select Scalafmt as **Formatter** and flag the checkbox "**Reformat on file save**" (_Settings -> Editor -> Code Style -> Scala).
You can format your code by using the _alt+shift+L_ or _option+command+L_ shortcut

Format at project level under src/test
```
sbt test:scalafmt
```

Formatting is also taken care as part of pre-commit hooks by running 
```
git commit
```  
 To run Interesting forecasting api tests against localhost, use the following command:
```
sm --start DTD_ALL
```
```
./run_interesting_forecasting_api_tests.sh
```

To run statement of liability api tests against localhost, use the following command:
```
sm --start DTD_ALL
```
```
run_statement_of_liability_api_tests.sh
```
To run zap tests for any of the services, download from https://www.zaproxy.org/download/, extract and execute the following in the root of the extracted folder:
```
./zap.sh -daemon -config api.disablekey=true -port 11000
```
and run 
```
./run_{service_name}_api_zap_tests.sh
