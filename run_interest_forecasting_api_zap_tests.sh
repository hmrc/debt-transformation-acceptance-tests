#!/usr/bin/env bash

set -e
environment="local"
scalaTestTags="\"WIP IGNORE\""
if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  scalaTestTags="\"WIP IGNORE\""
fi

if [ "$environment" != "local" ]; then
  echo "Only the local environment is supported (see README)."
  exit 1
fi

echo "*** running on $environment for scala tags '$scalaTestTags' ***"

sbt -Dsecurity.assessment="true" -Denvironment="$environment" clean "testOnly uk.gov.hmrc.test.api.scalatest.specs.ifs.* -- -l $scalaTestTags"
