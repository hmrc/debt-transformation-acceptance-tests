Feature: statement of liability Debt details

  @DTD-3003
  Scenario: 1. TPSS Account Tax Assessment debt statement of liability, 2 duties, no payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt001 | 1525      | 1000     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 907817         | 63                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription         | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt001 | 1525      | TPSS Account Tax Assessment | 7817                 | 907817             | 63                   |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1000     | IT                  | 500000           | 35                   | true            | false                 |
      | 1000     | IT                  | 400000           | 28                   | true            | false                 |


  Scenario: 2. Child benefit debt statement of liability, 2 duties, with payment history.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt003 | 5330      | 7006     | 2023-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 625127         | 35                   |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt003 | 5330      | UI: ChB Debt        | 25127                | 625127             | 35                   |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription    | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7006     | UI: Child Benefit Debt | 400000           | 0                    | false           | false                 |
      | 1000     | IT                     | 200000           | 35                   | true            | false                 |

  Scenario: 3. Non interest bearing with payment history and breathing space.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt004 | 5350      | 7012     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt004 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |

  Scenario: 4. Non interest bearing with payment history and no breathing space.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt005 | 5350      | 7012     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |

    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt005 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |

  Scenario: 5. Non interest bearing with payment history and no breathing space.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt005 | 1441      | 1150     | 2023-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt005 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |

  Scenario: 6.  Non interest bearing with payment history and no breathing space.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | CO      | debt005 | 2421      | 1150     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 200000         | 0                    |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt005 | 5350      | CO: ChB Migrated Debt | 0                    | 200000             | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7012     | CO: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |


  Scenario: 7. Large non interest bearing debt with breathing space and no payment history - 9999999999.
    Given debt details
      | solType | debtId   | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt0012 | 1520      | 1090     | 2022-04-25          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 9999999999     | 0                    |
    And the 1st sol debt summary will contain
      | debtId   | mainTrans | debtTypeDescription | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt0012 | 1520      | TPSS Penalty        | 0                    | 9999999999         | 0                    |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1090     | TGPEN               | 9999999999       | 0                    | false           | false                 |


  Scenario: 8. Large interest bearing debt with breathing space and no payment history - 9999999999.
    Given debt details
      | solType | debtId  | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt009 | 1525      | 1000     | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 10101841602    | 712328               |
    And the 1st sol debt summary will contain
      | debtId  | mainTrans | debtTypeDescription         | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt009 | 1525      | TPSS Account Tax Assessment | 101841603            | 10101841602        | 712328               |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1000     | IT                  | 9999999999       | 712328               | true            | false                 |

  Scenario: 9. Interest bearing debts - 2 duties each with payment history and breathing space
    Given debt details
      | solType | debtId   | mainTrans | subTrans | interestRequestedTo |
      | UI      | debt0010 | 1525      | 1000     | 2023-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 15916039       | 2314                 |
    And the 1st sol debt summary will contain
      | debtId   | mainTrans | debtTypeDescription         | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrual |
      | debt0010 | 1525      | TPSS Account Tax Assessment | 2916039              | 15916039           | 2314                 |
    And the 1st sol debt summary will contain duties
      | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1000     | IT                  | 10000000         | 1780                 | true            | false                 |
      | 1000     | IT                  | 3000000          | 534                  | true            | false                 |
