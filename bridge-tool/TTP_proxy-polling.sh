#!/usr/bin/env bash

# DTD-446, DTD-448
# Script / Hack to pass requests from TTP proxy on External Test to the TTP service on QA, returning the responses back to ET.
# 1) Polls the GET /requests endpoint on ET TTP Proxy to find next request.
# 2) Calls the QA TTP service with the request from previous step.
# 3) Returns the response back to the ET TTP Proxy.
#
# If an error is found calling the QA TTP service, the error will be written to the ET response endpoint and processing of the request will end.
countRequests=0
lastProcessedTime;
set -x

QAttpProxyEndpoint="https://api.qa.tax.service.gov.uk"
ETttpProxyEndpoint="https://test-api.service.hmrc.gov.uk/individuals/time-to-pay-proxy/"


ETttpStubEndpointRequests=$ETttpProxyEndpoint"test-only/requests"
ETttpStubEndpointResponse=$ETttpProxyEndpoint"test-only/response"

ETTokenEndpoint="https://test-api.service.hmrc.gov.uk/oauth/token"

QATokenEndpoint="https://api.qa.tax.service.gov.uk/oauth/token"

et_token_body="client_secret=a3438df2-c78a-4926-92b5-bc50382e6d0c&client_id=IRSzWvRL34nBwQqhKVV8ACzchThp&grant_type=client_credentials&scope=read:time-to-pay-proxy"

curl -s -o et_token_response.txt -w %{http_code} --request POST --header "content-type: application/x-www-form-urlencoded" --data $et_token_body $ETTokenEndpoint

et_token_json=$(<et_token_response.txt)
et_token=$(echo $et_token_json | cut -d'"' -f 4)
echo "*** et token json is $et_token_json"
echo "*** et token is $et_token"

# Get token for qa
qa_token_body="client_secret=6c2fc716-b9c6-4bb8-a57e-4908d32b9b27&client_id=reRg5ZSks9hGLpzxS5RRnYHjHYtW&grant_type=client_credentials&expires_in=500000&scope=read:debt-management-api"
curl -s -o qa_token_response.txt -w %{http_code} --request POST --header "content-type: application/x-www-form-urlencoded" --data $qa_token_body $QATokenEndpoint

qa_token_json=$(<qa_token_response.txt)
qa_token=$(echo $qa_token_json | cut -d'"' -f 4)
echo "*** qa token json is $qa_token_json"
echo "*** qa token is $qa_token"

for (( ; ; )); do
  NOW=`date '+%F_%H:%M'`;
  echo "Current time is:" $NOW
  echo "Last time a request was seen was:" $lastProcessedTime
  echo "Count of requests found is:" $countRequests
  sleep 2
  # ******* 1) Poll the GET /requests endpoint on ET TTP Proxy to find next request. *******
  echo "******* START 1) Polling the GET /requests endpoint on ET TTP Proxy to find next request.******* "
  echo "********* calling external test ET proxy endpoint ${ETttpStubEndpointRequests} to check for requests *********"
  echo "********* et token is $et_token ********"

  et_header_token="Authorization: Bearer $et_token"
  curl -s -o -w %{http_code} $ETttpStubEndpointRequests --header "$et_header_token" | jq --raw-output "." | jq "." | sed -e 's/\\\"/\"/g' >et_response.txt

  body=$(<et_response.txt)

  if [[ "$body" != *"requestId"* ]]; then
    echo "No requests found on TTP proxy on ET $ETttpStubEndpointRequests Exiting !!!"
    # end processing of request
    continue
  else
    countRequests=$((countRequests + 1))
    echo "Request found! Count of requests found is:" $countRequests
    lastProcessedTime=`date '+%F_%H:%M'`;
    #    No Error found. Set params then continue to next step to call QA
    requestId="$(echo ${body} | sed 's/\[.*requestId\": \"\(.*\)\", \"content.*/\1/')"
    content="$(echo ${body} | sed 's/\[.*content\": \"\(.*\)\", \"uri.*/\1/')"
    uri="$(echo ${body} | sed 's/\[.*uri\": \"\(.*\)\", \"isResponse.*/\1/')"
    QAuri="${QAttpProxyEndpoint}${uri}"

    echo "requestId variable is ^^^^^^^^" ${requestId}
    echo "content variable is ^^^^^^^^" ${content}
    echo "uri variable is ^^^^^^^^" ${uri}
    echo "QA uri variable is ^^^^^^^^" ${QAuri}
  fi

  # ********* 2) Call the QA TTP service with the request from previous step. **********
  echo "********* 2) Call the QA TTP Service with the request from previous step. **********"

  qa_header_token="Authorization: Bearer $qa_token"
  echo "*** qa header token is ${qa_header_token}"
  echo "******** calling QAttpProxyEndpoint ${QAuri} with body from content "${content}" ********"

  qa_status_code=$(curl -s -o qaResponse.txt -w %{http_code} --request POST -H "${qa_header_token}" -H "Content-Type: application/json" -d "${content}" ${QAuri})

  if [ "$qa_status_code" != 200 ]; then
    echo "${qa_status_code} Error received calling qa endpoint $QAuri Handling error and exiting !!!"
    echo ${content} > content.txt
    sed 's/\"/\\\"/g' qaResponse.txt > content_escaped.txt
    content_escaped=$(<content_escaped.txt)
    errors_body_json="{\"requestId\": \"${requestId}\", \"content\": \"${content_escaped}\", \"uri\": \"${uri}\",\"isResponse\": true, \"status\": ${qa_status_code}}"

    #    Write error to response endpoint with error status code
    et_error_db_status_code=$(curl -s -w %{http_code} ${ETttpStubEndpointResponse} --request POST -H "${et_header_token}" -H "Content-Type: application/json" --data "${errors_body_json}")

    if [ "${et_error_db_status_code}" != 200 ]; then
      echo "${et_error_db_status_code} Error when adding error to db 1 ETttpStubEndpointResponse Exiting !!!"
      echo "errors_body_json is... ${errors_body_json}"
      continue
    else
      echo "*** Written error to ET response endpoint ***"
    fi
    continue

  else
    #    No Error found. Continue to next step to return response back to ET
    qa_response=$(<qaResponse.txt)
    echo " *** Response returned from QA ttp endpoint is $qa_response"
    sed 's/\"/\\\"/g' qaResponse.txt | tr '\n' ' ' >qa_response_escaped.txt
    qa_response_escaped=$(<qa_response_escaped.txt)
    echo " *** Escaped response from QA ttp endpoint is ${qa_response_escaped}"
  fi

  # ******** 3) Return the response back to the ET TTP Proxy. ********
  echo "******** 3) Returning the response back to the ET TTP Proxy. *******"

  json_response_to_post_back_to_et="{\"requestId\": \"${requestId}\",\"content\": \"${qa_response_escaped}\",\"uri\": \"\",\"isResponse\": true}"
  echo "******** QA response content for sending to ET TTP Proxy is $qa_response ********"
  echo "******** calling ETttpStubEndpointResponse $ETttpStubEndpointResponse with body ${json_response_to_post_back_to_et} ********"

  et_status_code=$(curl -i -H "Content-Type: application/json" -H "$et_header_token" -o etPostResponse4.txt -w %{http_code} -X POST --data "${json_response_to_post_back_to_et}" ${ETttpStubEndpointResponse})

  if [ "$et_status_code" != 200 ]; then
    echo "$et_status_code Error received posting response back to ET endpoint $ETttpStubEndpointResponse Exiting !!!"
    continue
  else
    echo "******* Success! Response has been sent back to External Test *******"
  fi

done
