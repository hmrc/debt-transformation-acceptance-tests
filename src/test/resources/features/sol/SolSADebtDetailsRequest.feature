Feature: Statement of liability Debt details for Self Assessment Debts

  @DTD-1959
  Scenario: 1. SA debt statement of liability, 2 duties, no payment history.
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
