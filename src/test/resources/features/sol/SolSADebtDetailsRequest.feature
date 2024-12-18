Feature: Statement of liability Debt details for Self Assessment Debts

  @DTD-1959
  Scenario: 1. SA debt statement of liability, 2 duties and multiple breathing space with no payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA001 | 4920      | 1553     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
      | duty02 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 907817         | 63                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription       | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA001 | 4920      | SA 1st Payment on Account | 7817                 | 907817             | 63                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription          | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1553     | SA 1st Payment on Account    | 500000           | 35                   | true            | false                 |
      | duty02 | 1090     | SA Pship Late Filing Penalty | 400000           | 28                   | true            | false                 |

  @DTD-1959
  Scenario: 1. SA debt statement of liability. Single duty non interest bearing.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA002 | 5073      | 1553     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |

    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA002 | 5073      | SA Transfer to OAS  | 0                    | 500000             | 0                    |

    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1553     | 500000           | 0                    | false           | false                 |

  @DTD-2166
  Scenario: 1. SA debt - 2 duties Multiple breathing space and payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA003 | 4920      | 1553     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
      | duty02 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 605264         | 41                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription       | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA003 | 4920      | SA 1st Payment on Account | 5264                 | 605264             | 41                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription          | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1553     | SA 1st Payment on Account    | 350000           | 24                   | true            | false                 |
      | duty02 | 1090     | SA Pship Late Filing Penalty | 250000           | 17                   | true            | false                 |


  @DTD-2714
  Scenario: 1. SA debt statement of liability - 2 duties Multiple breathing space and payment history.
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA004 | 4003      | 1015     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
      | duty02 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 605264         | 41                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA004 | 4003      | ITSA Misc Charge    | 5264                 | 605264             | 41                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | dutyTypeDescription       | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1015     | ITSA Misc Charge          | 350000           | 24                   | true            | false                 |
      | duty02 | 1046     | SA 1st Payment on Account | 250000           | 17                   | true            | false                 |


  @DTD-2714
  Scenario: 1. SA debt statement of liability - Single duty interest bearing ETMP debt .
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA005 | 4930      | 1011     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 504629         | 35                   |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription     | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA005 | 4930      | SA Payment on Account 2 | 4629                 | 504629             | 35                   |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1011     | 500000           | 35                   | true            | false                 |


  @DTD-2714
  Scenario: 1. SA customer statement of liability - with Single duty non interest bearing ETMP debt .
    Given debt details
      | solType | debtId    | mainTrans | subTrans | interestRequestedTo | solRequestedDate |
      | UI      | debtSA006 | 6010      | 1575     | 2021-08-10          | 2021-05-13       |
    And add debt item chargeIDs to the debt
      | dutyId |
      | duty01 |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 500000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId    | mainTrans | debtTypeDescription      | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debtSA006 | 6010      | SA Late Payment Interest | 0                    | 500000             | 0                    |
    And the 1st sol debt summary will contain duties
      | dutyId | subTrans | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1575     | 500000           | 0                    | false           | true                  |