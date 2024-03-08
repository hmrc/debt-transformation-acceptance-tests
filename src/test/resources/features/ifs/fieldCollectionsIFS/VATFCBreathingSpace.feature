Feature: FC VAT Debt Calculation with Breathing Space

  Scenario: Breathing space for interest bearing debt with payments.
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2021-11-15          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-11-01      | 2021-12-01    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                  | 400000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                      | 0.0          |


  Scenario: Breathing space for interest bearing debt with no payments.
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2021-10-04          | Y                 |
    And the fc vat debt item has no payment history
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-09-01      | 2021-12-01    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                   | 500000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                       | 0.0         |


  Scenario: Non interest bearing debt should not have breathing space applied
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2021-11-30          | N                 |
    And the fc vat debt item has no payment history
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-11-01      | 2021-12-01    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 500000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                       | 0.0          |

  @wip
  Scenario: Open Ended Breathing Space
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2023-03-01          | Y                 |
    And the fc vat debt item has no payment history
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-11-01      |               |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 500000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                       | 0.0          |

  @wip
  Scenario: Multiple debts with multiple breathing Spaces
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2022-02-07        | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-01-30      | 2022-02-28   |
      | 2021-08-16      | 2021-08-18    |
    Given a fc vat debt item
      | debtItemChargeId  | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | debtItemChargeId1 | 500000         | 2022-04-01 | 2021-03-14          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 50000         | 2021-10-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-01-04      | 2021-02-14    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 32                   | 850000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                      | 0.0         |
    And the 2nd fc vat debt summary will contain
      | debtItemChargeId  | interestDueDailyAccrual | interestRate |
      | debtItemChargeId1 | 32                       | 2.6         |


  Scenario: Multiple debts, 1 with a breathing Space, 1 without
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 800000         | 2022-04-01 | 2021-09-10          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-06-30      | 2021-08-14    |
      | 2021-09-01      | 2021-10-01    |
    Given a fc vat debt item
      | debtItemChargeId  | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | debtItemChargeId1 | 600000         | 2019-04-01 | 2021-01-01          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 50000         | 2020-01-04  |
    And no breathing spaces have been applied to the fc vat customer
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 39                   | 1250000            |
    And the 1st fc vat debt summary will contain
      | debtItemChargeId | interestDueDailyAccrual | interestRate |
      | 123              | 0                      | 0.0          |
    And the 2nd fc vat debt summary will contain
      | debtItemChargeId  | interestDueDailyAccrual | interestRate |
      | debtItemChargeId1 | 39                      | 2.6         |


  Scenario: Breathing space for  interest bearing debt with payments -no debtRespiteFrom
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2020-08-02           | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      |                 | 2020-08-04    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service will respond with Field at path '/debtItems(0)/breathingSpaces(0)/debtRespiteFrom' missing or invalid


  Scenario: Breathing space -no interest bearing indictor.
    Given a fc vat debt item
      | debtItemChargeId | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 123              | 500000         | 2022-04-01 | 2021-11-15          |                   |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-06-01  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-11-01      | 2021-12-01    |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service will respond with Field at path '/debtItems(0)/interestIndicator' missing or invalid