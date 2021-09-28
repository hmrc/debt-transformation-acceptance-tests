Feature: Payment plan frequency calculation for 1 debt 1 duty with initial payment

  Scenario: Payment plan calculation instalment - Single payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | numberOfDay |
      | 10000                   | single           | 1                    | 1423                 | 1           |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns single payment frequency instalment calculation plan


  Scenario: Payment plan calculation instalment - weekly payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | weekly           | 1                    | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns weekly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 2Weekly payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | 2Weekly          | 1                    | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns 2-Weekly frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 4Weekly payment frequency with end of month instalment start Date
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | 4Weekly          | 1                    | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns 4-Weekly frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Monthly payment frequency type
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | monthly          | 1                    | 9542                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns monthly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Quarterly payment frequency with end of Leap year instalment Date
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | quarterly        | 1                    | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns Quarterly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 6Monthly payment frequency instalment Date starts in non leap year to Leap year

    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | 6Monthly         | 1                    | 3538                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns 6Monthly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Annually payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | annually         | 1                    | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns Annually payment frequency instalment calculation plan

    #Need to fix Then step failure
  @wip
  Scenario: Single debt payment instalment calculation plan - Monthly payments with initial payment
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal |
      | 10000                   | 129                  | monthly          | 1423                 |
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDays |
      | 10000                | 129                |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |

    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns monthly instalment calculation plan with initial payment

  Scenario: Single debt payment instalment calculation plan - Weekly payments with initial payment 129
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal |
      | 5000                    | 129                  | weekly           | 1423                 |
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDays |
      | 5000                 | 129                |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns weekly frequency instalment calculation plan with initial payment


  Scenario: Payment plan calculation request -initialPaymentAmount missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal |
      | 10000                   | 129                  | single           | 1423                 |
    And debt plan details with initial payment
      | initialPaymentDays |
      | 129                |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/initialPaymentAmount' missing or invalid"}


  Scenario: Payment plan calculation request -initialPaymentDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal |
      | 10000                   | 129                  | single           | 1423                 |
    And debt plan details with initial payment
      | initialPaymentAmount |
      | 5000                 |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/initialPaymentDate' missing or invalid"}


  Scenario: Payment plan calculation request -initialPaymentDate is after instalmentPaymentDate
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal |
      | 10000                   | single           | 1                    | 1423                 |
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDays |
      | 5000                 | 129                |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"The Initial Payment Date should be on or before Instalment Payment Date"}

  Scenario: Payment plan calculation request error  - instalmentPaymentDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | interestCallDueTotal |
      | 10000                   | monthly          | 1423                 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/instalmentPaymentDate' missing or invalid"}


  Scenario: Payment plan calculation request error  - quote date in past
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteDate  |
      | 10000                   | monthly          | 1                    | 1423                 | 2021-09-21 |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Could not parse body due to requirement failed: Quote Date must be today's Date."}

  Scenario: Payment plan calculation request error  -quoteDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteDate |
      | 10000                   | monthly          | 1                    | 1423                 |           |
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/quoteDate' missing or invalid"}
