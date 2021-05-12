Feature: Leap years

  Scenario: Debt ending in a leap year
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01  | 2018-01-01        | 2020-04-01        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 37                   | 35601                | 500000            | 535601              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 818                  | 37                      | 35601                | 500000           | 535601             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               | 509493             |
      | 2018-08-21 | 2019-12-31 | 497          | 3.25         | 44                      | 22126             | 500000               | 522126             |
      | 2020-01-01 | 2020-03-29 | 88           | 3.25         | 44                      | 3907              | 500000               | 503907             |
      | 2020-03-30 | 2020-04-01 | 2            | 2.75         | 37                      | 75                | 500000               | 500075             |


  Scenario: Debt starting in a leap year
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-05-02  | 2020-05-02        | 2021-05-01        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 12904                | 500000            | 512904              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 363                  | 35                      | 12904                | 500000           | 512904             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-05-02 | 2020-12-31 | 243          | 2.6          | 35                      | 8631              | 500000               | 508631             |
      | 2021-01-01 | 2021-05-01 | 120          | 2.6          | 35                      | 4273              | 500000               | 504273             |

  Scenario: Debt crossing a leap year
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01  | 2018-01-01        | 2021-04-01        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 48512                | 500000            | 548512              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 1181                 | 35                      | 48512                | 500000           | 548512             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               | 509493             |
      | 2018-08-21 | 2019-12-31 | 497          | 3.25         | 44                      | 22126             | 500000               | 522126             |
      | 2020-01-01 | 2020-03-29 | 88           | 3.25         | 44                      | 3907              | 500000               | 503907             |
      | 2020-03-30 | 2020-04-06 | 7            | 2.75         | 37                      | 262               | 500000               | 500262             |
      | 2020-04-07 | 2020-12-31 | 268          | 2.6          | 35                      | 9519              | 500000               | 509519             |
      | 2021-01-01 | 2021-04-01 | 90           | 2.6          | 35                      | 3205              | 500000               | 503205             |

  Scenario: 2.Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2019-12-16  | 2019-12-16        | 2020-05-05        | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000     | 2020-05-03    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 28                   | 5814                 | 400000            | 405814              | 400000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 274                  | 28                      | 5814                 | 400000           | 405814             | 400000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 8                       | 133               | 100000               | 100133             |
      | 2020-01-01 | 2020-03-29 | 88           | 3.25         | 8                       | 781               | 100000               | 100781             |
      | 2020-03-30 | 2020-04-06 | 7            | 2.75         | 7                       | 52                | 100000               | 100052             |
      | 2020-04-07 | 2020-05-03 | 26           | 2.6          | 7                       | 184               | 100000               | 100184             |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 35                      | 534               | 400000               | 400534             |
      | 2020-01-01 | 2020-03-29 | 88           | 3.25         | 35                      | 3125              | 400000               | 403125             |
      | 2020-03-30 | 2020-04-06 | 7            | 2.75         | 30                      | 210               | 400000               | 400210             |
      | 2020-04-07 | 2020-05-05 | 28           | 2.6          | 28                      | 795               | 400000               | 400795             |

  Scenario: Debt spanning multiple leap years
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2011-01-01  | 2011-01-01        | 2017-02-22        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 37                   | 91282                | 500000            | 591282              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 2239         | 37                      | 91282                | 500000           | 591282             | 500000             |         false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2011-01-01 | 2011-12-31 | 364          | 3.0          | 41                      | 14958             | 500000               | 514958             |
      | 2012-01-01 | 2012-12-31 | 365          | 3.0          | 40                      | 14959             | 500000               | 514959             |
      | 2013-01-01 | 2015-12-31 | 1094         | 3.0          | 41                      | 44958             | 500000               | 544958             |
      | 2016-01-01 | 2016-08-15 | 227          | 3.0          | 40                      | 9303              | 500000               | 509303             |
      | 2016-08-16 | 2016-12-31 | 137          | 2.75         | 37                      | 5146              | 500000               | 505146             |
      | 2017-01-01 | 2017-02-22 | 52           | 2.75         | 37                      | 1958              | 500000               | 501958             |


