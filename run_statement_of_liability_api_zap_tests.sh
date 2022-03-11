git checkout maingit checkout main#!/usr/bin/env bash

set -e

environment="local"
tags="not @smoke and not @ignore"
if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  tags="not @ignore"
fi

echo "*** running on $environment for tags '$tags' ***"

sbt -Dhttp.proxyHost=localhost -Dhttp.proxyPort=11000 -Denvironment="$environment" -Dcucumber.options="--tags '$tags'" clean "testOnly uk.gov.hmrc.test.api.cucumber.runner.StatementOfLiabilityApiTestRunner"