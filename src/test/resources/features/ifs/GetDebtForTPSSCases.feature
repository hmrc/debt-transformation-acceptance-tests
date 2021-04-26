#  Assumptions
#  1 Debt item
#  Initial originalAmount and date
#  MainTrans == 1525 (TPSS)
#  Where mainTrans is 1525 and subTrans is 1000, then interest bearing is assumed to be true
#  Where subTrans is 1000 HiPG, then interest bearing is assumed to be false
#  NO repayments
#  NO suppression period
#  NO breathing space
#  Date Amount  == Interest start date
#  No outstanding interests to pay
#  When bearing the interest rate is 1%

#  DTD-170 Get Debt For MainTrans (1525) case

Feature: Debt Calculation For TPSS MainTrans (1525) case (MVP)

  Scenario: Interest Bearing TPSS MainTrans (1525) debt (MVP)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 249                  | 500000            | 500249              | 500000              |
    Then the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | intRate | unpaidAmountDebt | totalAmountIntDebt | numberOfDays | amountOnIntDueDebt |
      | 35                      | 249                  | 2.6     | 500000           | 500249             | 7            | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2021-03-01 | 2021-03-08 | 7            | 2.6          | 35                      | 249               | 500000               | 500249             |

  Scenario: Non Interest Bearing TPSS MainTrans (1525) debt (MVP)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1520      | 1090     | false           |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 500000            | 500000              | 500000              |
    Then the 1st debt summary will contain
      | interestDueDailyAccrual | interestDueDebtTotal | intRate | unpaidAmountDebt | totalAmountIntDebt | numberOfDays | amountOnIntDueDebt |
      | 0                       | 0                    | 0       | 500000           | 500000             | 0            | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2021-03-01 | 2021-03-08 | 0            | 0.0            | 0                       | 0                 | 500000               | 500000             |

#    TODO DTD-200
#  Scenario: TPSS MainTrans (1525) debt Zero Amount Edge Case
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
#      | 0              | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    When the debt item is sent to the ifs service
#    Then the 1st debt summary will contain
#      | interestDueDailyAccrual | interestDueDebtTotal | intRate | unpaidAmountDebt | totalAmountIntDebt | numberOfDays | amountOnIntDueDebt |
#      | 0                       | 0                    | 1       | 0                | 0                  | 8            | 0                  |
#
## Below scenario currently fails as api returns daily interest of -0.0001. Should negative amounts be possible?
#  @ignore
#  Scenario: TPSS MainTrans (1525) debt Amount is negative (Edge Case)
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
#      | -1             | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    When the debt item is sent to the ifs service
#    Then the 1st debt summary will contain
#      | interestDueDailyAccrual | interestDueDebtTotal | intRate | unpaidAmountDebt | totalAmountIntDebt | numberOfDays | amountOnIntDueDebt |
#      | 0                       | 0                    | 1       | 0                | 0                  | 8            | 0                  |

  Scenario: TPSS MainTrans (1525) debt Amount non integer (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | \"\"           | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /originalAmount' missing or invalid

  Scenario: TPSS MainTrans (1525) debt Amount non integer (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 1.2            | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /originalAmount' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid entry in Date Amount (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | d           | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty entry in Date Amount (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         |             | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid Date Amount (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-30  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid entry in dateCalculationTo (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        | d                 | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty dateCalculationTo (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        |                   | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid dateCalculationTo (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-01  | 2021-02-01        | 2021-02-30        | 1525      | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: Debt invalid mainTrans (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        | 99999     | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /mainTrans' missing or invalid

  Scenario: TPSS debt empty mainTrans (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        |           | 1000     | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /mainTrans' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid subTrans (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | invalid  | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /subTrans' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty subTrans (Edge Case)
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | dateCalculationTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      |          | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /subTrans' missing or invalid
