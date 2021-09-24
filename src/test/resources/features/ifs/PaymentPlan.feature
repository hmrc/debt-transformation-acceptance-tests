@wip32
Feature: Payment plan frequency calculation for 1 debt 1 duty with initial payment

  Scenario: Payment plan calculation instalment - Single payment frequency
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | single           | 2021-12-01            | 1530      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    Then ifs service returns single payment freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - weekly payment frequency
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | weekly           | 1525      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    Then ifs service returns weekly payment freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - 2Weekly payment frequency
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | 2Weekly          | 1530      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    And ifs service returns 2-Weekly freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - 4Weekly payment frequency with end of month instalment start Date
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | 4Weekly          | 1530      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    Then ifs service returns 4-Weekly freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - Monthly payment frequency type
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | monthly          | 1525      | 1000     | 9542                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    Then ifs service returns monthly payment freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - Quarterly payment frequency with end of Leap year instalment Date
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | quarterly        | 1525      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    And ifs service returns Quarterly payment freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - 6Monthly payment frequency instalment Date starts in non leap year to Leap year
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | 6Monthly         | 1525      | 1000     | 3538                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    And ifs service returns 6Monthly payment freqeuncy instalment calculation plan

  Scenario: Payment plan calculation instalment - Annually payment frequency
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 10000                   | annually         | 1525      | 1000     | 1423                 | 1           |
    And no initial payment for the debtItem
    When the payment plan detail is sent to the ifs service
    Then ifs service returns Annually payment freqeuncy instalment calculation plan


  Scenario: Single debt payment instalment calculation plan - Monthly payments with initial payment
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | initialPaymentAmount | numberOfDay |
      | debtId | 100000     | 10000                   | monthly          | 1525      | 1000     | 1423                 | 100                  | 129         |
    And add initial payment for the debtItem
      | initialPaymentAmount |
      | 100                  |
    When the payment plan detail is sent to the ifs service
    Then ifs service returns monthly instalment calculation plan with initial payment

  Scenario: Single debt payment instalment calculation plan - Weekly payments with initial payment 129
    Given debt payment plan details
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | numberOfDay |
      | debtId | 100000     | 5000                    | weekly           | 1525      | 1000     | 2051                 | 129         |
    And add initial payment for the debtItem
      | initialPaymentAmount | initialPaymentDays |
      | 5000                 | 129                |
    When the payment plan detail is sent to the ifs service
    Then ifs service returns weekly freqeuncy instalment calculation plan with initial payment

  Scenario: Payment plan calculation request -initialPaymentAmount missing
    Given plan details with no initial payment amount
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | quoteDate  | mainTrans | subTrans | interestCallDueTotal |
      | debtId | 100000     | 10000                   | single           | 2024-07-01            | 2021-07-01 | 1530      | 1000     | 1423                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Both Initial Payment Date and Initial Payment Amount should be given"}

  Scenario: Payment plan calculation request -initialPaymentDate missing
    Given plan details with no initial payment date
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | quoteDate  | mainTrans | subTrans | interestCallDueTotal | initialPaymentAmount |
      | debtId | 100000     | 10000                   | single           | 2024-07-01            | 2021-07-01 | 1530      | 1000     | 1423                 | 1000                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Both Initial Payment Date and Initial Payment Amount should be given"}

  Scenario: Payment plan calculation request -initialPaymentDate is after instalmentPaymentDate
    Given plan details with initialPaymentDate is after instalmentPaymentDate
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal | initialPaymentAmount |
      | debtId | 100000     | 10000                   | single           | 1530      | 1000     | 1423                 | 1000                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"The Initial Payment Date should be on or before Instalment Payment Date"}

  Scenario: Payment plan calculation request error  - instalmentPaymentDate missing
    Given plan details with no instalment date
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal |
      | debtId | 100000     | 10000                   | monthly          | 1530      | 1000     | 1423                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/instalmentPaymentDate' missing or invalid"}

  Scenario: Payment plan calculation request error  - quote date in past
    Given plan details with quote date in past
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | mainTrans | subTrans | interestCallDueTotal |
      | debtId | 100000     | 10000                   | monthly          | 1530      | 1000     | 1423                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Could not parse body due to requirement failed: Quote Date must be today's Date."}

  Scenario: Payment plan calculation request error  -quoteDate missing
    Given plan details with no quote date
      | debtId | debtAmount | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | quoteDate | mainTrans | subTrans | interestCallDueTotal |
      | debtId | 100000     | 10000                   | monthly          | 2021-07-01            |           | 1530      | 1000     | 1423                 |
    When the payment plan detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/quoteDate' missing or invalid"}
