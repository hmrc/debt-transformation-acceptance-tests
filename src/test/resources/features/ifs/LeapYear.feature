Feature: Leap years

  Scenario: 2.Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2019-12-16        | 2020-05-05          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2020-05-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 28                   | 5933                 | 400000            | 405933         | 400000              |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 280                  | 28                      | 5933                 | 400000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 8                       | 133               | 100000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 8                       | 790               | 100000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 7                       | 60                | 100000               |
      | 2020-04-07 | 2020-05-03 | 27           | 2.6          | 7                       | 191               | 100000               |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 35                      | 534               | 400000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 35                      | 3161              | 400000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 30                      | 240               | 400000               |
      | 2020-04-07 | 2020-05-05 | 29           | 2.6          | 28                      | 824               | 400000               |
