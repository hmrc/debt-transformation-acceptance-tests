Feature: Generate Quote test

  Scenario: Retrieve generate quote response from Time to Pay Proxy
    Given an update quote request
      | customerReference | planId  | updateType | cancellationReason | paymentMethod | paymentReference | thirdPartyBank |
      | customerRef1234   | some id | some type  | some reason        | CC            | some reference   | false          |

    When the update quote request is sent to the ttpp service

    Then the ttp service is going to return an update response with
      | customerReference | planId         | quoteStatus | quoteUpdatedDate |
      | customerRef1234   | pegaPlanId1234 | updated     | 2021-05-13       |