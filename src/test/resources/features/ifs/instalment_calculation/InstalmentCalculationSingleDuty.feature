Feature: Instalment calculation for 1 debt 1 duty

  Scenario: Payment plan calculation instalment - Single payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | numberOfDay | quoteType | quoteDate  |
      | 10000                   | single           | 2022-03-14            | 1423                 | 1           | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns single payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - weekly payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | weekly           | 2022-03-14            | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns weekly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 2Weekly payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 2Weekly          | 2022-03-14            | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns 2-Weekly frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 4Weekly payment frequency with end of month instalment start Date
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 4Weekly          | 2022-03-14            | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns 4-Weekly frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Monthly payment frequency type
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | monthly          | 2022-03-14            | 9542                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns monthly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Quarterly payment frequency with end of Leap year instalment Date
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | quarterly        | 2022-03-14            | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns Quarterly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - 6Monthly payment frequency instalment Date starts in non leap year to Leap year
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 6Monthly         | 2022-03-14            | 3538                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    And ifs service returns 6Monthly payment frequency instalment calculation plan

  Scenario: Payment plan calculation instalment - Annually payment frequency
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | annually         | 2011-03-14            | 1423                 | duration  | 2011-03-13 |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns Annually payment frequency instalment calculation plan

  Scenario: Single debt payment instalment calculation plan - Monthly payments with initial payment
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 2022-03-14            | monthly          | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 100                  | 2022-03-14         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |

    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns monthly instalment calculation plan with initial payment

  Scenario: Single debt payment instalment calculation plan - Weekly payments with initial payment 129
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 5000                    | 2022-07-20            | weekly           | 1423                 | duration  | 2022-03-13 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2022-07-20         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then ifs service returns weekly frequency instalment calculation plan with initial payment

  Scenario: Single debt instalment calculation - duration should not include initial payment
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 15000                   | 2021-08-01            | monthly          | 5000                 | duration  | 2021-06-10 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 45000                | 2021-07-01         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 6                   | 5        | 5000            | 440          | 5440          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue |
      | 1                | 2021-07-01 | 45000     |
      | 2                | 2021-08-01 | 15000     |
      | 6                | 2021-12-01 | 440       |

  Scenario: Initial payment on same day as instalment start date
    Given debt instalment calculation with 129 details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 15000                   | 2021-07-01            | monthly          | 5000                 | duration  | 2021-06-10 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 45000                | 2021-07-01         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the instalment calculation summary contains values
      | numberOfInstalments | duration | interestAccrued | planInterest | totalInterest |
      | 5                   | 5        | 5000            | 320          | 5320          |
    And IFS response contains expected values
      | instalmentNumber | dueDate    | amountDue |
      | 1                | 2021-07-01 | 60000     |
      | 2                | 2021-08-01 | 15000     |
      | 5                | 2021-11-01 | 320       |

  Scenario: Payment plan calculation request -initialPaymentAmount missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 2022-03-15            | single           | 1423                 | duration  | 2022-03-14 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentDate |
      | 2022-07-21         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/initialPaymentAmount' missing or invalid"}

  Scenario: Payment plan calculation request -initialPaymentDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal | quoteType |
      | 10000                   | 129                  | single           | 1423                 | duration  |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount |
      | 5000                 |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/initialPaymentDate' missing or invalid"}

  Scenario: Payment plan calculation request -initialPaymentDate in past
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDate | paymentFrequency | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | 2022-03-15            | single           | 1423                 | duration  | 2022-03-14 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2022-03-13         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid Initial Payment Date","message":"The Initial Payment Date should be on or after quoteDate"}

  Scenario: Payment plan calculation request -initialPaymentDate can be today
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal | quoteType |
      | 10000                   | 129                  | single           | 1423                 | duration  |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDays |
      | 5000                 | 0                  |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 200

  Scenario: Payment plan calculation request -instalmentDate can be today
    Given debt instalment calculation with details
      | instalmentPaymentAmount | instalmentPaymentDay | paymentFrequency | interestCallDueTotal | quoteType |
      | 10000                   | 0                    | single           | 1423                 | duration  |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDays |
      | 5000                 | 0                  |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 200

  Scenario: Payment plan calculation request -initialPaymentDate is after instalmentPaymentDate
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDate | interestCallDueTotal | quoteType | quoteDate  |
      | 10000                   | single           | 2022-03-15            | 1423                 | duration  | 2022-03-14 |
    And the instalment calculation has no postcodes
    And debt plan details with initial payment
      | initialPaymentAmount | initialPaymentDate |
      | 5000                 | 2022-03-16         |
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid Initial Payment Date","message":"The Initial Payment Date should be on or before Instalment Payment Date"}

  Scenario: Payment plan calculation request error  - instalmentPaymentDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | interestCallDueTotal | quoteType |
      | 10000                   | monthly          | 1423                 | duration  |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/instalmentPaymentDate' missing or invalid"}

  Scenario: Payment plan calculation request error  -quoteDate missing
    Given debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteDate | quoteType |
      | 10000                   | monthly          | 1                    | 1423                 |           | duration  |
    And the instalment calculation has no postcodes
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1530      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then Ifs service returns response code 400
    And Ifs service returns error message {"statusCode":400,"reason":"Invalid JSON error from IFS","message":"Field at path '/quoteDate' missing or invalid"}
