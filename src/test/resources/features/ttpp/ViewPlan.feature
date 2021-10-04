Feature: TTP View Plan Request

  Scenario: Retrieve existing plan from Time to Pay Proxy
    Given a view plan request
      | customerReference | planId     |
      | customerRef1234   | planId1234 |

    When the view plan request is sent to the ttpp service

    Then the ttp service is going to return a view response with
      | customerReference | channelIdentifier |
      | customerRef1234   | selfService       |

    And the plan will contain
      | planId     | quoteId | quoteDate  | quoteType        | paymentPlanType | thirdPartyBank | numberOfInstalments | totalDebtIncInt | totalInterest | interestAccrued | planInterest |
      | planId1234 | quoteId | 2021-09-28 | instalmentAmount | timeToPay       | true           | 11                  | 1248.29         | 48.29         | 36.71           | 11.58         |

    And the 1st view response instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-06-01 | 100       | 100             | 2.6          | 1                | 1.85                      | 1000              |

    And the 2nd view response instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-07-01 | 100       | 100             | 2.6          | 2                | 1.92                      | 900               |