Feature: FC Debt Calculation End point testing

  Scenario: 0. Interest Indicators. 2 debt. 1 payment history and cotax charge interest

    Given fc debt item with cotax charge interest
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | chargedInterest | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 200             | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | chargedInterest | debtId |
      | 300000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 100             | 456    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 61                   | 700000            | 8052                 | 708052              | 700000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 35                      | 4874                 | 400000           | 404874             | 400000             |


  Scenario: 1. Interest Indicators. 2 debt. 1 payment history
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 300000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 456    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 61                   | 700000            | 7852                 | 707852              | 700000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 35                      | 4674                 | 400000           | 404674             | 400000             |


  Scenario: 2. Interest Indicator. 1 Payment of 1 debt.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 400000            | 4674                 | 404674              | 400000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 35                      | 4674                 | 400000           | 404674             | 400000             |


  Scenario: 3. No Interest Indicator. 1 Payment of 1 debt.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | N                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 400000            | 0                    | 400000              | 400000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 0                       | 0                    | 400000           | 400000             | 400000             |
    And the 1st fc debt summary will not have any calculation windows


  Scenario: 4. Interest Indicator. 1 Payment of 1 debt. No breathing space.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 400000            | 4674                 | 404674              | 400000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 35                      | 4674                 | 400000           | 404674             | 400000             |


  Scenario: 5. 1 debt, no payment history
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-07-16        | 2019-04-16          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 44                   | 500000            | 12078                | 512078              | 500000              |
    And the 1st fc debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 0                    | 44                      | 12078                | 500000           | 512078             | 500000             |


  Scenario: 6. Interest Indicator. 1 Payment of 1 debt. Payment Done.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 500000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                 | 2181                 | 2181                | 0                   |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 0                       | 2181                 | 0                | 2181               | 0                  |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 44                      | 502181             |


  Scenario: 7. Total Payments cannot be 0.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 0             | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service will respond with Could not parse body due to requirement failed: Payment amount must not be zero

  Scenario: 8. Total Payments cannot be negative.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | -1000         | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service will respond with Could not parse body due to requirement failed: Payment amount must be positive


  Scenario: 9. Total Payment amounts cannot be more than the original amount.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-12-16        | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 555555        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service will respond with Could not parse body due to requirement failed: Total Payment amounts cannot be more than the original amount


  Scenario: 10. No InterestStartDate but InterestIndicator is Yes.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         |                   | 2019-04-14          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service will respond with Field at path '/debtItems(0)/interestStartDate' missing or invalid


  Scenario: FC Debt ending in a leap year
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-01        | 2020-04-01          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 37                   | 500000            | 35727                | 535727              | 500000              |
    And the 1st fc debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 821                  | 37                      | 35727                | 500000           | 535727             | 500000             |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               | 509493             |
      | 2018-08-21 | 2019-12-31 | 498          | 3.25         | 44                      | 22171             | 500000               | 522171             |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 44                      | 3951              | 500000               | 503951             |
      | 2020-03-30 | 2020-04-01 | 3            | 2.75         | 37                      | 112               | 500000               | 500112             |


  Scenario: FC Debt starting in a leap year
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2020-05-02        | 2021-05-01          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal |
      | 35                   | 500000            | 12940                |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 35                      | 12940                | 500000           |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2020-05-02 | 2020-12-31 | 243          | 2.6          | 35                      | 8631              | 500000               |
      | 2021-01-01 | 2021-05-01 | 121          | 2.6          | 35                      | 4309              | 500000               |


  Scenario: FC Debt crossing a leap year
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-01        | 2021-04-01          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 48710                | 500000            |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 35                      | 48710                | 500000           |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 41                      | 9493              | 500000               |
      | 2018-08-21 | 2019-12-31 | 498          | 3.25         | 44                      | 22171             | 500000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 44                      | 3951              | 500000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 37                      | 300               | 500000               |
      | 2020-04-07 | 2020-12-31 | 269          | 2.6          | 35                      | 9554              | 500000               |
      | 2021-01-01 | 2021-04-01 | 91           | 2.6          | 35                      | 3241              | 500000               |


  Scenario: FC Interest rate changes from 3.25%, 2.75% and 2.6% after a payment is made
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2019-12-16        | 2020-05-05          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2020-05-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 28                   | 5933                 | 400000            | 405933         | 400000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 28                      | 5933                 | 400000           |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 8                       | 133               | 100000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 8                       | 790               | 100000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 7                       | 60                | 100000               |
      | 2020-04-07 | 2020-05-03 | 27           | 2.6          | 7                       | 191               | 100000               |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 35                      | 534               | 400000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 35                      | 3161              | 400000               |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 30                      | 240               | 400000               |
      | 2020-04-07 | 2020-05-05 | 29           | 2.6          | 28                      | 824               | 400000               |


  Scenario: FC Debt spanning multiple leap years
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2011-01-01        | 2017-02-22          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 37                   | 91506                | 500000            |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual |
      | 37                      |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2011-01-01 | 2011-12-31 | 364          | 3.0          | 41                      | 14958             | 500000               |
      | 2012-01-01 | 2012-12-31 | 366          | 3.0          | 40                      | 15000             | 500000               |
      | 2013-01-01 | 2015-12-31 | 1095         | 3.0          | 41                      | 45000             | 500000               |
      | 2016-01-01 | 2016-08-22 | 235          | 3.0          | 40                      | 9631              | 500000               |
      | 2016-08-23 | 2016-12-31 | 131          | 2.75         | 37                      | 4921              | 500000               |
      | 2017-01-01 | 2017-02-22 | 53           | 2.75         | 37                      | 1996              | 500000               |


  Scenario: FC Interest rate changes from 3% to 3.25%
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2017-12-01        | 2019-03-31          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 44                   | 20695                | 500000            | 520695         | 500000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | 44                      | 20695                | 500000           | 520695             | 485                  | 500000             |                       |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2017-12-01 | 2018-08-20 | 262          | 3.0          | 41                      | 10767             | 500000               | 510767             |
      | 2018-08-21 | 2019-03-31 | 223          | 3.25         | 44                      | 9928              | 500000               | 509928             |


  Scenario: FC Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap year
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2019-01-01        | 2020-03-31          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2020-02-01  |
      | 100000        | 2020-02-01  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 22                   | 19188                | 300000            | 319188         | 300000              |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 17                      | 6482              | 206482             | 200000               |
      | 2020-01-01 | 2020-02-01 | 32           | 3.25         | 17                      | 568               | 200568             | 200000               |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 26                      | 9723              | 309723             | 300000               |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 26                      | 2370              | 302370             | 300000               |
      | 2020-03-30 | 2020-03-31 | 2            | 2.75         | 22                      | 45                | 300045             | 300000               |


  Scenario: FC Interest rate changes from 3% to 3.25% after a payment is made
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-01        | 2019-03-31          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2018-03-15  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 16136                | 400000            |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | amountOnIntDueWindow |
      | 2018-01-01 | 2018-03-15 | 3.0          | 8                       | 100000               |
      | 2018-01-01 | 2018-08-20 | 3.0          | 32                      | 400000               |
      | 2018-08-21 | 2019-03-31 | 3.25         | 35                      | 400000               |


  Scenario: FC Interest rate changes from 3% to 3.25% with 2 payments on same date
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-01        | 2019-03-31          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2018-09-01  |
      | 100000        | 2018-09-01  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal |
      | 26                   | 15661                | 300000            | 315661         |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueWindow |
      | 2018-01-01 | 2018-08-20 | 3.0          | 3797              |
      | 2018-08-21 | 2018-09-01 | 3.25         | 213               |
      | 2018-01-01 | 2018-08-20 | 3.0          | 5695              |
      | 2018-08-21 | 2019-03-31 | 3.25         | 5956              |


  Scenario: FC 2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-01        | 2019-03-31          | Y                 | 2018-04-06 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-03-15  |
      | 100000        | 2019-04-15  |
    And a fc debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2018-01-16  | 2018-01-16        | 2019-04-14          | Y                 | 2018-04-06 | 456    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-01-20  |
      | 100000        | 2019-03-10  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | amountOnIntDueTotal |
      | 52                   | 37775                | 637775         | 600000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | amountOnIntDueDuty |
      | 26                      | 19409                | 300000             |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow |
      | 2018-01-01 | 2018-08-20 | 3.0          | 8                       | 1898              |
      | 2018-08-21 | 2019-03-15 | 3.25         | 8                       | 1843              |
      | 2018-01-01 | 2018-08-20 | 3.0          | 8                       | 1898              |
      | 2018-08-21 | 2019-04-15 | 3.25         | 8                       | 2119              |
      | 2018-01-01 | 2018-08-20 | 3.0          | 24                      | 5695              |
      | 2018-08-21 | 2019-03-31 | 3.25         | 26                      | 5956              |
    And the 2nd fc debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | amountOnIntDueDuty |
      | 1240                 | 26                      | 18366                | 300000             |
    And the 2nd fc debt summary will have calculation windows
      | periodFrom | periodTo   | interestRate | interestDueDailyAccrual | interestDueWindow |
      | 2018-01-16 | 2018-08-20 | 3.0          | 8                       | 1775              |
      | 2018-08-21 | 2019-01-20 | 3.25         | 8                       | 1362              |
      | 2018-01-16 | 2018-08-20 | 3.0          | 8                       | 1775              |
      | 2018-08-21 | 2019-03-10 | 3.25         | 8                       | 1798              |
      | 2018-01-16 | 2018-08-20 | 3.0          | 24                      | 5326              |
      | 2018-08-21 | 2019-04-14 | 3.25         | 26                      | 6330              |


  Scenario: FC Interest rate changes from 2.75% to 2.6% - interestRequestedTo before interestStartDate
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2020-04-10        | 2020-03-31          | Y                 | 2018-04-06 | 123    |
    And the fc debt item has no payment history
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 500000         | 500000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | totalAmountIntDuty |
      | 0                       | 500000             |
    And the 1st fc debt summary will not have any calculation windows


  Scenario: periodEnd and interestStartDate is missing or invalid.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd | debtId |
      | 500000         |                   | 2019-04-14          | Y                 |           | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc debt item
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service will respond with Field at path '/debtItems(0)/periodEnd' missing or invalid\nField at path '/debtItems(0)/interestStartDate' missing or invalid