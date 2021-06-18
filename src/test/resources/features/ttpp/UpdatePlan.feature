Feature: Update Plan

  Scenario: Retrieve update plan response from Time to Pay Proxy
    Given an update plan request
      | customerReference | planId  | updateType | cancellationReason | paymentMethod | paymentReference | thirdPartyBank |
      | customerRef1234   | some id | some type  | some reason        | CC            | some reference   | false          |

    When the update plan request is sent to the ttpp service

    Then the ttp service is going to return an update response with
      | customerReference | planId     | quoteStatus | quoteUpdatedDate |
      | customerRef1234   | planId1234 | updated     | 2021-05-13       |
