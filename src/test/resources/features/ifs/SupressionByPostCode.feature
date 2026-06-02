@suppression
Feature: Suppression by Postcode
  Scenario: Suppression applied to customers latest postcode - 2 postcodes one before interest start date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2020-12-04 | 2021-03-01 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4PR  |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4QR  |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QR  | 2020-12-10   |
      | TW3 4PR  | 2021-04-10   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 35                   | 2314                 | 502314         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 65                   | 35                      | 2314                 | 500000           | 502314             | 500000             |
    And the 1st debt summary will have suppression applied calculation windows
      | dateFrom   | dateTo     | reason      | reasonDesc | postcode |
      | 2020-12-04 | 2021-03-01 | LEGISLATIVE | COVID      | TW3 4PR  |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 35                      | 500071             | false                 |             |             |                                      |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             | false                 |             |             |                                      |


  Scenario: Suppression not applied to customers previous postcode
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans | postcode |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      | EC2M 2LS |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | TW2 4TW  | 2018-07-06   |
      | TW3 4QQ  | 2019-07-06   |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal |
      | 35                   | 2314                 | 502314         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 65                   | 35                      | 502314             |
    And the 1st debt summary will have suppression applied calculation windows
      | dateFrom   | dateTo     | reason      | reasonDesc | postcode | mainTrans |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | EC2M 2LS | 1535      |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | code                                 | description |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 35                      | 71                | 500071             | 500000               | false                 |             |                                      |             |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 2243              | 502243             | 500000               | false                 |             |                                      |             |
    And the 1st debt summary will have suppression applied calculation windows
      | dateFrom   | dateTo     | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | code                                 | description | reasonDesc | postcode | mainTrans |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 35                      | 71                | 500071             | 500000               | false                 |             |                                      |             |            |          |           |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |            |          |           |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       | 0                 | 100000             | 100000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       | COVID      | EC2M 2LS | 1535      |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 2243              | 502243             | 500000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |            |          |           |

  @DTD-400
  Scenario: Suppression applied to customers latest postcode - 2 postcodes
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4QR  |
      | 2020-12-04 | 2021-03-01 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4PR  |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QR  | 2021-04-10   |
      | TW3 4PR  | 2020-12-10   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 35                   | 2243                 | 502243         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 63                   | 35                      | 2243                 | 500000           | 502243             | 500000             |
    And the 1st debt summary will have suppression applied calculation windows
      | dateFrom   | dateTo     | reason      | reasonDesc | postcode |
      | 2020-12-04 | 2021-03-01 | LEGISLATIVE | COVID      | TW3 4PR  |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason                   | description  | code                                 |
      | 2021-02-01 | 2021-02-03 | 2            | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE              | COVID        | Converted from new suppression style |
      | 2021-02-04 | 2021-03-01 | 26           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE; LEGISLATIVE | COVID; COVID | Converted from new suppression style |
      | 2021-03-02 | 2021-05-04 | 64           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE              | COVID        | Converted from new suppression style |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             | false                 |                          |              |                                      |
    And the 1st debt summary will have suppression applied calculation windows
      | dateFrom   | dateTo     | reason      | reasonDesc | postcode |
      | 2020-12-04 | 2021-03-01 | LEGISLATIVE | COVID      | TW3 4PR  |


  #TODO Fails Suppression not applied when customer has 2 or more matching postcodes
#  @DTD-400
#  Scenario: Suppression applied to customers latest postcode 3 postcodes
#    Given suppression configuration data is created
#      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
#      | 2021-01-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4TW  |
#      | 2021-01-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW2 4QQ  |
#      | 2021-01-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4QR  |
#    When suppression configuration is sent to ifs service
#    Given suppression data has been created
#      | reason | description | enabled | fromDate   | toDate     |
#      | POLICY | COVID       | true    | 2021-02-04 | 2021-05-04 |
#    And suppression rules have been created
#      | ruleId | postCode | suppressionIds |
#      | 1      | TW3      | 1              |
#    And a debt item
#      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
#      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
#    And the debt item has no payment history
#    And no breathing spaces have been applied to the debt item
#    And the customer has post codes
#      | postCode | postCodeDate |
#      | TW3 4TW  | 2018-07-06   |
#      | TW2 4QQ  | 2019-07-06   |
#      | TW3 4QR  | 2021-02-06   |
#    When the debt item is sent to the ifs service
#    Then the ifs service wilL return a total debts summary of
#      | combinedDailyAccrual | interestDueCallTotal |
#      | 35                   | 5520                 |
#    And the 1st debt summary will contain
#      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
#      | 155                   | 35                      | 502314             |
#    And the 1st debt summary will have calculation windows
#      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
#      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      |

  Scenario: Suppression Start Date for a Postcode before interest start date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2021-01-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal |
      | 35                   | 2243                 |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 63                   | 35                      | 502243             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual |
      | 2021-02-01 | 2021-05-04 | 92           | 0.0          | 0                       |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      |


  Scenario: Suppression should not be applied where postcode date after suppression period - border case
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
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
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QR  | 2021-05-04   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 35                   | 2314                 | 502314         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 65                   | 35                      | 2314                 | 500000           | 502314             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2021-02-01 | 2021-02-03 | 2            | 2.6          | 35                      | 500071             | false                 |             |             |                                      |
      | 2021-02-04 | 2021-05-04 | 90           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 502243             | false                 |             |             |                                      |


  Scenario Outline: Suppression should be applied to customer sub district
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode   |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | <postCode> |
    When suppression configuration is sent to ifs service

    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
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
      | A99         | A99 9AA  |
      | AA9         | AA9 9AA  |
      | AA99        | AA99 9AA |

  Scenario: Suppression should not be applied for non matching postcodes
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2021-02-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | TW3 4PR  |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | AA9 9AA  | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 35                   | 5520                 | 505520         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 155                  | 35                      | 5520                 | 500000           | 505520             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason | description | code |
      | 2021-02-01 | 2021-07-06 | 155          | 2.6          | 35                      | 505520             | false                 |        |             |      |