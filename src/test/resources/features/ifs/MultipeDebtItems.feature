#  Assumptions
#  Initial amount and date
#  REGIME == DRIERd
#  NO suppression period
#  NO breathing space
#  Date Amount  == Interest start date


#Multiple Debt Items
#1 payment of 1 debt no interest
#1 payment of 1 debt with interest
#2 payments of 1 debt with interest
#2 debts, 1 debt with a payment, the second debt with no payment

Feature: Multiple Debt Items

  Scenario: 1. Non Interest Bearing. 1 Payment of 1 debt.
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2020-12-16 | 2021-04-14        | DRIER  | HIPG       | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 0             | 0             | 400000           | 400000                  | 400000                        |
    And the 1st debt summary will contain
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | numberChargeableDays | totalAmountOnWhichInterestDue |
      | 0             | 0             | 400000           | 400000                  | 0                    | 400000                        |
    And the 1st debt summary will have calculation windows
      | dateFrom   | dateTo     | numberDays | intRate | dailyInterest | totalInterest | totalAmountOnWhichInterestDue |
      | 2020-12-16 | 2021-02-02 | 0          | 0       | 0             | 0             | 500000                        |
      | 2021-02-03 | 2021-04-14 | 0          | 0       | 0             | 0             | 400000                        |

  Scenario: 2. Interest Bearing. 1 Payment of 1 debt.
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2020-12-16 | 2021-04-14        | DRIER  | NI         | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 10            | 1449          | 400000           | 401449                  | 400000                        |
    And the 1st debt summary will contain
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 10            | 1449          | 400000           | 401449                  | 400000                        |
    And the 1st debt summary will have calculation windows
      | dateFrom   | dateTo     | numberDays | intRate | dailyInterest | totalInterest | totalAmountOnWhichInterestDue |
      | 2020-12-16 | 2021-02-02 | 49         | 1       | 13            | 671           | 500000                        |
      | 2021-02-03 | 2021-04-14 | 71         | 1       | 10            | 778           | 400000                        |


#    Failing test. debt summary daily interest on ticket says 8 but api returns 13
#    Failing test. total interest on ticket says 1390 but api returns 1391
  @wip
  Scenario: 3. Interest Bearing. 2 Payments of 1 debt.
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2020-12-16 | 2021-04-14        | DRIER  | NI         | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-23    |
      | 100000     | 2021-03-05    |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 8             | 1390          | 300000           | 301390                  | 300000                        |
    And the 1st debt summary will contain
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 8             | 1390          | 300000           | 301390                  | 300000                        |
    And the 1st debt summary will have calculation windows
      | dateFrom   | dateTo     | numberDays | intRate | dailyInterest | totalInterest | totalAmountOnWhichInterestDue |
      | 2020-12-16 | 2021-02-22 | 69         | 1       | 13            | 945           | 500000                        |
      | 2021-02-23 | 2021-03-04 | 10         | 1       | 10            | 109           | 400000                        |
      | 2021-03-05 | 2021-04-14 | 41         | 1       | 8             | 336           | 300000                        |

  Scenario: 4. Interest Bearing. 2 debts. 1 debt with payment the second debt with no payment.
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2020-12-16 | 2021-04-14        | DRIER  | NI         | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2021-02-03    |
    And a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2020-12-16 | 2021-04-14        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 23            | 3092          | 900000           | 903092                  | 900000                        |
    And the 1st debt summary will contain
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 10            | 1449          | 400000           | 401449                  | 400000                        |
    And the 1st debt summary will have calculation windows
      | dateFrom   | dateTo     | numberDays | intRate | dailyInterest | totalInterest | totalAmountOnWhichInterestDue |
      | 2020-12-16 | 2021-02-02 | 49         | 1       | 13            | 671           | 500000                        |
      | 2021-02-03 | 2021-04-14 | 71         | 1       | 10            | 778           | 400000                        |
    And the 2nd debt summary will contain
      | dailyInterest | totalInterest | totalAmountToPay | totalAmountWithInterest | totalAmountOnWhichInterestDue |
      | 13            | 1643          | 500000           | 501643                  | 500000                        |
    And the 2nd debt summary will have calculation windows
      | dateFrom   | dateTo     | numberDays | intRate | dailyInterest | totalInterest | totalAmountOnWhichInterestDue |
      | 2020-12-16 | 2021-04-14 | 120        | 1       | 13            | 1643          | 500000                        |

