###Debt 1. 2 duties. Interest bearing with no Payment history

#Debt 1 Duty 1 originalAmount= 500,000
#Debt 1 Duty 1 interestStartDate = 20/12/2020
#Debt 1 Duty 1 interestRequestedTo = 10/08/2021
#Debt 1 Duty 1 With Breathing spaces

#Debt 1 Duty 2 originalAmount= 400,000
#Debt 1 Duty 2 interestStartDate = 04/02/2021
#Debt 1 Duty 2 interestRequestedTo = 10/08/2021
#Debt 1 Duty 2 With Breathing spaces


###Debt 2. 1 duty, Non-interest bearing with Payment history

#Debt 2 Duty 1 originalAmount= 100,000
#Debt 2 Duty 1 With Breathing spaces

Feature: statement of liability multiple debts

  Scenario: 1. TPSS Account Tax Assessment debt statement of liability, 2 debts with breathing spaces
    Given statement of liability multiple debt requests
      | solType | debtId  | debtId2 | interestRequestedTo |
      | UI      | debt001 | debt004 | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 1107817        | 63                   |
    And the 1st customer statement of liability contains debt values as
      | debtId  | mainTrans | debtTypeDescription         | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrualDebt |
      | debt001 | 1525      | TPSS Account Tax Assessment | 7817                 | 907817             | 63                       |
    And the 1st customer statement of liability contains duty values as
      | subTrans | dutyTypeDescription | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 1000     | IT                  | 500000           | 35                   | true            | false                 |
    And the 2nd customer statement of liability contains debt values as
      | debtId  | mainTrans | debtTypeDescription   | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrualDebt |
      | debt004 | 5350      | UI: ChB Migrated Debt | 0                    | 200000             | 0                        |
    And the 2nd customer statement of liability contains duty values as
      | subTrans | dutyTypeDescription             | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | 7012     | UI: Child Benefit Migrated Debt | 200000           | 0                    | false           | false                 |


  @DTD-2940
  Scenario: 2. Statement of liability for customer - 2 SA Non Interest bearing debts
    Given statement of liability multiple debt requests
      | solType | debtId     | debtId2    | interestRequestedTo |
      | UI      | debtSA0016 | debtSA0014 | 2021-08-10          |
    When a debt statement of liability is requested
    Then service returns debt statement of liability data
      | amountIntTotal | combinedDailyAccrual |
      | 1100000        | 0                    |
    And the 1st customer statement of liability contains debt values as
      | debtId     | mainTrans | debtTypeDescription          | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrualDebt | parentMainTrans |
      | debtSA0016 | 6010      | SA Balancing Charge Interest | 0                    | 600000             | 0                        | 25              |
    And the 1st customer statement of liability contains duty values as
      | dutyId | subTrans | dutyTypeDescription      | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1554     | SA Late Payment Interest | 400000           | 0                    | false           | true                  |
    And the 2nd customer statement of liability contains duty values as
      | dutyId | subTrans | dutyTypeDescription              | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1554     | SA Payment on Account 2 Interest | 500000           | 0                    | false           | true                  |
    And the 2nd customer statement of liability contains debt values as
      | debtId     | mainTrans | debtTypeDescription      | interestDueDebtTotal | totalAmountIntDebt | combinedDailyAccrualDebt | parentMainTrans |
      | debtSA0014 | 6010      | SA Late Payment Interest | 0                    | 500000             | 0                        | 33              |
    And the 2nd customer statement of liability contains duty values as
      | dutyId | subTrans | dutyTypeDescription              | unpaidAmountDuty | combinedDailyAccrual | interestBearing | interestOnlyIndicator |
      | duty01 | 1554     | SA Payment on Account 2 Interest | 500000           | 0                    | false           | true                  |

