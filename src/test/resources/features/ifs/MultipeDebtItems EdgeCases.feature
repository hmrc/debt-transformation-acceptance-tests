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
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1520      | 1090     | false           |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 28                   | 3768                 | 900000            | 903768              | 900000              |
    And the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 28                      | 3768                 | 400000           | 403768             | 400000               | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-02-02 | 49           | 2.6          | 35                      | 1745               | 500000               | 500000             |
      | 2021-02-03 | 2021-04-14 | 71           | 2.6          | 28                      | 2023               | 400000               | 400000             |
    And the 2nd debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 0                       | 0                    | 500000           | 500000             | 500000               | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-04-14 | 0            | 0.0            | 0                       | 0                 | 500000               | 500000             |

  Scenario: 6. 2 debts, 1 payment each of different amounts
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1520      | 1090     | false           |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 28                   | 3768                 | 800000            | 803768              | 800000              |
    And the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 28                      | 3768                 | 400000           | 403768             | 400000               | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-02-02 | 49           | 2.6          | 35                      | 1745               | 500000               | 500000             |
      | 2021-02-03 | 2021-04-14 | 71           | 2.6          | 28                      | 2023               | 400000               | 400000             |
    And the 2nd debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 0                       | 0                    | 400000           | 400000             | 400000               | 400000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-02-02 | 0            | 0.0          | 0                       | 0                 | 500000               | 500000             |
      | 2021-02-03 | 2021-04-14 | 0            | 0.0          | 0                       | 0                 | 400000               | 400000             |

  Scenario: 7. 3 debts, 1 payments
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 98                   | 12314                | 1400000           | 1412314             | 1400000             |
    And the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 35                      | 4273                 | 500000           | 504273             | 500000               | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-04-14 | 120          | 2.6          | 35                      | 4273              | 500000               | 500000             |
    And the 2nd debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 28                      | 3768                 | 400000           | 403768             | 400000               | 400000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-02-02 | 49           | 2.6          | 35                      | 1745               | 500000               | 500000             |
      | 2021-02-03 | 2021-04-14 | 71           | 2.6          | 28                      | 2023               | 400000               | 400000             |
    And the 3rd debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | 35                      | 4273                 | 500000           | 504273             | 500000               | 500000             |
    And the 3rd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-04-14 | 120          | 2.6          | 35                      | 4273              | 500000               | 500000             |

#    300 debts items is the max says Helen
  Scenario: 8. 300 debt items
    Given 300 debt items
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 10500                 | 1281900               | 150000000       | 151281900           | 150000000           |
    And the 300th debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 35                      | 4273                 | 500000           | 504273             | 500000             |

  Scenario: 9. 2 debts, 5 payments on 1 debt
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 1000000        | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
      | 200000     | 2021-02-03    |
      | 100000     | 2021-02-13    |
      | 100000     | 2021-02-06    |
      | 100000     | 2021-02-13    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 63                   | 9949                 | 900000            | 909949              | 900000              |
    And the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 28                      | 5676                 | 400000           | 405676             | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-02-02 | 49           | 2.6          | 71                      | 3490              | 1000000              | 1000000            |
      | 2021-02-03 | 2021-02-05 | 3            | 2.6          | 49                      | 149               | 700000               | 700000             |
      | 2021-02-06 | 2021-02-12 | 7            | 2.6          | 42                      | 299               | 600000               | 600000             |
      | 2021-02-13 | 2021-04-14 | 61           | 2.6          | 28                      | 1738              | 400000               | 400000             |
    And the 2nd debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | 35                      | 4273                 | 500000           | 504273             | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-12-16 | 2021-04-14 | 120          | 2.6          | 35                      | 4273              | 500000               | 500000             |

    #failing test
  Scenario: 10. date calculationTo in the past.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 1920-11-18        | 1520      | 1090     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with DateCalculationTo should be after dateCreated


  Scenario: 11. negative original debt amount.
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | -500000         | 2020-12-16  | 2020-12-16        | 2021-04-10        | 1520      | 1090     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Negative Amount

  @smoke  #failing test
  Scenario: 12. date of payement is before date created
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-12-16  | 2020-12-16        | 2021-04-14        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2020-02-03    |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Date of payement is before date created