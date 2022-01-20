@suppression
Feature: Suppression - Period End

  Scenario: Suppression applied to period End
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2020-12-20 | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2020-12-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 4415                 | 500000            | 504415         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 124                  | 35                      | 4415                 | 500000           | 504415             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason | code | description |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 35                      | 502172             |        |      |             |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 500000             | POLICY | 1    | COVID       |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             |        |      |             |

  Scenario: Suppression should NOT be applied for non matching period End
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2020-12-20 | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2021-12-21 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 5520                 |

  Scenario: Period End Suppression should NOT be applied where suppression period is before interest start date
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2019-02-04 | 2019-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2021-05-20 | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2021-05-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 5520                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 155                  | 35                      | 505520             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |

  @BugRelatedToDTD-784 @wip
  Scenario: Period End Suppression should NOT be applied where suppression start is same day as interest requested to
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2019-07-06 | 2019-08-01 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2019-05-20 | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2019-02-01        | 2019-07-06          | 1535      | 1000     | 2019-05-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 44                   | 6900                 |

  Scenario: Suppression for multiple period ends
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2020-11-20 | 1              |
      | 1      | 2020-12-20 | 1              |
      | 1      | 2022-12-20 | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2020-12-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | interestDueCallTotal |
      | 4415                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          |

  Scenario: Suppression for multiple period ends and postcode
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2020-11-20 | 1              |
      | 2      | 2020-12-20 | 1              |
      | 3      | 2022-12-20 | 1              |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 4      | TW3      | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2020-12-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | interestDueCallTotal |
      | 4415                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          |

  Scenario: Suppression for multiple period ends, postcode and main trans
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2020-11-20 | 1              |
      | 2      | 2020-12-20 | 1              |
      | 3      | 2022-12-20 | 1              |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 4      | TW3      | 1              |
    And suppression rules have been created
      | ruleId | mainTrans | suppressionIds |
      | 5      | 1546      | 1              |
      | 6      | 1535      | 1              |
      | 7      | 1540      | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2020-12-20 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | interestDueCallTotal |
      | 4415                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          |
