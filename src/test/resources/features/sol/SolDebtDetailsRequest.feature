#Assumptions
#Debt 1 dateCreated  = 01/01/2019
#Debt 1 SubTran 1 originalAmount= 250,000
#Debt 1 SubTran 2 originalAmount= 250,000
#Debt 1 mainTrans  = 1545
#Debt 1 subTrans = 1000 and 1090
#Debt 1 periodEnd = 31/03/2021 (Provided by Pega)
#Debt 1 interestStartDate = 01/01/2020
#Debt 1 = interest bearing
#Debt 1 dateCreated  = 01/01/2019
#Debt 2 SubTran 1 originalAmount= 1,000
#Debt 2 SubTran 2 originalAmount= 1,000
#Debt 2 mainTrans = 1085
#Debt 2 subTrans = 1000 and 1025
#Debt 2 periodEnd = 31/03/2021 (Provided by Pega)
#Debt 2 interestStartDate = 01/06/2020
#Debt 2 = not interest bearing
#Debt 1 and Debt 2 interestRequestedTo = 31/07/2021
#Debt 1 and Debt 2  solRequestedDate = 31/07/2021 
#Debt 1 and Debt 2  No repayments
#Debt 1 and Debt 2  No suppression
#Debt 1 and Debt 2  No breathing space
Feature: statement of liability Debt details


  Scenario: 1. TPSS Account Tax Assessment debt statement of liability, 2 duties, no payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |solRequestedDate|
      | UI      | debt001 | 1525      | 1000     | 2021-08-10          |2021-05-13      |

    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty01" |
      | "duty02" |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 913624         | 63                   |

    And the 1st sol debt summary will contain
      | debtID  | mainTrans | debtTypeDescription         | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt001 | 1525      | TPSS Account Tax Assessment | 13624                | 913624            | 63                   |

    And the 1st sol debt summary will contain duties
      | dutyID | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1000     | IT                  | 500000           | 35                   | true            | false                 |
      | duty02 | 1000     | IT                  | 400000           | 28                   | true            | false                 |


  Scenario: 2. Child benefit debt statement of liability, 2 duties, with payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt003 | 5330      | 7006     | 2021-08-10          |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty01" |
      | "duty02" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 608629         | 14                   |

    And the 1st sol debt summary will contain
      | debtID  | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt003 | 5330      | UI: ChB Debt        | 8629                 | 608629              | 14                   |

    And the 1st sol debt summary will contain duties
      | dutyID | subTrans | dutyTypeDescription    | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 7006     | UI: Child Benefit Debt | 400000           | 0                    | false           | false                 |
      | duty02 | 1000     | IT                     | 200000           | 14                   | true            | false                 |

  Scenario: 3. CO: Child Benefit Migrated Debt statement of liability, 1 duty, no payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt004 | 5350      | 7012     | 2021-08-10          |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty04" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |

    And the 1st sol debt summary will contain
      | debtID  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt004 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |

    And the 1st sol debt summary will contain duties
      | dutyID | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty04 | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |


  Scenario: 4. MainTrans and subTrans non interest bearing - IFS still calculates interest and it is zero
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt005 | 5350      | 7012     |2021-08-10           |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty06" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |

    And the 1st sol debt summary will contain
      | debtID  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt005 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |

    And the 1st sol debt summary will contain duties
      | dutyID | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty06 | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |