#  Assumptions
#  Initial originalAmount and date
#  MainTrans == 1525 (TPSS)
#  NO suppression period
#  NO breathing space
#  Date Amount  == Interest start date


# DTD-171. Multiple Debt Items
#1. 1 payment of 1 debt no interest
#2. 1 payment of 1 debt with interest
#3. 2 payments of 1 debt with interest
#4. 2 debts, 1 debt with a payment, the second debt with no payment
Feature: Multiple Debt Items
  Scenario: 1. Non Interest Bearing. 1 Payment of 1 debt.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1520      | 1090     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 400000            | 400000         | 400000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | false           | 0                    | 0                       | 0                    | 400000           | 400000             | 400000             |
    And the 1st debt summary will not have any calculation windows


  Scenario: 2. Interest Bearing. 1 Payment of 1 debt.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 4674                 | 400000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 168                  | 35                      | 4674                 | 400000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               |

  Scenario: 3. Interest Bearing. 2 Payments of 1 debt.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-23  |
      | 100000        | 2019-03-05  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal |
      | 26                   | 4495                 | 300000            | 304495         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 267                  | 26                      | 4495                 | 300000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow |
      | 2018-12-16 | 2019-03-05 | 79           | 3.25         | 8                       | 703               | 100000               |
      | 2018-12-16 | 2019-02-23 | 69           | 3.25         | 8                       | 614               | 100000               |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 26                      | 3178              | 300000               |


  Scenario: 4. Interest Bearing. 2 debts. 1 debt with payment the second debt with no payment.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 79                   | 909971         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 168                  | 35                      | 404674             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 404238             |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 119                  | 44                      | 505297             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |


  Scenario: 5. 1 debt, no payment interest requested to date is before the interest start date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2023-03-03        | 2022-02-02          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 1000000        | 1000000             |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 0                       | 1000000            |
    And the 1st debt summary will not have any calculation windows

  Scenario: 6. 1 debt, no payment interest requested to date is same as interest start date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2022-02-02        | 2022-02-02          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 75                   | 1000000        | 1000000             |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 75                      | 1000000            |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-02-02 | 2022-02-02 | 0            | 2.75         | 75                      | 1000000            | false                 |


  Scenario: 7. 1 debt, 1 payment interest requested to date is before the interest start date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2023-03-03        | 2022-02-02          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-01-01  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 900000         | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 0                       | 900000             |
    And the 1st debt summary will not have any calculation windows

  Scenario: 8. 1 debt, non interest bearing, interest requested to date is before the interest start date, no dates are required
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2023-03-03        | 2022-02-02          | 1520      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-01-01  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 900000         | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | false           | 0                    | 0                       | 900000             |
    And the 1st debt summary will not have any calculation windows


  Scenario: 2. Interest Bearing. 1 Payment of 1 debt - eligible for Breathing Space protections
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-01-23  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2019-01-03      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 3471                 | 400000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 126                  | 35                      | 3471                 | 400000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | breathingSpaceApplied |
      | 2018-12-16 | 2019-01-23 | 38           | 3.25         | 8                       | 338               | 100000               | false                 |
      | 2018-12-16 | 2019-01-02 | 17           | 3.25         | 35                      | 605               | 400000               | false                 |
      | 2019-01-03 | 2019-02-03 | 31           | 0.0            | 0                       | 0                 | 400000               | true                 |
      | 2019-02-03 | 2019-04-14 | 71           | 3.25         | 35                      | 2528              | 400000               | false                 |

  @wip
  Scenario: 4. Interest Bearing. 2 debts 1 payment history both with breathing space.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-04  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2019-01-03      | 2019-02-03    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-04  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2019-01-03      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 70                   | 807156         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 168                  | 35                      | 404674             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 404238             |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 119                  | 44                      | 505297             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |


@wip
  Scenario: Scenario 1 - Customer has 2 debts which are eligible for the Breathing Space protections.
  A part payment is received post BS period & a further Interest Charge is raised.
  IFS is called during BS period, after BS period for issue of an SOL and clerically following receipt of a payment.
  Breathing space for interest bearing debt with payments.
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2021-11-15          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-11-01      | 2021-12-01    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 400000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                       | 0.0          |

  @wip
  Scenario: Multiple debts with multiple breathing Spaces  - payment whilst in an active Breathing Space period
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 5000000        | 2022-02-01        | 2022-05-31          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 250000        | 2022-06-03  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-04-01      | 2023-06-17    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 100000         | 2022-05-31        | 2022-05-31          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 10000         | 2022-07-20  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-09-01      | 2023-06-17    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal | interestOnlyIndicator |
      | 8                  | 4890593        | 4840000             | false                 |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 241                  | 455                     | 4800546            |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-02-01 | 2022-02-20 | 19           | 2.75         | 18                      | 250357             | false                 |
      | 2022-02-21 | 2022-04-04 | 43           | 3.0          | 20                      | 250883             | false                 |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 22                      | 251090             | false                 |
      | 2022-05-24 | 2022-06-03 | 11           | 3.5          | 23                      | 250263             | false                 |
      | 2022-05-24 | 2022-05-31 | 8            | 3.5          | 455                     | 4753643            | false                 |

    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 119                  | 44                      | 505297             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |

  @wip
  Scenario: 4. Customer in Breathing Space (Mental Health) & a further charge is created & becomes due after the BS start date.
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2022-02-01        | 2022-02-28          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 500000        | 2023-04-01  |

    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2022-07-31        | 2023-03-31          | 1525      | 1000     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2023-06-17  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-04-01      | 2023-06-17    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 71                   | 436103         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 424                  | 0                       | 17358              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2022-02-01 | 2022-02-20 | 19           | 2.75         | 37                      | 500715             |
      | 2022-02-21 | 2023-04-01 | 405          | 3.0          | 41                      | 516643             |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 119                  | 44                      | 505297             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-07-31 | 2022-08-22 | 22           | 3.75         | 10                      | 100226             | false                 |

