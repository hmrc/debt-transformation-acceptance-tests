#  Assumptions
#  Initial amount and date
#  REGIME == DRIERd
#  NO suppression period
#  NO breathing space
#  Date Amount  == Interest start date


# DTD-171. Multiple Debt Items Edge cases
#5. 2 debts, 1 interest bearing. 1 non interest bearing
#6. 2 debts, 1 payment each of different amounts
#7. 3 debts, 2 payments
#8. 300 debt items
#9. debts, 5 payments on 1 debt
Feature: Multiple Debt Items - Edge Cases

  Scenario: 5. 2 debts, 1 interest bearing. 1 non interest bearing
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1520      | 1090     | false           |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 4674                 | 900000            | 904674              | 900000              |
    And the 1st debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 168          | 35                      | 4674                 | 400000           | 404674             | 400000               | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |
    And the 2nd debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 0            | 0                       | 0                    | 500000           | 500000             | 500000               | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 0            | 0.0          | 0                       | 0                 | 500000               | 500000             |

  Scenario: 6. 2 debts, 1 payment each of different amounts
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1520      | 1090     | false           |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 4674                 | 800000            | 804674              | 800000              |
    And the 1st debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 168          | 35                      | 4674                 | 400000           | 404674             | 400000               | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |
    And the 2nd debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 0            | 0                       | 0                    | 400000           | 400000             | 400000               | 400000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 0            | 0.0          | 0                       | 0                 | 100000               | 100000             |
      | 2018-12-16 | 2019-04-14 | 0            | 0.0          | 0                       | 0                 | 400000               | 400000             |

  Scenario: 7. 3 debts, 1 payments
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
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
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 123                  | 15268                | 1400000           | 1415268             | 1400000             |
    And the 1st debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 119          | 44                      | 5297                 | 500000           | 505297             | 500000               | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 5297              | 500000               | 505297             |
    And the 2nd debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 168          | 35                      | 4674                 | 400000           | 404674             | 400000               | 400000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |
    And the 3rd debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 119          | 44                      | 5297                 | 500000           | 505297             | 500000               | 500000             |
    And the 3rd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 5297              | 500000               | 505297             |

#    300 debts items is the max says Helen
  @runMe
  Scenario: 8. 300 debt items
    Given 300 debt items
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 10500                | 1271400              | 150000000         | 151271400           | 150000000           |
    And the 300th debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 119          | 35                      | 4238                 | 500000           | 504238             | 500000             |

  Scenario: 9. 2 debts, 5 payments on 1 debt
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 1000000        | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-02-03    |
      | 200000     | 2019-02-03    |
      | 100000     | 2019-02-13    |
      | 100000     | 2019-02-06    |
      | 100000     | 2019-02-13    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16  | 2018-12-16        | 2019-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 79                   | 12356                | 900000            | 912356              | 900000              |
    And the 1st debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 279          | 35                      | 7059                 | 400000           | 407059             | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25          | 26                      | 1308              | 300000               | 301308             |
      | 2018-12-16 | 2019-02-06 | 52           | 3.25          | 8                       | 463               | 100000               | 100463             |
      | 2018-12-16 | 2019-02-13 | 59           | 3.25          | 17                      | 1050              | 200000               | 201050             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25          | 35                      | 4238              | 400000               | 404238             |
    And the 2nd debt summary will contain
      | numberOfDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 119          | 44                      | 5297                 | 500000           | 505297             | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 5297              | 500000               | 505297             |