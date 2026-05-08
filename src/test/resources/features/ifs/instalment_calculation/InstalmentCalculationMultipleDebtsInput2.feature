Feature: Instalment calculation for multiple debts - Input 2

  Scenario: Should calculate instalment amount for multiple debts no initial payment debt 1 (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  |
      | 24       | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | Debt1  | 100000     | 1525      | 1000     |
      | Debt2  | 150000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 2020-03-14 | monthly          | 10680     | 100000            | 3.25         | 25                          |

  Scenario: calculate instalment amount -On day of interest rate change
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  |
      | 2        | monthly          | 2022-11-23            | 0                    | 1           | instalmentAmount | 2022-11-22 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | Debt1  | 100000     | 1525      | 1000     |
      | Debt2  | 150000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | paymentFrequency | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | monthly          | 100256    | 100000            | 5.5          | 3                           |


  Scenario: calculate instalment amount -Day before interest rate change
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate |
      | 2        | monthly          | 2022-11-20            | 0                    | 1           | instalmentAmount | 2022-11-21 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | Debt1  | 100000     | 1525      | 1000     |
      | Debt2  | 150000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | paymentFrequency | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | monthly          | 100224    | 100000            | 4.75         | 3                           |