#!/usr/bin/env bash

environment="qa"
tags="@smoke and not @ignore"
if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  tags="not @ignore"
fi

echo "*** running on $environment for tags '$tags' ***"

sbt -Denvironment="$environment" -Dcucumber.options="--tags '@smoke'" clean 'testOnly uk.gov.hmrc.test.api.cucumber.runner.StatementOfLiabilityApiPegaTestRunner'