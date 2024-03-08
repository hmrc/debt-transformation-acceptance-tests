Feature: Leap years

  Scenario: Debt ending in a leap year
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01        | 2020-04-01          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 37                   | 35727                | 500000            | 535727         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 821                  | 37                      | 35727                | 500000           | 535727             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               | 509493             |
      | 2018-08-21 | 2019-12-31 | 498          | 3.25         | 44                      | 22171             | 500000               | 522171             |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 44                      | 3951              | 500000               | 503951             |
      | 2020-03-30 | 2020-04-01 | 3            | 2.75         | 37                      | 112               | 500000               | 500112             |


  Scenario: Debt starting in a leap year
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-05-02        | 2021-05-01          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 12940                | 500000            |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 364                  | 35                      | 12940                | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2020-05-02 | 2020-12-31 | 243          | 2.6          | 35                      | 8631              | 500000               |
      | 2021-01-01 | 2021-05-01 | 121          | 2.6          | 35                      | 4309              | 500000               |

  Scenario: Debt crossing a leap year
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-01-01        | 2021-04-01          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 48710                | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 1186                 | 35                      | 48710                | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               |
      | 2018-08-21 | 2019-12-31 | 498          | 3.25         | 44                      | 22171             | 500000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 44                      | 3951              | 500000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 37                      | 300               | 500000               |
      | 2020-04-07 | 2020-12-31 | 269          | 2.6          | 35                      | 9554              | 500000               |
      | 2021-01-01 | 2021-04-01 | 91           | 2.6          | 35                      | 3241              | 500000               |

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

  Scenario: Debt spanning multiple leap years
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2011-01-01  | 2011-01-01        | 2017-02-22          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 37                   | 91506                | 500000            |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual |
      | 2244                 | 37                      |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2011-01-01 | 2011-12-31 | 364          | 3.0          | 41                      | 14958             | 500000               |
      | 2012-01-01 | 2012-12-31 | 366          | 3.0          | 40                      | 15000             | 500000               |
      | 2013-01-01 | 2015-12-31 | 1095         | 3.0          | 41                      | 45000             | 500000               |
      | 2016-01-01 | 2016-08-22 | 235          | 3.0          | 40                      | 9631              | 500000               |
      | 2016-08-23 | 2016-12-31 | 131          | 2.75         | 37                      | 4921              | 500000               |
      | 2017-01-01 | 2017-02-22 | 53           | 2.75         | 37                      | 1996              | 500000               |


