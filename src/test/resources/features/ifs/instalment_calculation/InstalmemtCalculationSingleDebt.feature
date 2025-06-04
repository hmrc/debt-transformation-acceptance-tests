Feature: Instalment calculation for single debt - Input 2
  Scenario: Should calculate debts amount for 1 debt 1 duty (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 24       | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 | false                   |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 100000     | 1545      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 2020-03-14 | monthly          | 0               | 4271      | 100000            | 3.25         | 24                          |

#  DTD-1730
  Scenario:  Plan with isQuoteDateNonInclusive flag should not include quote date in interest accrued
    Given debt instalment calculation with details
      | duration | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 6        | 2023-04-20            | monthly          | 178                  | instalmentAmount | 2023-03-17 | true                    |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId    | debtAmount | mainTrans | subTrans |
      | TPSSDebt1 | 100000     | 1525      | 1000     |
    When the instalment calculation is sent to the ifs service with query parameters
      | combineLastInstalments |
      | false                  |
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 6                   | 6        | 178             | 1961         | 2139          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue | instalmentInterestAccrued |
      | 1                | 2023-04-20 | 17022     | 605                       |
      | 6                | 2023-09-20 | 17029     | 92                        |

#  DTD-1730
  Scenario: Plans with initial payment and isQuoteDateNonInclusive flag should not include quote date
    Given debt instalment calculation with details
      | duration | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 4        | 2023-05-20            | monthly          | 0                    | instalmentAmount | 2023-03-23 | true                    |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100000               | 2023-04-02         |
    And the instalment calculation has debt item charges
      | debtId    | debtAmount | mainTrans | subTrans |
      | TPSSDebt1 | 1425623    | 1525      | 1000     |
    When the instalment calculation is sent to the ifs service with query parameters
      | combineLastInstalments |
      | false                  |
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 5                   | 4        | 0               | 24727        | 24727         |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue | instalmentInterestAccrued |
      | 1                | 2023-04-02 | 100000    | 2538                      |
      | 5                | 2023-08-20 | 337592    | 1829                      |


  @DTD-3163
  Scenario: interestStartDate is included but not in the Future, then quote date should be used
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 24       | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 | false                   |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2020-03-13         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | interestStartDate |
      | 1234   | 100000     | 1545      | 1000     | 2025-01-14        |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | interestRate |
      | 1                | 2020-03-13 | 3.25         |

  @DTD-3163
  Scenario: interestStartDate is included but in the Future, then interestStartDate should be used
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 24       | monthly          | 2025-08-25            | 0                    | 1           | instalmentAmount | 2025-06-01 | false                   |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | interestStartDate |
      | 1234   | 100000     | 1545      | 1000     | DateInFuture      |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | interestRate |
      | 1                | 2025-08-25 | 6.5          |

  @DTD-3163
  Scenario: With initial payment - interestStartDate is included but in the Future, then interestStartDate should be used
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  | isQuoteDateNonInclusive |
      | 24       | monthly          | 2025-06-10            | 0                    | 1           | instalmentAmount | 2025-06-01 | false                   |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2025-06-01         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | interestStartDate |
      | 1234   | 100000     | 1545      | 1000     | DateInFuture      |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | interestRate |
      | 1                | 2025-06-01 | 6.5          |

# DTD-397 Edge-cases below
  Scenario: Should return an error from IFS if quote type is duration and duration is provided
    Given debt instalment calculation with details
      | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType | duration |
      | monthly          | 1                    | 0                    | 1           | duration  | 24       |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | interestStartDate |
      | 1234   | 100000     | 1545      | 1000     | 2020-03-14        |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Missing instalment amount","message":"For a quote type of `duration`, the instalmentPaymentAmount is required"}

  Scenario: Should return an error from IFS if quote type is instalment amount and instalment payment amount is provided
    Given debt instalment calculation with details
      | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay | quoteType        | instalmentPaymentAmount |
      | monthly          | 1                    | 0                    | 1           | instalmentAmount | 100000                  |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 100000     | 1545      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Missing duration","message":"For a quote type of `instalmentAmount`, the duration field is required"}