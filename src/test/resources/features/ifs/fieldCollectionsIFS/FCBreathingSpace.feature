Feature: FC Debt Calculation Breathing Space
  Scenario: Breathing space for interest bearing debt with no payments.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
    And the fc debt item has no payment history
    And the fc customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-15      | 2021-08-14    |
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal | totalAmountIntTotal | amountOnIntDueTotal |
      | 35                   | 500000            | 8582                 | 508582              | 500000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | 35                      | 8582                 | 500000           | 508582             | 500000             |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
      | 2021-02-01 | 2021-06-14 | 133          | 2.6          | 35                      | 4736              | 504736             |
      | 2021-06-15 | 2021-08-14 | 61           | 0.0          | 0                       | 0                 | 500000             |
      | 2021-08-15 | 2021-11-30 | 108          | 2.6          | 35                      | 3846              | 503846             |

  Scenario: Breathing space for interest bearing debt with payments.
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
    And the debt item has fc payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-02-03  |
    And the fc customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-15      | 2021-08-14    |
    And the fc customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal | interestDueCallTotal |
      | 28                   | 400000            | 6880                 |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | 28                      | 6880                 | 400000           |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 7                       | 14                | 100014             |
      | 2021-02-01 | 2021-06-14 | 133          | 2.6          | 28                      | 3789              | 403789             |
      | 2021-06-15 | 2021-08-14 | 61           | 0.0          | 0                       | 0                 | 400000             |
      | 2021-08-15 | 2021-11-30 | 108          | 2.6          | 28                      | 3077              | 403077             |

  Scenario: Non interest bearing debt should not have breathing space applied
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2021-02-01        | 2021-11-30          | N                 | 2022-04-01 | 123    |
    And the fc debt item has no payment history
    And the fc customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-15      | 2021-08-14    |
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | totalAmountIntTotal | amountOnIntDueTotal |
      | 0                    | 500000              | 500000              |
    And the 1st fc debt summary will contain
      | interestDueDailyAccrual | interestDueDutyTotal |
      | 0                       | 0                    |
    And the 1st fc debt summary will not have any calculation windows

  Scenario: Multiple debts with multiple breathing Spaces
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
    And the fc debt item has no payment history
    And the fc customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-15      | 2021-08-14    |
    Given a fc debt item
      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
    And the fc debt item has no payment history
    And the fc customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-15      | 2021-08-14    |
      | 2021-08-16      | 2021-08-18    |
    And the fc customer has no post codes
    When the debt item is sent to the fc ifs service
    Then the fc ifs service wilL return a total debts summary of
      | combinedDailyAccrual | totalAmountIntTotal |
      | 70                   | 1017057             |
    And the 1st fc debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 0                    | 35                      | 508582            |
    And the 1st fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
      | 2021-02-01 | 2021-06-14 | 133          | 2.6          | 35                      | 4736              | 504736             |
      | 2021-06-15 | 2021-08-14 | 61           | 0.0          | 0                       | 0                 | 500000             |
      | 2021-08-15 | 2021-11-30 | 108          | 2.6          | 35                      | 3846              | 503846             |
    And the 2nd fc debt summary will contain
      | interestDueDailyAccrual | totalAmountIntDuty |
      | 35                      | 508475             |
    And the 2nd fc debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
      | 2021-02-01 | 2021-06-14 | 133          | 2.6          | 35                      | 4736              | 504736             |
      | 2021-06-15 | 2021-08-14 | 61           | 0.0          | 0                       | 0                 | 500000             |
      | 2021-08-15 | 2021-08-15 | 1            | 2.6          | 35                      | 35                | 500035             |
      | 2021-08-16 | 2021-08-18 | 3            | 0.0          | 0                       | 0                 | 500000             |
      | 2021-08-19 | 2021-11-30 | 104          | 2.6          | 35                      | 3704              | 503704             |

#  @wip
#  Scenario: Multiple debts, 1 with a breathing Space, 1 without
#    Given a fc debt item
#      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
#      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
#    And the fc debt item has no payment history
#    And no breathing spaces have been applied to the debt item
#    Given a fc debt item
#      | originalAmount | interestStartDate | interestRequestedTo | interestIndicator | periodEnd  | debtId |
#      | 500000         | 2021-02-01        | 2021-11-30          | Y                 | 2022-04-01 | 123    |
#    And the fc debt item has no payment history
#    And the fc customer has breathing spaces applied
#      | debtRespiteFrom | debtRespiteTo |
#      | 2021-06-15      | 2021-08-14    |
#    And the fc customer has no post codes
#    When the debt item is sent to the fc ifs service
#    Then the fc ifs service wilL return a total debts summary of
#      | combinedDailyAccrual | totalAmountIntTotal |
#      | 70                   | 1019374             |
#    And the 1st fc debt summary will contain
#      | interestDueDailyAccrual | totalAmountIntDuty |
#      | 35                      | 510756             |
#    And the 1st fc debt summary will have calculation windows
#      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
#      | 2021-02-01 | 2021-11-30 | 302          | 2.6          | 35                      | 10756             | 510756             |
#    And the 2nd fc debt summary will contain
#      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
#      | 0                    | 35                      | 508618             |
#    And the 2nd fc debt summary will have calculation windows
#      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow |
#      | 2021-02-01 | 2021-06-14 | 133          | 2.6          | 35                      | 4736              | 504736             |
#      | 2021-06-15 | 2021-08-14 | 61           | 0.0          | 0                       | 0                 | 500000             |
#      | 2021-08-14 | 2021-11-30 | 109          | 2.6          | 35                      | 3882              | 503882             |
