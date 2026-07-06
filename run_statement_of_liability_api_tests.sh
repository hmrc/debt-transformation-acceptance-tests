#!/usr/bin/env bash

environment="local"

scalaTestTags="\"WIP IGNORE\""

if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  scalaTestTags="\"WIP IGNORE\""
fi

scala_exit=0

echo "*** running on $environment for scala tags '$scalaTestTags' ***"
sbt -Denvironment="$environment" clean \
  "testOnly uk.gov.hmrc.test.api.scalatest.specs.sol.* -- -l $scalaTestTags" \
  || scala_exit=$?

if [ $scala_exit -ne 0 ]; then
  exit 1
fi