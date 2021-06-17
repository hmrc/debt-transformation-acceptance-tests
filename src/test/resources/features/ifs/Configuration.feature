Feature: Configuration

  Scenario: A Rule has been updated
    Given a new interest rate table
    When a rule has been updated
      | mainTrans | subTrans | intRate |
      | 5330      | 7006     |  22     |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2010-01-01  | 2011-02-01        | 2021-07-06          | 5330      | 7006     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    And the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 273                   | 145793                 | 500000            | 645793         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 3808                  | 273                      | 145793                | 500000           | 645793             | 500000             |

  Scenario: A new interest rate is added
    Given a new interest rate table
    When a new interest rate is added
      | interestRate | date       |
      | 30.0         | 2021-04-01 |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2010-01-01  | 2021-01-01        | 2021-07-06          | 5330      | 7006     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    And the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 410                   | 64246                 | 500000            | 564246         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 186                  | 410                      | 64246                | 500000           | 564246             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2021-01-01 | 2021-03-31 | 89           | 20.0          | 273                     | 24383             | 500000               | 524383             |
      | 2021-04-01 | 2021-07-06 | 97           | 30.0         | 410                     | 39863             | 500000               | 539863             |


