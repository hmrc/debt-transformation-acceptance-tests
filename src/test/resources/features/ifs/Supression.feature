#*Assumptions*
#* Suppression is on mainTrans 1535 and subTrans 1000

#BELOW Suppression scenario edge cases to be added

#Scenario: Suppression, payment before suppression
#Scenario: Suppression, payment after suppression
#Scenario: Suppression, breathing space with suppression

Feature: Suppression

  Scenario: Suppression applied to sub trans
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 4415                 | 500000            | 504415         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 124                  | 35                      | 4415                 | 500000           | 504415             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason | code |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 35                      | 502172             |        |      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 500000             | POLICY | 1    |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             |        |      |

  Scenario: Suppression, 2 payments on same day during suppression
    Given suppression data has been created
      | reason      | enabled | fromDate   | toDate     |
      | LEGISLATIVE | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-04-20  |
      | 50000         | 2021-04-20  |
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 24                   | 353741         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 185                  | 24                      | 353741             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason      | code |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 10                      | 150651             |             |      |
      | 2021-04-04 | 2021-04-20 | 17           | 0.0          | 0                       | 150000             | LEGISLATIVE | 1    |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 24                      | 351520             |             |      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 350000             | LEGISLATIVE | 1    |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 24                      | 351570             |             |      |

  Scenario: Suppression, 2 duties, 2 payments on same day for one of the duties
    Given suppression data has been created
      | reason      | enabled | fromDate   | toDate     |
      | LEGISLATIVE | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 400000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-02-20  |
      | 50000         | 2021-02-20  |
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 400000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | amountOnIntDueWindow | reason      |
      | 2021-02-01 | 2021-02-20 | 19           | 2.6          | 150000               |             |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 250000               |             |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 250000               | LEGISLATIVE |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 250000               |             |
    And the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 45                   | 650000            |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | unpaidAmountDuty |
      | 143                  | 17                      | 250000           |
    Then the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | amountOnIntDueWindow | reason      |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 400000               |             |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 400000               | LEGISLATIVE |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 400000               |             |
    And the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 45                   | 650000            |
    And the 2nd debt summary will contain
      | numberChargeableDays | unpaidAmountDuty |
      | 124                  | 400000           |
  @wip5
  Scenario: Suppression, open ended suppression
    Given suppression data has been created
      | reason      | enabled | fromDate   | toDate     |
      | LEGISLATIVE | true    | 2020-04-04 | 9999-12-31 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2020-04-01        | 2020-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | amountOnIntDueWindow | reason      |
      | 2020-04-01 | 2020-04-03 | 2            | 2.75         | 500000               |             |
      | 2020-04-04 | 2020-07-06 | 94           | 0.0          | 500000               | LEGISLATIVE |
    And the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 500000            |
    And the 1st debt summary will contain
      | numberChargeableDays |
      | 2                    |

   Scenario: Suppression, 2 payments before suppression dates
    Given suppression data has been created
      | reason      | enabled | fromDate   | toDate     |
      | LEGISLATIVE | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-03-10  |
      | 50000         | 2021-03-20  |
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 24                   | 353520         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 208                  | 24                      | 353520             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason      | code |
      | 2021-02-01 | 2021-03-20 | 47           | 2.6          | 3                       | 50167              |             |      |
      | 2021-02-01 | 2021-03-10 | 37           | 2.6          | 7                       | 100263             |             |      |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 24                      | 351520             |             |      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 350000             | LEGISLATIVE | 1    |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 24                      | 351570             |             |      |

  Scenario: Suppression, 2 payments after suppression dates
    Given suppression data has been created
      | reason      | enabled | fromDate   | toDate     |
      | LEGISLATIVE | true    | 2021-02-04 | 2021-03-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-04-20  |
      | 50000         | 2021-04-20  |
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 24                   | 353663         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 175                  | 24                      | 353663             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason      | code |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 10                      | 150021             |             |      |
      | 2021-02-04 | 2021-03-04 | 29           | 0.0          | 0                       | 150000             | LEGISLATIVE | 1    |
      | 2021-03-05 | 2021-04-20 | 47           | 2.6          | 10                      | 150502             |             |      |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 24                      | 350049             |             |      |
      | 2021-02-04 | 2021-03-04 | 29           | 0.0          | 0                       | 350000             | LEGISLATIVE | 1    |
      | 2021-03-05 | 2021-07-06 | 124          | 2.6          | 24                      | 353091             |             |      |