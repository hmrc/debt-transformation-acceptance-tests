Feature: Instalment calculation for single debt - Input 2

  Scenario: Should calculate debts amount for 1 debt 1 duty (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType |
      | 24       | monthly          | 1                    | 0                    | 1           | duration  |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 100000     | 1545      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | daysAfterToday | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 1              | monthly          | 0               | 4270      | 100000            | 2.6          | 24                          |

# DTD-397 Edge-cases below

  Scenario: Should return an error from IFS if duration is not provided and quote type is duration
    Given debt instalment calculation with details
      | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType |
      | monthly          | 1                    | 0                    | 1           | duration  |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 100000     | 1545      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"For a quote type of `duration`, the duration field is required"}

  Scenario: Should return an error from IFS if quote type is instalment amount but duration is provided
    Given debt instalment calculation with details
      | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType        | duration |
      | monthly          | 1                    | 0                    | 1           | instalmentAmount | 24       |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 100000     | 1545      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"For a quote type of `instalmentAmount`, the instalmentPaymentAmount is required"}