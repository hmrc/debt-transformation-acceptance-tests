Feature: Statement of liability Debt details for Self Assessment Debts

  @DTD-1959 @DTD-3003
  Scenario: 1. SA debt statement of liability, 2 duties and multiple breathing space with no payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA001 | 4920      | 1553     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 907817         | 63                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription       | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA001 | 4920      | SA 1st Payment on Account | 7817                 | 907817             | 63                   |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription          | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1553     | SA 1st Payment on Account    | 500000           | 35                   | true            | false                 |
      | 1090     | SA Pship Late Filing Penalty | 400000           | 28                   | true            | false                 |

  @DTD-1959
  Scenario: 2. SA debt statement of liability. Single duty non interest bearing.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA002 | 5073      | 1553     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA002 | 5073      | SA Transfer to OAS  | 0                    | 500000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1553     | 500000           | 0                    | false           | false                 |

  @DTD-2166
  Scenario: 3. SA debt - 2 duties Multiple breathing space and payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA003 | 4920      | 1553     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 605264         | 41                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription       | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA003 | 4920      | SA 1st Payment on Account | 5264                 | 605264             | 41                   |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription          | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1553     | SA 1st Payment on Account    | 350000           | 24                   | true            | false                 |
      | 1090     | SA Pship Late Filing Penalty | 250000           | 17                   | true            | false                 |


  @DTD-2714
  Scenario: 4. SA debt statement of liability - 2 duties Multiple breathing space and payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA004 | 4003      | 1015     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 605264         | 41                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA004 | 4003      | ITSA Misc Charge    | 5264                 | 605264             | 41                   |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription       | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1015     | ITSA Misc Charge          | 350000           | 24                   | true            | false                 |
      | 1046     | SA 1st Payment on Account | 250000           | 17                   | true            | false                 |


  @DTD-2714
  Scenario: 5. SA debt statement of liability - Single duty interest bearing ETMP debt .
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA005 | 4930      | 1011     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 504629         | 35                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription     | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA005 | 4930      | SA Payment on Account 2 | 4629                 | 504629             | 35                   |
    And the 1st sol debt summary will contain duties
      | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1011     | 500000           | 35                   | true            | false                 |


  @DTD-2714
  Scenario: 6. SA customer statement of liability - with Single duty non interest bearing ETMP debt .
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo |
      | UI      | debtSA006 | 6010      | 1575     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription      | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA006 | 6010      | SA Late Payment Interest | 0                    | 500000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1575     | 500000           | 0                    | false           | true                  |


  @DTD-2940
  Scenario: 7. Statement of liability for customer with ETMP parentMainTrans   - Single Non Interest bearing debt
    Given debt details
      | solType | debtId     | mainTrans | subTrans | parentMainTrans | interestRequestedTo |
      | UI      | debtSA0014 | 6010      | 1554     | 33              | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId     | mainTrans | debtTypeDescription      | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual | parentMainTrans |
      | debtSA0014 | 6010      | SA Late Payment Interest | 0                    | 500000             | 0                    | 33              |
    And the 1st sol debt summary will contain duties
      | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1554     | 500000           | 0                    | false           | true                  |


  @DTD-2940
  Scenario: 8. Statement of liability for customer with parentMainTrans   - Single SA Non Interest bearing debt
    Given debt details
      | solType | debtId     | mainTrans | subTrans | parentMainTrans | interestRequestedTo |
      | UI      | debtSA0015 | 6010      | 1554     | 25              | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId     | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual | parentMainTrans |
      | debtSA0015 | 6010      | SA Balancing Charge Interest | 0                    | 500000             | 0                    | 33              |
    And the 1st sol debt summary will contain duties
      | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1554     | 500000           | 0                    | false           | true                  |

  @DTD-3523
  Scenario Outline: 9. SA customer statement of liability - Penalty Reform Charge - Interest bearing debt
    Given debt details
      | solType | debtId   | mainTrans   | subTrans   | interestRequestedTo |
      | UI      | <debtId> | <mainTrans> | <subTrans> | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 504629         | 35                   |
    And the 1st sol debt summary will contain
      | debtId   | mainTrans   | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | <debtId> | <mainTrans> | Penalty reform charge | 4629                 | 504629             | 35                   |
    And the 1st sol debt summary will contain duties
      | subTrans   | unpaidAmountDuty | combinedDailyAccrual | interestBearing   | interestOnlyIndicator   |
      | <subTrans> | 500000           | 35                   | <interestBearing> | <interestOnlyIndicator> |
    Examples:
      | debtId     | mainTrans | subTrans | interestBearing | interestOnlyIndicator |
      | debtSA0017 | 4027      | 1080     | true            | false                 |
      | debtSA0018 | 4028      | 1085     | true            | false                 |
      | debtSA0019 | 4029      | 1090     | true            | false                 |
      | debtSA0020 | 4031      | 1095     | true            | false                 |
      | debtSA0021 | 4032      | 1090     | true            | false                 |
      | debtSA0022 | 4033      | 1095     | true            | false                 |

  @DTD-3523
  Scenario Outline: 10. SA customer statement of liability - Penalty Reform Charge - Non Interest bearing debt
    Given debt details
      | solType | debtId   | mainTrans   | subTrans   | interestRequestedTo |
      | UI      | <debtId> | <mainTrans> | <subTrans> | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId   | mainTrans   | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | <debtId> | <mainTrans> | Penalty reform charge | 0                    | 500000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans   | unpaidAmountDuty | combinedDailyAccrual | interestBearing   | interestOnlyIndicator   |
      | <subTrans> | 500000           | 0                    | <interestBearing> | <interestOnlyIndicator> |
    Examples:
      | debtId     | mainTrans | subTrans | interestBearing | interestOnlyIndicator |
      | debtSA0023 | 6010      | 1611     | false           | true                  |
      | debtSA0024 | 6010      | 2090     | false           | true                  |
      | debtSA0025 | 6010      | 2095     | false           | true                  |
      | debtSA0026 | 6010      | 2096     | false           | true                  |