Feature: Instalment calculation for multiple debts - Input 2

  Scenario: Should calculate instalment amount for multiple debts no initial payment debt 1 (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType        |
      | 24       | monthly          | 1                    | 0                    | 1           | instalmentAmount |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | Debt1  | 100000     | 1525      | 1000     |
      | Debt2  | 150000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | daysAfterToday | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 1              | monthly          | 0               | 10715     | 100000            | 3.0         | 25                          |

  Scenario: Should calculate instalment amount for multiple debts no initial payment debt 2 (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType        |
      | 24       | monthly          | 1                    | 0                    | 1           | instalmentAmount |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | Debt1  | 100000     | 1525      | 1000     |
      | Debt2  | 150000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | daysAfterToday | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 11               | 274            | monthly          | 0               | 4286      | 150000            | 3.0         | 25                          |
