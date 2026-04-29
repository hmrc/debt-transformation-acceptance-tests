Feature: Debt Calculation Validation

# Test redundant - Scenario not explicitly defined in requirements  - see DTD-775
#  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
#    Given a debt item
#      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
#      | 1.2            | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
#    And the debt item has no payment history
#    And no breathing spaces have been applied to the debt item
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
    And no breathing spaces have been applied to the debt item
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
    And no breathing spaces have been applied to the debt item
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
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                      |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/dateCreated' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt empty interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        |                     | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
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
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                      | message                                                              |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/interestRequestedTo' missing or invalid |

  Scenario: TPSS debt empty mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          |           | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                            | message                                                                                                          |
      | 400        | Invalid mainTrans and/or subTrans | Invalid mainTrans and/or subTrans. The mainTrans:  and subTrans: invalid values are not found in any stored rule |

  Scenario: TPSS MainTrans (1525) debt invalid subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | invalid  | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                            | message                                                                                                          |
      | 400        | Invalid mainTrans and/or subTrans | Invalid mainTrans and/or subTrans. The mainTrans:  and subTrans: invalid values are not found in any stored rule |

  Scenario: TPSS MainTrans (1525) debt empty subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      |          | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                            | message                                                                                                          |
      | 400        | Invalid mainTrans and/or subTrans | Invalid mainTrans and/or subTrans. The mainTrans:  and subTrans: invalid values are not found in any stored rule |

  @DTD-2216
  Scenario: 1 SA debt - 2 payment amounts with one of them less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 50000          | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 1000          | 2019-02-03  |
      | -1000         | 2019-03-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                          | message                                                                                                        |
      | 400        | Could not validate Json request | Could not parse body due to requirement failed: paymentAmount can be zero or greater, negative values are not accepted |

  @DTD-2216
  Scenario: SA original amount and payment amount less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | -50000         | 2019-12-16        | 2020-05-05          | 4990      | 1090     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | -1000         | 2019-02-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                          | message                                                                                                        |
      | 400        | Could not validate Json request | Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted |

  @DTD-2216
  Scenario: SA original amount less than zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | -50000         | 2019-12-16        | 2020-05-05          | 5200      | 1553     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                          | message                                                                                                        |
      | 400        | Could not validate Json request | Could not parse body due to requirement failed: originalAmount can be zero or greater, negative values are not accepted |

  @DTD-2216
  Scenario: SA Original and payment amounts can be equal to zero
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 0              | 2019-12-16        | 2020-05-05          | 5200      | 1553     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 0             | 2019-02-03  |
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 0                 | 0              | 0                   |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 0                    | 0                       | 0                    | 0                | 0                  | 0                  | false                 |