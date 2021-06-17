Feature: Create Plan test

  Scenario: Retrieve create plan response from Time to Pay Proxy
    Given a create plan request
      | customerReference | planId     | paymentMethod | paymentReference | thirdPartyBank | numberOfInstalments | totalDebtAmount | totalInterest |
      | customerRef1234   | planId1234 | BACS          | paymentRef1234   | false          | 2                   | 1000            | 0.26          |

    And instalments are
      | dutyId | debtId | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber |
      | duty01 | debt01 | 2021-05-01 | 100       | 100             | 0.26         | 1                |
      | duty01 | debt01 | 2021-05-01 | 100       | 100             | 0.26         | 2                |

    When the create plan request is sent to the ttpp service

    Then the ttp service is going to return a create plan response with
      | customerReference | planId     | planStatus |
      | customerRef1234   | planId1234   | xyz        |
