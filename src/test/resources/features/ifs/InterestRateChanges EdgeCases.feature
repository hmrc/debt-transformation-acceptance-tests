# originalAmount (Amount) = 500,000
# dateCreated (Date Amount) = 01/01/2020
# mainTrans (Regime = DRIER) = 1525, 1545
# subTrans (CHARGE_TYPE == Duty Repaid in Error - National Insurance) = 1000, 1090
# interestStartDate (Interest start date = Date Amount) 01/02/2020
# interestRateDate (New data item) = 07/04/2020
# Interest requested to (add in old value) 31/03/2021
# No repayments
# No suppression
# No breathing space


# DTD-172 - Request interest for Drier case (interest rate changes)
Feature: Interest Rate Changes - Edge cases

  Scenario: 300 Debt items - Interest rate changes from 3.0% to 3.25%
    Given 300 debt items where interest rate changes from 3.0 to 3.25
    And no breathing spaces have been applied to the customer
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 13200                | 3782700              | 150000000         | 153782700      | 150000000           |
    And the 300th debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 301                  | 44                      | 12609                | 500000           |

  Scenario: 2 Debts - Interest rate changes from 3.25% to 2.75% - leap year - payment is made for 1 debt - Interest rate changes from 2.75% to 2.6%
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-06-01  | 2018-06-01        | 2021-03-31          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-03-15  |
      | 100000        | 2020-04-15  |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2009-01-01  | 2009-01-01        | 2010-01-01          | 1545      | 1090     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountOnIntDueTotal |
      | 62                   | 48293          | 800000              |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 1995                 | 21                      | 33823                | 300000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2018-06-01 | 2018-08-20 | 3.0          | 8                       | 657               | 100000               |
      | 2018-08-21 | 2019-12-31 | 3.25         | 8                       | 4425              | 100000               |
      | 2020-01-01 | 2020-03-29 | 3.25         | 8                       | 781               | 100000               |
      | 2020-03-30 | 2020-04-06 | 2.75         | 7                       | 52                | 100000               |
      | 2020-04-07 | 2020-04-15 | 2.6          | 7                       | 56                | 100000               |
      | 2018-06-01 | 2018-08-20 | 3.0          | 8                       | 657               | 100000               |
      | 2018-08-21 | 2019-03-15 | 3.25         | 8                       | 1834              | 100000               |
      | 2018-06-01 | 2018-08-20 | 3.0          | 24                      | 1972              | 300000               |
      | 2018-08-21 | 2019-12-31 | 3.25         | 26                      | 13276             | 300000               |
      | 2020-01-01 | 2020-03-29 | 3.25         | 26                      | 2344              | 300000               |
      | 2020-03-30 | 2020-04-06 | 2.75         | 22                      | 157               | 300000               |
      | 2020-04-07 | 2020-12-31 | 2.6          | 21                      | 5711              | 300000               |
      | 2021-01-01 | 2021-03-31 | 2.6          | 21                      | 1901              | 300000               |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 361                  | 41                      | 14470                | 500000           |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2009-01-01 | 2009-01-05 | 5.5          | 75                      | 301               | 500000               |
      | 2009-01-06 | 2009-01-26 | 4.5          | 61                      | 1232              | 500000               |
      | 2009-01-27 | 2009-03-23 | 3.5          | 47                      | 2636              | 500000               |
      | 2009-03-24 | 2009-09-28 | 2.5          | 34                      | 6438              | 500000               |
      | 2009-09-29 | 2010-01-01 | 3.0          | 41                      | 3863              | 500000               |

  Scenario:  Interest rate changes from 2.75% to 2.6% - leap year - payment is made for 1 debt on the same day the interest rate changes
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-01-01  | 2020-01-01        | 2021-03-31          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2020-04-07  |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 28                   | 14326                | 400000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty |
      | true            | 548                  | 28                      | 14326                | 400000           | 414326             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2020-01-01 | 2020-03-29 | 3.25         | 8                       | 781               | 100000               |
      | 2020-03-30 | 2020-04-07 | 2.75         | 7                       | 60                | 100000               |
      | 2020-01-01 | 2020-03-29 | 3.25         | 35                      | 3125              | 400000               |
      | 2020-03-30 | 2020-04-06 | 2.75         | 30                      | 210               | 400000               |
      | 2020-04-07 | 2020-12-31 | 2.6          | 28                      | 7615              | 400000               |
      | 2021-01-01 | 2021-03-31 | 2.6          | 28                      | 2535              | 400000               |


#  BUG: Test failing due to issue with interest accrued returned
#  Scenario: Interest rate changes from 0% to 8.5%
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
#      | 500000         | 2000-01-01  | 2000-01-01 | 2001-03-31        | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    When the debt item is sent to the ifs service
#    Then the ifs service wilL return a total debts summary of
#      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
#      | 0                    | 0                    | 500000            | 500000              | 500000              |
#    And the 1st debt summary will contain
#      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
#      | true            | 0                       | 0                    | 500000           | 500000             | 0                    | 500000             | false                 |
#    And the 1st debt summary will have calculation windows
#      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | amountOnIntDueWindow | interestDueWindow | unpaidAmountWindow |
#      | 2000-01-01 | 2000-02-05 | 0            | 0.0          | 0                       | 500000               | 0                 | 500000             |
#      | 2000-02-06 | 2001-03-31 | 418          | 8.5          | 13                      | 500904               | 780               | 500780             |

