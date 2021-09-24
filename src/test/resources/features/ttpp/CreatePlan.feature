Feature: TTP Create Plan Request
@wip32
  Scenario: TTP Promote an Arrangement request to create a plan for storage
    Given a create plan
      | customerReference | quoteReference | channelIdentifier |
      | customerRef1234 | quoteReference | advisor           |
    And create payment plan details
      | quoteId  | quoteType        | quoteDate  | instalmentStartDate |instalmentPaymentAmount |paymentPlanType | thirdPartyBank | numberOfInstalments | frequency | duration | initialPaymentDate | initialPaymentAmount | totalDebtincInt | totalInterest | interestAccrued | planInterest |
      | quoteId1234 | instalmentPaymentAmount | 2021-09-08 | 2021-05-13          |100               |timeToPay       | true           | 1                   | annually  | 12       | 2021-05-13         | 100                  | 100             | 10             | 10             |10            |
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
