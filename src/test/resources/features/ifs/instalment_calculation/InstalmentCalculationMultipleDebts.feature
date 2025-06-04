Feature: Instalment calculation for multiple debts - Input 1 & 2
  #  Input 1
  Scenario: Should calculate quote for multiple debts with interest bearing & non-interest bearing debts combined
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType | quoteDate  |
      | 10000                   | monthly          | 2020-03-14            | 5900                 | 1           | duration  | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2020-03-14         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 80000      | 1525      | 1000     |
      # interest bearing debt
      | 12345  | 70000      | 1541      | 2000     |
      # non-interest bearing debt
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 9                | 2020-10-14 | monthly          | 7               | 100       | 70000             | 0.0          | 17                          |

# it shows only the Quote date instead of the interest start date
  @DTD-3163
  Scenario: interestStartDate is included but in the Future, then interestStartDate should be used
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType | quoteDate  |
      | 10000                   | monthly          | 2025-03-30            | 5900                 | 1           | duration  | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2025-03-14         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | interestStartDate |
      | 1234   | 80000      | 1525      | 1000     | DateInFuture      |
      # interest bearing debt
      | 12345  | 70000      | 1541      | 2000     | DateInFuture      |
      # non-interest bearing debt
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 9                | 2025-10-30 | monthly          | 7               | 9900      | 9900              | 6.5          | 20                          |

  Scenario: Should calculate quote for multiple debts both with interest bearing & 1 initial payment history
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType | quoteDate  |
      | 10000                   | monthly          | 2020-03-14            | 5900                 | 1           | duration  | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2020-03-14         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | 1234   | 80000      | 1525      | 1000     |
      # interest bearing debt
      | 12345  | 70000      | 1530      | 1000     |
      # interest bearing debt
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 2020-03-14 | monthly          | 0               | 10100     | 80000             | 3.25         | 17                          |

    #  Input 2
  Scenario: Should calculate debts amount for 2 debts with initial payment (input 2)
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  |
      | 24       | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2020-03-14         |
    And the instalment calculation has debt item charges
      | debtId     | debtAmount | mainTrans | subTrans |
      | TPSSDebt1  | 100000     | 1525      | 1000     |
      | DRIERDebt1 | 100000     | 1085      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 25                  | 24       | 0               | 1232         | 1232          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | paymentFrequency | frequencyPassed | amountDue | instalmentBalance | interestRate | expectedNumberOfInstalments |
      | 1                | 2020-03-14 | monthly          | 0               | 8480      | 100000            | 3.25         | 25                          |

  Scenario: Multiple debt item charges - duration should not include initial payment (initial payment date before instalment date)
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 6000                    | 2020-08-01            | monthly          | 1000                 | duration  | 2020-06-10 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2020-07-01         |
    And the instalment calculation has debt item charges
      | debtId     | debtAmount | mainTrans | subTrans |
      | TPSSDebt1  | 16000      | 1525      | 1000     |
      | DRIERDebt1 | 14000      | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 7                   | 5        | 1000            | 187          | 1187          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue |
      | 1                | 2020-07-01 | 5000      |
      | 2                | 2020-08-01 | 6000      |
      | 7                | 2020-12-01 | 2187      |

  @DTD-3163
  Scenario: interestStartDate is included but not in the Future, then quote date should be used
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 6000                    | 2025-06-30            | monthly          | 1000                 | duration  | 2025-05-31 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2025-06-01         |
    And the instalment calculation has debt item charges
      | debtId     | debtAmount | mainTrans | subTrans | interestStartDate |
      | TPSSDebt1  | 16000      | 1525      | 1000     | 2025-01-01        |
      | DRIERDebt1 | 14000      | 1525      | 1000     | 2025-03-01        |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 7                   | 5        | 1000            | 577          | 1577          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue |
      | 1                | 2025-06-01 | 5000      |
      | 2                | 2025-06-30 | 6000      |
      | 7                | 2025-10-30 | 2577      |

  Scenario: Multiple debt item charges - duration should not include initial payment (initial payment on instalment date)
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 6000                    | 2020-08-01            | monthly          | 1000                 | duration  | 2020-06-10 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2020-08-01         |
    And the instalment calculation has debt item charges
      | debtId     | debtAmount | mainTrans | subTrans |
      | TPSSDebt1  | 16000      | 1525      | 1000     |
      | DRIERDebt1 | 14000      | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 6                   | 5        | 1000            | 198          | 1198          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue |
      | 1                | 2020-08-01 | 11000     |
      | 2                | 2020-09-01 | 5000      |
      | 3                | 2020-09-01 | 1000      |
      | 4                | 2020-10-01 | 6000      |
      | 5                | 2020-11-01 | 6000      |
      | 6                | 2020-12-01 | 2198      |

  Scenario: Multiple Debts should be returned in the order they are sent in
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  |
      | 12       | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2020-03-14         |
    And the instalment calculation has debt item charges
      | debtId   | debtAmount | mainTrans | subTrans |
      | DebtId1  | 100000     | 1525      | 1000     |
      | DebtId2  | 200000     | 1085      | 1000     |
      | DebtId3  | 100000     | 1525      | 1000     |
      | DebtId4  | 70000      | 1541      | 2000     |
      | DebtId5  | 200000     | 1085      | 1000     |
      | DebtId6  | 6000       | 1085      | 1000     |
      | DebtId7  | 7000       | 1085      | 1000     |
      | DebtId7  | 8000       | 1085      | 1000     |
      | DebtId8  | 8000       | 1540      | 1000     |
      | DebtId9  | 9000       | 1085      | 1000     |
      | DebtId10 | 17000      | 1535      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | debtId   |
      | 1                | DebtId1  |
      | 3                | DebtId2  |
      | 8                | DebtId3  |
      | 10               | DebtId4  |
      | 12               | DebtId5  |
      | 17               | DebtId6  |
      | 18               | DebtId7  |
      | 19               | DebtId8  |
      | 20               | DebtId9  |
      | 21               | DebtId10 |

  @DTD-1874
  Scenario: Multiple Debts can be paid off within the same instalment period
    Given debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType        | quoteDate  |
      | 6        | monthly          | 2020-03-14            | 0                    | 1           | instalmentAmount | 2020-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2020-03-14         |
    And the instalment calculation has debt item charges
      | debtId  | debtAmount | mainTrans | subTrans |
      | DebtId1 | 100000     | 1525      | 1000     |
      | DebtId2 | 2000       | 2130      | 1355     |
      | DebtId3 | 1000       | 4766      | 1090     |
      | DebtId4 | 700        | 4745      | 1090     |
      | DebtId5 | 60000      | 4770      | 1090     |
      | DebtId6 | 30000      | 4700      | 1174     |

    When the instalment calculation detail is sent to the ifs service
    Then IFS response contains expected values
      | instalmentNumber | dueDate    | debtId  | interestRate | instalmentInterestAccrued |
      | 1                | 2020-03-14 | DebtId1 | 3.25         | 17                        |
      | 4                | 2020-06-14 | DebtId1 | 2.6          | 6                         |
      | 5                | 2020-06-14 | DebtId2 | 2.6          | 13                        |
      | 6                | 2020-06-14 | DebtId3 | 2.6          | 5                         |
      | 7                | 2020-06-14 | DebtId4 | 2.6          | 4                         |
      | 8                | 2020-06-14 | DebtId5 | 2.6          | 420                       |
      | 9                | 2020-07-14 | DebtId5 | 2.6          | 73                        |
      | 10               | 2020-08-14 | DebtId5 | 2.6          | 4                         |
      | 11               | 2020-08-14 | DebtId6 | 2.6          | 340                       |
