#!/usr/bin/env bash

environment="local"

scalaTestTags="\"WIP IGNORE\""

if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  scalaTestTags="\"WIP IGNORE\""
fi

scala_exit=0

printf "\n\n\n\n*****************STARTING SCALATEST TESTS*****************\n\n"
echo "*** running on $environment for scala tags '$scalaTestTags' ***"
sbt -Denvironment="$environment" clean \
  "testOnly uk.gov.hmrc.test.api.scalatest.specs.* -- -l $scalaTestTags" \
  || scala_exit=$?

scala_result=$([ $scala_exit -eq 0 ] && echo "PASSED" || echo "FAILED")
printf "\n\n\n\nScalaTest Tests: $scala_result"

if [ $scala_exit -ne 0 ]; then
  exit 1
fi