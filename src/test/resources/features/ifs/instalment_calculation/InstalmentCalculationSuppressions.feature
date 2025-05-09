Feature: Suppression Period ends after quote date
  Scenario: Instalment calculation has been requested where a postcode suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType |
      | 10000                   | monthly          | 1                    | 1423                 | duration  |
    And the instalment calculation has postcode BS39 5DP with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0


  Scenario: Instalment calculation has been requested where a period end suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType |
      | 10000                   | monthly          | 1                    | 1423                 | duration  |
    And the instalment calculation has postcode TW3 with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0


  Scenario: Instalment calculation has been requested where a main trans suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | instalmentPaymentAmount | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType |
      | 10000                   | monthly          | 1                    | 1423                 | duration  |
    And the instalment calculation has postcode TW3 with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | periodEnd  |
      | debtId | 100000     | 1525      | 1000     | 2021-08-16 |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0

#  Input 2 DTD-417
#  Instalment calculation for suppression period ends after quote date (Input 2)

  Scenario: Should calculate instalment where suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType        |
      | 24       | monthly          | 1                    | 0                    | instalmentAmount |
    And the instalment calculation has postcode BS39 5DP with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0


  Scenario: Should calculate instalment where a period end suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType        |
      | 24       | monthly          | 1                    | 0                    | instalmentAmount |
    And the instalment calculation has postcode TW3 with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans |
      | debtId | 100000     | 1525      | 1000     |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0


  Scenario: Should calculate instalment where a main trans suppression period ends after the quote date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And debt instalment calculation with details
      | duration | paymentFrequency | instalmentPaymentDay | interestCallDueTotal | quoteType        |
      | 24       | monthly          | 1                    | 0                    | instalmentAmount |
    And the instalment calculation has postcode TW3 with postcode date a year in the past
    And no initial payment for the debt item charge
    And the instalment calculation has debt item charges
      | debtId | debtAmount | mainTrans | subTrans | periodEnd  |
      | debtId | 100000     | 1525      | 1000     | 2021-08-16 |
    When the instalment calculation detail is sent to the ifs service
    Then the IFS request should return status 200
    And the 1st instalment should have an interest accrued of 0
    And the 2nd instalment should have an interest accrued of 0