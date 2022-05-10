Feature: fc statement of liability multiple debts


  Scenario: 1. FC Sol request with multiple debt ID's and multiple payments.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          | 2020-05-13        | 2021-08-01          | Y                 | 2020-05-13 | Debt1          |
      | duty02 | 10000          | 2020-05-13        | 2021-08-01          | Y                 | 2020-05-13 | Debt1          |
    And the debt item has fc sol payment history
      | paymentAmount | paymentDate |
      | 300           | 2021-04-06  |
      | 100           | 2021-05-06  |
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 19200          | 0                    |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | duty01 | 0                    | 9910               |
      | duty02 | 0                    | 9910               |

  Scenario: 2. FC Sol request with Single debt ID's and single payments.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          | 2020-05-13        | 2021-08-01          | Y                 | 2020-05-13 | Debt1          |
    And the debt item has fc sol payment history
      | paymentAmount | paymentDate |
      | 300           | 2021-04-06  |
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 9700           | 0                    |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | duty01 | 0                    | 10012              |

  Scenario: 3. FC Sol request with Single debt ID's and single payments with Interest Indicator as N.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          | 2020-05-13        | 2021-08-01          | N                 | 2020-05-13 | Debt1          |
    And the debt item has fc sol payment history
      | paymentAmount | paymentDate |
      | 300           | 2021-04-06  |
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 9700           | 0                    |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | duty01 | 0                    | 9700               |


  Scenario: 4. FC Sol request with Single debt ID's and no payments.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          | 2020-05-13        | 2021-08-01          | Y                 | 2020-05-13 | Debt1          |
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 10000          | 0                    |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | duty01 | 0                    | 10315              |

  Scenario: 5. FC Sol request with invalid or empty original amount.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          |                   | 2021-08-01          | Y                 | 2020-05-13 | Debt1          |
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then the fc sol service will respond with Invalid Json

  Scenario: 6. FC Sol request with no debt items.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has no debts
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then the fc sol service will respond with Invalid Json

  Scenario: 7. FC Sol request with missing Interest Indicator.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-05-13       |
    And the fc sol debt item has multiple debts
      | debtId | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | duty01 | 10000          | 2020-05-13        | 2021-08-01          |                  | 2020-05-13 | Debt1          |
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then the fc sol service will respond with Field at path '/debts(0)/interestIndicator' missing or invalid


  Scenario: 4. Large Interest bearing debt with no payments.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-08-01       |
    And the fc sol debt item has multiple debts
      | debtId         | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | XS002610170037 | 9999999999     | 2021-08-01        | 2021-08-01          | Y                 | 2021-08-01 | Debt1          |
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 9999999999     | 712328               |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | XS002610170037 | 712328               | 9999999999         |


  Scenario: 4. Large Non Interest bearing debt with no payments.

    Given fc sol request
      | customerUniqueRef | solRequestedDate |
      | NEHA1234          | 2021-08-01       |
    And the fc sol debt item has multiple debts
      | debtId         | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | solDescription |
      | XS002610170037 | 9999999999     | 2021-08-01        | 2021-08-01          | N                 | 2021-08-01 | Debt1          |
    And the fc sol debt item has no payment history
    When a debt fc statement of liability is requested
    Then service returns fc debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 9999999999     | 0                    |
    And the 1st multiple fc statement of liability debt summary will contain duties
      | debtId | interestDueDebtTotal | totalAmountIntDebt |
      | XS002610170037 | 0                    | 9999999999         |


