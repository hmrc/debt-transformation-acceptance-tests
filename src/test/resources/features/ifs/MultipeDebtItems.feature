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
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1520      | 1090     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 400000            | 400000              | 400000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | false            | 0                    | 0                       | 0                    | 400000           | 400000             | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 0            | 0.0          | 0                       | 0                 | 100000               | 100000             |
      | 2018-12-16 | 2019-04-14 | 0            | 0.0          | 0                       | 0                 | 400000               | 400000             |

  Scenario: 2. Interest Bearing. 1 Payment of 1 debt.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 4674                 | 400000            | 404674              | 400000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 168                  | 35                      | 4674                 | 400000           | 404674             | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |

  Scenario: 3. Interest Bearing. 2 Payments of 1 debt.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-23    |
      | 100000     | 2019-03-05    |
    And no breathing spaces have been applied to the customer
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 26                   | 4495                 | 300000            | 304495              | 300000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 267                  | 26                      | 4495                 | 300000           | 304495             | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-03-05 | 79           | 3.25         | 8                       | 703               | 100000               | 100703             |
      | 2018-12-16 | 2019-02-23 | 69           | 3.25         | 8                       | 614               | 100000               | 100614             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 26                      | 3178              | 300000               | 303178             |

  Scenario: 4. Interest Bearing. 2 debts. 1 debt with payment the second debt with no payment.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 79                   | 9971                 | 900000            | 909971              | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 168                  | 35                      | 4674                 | 400000           | 404674             | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 119                  | 44                      | 5297                 | 500000           | 505297             | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 5297              | 500000               | 505297             |

