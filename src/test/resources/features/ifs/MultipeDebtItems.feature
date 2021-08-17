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
    And no breathing spaces have been applied to the customer
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
    And no breathing spaces have been applied to the customer
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
    And no breathing spaces have been applied to the customer
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
    And no breathing spaces have been applied to the customer
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
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 1000000        | 1000000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 0                       | 1000000            |
    And the 1st debt summary will not have any calculation windows

  Scenario: 6. 1 debt, no payment interest requested to date is same as interest start date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2022-02-02        | 2022-02-02          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 71                   | 1000000        | 1000000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 0                    | 71                      | 1000000            |
    And the 1st debt summary will not have any calculation windows

  Scenario: 7. 1 debt, 1 payment interest requested to date is before the interest start date
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2023-03-03        | 2022-02-02          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-01-01  |
    And no breathing spaces have been applied to the customer
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
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 900000         | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | false           | 0                    | 0                        | 900000            |
    And the 1st debt summary will not have any calculation windows
