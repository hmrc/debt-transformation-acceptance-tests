@suppression @DTD-2790
Feature: Suppression - Edge cases

  Scenario: Suppression, interest rate change during suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2022-01-07 | 2022-04-05 | LEGISLATIVE | COVID      | SA-Suppression               |  EC2M 2LS |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | postcode |
      | 500000         | 2022-04-01        | 2022-07-06          | 1535      | 1000     | EC2M 2LS |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-01-05   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 4251                 | 504251         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 92                   | 51                      | 4251                 | 500000           | 504251             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-04-01 | 2022-04-04 | 3            | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-04-05 | 2022-04-05 | 1            | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-04-06 | 2022-05-23 | 48           | 3.25         | 44                      | 2136              | 500000               | 502136             | false                 |             |             |                                      |


  Scenario: Suppression, interest rate change after suppression ends
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans  |
      | 2022-02-21 | 2022-04-04 | LEGISLATIVE | COVID      | SA-Suppression               | 1535       |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-02-01        | 2022-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5011                 | 505011         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 112                  | 51                      | 5011                 | 500000           | 505011             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-02-01 | 2022-02-20 | 19           | 2.75         | 37                      | 715               | 500000               | 500715             | false                 |             |             |                                      |
      | 2022-02-21 | 2022-04-04 | 43           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 44                      | 2181              | 500000               | 502181             | false                 |             |             |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 500000               | 502013             | false                 |             |             |                                      |
      | 2022-07-05 | 2022-07-06 | 2            | 3.75         | 51                      | 102               | 500000               | 500102             | false                 |             |             |                                      |


  Scenario: Suppression - interest rate change before suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2022-01-07 | 2022-03-05 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5706                 | 505706         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 128                  | 51                      | 5706                 | 500000           | 505706             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-01-01 | 2022-01-06 | 5            | 2.6          | 35                      | 178               | 500000               | 500178             | false                 |             |             |                                      |
      | 2022-01-07 | 2022-02-20 | 45           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-02-21 | 2022-03-05 | 13           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-03-06 | 2022-04-04 | 30           | 3.0          | 41                      | 1232              | 500000               | 501232             | false                 |             |             |                                      |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 44                      | 2181              | 500000               | 502181             | false                 |             |             |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 500000               | 502013             | false                 |             |             |                                      |


  Scenario: Suppression, interest rate change before and after suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2022-01-07 | 2022-03-05 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5706                 | 505706         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 128                  | 51                      | 5706                 | 500000           | 505706             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-01-01 | 2022-01-06 | 5            | 2.6          | 35                      | 178               | 500000               | 500178             | false                 |             |             |                                      |
      | 2022-01-07 | 2022-02-20 | 45           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-02-21 | 2022-03-05 | 13           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-03-06 | 2022-04-04 | 30           | 3.0          | 41                      | 1232              | 500000               | 501232             | false                 |             |             |                                      |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 44                      | 2181              | 500000               | 502181             | false                 |             |             |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 500000               | 502013             | false                 |             |             |                                      |


  Scenario: Suppression, 1 debt, 2 overlapping suppressions that start on same day
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2022-01-07 | 2022-03-05 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
      | 2022-01-07 | 2022-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5090                 | 505090         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 113                  | 51                      | 5090                 | 500000           | 505090             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason                   | description  | code                                 |
      | 2022-01-01 | 2022-01-06 | 5            | 2.6          | 35                      | 178               | 500000               | 500178             | false                 |                          |              |                                      |
      | 2022-01-07 | 2022-02-20 | 45           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE; LEGISLATIVE | COVID; COVID | Converted from new suppression style |
      | 2022-02-21 | 2022-03-05 | 13           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE; LEGISLATIVE | COVID; COVID | Converted from new suppression style |
      | 2022-03-06 | 2022-03-20 | 15           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE              | COVID        | Converted from new suppression style |
      | 2022-03-21 | 2022-04-04 | 15           | 3.0          | 41                      | 616               | 500000               | 500616             | false                 |                          |              |                                      |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 44                      | 2181              | 500000               | 502181             | false                 |                          |              |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 500000               | 502013             | false                 |                          |              |                                      |
      | 2022-07-05 | 2022-07-06 | 2            | 3.75         | 51                      | 102               | 500000               | 500102             | false                 |                          |              |                                      |


  Scenario: Suppression, 1 debt, 2 overlapping suppressions - starting on different dates
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2022-01-07 | 2022-03-05 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
      | 2022-01-06 | 2022-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5054                 | 505054         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 112                  | 51                      | 5054                 | 500000           | 505054             | 500000             | false                 |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason                   | description  | code                                 |
      | 2022-01-01 | 2022-01-05 | 4            | 2.6          | 35                      | 142               | 500000               | 500142             | false                 |                          |              |                                      |
      | 2022-01-06 | 2022-01-06 | 1            | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE              | COVID        | Converted from new suppression style |
      | 2022-01-07 | 2022-02-20 | 45           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE; LEGISLATIVE | COVID; COVID | Converted from new suppression style |
      | 2022-02-21 | 2022-03-05 | 13           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE; LEGISLATIVE | COVID; COVID | Converted from new suppression style |
      | 2022-03-06 | 2022-03-20 | 15           | 0.0          | 0                       | 0                 | 500000               | 500000             | false                 | LEGISLATIVE              | COVID        | Converted from new suppression style |
      | 2022-03-21 | 2022-04-04 | 15           | 3.0          | 41                      | 616               | 500000               | 500616             | false                 |                          |              |                                      |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 44                      | 2181              | 500000               | 502181             | false                 |                          |              |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 500000               | 502013             | false                 |                          |              |                                      |
      | 2022-07-05 | 2022-07-06 | 2            | 3.75         | 51                      | 102               | 500000               | 500102             | false                 |                          |              |                                      |


  Scenario: Suppression period starts on same day as interest start date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2024-03-01 | 2024-04-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 6837                 | 506837         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 77                   | 88                      | 6837                 | 500000           | 506837             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-04-20 | 50           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-04-21 | 2024-07-06 | 77           | 6.5          | 88                      | 506837             | false                 |             |             |                                      |


  Scenario: Suppression period starts before interest start date
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2024-03-01 | 2024-04-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-04-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 6837                 | 506837         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 77                   | 88                      | 6837                 | 500000           | 506837             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-04-01 | 2024-04-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-04-21 | 2024-07-06 | 77           | 6.5          | 88                      | 506837             | false                 |             |             |                                      |


  Scenario: Suppression, interest rate change before suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2020-04-03 | 2020-04-04 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2020-03-01        | 2020-04-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M     | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 37                   | 1468                 | 501468         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 34                   | 37                      | 1468                 | 500000           | 501468             | 500000             |
    Then the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | amountOnIntDueWindow | reason      | code                                 | description |
      | 2020-03-01 | 2020-03-29 | 28           | 3.25         | 500000               |             |                                      |             |
      | 2020-03-30 | 2020-04-02 | 4            | 2.75         | 500000               |             |                                      |             |
      | 2020-04-03 | 2020-04-04 | 2            | 0.0          | 500000               | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2020-04-05 | 2020-04-06 | 2            | 2.75         | 500000               |             |                                      |             |


  Scenario: Suppression on non interest bearing debt
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2021-01-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | 1090     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-02-01        | 2021-07-06          | 1520      | 1090     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M     | 2018-07-06   |
      | EC2M 4QQ | 2019-07-06   |
      | EC2M 4QR | 2020-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 0                   | 0                 | 500000         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | false            | 0                   | 0                      | 0                 | 500000           | 500000             | 500000             |
    And the 1st debt summary will not have any calculation windows
