Feature: Retrieve generate quote response from Time to Pay Proxy

  Scenario: Single instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | single    | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |


  Scenario: Weekly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | weekly    | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |

  Scenario:  2Weekly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | 2Weekly   | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |

  Scenario:  4Weekly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | 4Weekly   | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |


  Scenario:  Monthly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | monthly   | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |


  Scenario:  quarterly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | quarterly | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |

  Scenario:  6Monthly instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | 6Monthly  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |


  Scenario:  Annually instalment payment frequency with for 1 debt -No initial payment
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then the ttp service is going to return a generate quote response with
      | quoteReference | customerReference | quoteType        | quoteDate  | numberOfInstalments | totalDebtincInt | interestAccrued | planInterest |
      | quoteRef1234   | custRef1234       | instalmentAmount | 2021-05-13 | 1                   | 10              | 10              | 0.25         |
    And the 2nd instalment will contain
      | debtItemChargeId  | debtItemId  | dueDate    | amountDue | expectedPayment | interestRate | instalmentNumber | instalmentInterestAccrued | instalmentBalance |
      | debtItemChargeId1 | debtItemId1 | 2021-05-13 | 100       | 100             | 0.24         | 1                | 10                        | 10                |


  Scenario: TTP service returns  -Invalid debtItemChargeId.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 |                  | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: debtItemChargeId should not be empty"}


  Scenario: TTP service returns  -Invalid paymentAmount of zero.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | -100            |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: paymentAmount should be a positive amount."}


  Scenario: TTP service returns  -Invalid debtItemId.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      |            | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: debtItemId should not be empty"}


  Scenario: TTP service returns  -Invalid mainTrans .
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1525k        | 1090     | 100                | 2021-05-13        |
    And Payments are
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid GenerateQuoteRequest payload: Payload has a missing field or an invalid format. Field name: mainTrans. Valid enum value should be provided"}


  Scenario: TTP service returns  -Invalid paymentDate.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | debtItemId1 | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid GenerateQuoteRequest payload: Payload has a missing field or an invalid format. Field name: paymentDate. Date format should be correctly provided"}


  Scenario: TTP service returns  -Invalid customerReference.
    Given a generate quote request
      | customerReference | channelIdentifier |
      |                   | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | NW9 5XW         | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | 2021-05-13  | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: customerReference should not be empty"}

  Scenario: TTP service returns  -empty addressPostcode field.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      |                 | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | debtItemId1 | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Could not parse body due to requirement failed: addressPostcode should not be empty"}

  Scenario: TTP service returns  -Invalid addressPostcode.
    Given a generate quote request
      | customerReference | channelIdentifier |
      | uniqRef1234       | selfService       |
    And payment plan details
      | quoteType        | quoteDate  | instalmentStartDate | instalmentAmount | frequency | duration | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | instalmentAmount | 2021-05-13 | 2021-05-13          | 100              | annually  | 12       | 100                  | 2021-05-13         | timeToPay       |
    And post codes details
      | addressPostcode | postcodeDate |
      | 2021-05-13        | 2021-05-13   |
    And customer debtItem details
      | debtItemId  | debtItemChargeId  | mainTrans | subTrans | originalDebtAmount | interestStartDate |
      | debtItemId1 | debtItemChargeId1 | 1546      | 1090     | 100                | 2021-05-13        |
    And the debt item has payment history
      | paymentDate | paymentAmount |
      | debtItemId1 | 100           |
    When the generate quote request is sent to the ttpp service
    Then service returns response code 400
    And service returns error message {"statusCode":400,"errorMessage":"Invalid GenerateQuoteRequest payload: Payload has a missing field or an invalid format. Field name: paymentDate. Date format should be correctly provided"}
