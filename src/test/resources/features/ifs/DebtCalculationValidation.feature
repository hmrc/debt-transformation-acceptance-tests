Feature: Debt Calculation Validation

  Scenario: Send error message where no debt items are provided when IFS is called DTD-545
    Given no debt item
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                                                            |
      | 400        | Invalid JSON error from IFS | Could not parse body due to requirement failed: Debt item charges which are mandatory, are missing |

  Scenario: TPSS MainTrans 1525 debt Amount is negative - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | -1             | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                                                                                                                                                  |
      | 400        | Invalid JSON error from IFS | Could not parse body due to requirement failed: Original Amount can be zero or greater, negative values are not accepted; Amount paid in payments cannot be greater than Original Amount |

  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | \"\"           | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                         |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/originalAmount' missing or invalid |


# Test redundant - Scenario not explicitly defined in requirements  - see DTD-775
#  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
#      | 1.2            | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    And no breathing spaces have been applied to the customer
#    And no post codes have been provided for the customer
#    When the debt item is sent to the ifs service
#    Then the ifs service will respond with
#      | statusCode | reason                      | message                                                         |
#      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/originalAmount' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt invalid entry in Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | d           | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                      |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/dateCreated' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt empty entry in Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         |             | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                      |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/dateCreated' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt invalid Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-30  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                      |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/dateCreated' missing or invalid |

  Scenario: interestStartDate should be mandatory for interest bearing debts - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-04-03  | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                                   |
      | 400        | Invalid JSON error from IFS | Interest Start Date missing. This is mandatory for interest bearing debts |

  Scenario: TPSS MainTrans (1525) debt empty interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        |                     | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                              |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/interestRequestedTo' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt invalid interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-02-01  | 2021-02-01        | 2021-02-30          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                              |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/interestRequestedTo' missing or invalid |

  Scenario: Debt invalid mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 99999     | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                           |
      | 400        | Invalid JSON error from IFS | Invalid mainTrans and/or subTrans |

  Scenario: TPSS debt empty mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          |           | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                           |
      | 400        | Invalid JSON error from IFS | Invalid mainTrans and/or subTrans |

  Scenario: TPSS MainTrans (1525) debt invalid subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | invalid  | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                           |
      | 400        | Invalid JSON error from IFS | Invalid mainTrans and/or subTrans |

  Scenario: TPSS MainTrans (1525) debt empty subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      |          | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                           |
      | 400        | Invalid JSON error from IFS | Invalid mainTrans and/or subTrans |

  Scenario: TPSS interestStartDate debt before 1999-03-06 - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 1999-04-01  | 1999-03-05        | 2001-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                                              |
      | 400        | Invalid JSON error from IFS | Invalid Interest Start Date. IFS does not store or calculate historic interest rates |

  @DTD-2216
  Scenario: 1 SA debt with a payment amount greater than original debt amount
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50             | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 1000          | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: Amount paid in payments cannot be greater than Original Amount

  @wip @DTD-2216
  Scenario: 1 SA debt with a payment amount less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50             | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | -1000         | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: paymentAmount can be zero or greater, negative values are not accepted

  @wip @DTD-2216
  Scenario: 1 SA debt - 2 payment amounts with one of them less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50000          | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 1000          | 2019-02-03  |
      | -1000         | 2019-03-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: paymentAmount can be zero or greater, negative values are not accepted

  @wip @DTD-2216
  Scenario: SA original amount and payment amount less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | -50000         | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | -1000         | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with originalAmount can be zero or greater, negative values are not accepted; paymentAmount can be zero or greater, negative values are not accepted

  @wip @DTD-2216
  Scenario: SA original amount less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | -50000         | 2019-12-16        | 2020-05-05          | 5200      | 1553     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted

  @wip  @DTD-2216
  Scenario: SA Original and payment amounts can be equal to zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 0              | 2019-12-16        | 2020-05-05          | 5200      | 1553     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 0            | 2019-02-03  |
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 0                 | 0              | 0                   |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 0                    | 0                       | 0                    | 0           | 0                  | 0                  | false                 |
