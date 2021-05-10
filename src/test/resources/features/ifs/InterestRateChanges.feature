Feature: Interest Rate Changes
# originalAmount (Amount) = 500,000
# dateCreated (Date Amount) = 01/01/2020
# mainTrans (Regime = DRIER) = 1525, 1545
# subTrans (CHARGE_TYPE == Duty Repaid in Error - National Insurance) = 1000, 1090
# interestStartDate (Interest start date = Date Amount) 01/02/2020
# interestRateDate (New data item) = 07/04/2020
# Interest requested to (add in old value) 31/03/2019
# No repayments
# No suppression
# No breathing space

  Scenario: Interest rate changes from 3% to 3.25% with 2 payments on same date in a leap
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2019-01-01  | 2019-01-01        | 2020-03-31        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2020-02-01    |
      | 100000     | 2020-02-01    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 22                   | 19121                | 300000            | 319121              | 300000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 848                  | 22                      | 19121                | 300000           | 319121             | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 17                      | 6482              | 206482             | 200000               |
      | 2020-01-01 | 2020-02-01 | 31           | 3.25         | 17                      | 550               | 200550             | 200000               |
      | 2019-01-01 | 2019-12-31 | 364          | 3.25         | 26                      | 9723              | 309723             | 300000               |
      | 2020-01-01 | 2020-03-29 | 88           | 3.25         | 26                      | 2344              | 302344             | 300000               |
      | 2020-03-30 | 2020-03-31 | 1            | 2.75         | 22                      | 22                | 300022             | 300000               |


  Scenario: Interest rate changes from 3% to 3.25%
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2017-12-01  | 2017-12-01        | 2019-03-31        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 44                   | 20650                | 500000            | 520650              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | true            | 44                      | 20650                | 500000           | 520650             | 484                  | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2017-12-01 | 2018-08-20 | 262          | 3.0          | 41                      | 10767             | 500000               | 510767             |
      | 2018-08-21 | 2019-03-31 | 222          | 3.25         | 44                      | 9883              | 500000               | 509883             |

#  Scenario: Interest rate changes from non-interest bearing to interest bearing
  #Scenario: Interest rate changes from interest bearing to non-interest bearing
  #TBD: No test data currently available to implement this scenario
  Scenario: Interest rate changes from 3% to 3.25% after a payment is made
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01  | 2018-01-01        | 2019-03-31        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2018-03-15    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 16100                | 400000            | 416100              | 400000              |
    And the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | true            | 35                      | 16100                | 400000           | 416100             | 526                  | 400000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2018-01-01 | 2018-03-15 | 73           | 3.0          | 8                       | 600               | 100600             | 100000               |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 32                      | 7594              | 407594             | 400000               |
      | 2018-08-21 | 2019-03-31 | 222          | 3.25         | 35                      | 7906              | 407906             | 400000               |

  Scenario: Interest rate changes from 3% to 3.25% with 2 payments on same date
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01  | 2018-01-01        | 2019-03-31        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2018-09-01    |
      | 100000     | 2018-09-01    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 26                   | 15617                | 300000            | 315617              | 300000              |
    And the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | numberChargeableDays | amountOnIntDueDebt |
      | true            | 26                      | 15617                | 300000           | 315617             | 695                  | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 16                      | 3797              | 203797             | 200000               |
      | 2018-08-21 | 2018-09-01 | 11           | 3.25         | 17                      | 195               | 200195             | 200000               |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 24                      | 5695              | 305695             | 300000               |
      | 2018-08-21 | 2019-03-31 | 222          | 3.25         | 26                      | 5930              | 305930             | 300000               |


  Scenario: 2 Debts - Interest rate changes from 3% to 3.25% and then multiple payments are made for both debts
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-01  | 2018-01-01        | 2019-03-31        | 1525      | 1000     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-03-15    |
      | 100000     | 2019-04-15    |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-01-16  | 2018-01-16        | 2019-04-14        | 1545      | 1090     | true            |
    And the debt item has payment history
      | amountPaid | dateOfPayment |
      | 100000     | 2019-01-20    |
      | 100000     | 2019-03-10    |
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 52                   | 37687                | 600000            | 637687              | 600000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 1358                 | 26                      | 19365                | 300000           | 319365             | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 8                       | 1898              | 101898             | 100000               |
      | 2018-08-21 | 2019-04-15 | 237          | 3.25         | 8                       | 2110              | 102110             | 100000               |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 8                       | 1898              | 101898             | 100000               |
      | 2018-08-21 | 2019-03-15 | 206          | 3.25         | 8                       | 1834              | 101834             | 100000               |
      | 2018-01-01 | 2018-08-20 | 231          | 3.0          | 24                      | 5695              | 305695             | 300000               |
      | 2018-08-21 | 2019-03-31 | 222          | 3.25         | 26                      | 5930              | 305930             | 300000               |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | true            | 1237                 | 26                      | 18322                | 300000           | 318322             | 300000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow |
      | 2018-01-16 | 2018-08-20 | 216          | 3.0          | 8                       | 1775              | 101775             | 100000               |
      | 2018-08-21 | 2019-03-10 | 201          | 3.25         | 8                       | 1789              | 101789             | 100000               |
      | 2018-01-16 | 2018-08-20 | 216          | 3.0          | 8                       | 1775              | 101775             | 100000               |
      | 2018-08-21 | 2019-01-20 | 152          | 3.25         | 8                       | 1353              | 101353             | 100000               |
      | 2018-01-16 | 2018-08-20 | 216          | 3.0          | 24                      | 5326              | 305326             | 300000               |
      | 2018-08-21 | 2019-04-14 | 236          | 3.25         | 26                      | 6304              | 306304             | 300000               |


  Scenario: Interest rate changes from 2.75% to 2.6% - dateCalculationTo before interestStartDate
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2020-01-01  | 2020-04-10        | 2020-03-31        | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 500000            | 500000              | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDebtTotal | unpaidAmountDebt | totalAmountIntDebt | amountOnIntDueDebt |
      | false            | 0                    | 0                       | 0                    | 500000           | 500000             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2020-04-10 | 2020-03-31 | 0            | 0.0          | 0                       | 0                 | 500000               | 500000             |