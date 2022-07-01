Feature: Interest Rate Changes
# originalAmount (Amount) = 500,000
# dateCreated (Date Amount) = 01/01/2020
# mainTrans (Regime = DRIER) = 1525, 1545
# subTrans (CHARGE_TYPE == Duty Repaid in Error - National Insurance) = 1000, 1090
# interestStartDate (Interest start date = Date Amount) 01/02/2020
# interestRateDate (New data item) = 07/04/2020
# Interest requested to (add in old value) 31/03/2019
# No repayments
# No suppression
# No breathing space
  Scenario: Interest rate changes from 3% to 3.25%
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2017-12-01        | 2019-03-31          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 44                   | 20695                | 500000            | 520695         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 44                      | 20695                | 500000           | 520695             | 485                  | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2017-12-01 | 2018-08-20 | 262          | 3.0          | 41                      | 10767             | 500000               | 510767             |
      | 2018-08-21 | 2019-03-31 | 223          | 3.25         | 44                      | 9928              | 500000               | 509928             |

  Scenario: Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2019-01-01        | 2020-03-31          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2020-02-01  |
      | 100000        | 2020-02-01  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 22                   | 19188                | 300000            | 319188         | 300000              |
    And the 1st debt summary will contain
      | numberChargeableDays |
      | 851                  |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 17                      | 6482              | 206482             | 200000               |
      | 2020-01-01 | 2020-02-01 | 32           | 3.25         | 17                      | 568               | 200568             | 200000               |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 26                      | 9723              | 309723             | 300000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 26                      | 2370              | 302370             | 300000               |
      | 2020-03-30 | 2020-03-31 | 2            | 2.75         | 22                      | 45                | 300045             | 300000               |

  Scenario: Interest rate changes from 3% to 3.25%
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2017-12-01        | 2019-03-31          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal |
      | 44                   | 20695                | 500000            | 520695         |
    And the 1st debt summary will contain
      | interestDueDailyAccrual | totalAmountIntDuty | numberChargeableDays |
      | 44                      | 520695             | 485                  |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2017-12-01 | 2018-08-20 | 262          | 3.0          | 41                      | 10767             | 500000               |
      | 2018-08-21 | 2019-03-31 | 223          | 3.25         | 44                      | 9928              | 500000               |

#  Scenario: Interest rate changes from non-interest bearing to interest bearing
  #Scenario: Interest rate changes from interest bearing to non-interest bearing
  #TBD: No test data currently available to implement this scenario
  Scenario: Interest rate changes from 3% to 3.25% after a payment is made
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-01-01        | 2019-03-31          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2018-03-15  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 16136                | 400000            |
    And the 1st debt summary will contain
      | numberChargeableDays |
      | 527                  |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | amountOnIntDueWindow |
      | 2018-01-01 | 2018-03-15 | 3.0          | 8                       | 100000               |
      | 2018-01-01 | 2018-08-20 | 3.0          | 32                      | 400000               |
      | 2018-08-21 | 2019-03-31 | 3.25         | 35                      | 400000               |

  Scenario: Interest rate changes from 3% to 3.25% with 2 payments on same date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-01-01        | 2019-03-31          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2018-09-01  |
      | 100000        | 2018-09-01  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal |
      | 26                   | 15661                | 300000            | 315661         |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueWindow |
      | 2018-01-01 | 2018-08-20 | 3.0          | 3797              |
      | 2018-08-21 | 2018-09-01 | 3.25         | 213               |
      | 2018-01-01 | 2018-08-20 | 3.0          | 5695              |
      | 2018-08-21 | 2019-03-31 | 3.25         | 5956              |

  Scenario: 2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-01-01        | 2019-03-31          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-03-15  |
      | 100000        | 2019-04-15  |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-01-16  | 2018-01-16        | 2019-04-14          | 1545      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-01-20  |
      | 100000        | 2019-03-10  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | amountOnIntDueTotal |
      | 52                   | 37775                | 637775         | 600000              |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | amountOnIntDueDuty |
      | 1361                 | 26                      | 19409                | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow |
      | 2018-01-01 | 2018-08-20 | 3.0          | 8                       | 1898              |
      | 2018-08-21 | 2019-04-15 | 3.25         | 8                       | 2119              |
      | 2018-01-01 | 2018-08-20 | 3.0          | 8                       | 1898              |
      | 2018-08-21 | 2019-03-15 | 3.25         | 8                       | 1843              |
      | 2018-01-01 | 2018-08-20 | 3.0          | 24                      | 5695              |
      | 2018-08-21 | 2019-03-31 | 3.25         | 26                      | 5956              |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | amountOnIntDueDuty |
      | 1240                 | 26                      | 18366                | 300000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow |
      | 2018-01-16 | 2018-08-20 | 3.0          | 8                       | 1775              |
      | 2018-08-21 | 2019-03-10 | 3.25         | 8                       | 1798              |
      | 2018-01-16 | 2018-08-20 | 3.0          | 8                       | 1775              |
      | 2018-08-21 | 2019-01-20 | 3.25         | 8                       | 1362              |
      | 2018-01-16 | 2018-08-20 | 3.0          | 24                      | 5326              |
      | 2018-08-21 | 2019-04-14 | 3.25         | 26                      | 6330              |


  Scenario: Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-04-10        | 2020-03-31          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 500000         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 0                       | 500000             |
    And the 1st debt summary will not have any calculation windows

  Scenario: Interest rate changes from 3% -> 3.25% -> 3.5
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-04-03        | 2023-04-05          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 47                   | 17420                | 500000            | 517448         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 47                      | 17420                | 500000           | 517448             | 367                  | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2022-04-03 | 2022-04-04 | 1            | 3.0          | 41                      | 41                | 500000               | 500041             |
      | 2022-04-05 | 2022-05-15 | 41           | 3.25         | 44                      | 1825              | 500000               | 501825             |
      | 2022-05-16 | 2023-04-05 | 325          | 3.5          | 47                      | 15582             | 500000               | 515582             |