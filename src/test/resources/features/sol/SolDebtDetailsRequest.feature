
#Assumptions
#
#Debt 1 dateCreated  = 2018-06-10
#Debt 1 SubTran 1 originalAmount= 300000
#Debt 1 SubTran 2 originalAmount= 300000
#Debt 1 mainTrans  = 1085
#Debt 1 subTrans = 1000 and 1090
#Debt 1 periodEnd = 31/03/2021 (Provided by Pega)
#Debt 1 interestStartDate = 2018-06-10
#Debt 1 = interest bearing
#Debt 1 dateCreated  = 2018-06-10
#Debt 2 SubTran 1 originalAmount= 1,000
#Debt 2 SubTran 2 originalAmount= 1,000
#Debt 2 mainTrans = 1085
#Debt 2 subTrans = 1000 and 1025
#Debt 2 periodEnd = 31/03/2021 (Provided by Pega)
#Debt 2 interestStartDate = 2020-05-01
#Debt 2 = not interest bearing
#Debt 1 and Debt 2 interestRequestedTo =2020-06-01
#Debt 1 and Debt 2  solRequestedDate = 2021-08-10
#Debt 1 and Debt 2  No repayments
#Debt 1 and Debt 2  No suppression
#Debt 1 and Debt 2  No breathing space
#Debt 5 originalAmount= 400000
#Debt 5 mainTrans 5350
#Debt 5 subTrans 7012
#Debt 5 interestStartDate 2019-01-01
#Debt 5 dateCreated 2019-01-01
#Debt 5 interestRequestedTo 2020-04-15
#Debt 5 paymentAmount 100000
#Debt 5 paymentDate 2020-02-23
Feature: statement of liability Debt details

  Scenario: 1. TPSS Account Tax Assessment debt statement of liability, 2 duties, no payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans |
      | UI      | debt001 | 1525      | 1000     |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty01" |
      | "duty02" |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | totalAmountIntDuty | combinedDailyAccrual |
      | 6166               | 63                   |

    And the 1st sol debt summary will contain
      | debtID | mainTrans | description                 | periodEnd  | interestDueDutyTotal | interestRequestedTo | combinedDailyAccrual |
      | debt001             | 1525      | TPSS Account Tax Assessment | 2020-01-01 | 6166                 | 2021-05-04          | 63                   |

    And the 1st sol debt summary will contain duties
      | debtItemChargeID | subTrans | description | unpaidAmountDuty | combinedDailyAccrual |
      | duty01           | 1000     | IT          | 500000           | 35                   |
      | duty02           | 1000     | IT          | 400000           | 28                   |


  Scenario: 2. Child benefit debt statement of liability, 2 duties, with payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans |
      | UI      | debt003 | 5330      | 7006     |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty01" |
      | "duty02" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | totalAmountIntDuty | combinedDailyAccrual |
      | 2156               | 14                   |

    And the 1st sol debt summary will contain
      | debtID | mainTrans | description  | periodEnd  | interestDueDutyTotal | interestRequestedTo | combinedDailyAccrual |
      | debt003             | 5330      | UI: ChB Debt | 2020-01-01 | 2156                 | 2021-05-05          | 14                   |

    And the 1st sol debt summary will contain duties
      | debtItemChargeID | subTrans | description            | unpaidAmountDuty | combinedDailyAccrual |
      | duty01           | 7006     | UI: Child Benefit Debt | 400000           | 0                    |
      | duty02           | 1000     | IT                     | 200000           | 14                   |

  Scenario: 3. CO: Child Benefit Migrated Debt statement of liability, 1 duty, no payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans |
      | CO      | debt004 | 5350      | 7012     |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty04" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | totalAmountIntDuty | combinedDailyAccrual |
      | 0                  | 0                    |

    And the 1st sol debt summary will contain
      | debtID | mainTrans | description           | periodEnd  | interestDueDutyTotal | interestRequestedTo | combinedDailyAccrual |
      | debt004             | 5350      | CO: ChB Migrated Debt | 2020-01-01 | 0                    | 2021-05-05          | 0                    |

    And the 1st sol debt summary will contain duties
      | debtItemChargeID | subTrans | description                     | unpaidAmountDuty | combinedDailyAccrual |
      | duty04           | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    |


  Scenario: 4. MainTrans and subTrans non interest bearing - IFS still calculates interest and it is zero
    Given debt details
      | solType | debtId  | mainTrans | subTrans |
      | CO      | debt005 | 5350      | 7012     |
    And add debt item chargeIDs to the debt
      | dutyId   |
      | "duty06" |
    When a debt statement of liability is requested

    Then service returns debt statement of liability data
      | totalAmountIntDuty | combinedDailyAccrual |
      | 0                  | 0                    |

    And the 1st sol debt summary will contain
      | debtID | mainTrans | description           | periodEnd  | interestDueDutyTotal | interestRequestedTo | combinedDailyAccrual |
      | debt005             | 5350      | CO: ChB Migrated Debt | 2020-01-01 | 0                    | 2021-05-05          | 0                    |

    And the 1st sol debt summary will contain duties
      | debtItemChargeID | subTrans | description                     | unpaidAmountDuty | combinedDailyAccrual |
      | duty06           | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    |