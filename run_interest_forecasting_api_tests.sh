#!/usr/bin/env bash

environment="local"

scalaTestTags="\"WIP IGNORE\""

if [ $# -gt 0 -a "$1" != "$environment" ];
then
  environment="$1"
  scalaTestTags="\"WIP IGNORE\""
fi

# DTD-216: Workaround to load the rule set
status_code=$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:9946/ping/ping)
if [[ "$status_code" -ne 200 ]] ; then
  echo "ping endpoint response error $status_code"
  exit 1
fi
sleep 2

scala_exit=0

echo "*** running on $environment for scala tags '$scalaTestTags' ***"
sbt -Denvironment="$environment" clean \
  "testOnly uk.gov.hmrc.test.api.scalatest.specs.ifs.* -- -l $scalaTestTags" \
  || scala_exit=$?

if [ $scala_exit -ne 0 ]; then
  exit 1
fi