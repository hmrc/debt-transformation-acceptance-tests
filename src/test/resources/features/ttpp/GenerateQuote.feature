Feature: Generate Quote test

  Scenario: Retrieve generate quote response from Time to Pay Proxy
    Given a generate quote request
      | customerReference | debtAmount |
      | customerRef1234   | 100        |

    And adHoc values are
      | description  | adHocAmount |
      | description1 | 500         |
      | description2 | 600         |

    And customer values are
      | quoteType   | instalmentStartDate | instalmentAmount | frequency | duration | debtAmount | initialPaymentAmount | initialPaymentDate | paymentPlanType |
      | quoteType   | 2021-01-01          | 100              | 0.6       | 1 month  | 100        | 100                  | 2021-01-01         | paymentPlanType |
      | aaquoteType | 2021-01-01          | 100              | 0.6       | 1 month  | 100        | 100                  | 2021-01-01         | paymentPlanType |

    And Debt is
      | debtId  | mainTrans |
      | some id | 1546      |

    And Duty is
      | dutyId  | subTrans | originalDebtAmount | interestStartDate |
      | some id | 7010     | 100                | 2021-01-01        |

    And Breathing spaces are
      | debtRespiteFrom | debtRespiteTo | paymentDate | paymentAmount |
      | 2021-01-10      | 2021-02-10    | 2021-01-01  | 100           |
      | 2021-03-10      | 2021-04-10    | 2021-01-01  | 200           |

    When the quote is sent to the ttpp service

    Then the ttp service is going to return a response with
      | quoteReference | customerReference | quoteType | totalDebtAmount | numberOfInstalments | totalInterest |
      | quoteRef1234   | customerRef1234   | xyz       | 1000            | 2                   | 0.26          |

    And the 1st instalment will contain
      | dutyId | debtId | dueDate    | amountDue | interestRate | instalmentNumber |
      | duty01 | debt01 | 2021-05-01 | 100       | 0.26         | 1                |
    And the 2nd instalment will contain
      | dutyId | debtId | dueDate    | amountDue | interestRate | instalmentNumber |
      | duty01 | debt01 | 2021-06-01 | 100       | 0.26         | 2                |
