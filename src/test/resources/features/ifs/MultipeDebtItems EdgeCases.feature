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

  Scenario: 1. 2 debts, 1 interest bearing. 1 non interest bearing
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1520      | 1090     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 4674                 | 900000            | 904674         | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 168                  | 35                      | 4674                 | 400000           | 404674             | 400000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 436               | 100000               | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 4238              | 400000               | 404238             |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | false           | 0                    | 0                       | 0                    | 500000           | 500000             | 500000             | false                 |
    And the 2nd debt summary will not have any calculation windows


  Scenario: 2. 2 debts, 1 payment each of different amounts
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1520      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 4674                 | 800000            | 804674         | 800000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 168                  | 35                      | 404674             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 404238             |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | false           | 0                    | 0                       | 400000             |
    And the 2nd debt summary will not have any calculation windows


  Scenario: 3. 3 debts, 1 payments
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has no payment history
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 123                  | 15268                | 1400000           | 1415268        | 1400000             |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 119                  | 44                      | 505297             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 168                  | 35                      | 404674             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 8                       | 100436             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 404238             |
    And the 3rd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty |
      | true            | 119                  | 44                      | 5297                 | 500000           | 505297             |
    And the 3rd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |

#    300 debts items is the max says Helen

  Scenario: 4. 300 debt items
    Given 300 debt items
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt items is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | 1388100 | unpaidAmountTotal | amountIntTotal | 151388100 |
      | 12300                | 1333500 | 150000000         | 151388100      | 150000000 |
    And the 300th debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 119                  | 41                      | 504627             |


  Scenario: 5. 2 debts, 5 payments on 1 debt
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 1000000        | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
      | 200000        | 2019-02-03  |
      | 100000        | 2019-02-13  |
      | 100000        | 2019-02-06  |
      | 100000        | 2019-02-13  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2018-12-16        | 2019-04-14          | 1525      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 79                   | 912356         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 279                  | 35                      | 407059             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-02-03 | 49           | 3.25         | 26                      | 301308             |
      | 2018-12-16 | 2019-02-06 | 52           | 3.25         | 8                       | 100463             |
      | 2018-12-16 | 2019-02-13 | 59           | 3.25         | 17                      | 201050             |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 35                      | 404238             |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 119                  | 44                      | 505297             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2018-12-16 | 2019-04-14 | 119          | 3.25         | 44                      | 505297             |

  Scenario: 6. 1 debts, 1 payment, payment date before date created
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2019-12-16        | 2020-05-05          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |

    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 28                   | 404759         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 141                  | 28                      | 404759             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2019-12-16 | 2019-02-03 | 0            | 0.0          | 0                       | 100000             |
      | 2019-12-16 | 2019-12-31 | 15           | 3.25         | 35                      | 400534             |
      | 2020-01-01 | 2020-03-29 | 89           | 3.25         | 35                      | 403161             |
      | 2020-03-30 | 2020-04-06 | 8            | 2.75         | 30                      | 400240             |
      | 2020-04-07 | 2020-05-05 | 29           | 2.6          | 28                      | 400824             |


  Scenario: 7. 1 debts, 1 Payment  Amount paid greater than original debt amount
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50             | 2019-12-16        | 2020-05-05          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 1000          | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: Amount paid in payments cannot be greater than Original Amount

  Scenario: 6. 1 debts, 1 payment amount paid less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50             | 2019-12-16        | 2020-05-05          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | -1000         | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: Amount paid in payments cannot be negative values

  Scenario: 7. 1 debts, 2 payment amount paid less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50000          | 2019-12-16        | 2020-05-05          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 1000          | 2019-02-03  |
      | -1000         | 2019-03-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: Amount paid in payments cannot be negative values

  Scenario: 8. 1 debt, 1 payment, interest start date is before the debt created
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-12-22  | 2020-10-16        | 2021-02-22          | 1525      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-02-23  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 28                   | 404592         |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | true            | 259                  | 28                      | 404592             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow |
      | 2020-10-16 | 2020-12-31 | 76           | 2.6          | 7                       | 100539             |
      | 2021-01-01 | 2021-02-23 | 54           | 2.6          | 7                       | 100384             |
      | 2020-10-16 | 2020-12-31 | 76           | 2.6          | 28                      | 402159             |
      | 2021-01-01 | 2021-02-22 | 53           | 2.6          | 28                      | 401510             |

