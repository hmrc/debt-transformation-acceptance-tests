Feature: TTP Create Plan Request

  Scenario: TTP Promote an Arrangement request to create a plan for storage
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      | customerRef1234 | quoteReference | advisor           |
    And create payment plan details
      | quoteId     | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount | 2021-09-08 | 2021-05-13          |100               |timeToPay       | true           | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then the ttp service is going to return a create plan response with
      | customerReference | planId     | caseId | planStatus |
      | customerRef1234   | planId1234 | caseId | success    |


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty customerReference
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |                   | quoteReference | advisor           |
    And create payment plan details
      | quoteId  | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount | 2021-09-08 | 2021-05-13          |100               |timeToPay       | true           | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: customerReference should not be empty"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty quoteReference
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  |                | advisor           |
    And create payment plan details
      | quoteId  | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount | 2021-09-08 | 2021-05-13          |100               |timeToPay       | true           | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: quoteReference should not be empty"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty channelIdentifier
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  | quoteReference |                   |
    And create payment plan details
      | quoteId  | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount | 2021-09-08 | 2021-05-13       |100              |timeToPay      | true          | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid CreatePlanRequest payload: Payload has a missing field or an invalid format. Field name: channelIdentifier. Valid enum value should be provided"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty quoteId
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  | quoteReference | advisor           |
    And create payment plan details
      | quoteId  | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      |          | instalmentAmount | 2021-09-08 | 2021-05-13       |100              |timeToPay      | true          | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: quoteId should not be empty"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty quoteType
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  | quoteReference |    advisor               |
    And create payment plan details
      | quoteId     | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 |                  | 2021-09-08 | 2021-05-13       |100              |timeToPay      | true          | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid CreatePlanRequest payload: Payload has a missing field or an invalid format. Field name: quoteType. Valid enum value should be provided"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage -empty quoteDate
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  | quoteReference |     advisor       |
    And create payment plan details
      | quoteId     | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount |            | 2021-05-13       |100              |timeToPay      | true          | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid CreatePlanRequest payload: Payload has a missing field or an invalid format. Field name: quoteDate. Date format should be correctly provided"}


  Scenario: TTP Promote an Arrangement request to create a plan for storage negative instalment amount
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      |  customerRef1234  | quoteReference |     advisor       |
    And create payment plan details
      | quoteId     | quoteType        | quoteDate  | instalmentStartDate |instalmentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentAmount |  2021-09-08| 2021-05-13          |-100              |timeToPay      | true          | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
    And customer address details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525      | 1000     | 100                | 2021-05-13        |
    And the debtItem payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    And debt payment method details
      | paymentMethod | paymentReference |
      | BACS          | paymentRef123    |

    And debt instalment repayment details
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100              | 0.25         | 1                | 10                     | 90               |
    When the create plan request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: instalmentAmount should be a positive amount."}

