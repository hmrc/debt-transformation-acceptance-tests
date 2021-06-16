Feature: Generate Quote test

  Scenario: Retrieve generate quote response from Time to Pay Proxy
    Given an view plan request
      | customerReference | planId   |
      | customerRef1234   | pega1234 |

    When the view plan request is sent to the ttpp service

    Then the ttp service is going to return an view response with
      | customerReference | planId     | quoteType | paymentMethod | paymentReference | numberOfInstalments | totalDebtAmount | totalInterest |
      | customerRef1234   | pega1234   | xyz       | BACS          | paymentRef1234   | 2                   | 1000            | 0.26          |

    And the 1st view response instalment will contain
      | dutyId | debtId | dueDate    | amountDue | balance | interest | interestRate | instalmentNumber |
      | duty01 | debt01 | 2021-05-01 | 100       | 100     | 10.01    | 0.26        | 1                |
    And the 2nd view response instalment will contain
      | dutyId | debtId | dueDate    | amountDue | balance | interest | interestRate | instalmentNumber |
      | duty01 | debt01 | 2021-06-01 | 100       | 100     | 10.01    | 0.26         | 2                |
