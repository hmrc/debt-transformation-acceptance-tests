Feature: Sol With Suppression

  Scenario: Customer Outputs SoL where suppression is applied
    Given suppression data has been created
      | reason      | description | enabled | fromDate   | toDate     |
      | LEGISLATIVE | COVID       | true    | 2021-03-04 | 2021-03-05 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW33     | 1              |
    And debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | CO      | debt008 | 1545      | 1090     | 2021-03-08          | 2021-03-08       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500177         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt008 | 1545      | CO: TPSS Contract Settlement | 177                  | 500177             | 35                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1090     | CO: TGPEN           | 500000           | 35                   | true            | false                 |

  Scenario: Customer Outputs SoL suppression NOT applied to a different postcode
    Given suppression data has been created
      | reason      | description | enabled | fromDate   | toDate     |
      | LEGISLATIVE | COVID       | true    | 2021-03-04 | 2021-03-05 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW34     | 1              |
    And debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | CO      | debt008 | 1545      | 1090     | 2021-03-08          | 2021-03-08       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500249         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt008 | 1545      | CO: TPSS Contract Settlement | 249                  | 500249             | 35                   |

#  Fail. Suppression is not being applied by period end when request is sent from SOL. Is this a bug?
  @ignore @DTD-409
  Scenario: Customer Outputs SoL where suppression is applied by Period End
    Given suppression data has been created
      | reason      | description | enabled | fromDate   | toDate     |
      | LEGISLATIVE | COVID       | true    | 2021-03-04 | 2021-03-05 |
    And suppression rules have been created
      | ruleId | periodEnd  | suppressionIds |
      | 1      | 2021-04-20 | 1              |
    And debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | CO      | debt008 | 1545      | 1090     | 2021-03-08          | 2021-03-08       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500177         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt008 | 1545      | CO: TPSS Contract Settlement | 177                  | 500177             | 35                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1090     | CO: TGPEN           | 500000           | 35                   | true            | false                 |

  Scenario: Customer Outputs SoL where suppression is applied by Main Trans
    Given suppression data has been created
      | reason      | description | enabled | fromDate   | toDate     |
      | LEGISLATIVE | COVID       | true    | 2021-03-04 | 2021-03-05 |
    And suppression rules have been created
      | ruleId | mainTrans | suppressionIds |
      | 1      | 1545      | 1              |
    And debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | CO      | debt008 | 1545      | 1090     | 2021-03-08          | 2021-03-08       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500177         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt008 | 1545      | CO: TPSS Contract Settlement | 177                  | 500177             | 35                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1090     | CO: TGPEN           | 500000           | 35                   | true            | false                 |

  Scenario: Customer Outputs SoL suppression NOT applied to a different mainTrans
    Given suppression data has been created
      | reason      | description | enabled | fromDate   | toDate     |
      | LEGISLATIVE | COVID       | true    | 2021-03-04 | 2021-03-05 |
    And suppression rules have been created
      | ruleId | mainTrans | suppressionIds |
      | 1      | 1540      | 1              |
    And debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | CO      | debt008 | 1545      | 1090     | 2021-03-08          | 2021-03-08       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500249         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt008 | 1545      | CO: TPSS Contract Settlement | 249                  | 500249             | 35                   |
