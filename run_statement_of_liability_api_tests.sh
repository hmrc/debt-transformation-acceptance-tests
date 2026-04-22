#!/usr/bin/env bash

environment="local"

cucumberTags="not @wip and not @ignore"
scalaTestTags="\"uk.gov.hmrc.tags.WIP uk.gov.hmrc.tags.IGNORE\""

if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  cucumberTags="not @wip and not @ignore"
  scalaTestTags="\"uk.gov.hmrc.tags.WIP uk.gov.hmrc.tags.IGNORE\""
fi

echo "*** running on $environment for cucumber tags '$cucumberTags' ***"

scala_exit=0
cucumber_exit=0
scala_test_report_dir="target/scalatest-reports"

printf "\n\n\n\n*****************STARTING SCALATEST TESTS*****************\n\n"
printf "NOTE: ScalaTest test report is available in $scala_test_report_dir\n\n"
sbt -Denvironment="$environment" clean \
  "testOnly uk.gov.hmrc.test.api.scalatest.specs.* -- -l $scalaTestTags -u $scala_test_report_dir" \
  || scala_exit=$?

printf "\n\n\n\n*****************STARTING CUCUMBER TESTS*****************\n\n"
sbt -Denvironment="$environment" \
  -Dcucumber.options="--tags '$cucumberTags'" \
  'testOnly uk.gov.hmrc.test.api.cucumber.runner.StatementOfLiabilityApiTestRunner' \
  || cucumber_exit=$?

scala_result=$([ $scala_exit -eq 0 ] && echo "PASSED" || echo "FAILED")
cucumber_result=$([ $cucumber_exit -eq 0 ] && echo "PASSED" || echo "FAILED")
printf "\n\n\n\nScalaTest Tests: $scala_result | Cucumber Tests: $cucumber_result\n\n"

if [ $scala_exit -ne 0 ] || [ $cucumber_exit -ne 0 ]; then
  exit 1
fi