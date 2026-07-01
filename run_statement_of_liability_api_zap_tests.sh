#!/usr/bin/env bash

set -e

environment="local"
scalaTestTags="\"WIP IGNORE\""
if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
    scalaTestTags="\"WIP IGNORE\""
fi

echo "*** running on $environment for scala tags '$scalaTestTags' ***"

sbt -Dsecurity.assessment="true" -Denvironment="$environment" clean "testOnly uk.gov.hmrc.test.api.scalatest.specs.* -- -l $scalaTestTags"