Feature: FC VAT Debt Calculation End point testing


  Scenario: 1. Interest Indicator as Yes. 1 Payment of 1 debt.
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 500000         | 2018-12-16 | 2019-04-14          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2020-04-06      | 2021-04-06    |
    And the fc vat customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 400000            |


  Scenario: 2. Interest Indicator as No. 1 Payment of 1 debt.
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 500000         | 2018-12-16 | 2019-04-14          | N                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2019-02-03  |
    And no breathing spaces have been applied to the fc vat customer
    And the fc vat customer has no post codes
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 400000            |


  Scenario: 3. Interest Indicator as Yes. 2 Payment of 1 debt.
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 5000           | 2022-04-01 | 2021-11-24          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 200           | 2021-06-01  |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 200           | 2020-07-01  |
    And no breathing spaces have been applied to the fc vat customer
    And the fc vat customer has no post codes
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service wilL return a total debts summary of
      | combinedDailyAccrual | unpaidAmountTotal |
      | 0                    | 4800              |
    And the 1st fc vat debt summary will contain
      | interestRate |
      | 2.6          |
    And the 2nd fc vat debt summary will contain
      | interestRate |
      | 2.6          |

  Scenario: 4. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is more than Original amount
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 500000         | 2018-12-16 | 2019-04-14          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 1000000        | 2019-02-03  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2020-04-06      | 2021-04-06    |
    And the fc vat customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service will respond with Could not parse body due to requirement failed: Total Payment amounts cannot be more than the original amount

  Scenario: 5. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is 0
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 500000         | 2018-12-16 | 2019-04-14          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | 0        | 2019-02-03  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2020-04-06      | 2021-04-06    |
    And the fc vat customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service will respond with Could not parse body due to requirement failed: Payment amount must not be zero


  Scenario: 5. Interest Indicator as Yes. 1 Payment of 1 debt. Payment amount is -10000
    Given a fc vat debt item
      | originalAmount | periodEnd  | interestRequestedTo | interestIndicator |
      | 500000         | 2018-12-16 | 2019-04-14          | Y                 |
    And the fc vat debt item has payment history
      | paymentAmount | paymentDate |
      | -10000        | 2019-02-03  |
    And the fc vat customer has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2020-04-06      | 2021-04-06    |
    And the fc vat customer has post codes
      | addressPostcode | postcodeDate |
      | TW3 4QQ         | 2019-07-06   |
    When the debt item is sent to the fc vat ifs service
    Then the fc vat ifs service will respond with Could not parse body due to requirement failed: Payment amount must be positive