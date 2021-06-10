
Feature: Suppression by Postcode

  Scenario: Suppression applied to postcode
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-04-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal | amountIntTotal | amountOnIntDueTotal |
      | 35                   | 4415                 | 500000            | 504415         | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 124                  | 35                      | 4415                 | 500000           | 504415             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | reason | code |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 35                      | 502172             |        |      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 500000             | POLICY | 1    |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             |        |      |


  Scenario: Suppression not applied to customers previous postcode
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW2 4TW  | 2018-07-06   |
      | TW3 4QQ  | 2019-07-06   |
      | TW43 4QR | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 5520                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 155                  | 35                      | 505520             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |

 #TODO Fails Suppression not applied when customer has 2 matching postcodes
  @wip @DTD-400
  Scenario: Suppression applied to customers latest postcode - 2 postcodes
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
      | TW3 4QR  | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 2314                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 65                   | 35                      | 502314             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |

  #TODO Fails Suppression not applied when customer has 2 or more matching postcodes
  @wip @DTD-400
  Scenario: Suppression applied to customers latest postcode 3 postcodes
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4TW  | 2018-07-06   |
      | TW2 4QQ  | 2019-07-06   |
      | TW3 4QR  | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 2314                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 65                   | 35                      | 502314             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |


  Scenario: Suppression should not be applied where postcode date after suppression period - border case
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QR  | 2021-05-05   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 5520                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 155                  | 35                      | 505520             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |

  Scenario: Suppression should be applied if customer moved in on last day of suppression period - border case
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QR  | 2021-05-04   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 2314                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 65                   | 35                      | 502314             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 35                      |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      |

  Scenario Outline: Suppression should be applied to customer sub district
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode      | suppressionIds |
      | 1      | <subDistrict> | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode   | postCodeDate |
      | <postCode> | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | interestDueCallTotal |
      | 2314                 |
    Examples:
      | subDistrict | postCode |
      | AA9A        | AA9A 9AA |
      | A9A         | A9A 9AA  |
      | A9          | A9 9AA   |
      | A99         | A99 9AA  |
      | AA9         | AA9 9AA  |
      | AA99        | AA99 9AA |

  Scenario Outline: Suppression should not be applied for non matching postcodes
    Given suppression data has been created
      | reason | enabled | fromDate   | toDate     |
      | POLICY | true    | 2021-02-04 | 2021-05-04 |
    And suppression rules have been created
      | ruleId | postCode      | suppressionIds |
      | 1      | <subDistrict> | 1              |
    And a debt item
      | originalAmount | dateCreated | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-01-01  | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the customer
    And the customer has post codes
      | postCode   | postCodeDate |
      | <postCode> | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 5520                 |
    Examples:
      | subDistrict | postCode |
      | AA99        | AA9 9AA |
