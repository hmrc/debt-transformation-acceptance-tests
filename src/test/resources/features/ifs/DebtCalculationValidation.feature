Feature: Debt Calculation Validation

  Scenario: TPSS MainTrans 1525 debt Amount is negative - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | -1             | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                                                                                  |
      | 400        | Invalid JSON error from IFS | Could not parse body due to requirement failed: Original Amount can be zero or greater, negative values are not accepted |

  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | \"\"           | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                         |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/originalAmount' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt Amount non integer - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 1.2            | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                         |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/originalAmount' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt invalid entry in Date Created - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | d           | 2021-03-01        | 2021-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                      |
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
      | statusCode | reason       | message                                                      |
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
      | statusCode | reason       | message                                                      |
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
      | statusCode | reason       | message                                                                              |
      | 400        | Invalid JSON error from IFS | Interest Start Date missing. This is mandatory for interest bearing debts|

  Scenario: TPSS MainTrans (1525) debt empty interestRequestedTo - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-08  | 2021-03-08        |                     | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                              |
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
      | statusCode | reason       | message                                                              |
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
      | statusCode | reason       | message                                                    |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/mainTrans' missing or invalid |

  Scenario: TPSS debt empty mainTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          |           | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                    |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/mainTrans' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt invalid subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      | invalid  | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                   |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/subTrans' missing or invalid |

  Scenario: TPSS MainTrans (1525) debt empty subTrans - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-03-01  | 2021-03-01        | 2021-03-08          | 1525      |          | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                   |
      | 400        | Invalid JSON error from IFS | Field at path '/debtItems(0)/subTrans' missing or invalid |

  Scenario: TPSS interestStartDate debt before 2001 jan 01 - Edge Case
    Given a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2000-03-01  | 2000-12-01        | 2001-03-08          | 1525      | 1000     | true            |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason       | message                                                                              |
      | 400        | Invalid JSON error from IFS | Invalid Interest Start Date. IFS does not store or calculate historic interest rates |
