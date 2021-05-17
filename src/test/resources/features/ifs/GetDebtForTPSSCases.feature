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

Feature: Debt Calculation For TPSS MainTrans 1525 case

  Scenario: Interest Bearing TPSS MainTrans 1525 debt
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 249                  | 500000            | 500249         | 500000              |
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 35                      | 249                  | 2.6     | 500000           | 500249             | 7                    | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2021-03-01 | 2021-03-08 | 7            | 2.6          | 35                      | 249               | 500000               | 500249             |

    Scenario: Non Interest Bearing TPSS MainTrans 1525 debt
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1520      | 1090     | false           |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 500000            | 500000         | 500000              |
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | false           | 0                       | 0                    | 0       | 500000           | 500000             | 0                    | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow |
      | 2021-03-01 | 2021-03-08 | 0            | 0.0          | 0                       | 0                 | 500000               | 500000             |

#    TODO DTD-200
#  Scenario: TPSS MainTrans (1525) debt Zero Amount Edge Case
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
#      | 0              | 2021-03-01  | 2021-03-01        | 2021-03-08        | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    And no breathing spaces have been applied to the customer
#    When the debt item is sent to the ifs service
#    Then the 1st debt summary will contain
#      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberOfDays | amountOnIntDueDuty | interestOnlyIndicator |
#      | false           | 0                       | 0                    | 1       | 0                | 0                  | 8            | 0                  | false                 |


  Scenario: TPSS MainTrans 1525 debt Amount is negative - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | -1             | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: Original Amount can be zero or greater, negative values are not accepted

  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | \"\"           | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /originalAmount' missing or invalid

  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 1.2            | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /originalAmount' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid entry in Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | d           | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty entry in Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         |             | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-30  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCreated' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid entry in interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        | d                   | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /interestRequestedTo' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        |                     | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /interestRequestedTo' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-01  | 2021-02-01        | 2021-02-30          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /interestRequestedTo' missing or invalid

  Scenario: Debt invalid mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 99999     | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /mainTrans' missing or invalid

  Scenario: TPSS debt empty mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          |           | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /mainTrans' missing or invalid

  Scenario: TPSS MainTrans (1525) debt invalid subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | invalid  | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /subTrans' missing or invalid

  Scenario: TPSS MainTrans (1525) debt empty subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      |          | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /subTrans' missing or invalid

  Scenario: TPSS interestStartDate debt before 2001 jan 01 - Edge Case
        Given a debt item
          | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
          | -1             | 2000-03-01  | 2000-12-01        | 2001-03-08          | 1525      | 1000     | true            |
        And the debt item has no payment history
        And no breathing spaces have been applied to the customer
        When the debt item is sent to the ifs service
        Then the ifs service will respond with Could not parse body due to requirement failed: Invalid Interest Start Date. IFS does not store or calculate historic interest rates