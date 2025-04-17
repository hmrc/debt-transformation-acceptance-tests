@suppression  @2790
Feature: Suppression

  Scenario: Suppression - full address postCode
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | EC2M 2LS |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | postcode |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     | EC2M 2LS |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 9590                 | 509590         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 88                      | 9590                 | 500000           | 509590             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 88                      | 509590             | false                 |             |             |                                      |


  Scenario: Suppression - Partial postCode Suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | postcode |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | EC2M     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2024-03-03   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 9590                 | 509590         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 88                      | 9590                 | 500000           | 509590             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 88                      | 509590             | false                 |             |             |                                      |


  Scenario: Suppression - Missing mainTrans
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2024-03-03   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 9590                 | 509590         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 88                      | 9590                 | 500000           | 509590             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 88                      | 509590             | false                 |             |             |                                      |


  Scenario: Suppression - Missing SubTrans
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2024-03-03   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 9590                 | 509590         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 88                      | 9590                 | 500000           | 509590             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 88                      | 509590             | false                 |             |             |                                      |


  Scenario: Suppression applied - periodEnd
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | checkPeriodEnd |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | true           |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     | 2024-03-01 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 88                   | 9590                 | 509590         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 88                      | 9590                 | 500000           | 509590             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 500000             | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 88                      | 509590             | false                 |             |             |                                      |


  Scenario: Suppression, 2 payments on different dates during suppression
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans | subTrans | postcode | checkPeriodEnd | testRegime                                                                                              |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      | 1000     | EC2M 2LS | true           | fake regime suppressing (MainTrans,SubTrans) = (1234,0123) OR (4567,0456) OR (7890,0789) OR (1535,1000) |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | postcode | periodEnd  |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     | EC2M 2LS | 2024-03-06 |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2024-03-10  |
      | 50000         | 2024-03-15  |
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    And no breathing spaces have been applied to the debt item
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 62                   | 6713                 | 356713         | 350000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 62                      | 6713                 | 350000           | 356713             | 350000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | code                                 | description |
      | 2024-03-01 | 2024-03-15 | 14           | 0.0          | 0                       | 0                 | 50000              | 50000                | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2024-03-01 | 2024-03-10 | 9            | 0.0          | 0                       | 0                 | 100000             | 100000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 350000             | 350000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 62                      | 6713              | 356713             | 350000               | false                 |             |                                      |             |


  Scenario: Suppression, 2 debts 2 payments on same day for one of the debts
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      | 1000     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2024-03-20  |
      | 50000         | 2024-03-20  |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 400000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 133                  | 14385                | 764385         | 750000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 62                      | 6713                 | 350000           | 356713             | 350000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 150000             | 150000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 350000             | 350000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 62                      | 6713              | 356713             | 350000               | false                 |             |             |                                      |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 108                  | 71                      | 7672                 | 400000           | 407672             | 400000             |
    Then the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | code                                 | description |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 400000             | 400000               | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 71                      | 7672              | 407672             | 400000               | false                 |             |                                      |             |


  Scenario: Suppression, 2 payments after suppression dates
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans |
      | 2024-03-01 | 2024-03-20 | LEGISLATIVE | COVID      | SA-Suppression               | 1000     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2024-05-01  |
      | 100000        | 2024-05-20  |
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 53                   | 7582                 | 307582         | 300000            | 300000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 211                  | 53                      | 7582                 | 300000           | 307582             | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 100000             | 100000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-05-01 | 42           | 6.5          | 17                      | 745               | 100745             | 100000               | false                 |             |             |                                      |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 100000             | 100000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-05-20 | 61           | 6.5          | 17                      | 1083              | 101083             | 100000               | false                 |             |             |                                      |
      | 2024-03-01 | 2024-03-20 | 19           | 0.0          | 0                       | 0                 | 300000             | 300000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2024-03-21 | 2024-07-06 | 108          | 6.5          | 53                      | 5754              | 305754             | 300000               | false                 |             |             |                                      |


  Scenario: Suppression, open ended suppression no payment history
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2020-04-04 | 9999-12-31 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-03-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal |
      | 0                    | 0                    | 500000         | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 0                    | 0                       | 0                    | 500000           | 500000             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | code                                 | description |
      | 2021-03-01 | 2021-07-06 | 127          | 0.0          | 0                       | 500000               | 500000             | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |


  Scenario:Suppression, open ended suppression with payment history
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans |
      | 2020-04-04 | 9999-12-31 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-03-01        | 2021-07-06          | 1535      | 1000     |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 200000        | 2021-04-20  |
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 0                    | 0                    | 300000         | 300000            | 300000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 0                    | 0                       | 0                    | 300000           | 300000             | 300000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | amountOnIntDueWindow | unpaidAmountWindow | breathingSpaceApplied | reason      | code                                 | description |
      | 2021-03-01 | 2021-04-20 | 50           | 0.0          | 0                       | 200000               | 200000             | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |
      | 2021-03-01 | 2021-07-06 | 127          | 0.0          | 0                       | 300000               | 300000             | false                 | LEGISLATIVE | Converted from new suppression style | COVID       |


  Scenario: Suppression, 2 debts, 1 matching on period end
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | mainTrans | subTrans |
      | 2021-04-04 | 2021-05-04 | LEGISLATIVE | COVID      | SA-Suppression               | 1535      | 1000     |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2021-04-04 |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 100000        | 2021-03-20  |
      | 50000         | 2021-04-20  |
    And the debt item has no payment history
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2021-02-01        | 2021-07-06          | 1535      | 1000     | 2021-12-21 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 59                   | 8056                 | 858056         | 850000            | 850000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 232                  | 24                      | 350000               | 350000           | 353641             | 350000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2021-02-01 | 2021-03-20 | 47           | 2.6          | 7                       | 334               | 100334             | 100000               | false                 |             |             |                                      |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 3                       | 217               | 50217              | 50000                | false                 |             |             |                                      |
      | 2021-04-04 | 2021-04-20 | 17           | 0.0          | 0                       | 0                 | 50000              | 50000                | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 24                      | 1520              | 351520             | 350000               | false                 |             |             |                                      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 0                 | 350000             | 350000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 24                      | 1570              | 351570             | 350000               | false                 |             |             |                                      |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 124                  | 35                      | 4415                 | 500000           | 504415             | 500000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2021-02-01 | 2021-04-03 | 61           | 2.6          | 35                      | 2172              | 502172             | 500000               | false                 |             |             |                                      |
      | 2021-04-04 | 2021-05-04 | 31           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2021-05-05 | 2021-07-06 | 63           | 2.6          | 35                      | 2243              | 502243             | 500000               | false                 |             |             |                                      |


  Scenario: Suppression applied by all criteria on a single debt item.
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans | mainTrans | checkPeriodEnd | postcode |
      | 2022-01-07 | 2022-01-20 | SUBTRANS    | COVID      | SA-Suppression               | 1000     |           |                |          |
      | 2022-02-07 | 2022-02-20 | MAINTRANS   | COVID      | SA-Suppression               |          | 1535      |                |          |
      | 2022-03-07 | 2022-03-20 | PERIODEND   | COVID      | SA-Suppression               |          |           | true           |          |
      | 2022-04-07 | 2022-04-20 | LEGISLATIVE | COVID      | SA-Suppression               |          |           |                | EC2M 2LS |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     | 2022-03-09 |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2022-01-01   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 51                   | 5682                 | 505682         | 500000            | 500000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 130                  | 51                      | 5682                 | 500000           | 505682             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-01-01 | 2022-01-06 | 5            | 2.6          | 35                      | 178               | 500178             | 500000               | false                 |             |             |                                      |
      | 2022-01-07 | 2022-01-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | SUBTRANS    | COVID       | Converted from new suppression style |
      | 2022-01-21 | 2022-02-06 | 17           | 2.75         | 37                      | 640               | 500640             | 500000               | false                 |             |             |                                      |
      | 2022-02-07 | 2022-02-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | MAINTRANS   | COVID       | Converted from new suppression style |
      | 2022-02-21 | 2022-03-06 | 14           | 3.0          | 41                      | 575               | 500575             | 500000               | false                 |             |             |                                      |
      | 2022-03-07 | 2022-03-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | PERIODEND   | COVID       | Converted from new suppression style |
      | 2022-03-21 | 2022-04-04 | 15           | 3.0          | 41                      | 616               | 500616             | 500000               | false                 |             |             |                                      |
      | 2022-04-05 | 2022-04-06 | 2            | 3.25         | 44                      | 89                | 500089             | 500000               | false                 |             |             |                                      |
      | 2022-04-07 | 2022-04-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-04-21 | 2022-05-23 | 33           | 3.25         | 44                      | 1469              | 501469             | 500000               | false                 |             |             |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 502013             | 500000               | false                 |             |             |                                      |
      | 2022-07-05 | 2022-07-06 | 2            | 3.75         | 51                      | 102               | 500102             | 500000               | false                 |             |             |                                      |


   #TODO Test Fails as interest due is zero instead of 6.5 to be fix in DTD-3325
  @wip
  Scenario: Suppression applied by all criteria on 2 debt items.
    Given suppression configuration data is created
      | dateFrom   | dateTo     | reason      | reasonDesc | suppressionChargeDescription | subTrans | mainTrans | checkPeriodEnd | postcode |
      | 2022-01-07 | 2022-01-20 | SUBTRANS    | COVID      | SA-Suppression               | 1000     |           |                |          |
      | 2024-03-07 | 2024-04-20 | MAINTRANS   | COVID      | SA-Suppression               |          | 1535      |                |          |
      | 2022-03-07 | 2022-03-20 | PERIODEND   | COVID      | SA-Suppression               |          |           | true           |          |
      | 2022-04-07 | 2022-04-20 | LEGISLATIVE | COVID      | SA-Suppression               |          |           |                | EC2M 2LS |
    When suppression configuration is sent to ifs service
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | periodEnd  |
      | 500000         | 2022-01-01        | 2022-07-06          | 1535      | 1000     | 2022-03-09 |
    And the debt item has no payment history
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 400000         | 2024-03-01        | 2024-07-06          | 1535      | 1000     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And the customer has post codes
      | postCode | postCodeDate |
      | EC2M 2LS | 2022-01-01   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | amountIntTotal | unpaidAmountTotal | amountOnIntDueTotal |
      | 122                  | 11678                | 911678         | 900000            | 900000              |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 144                  | 51                      | 6209                 | 500000           | 506209             | 500000             |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason      | description | code                                 |
      | 2022-01-01 | 2022-01-06 | 5            | 2.6          | 35                      | 178               | 500178             | 500000               | false                 |             |             |                                      |
      | 2022-01-07 | 2022-01-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | SUBTRANS    | COVID       | Converted from new suppression style |
      | 2022-01-21 | 2022-02-20 | 31           | 2.75         | 37                      | 1167              | 501167             | 500000               | false                 |             |             |                                      |
      | 2022-02-21 | 2022-03-06 | 14           | 3.0          | 41                      | 575               | 500575             | 500000               | false                 |             |             |                                      |
      | 2022-03-07 | 2022-03-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | PERIODEND   | COVID       | Converted from new suppression style |
      | 2022-03-21 | 2022-04-04 | 15           | 3.0          | 41                      | 616               | 500616             | 500000               | false                 |             |             |                                      |
      | 2022-04-05 | 2022-04-06 | 2            | 3.25         | 44                      | 89                | 500089             | 500000               | false                 |             |             |                                      |
      | 2022-04-07 | 2022-04-20 | 14           | 0.0          | 0                       | 0                 | 500000             | 500000               | false                 | LEGISLATIVE | COVID       | Converted from new suppression style |
      | 2022-04-21 | 2022-05-23 | 33           | 3.25         | 44                      | 1469              | 501469             | 500000               | false                 |             |             |                                      |
      | 2022-05-24 | 2022-07-04 | 42           | 3.5          | 47                      | 2013              | 502013             | 500000               | false                 |             |             |                                      |
      | 2022-07-05 | 2022-07-06 | 2            | 3.75         | 51                      | 102               | 500102             | 500000               | false                 |             |             |                                      |
    And the 2nd debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty | totalAmountIntDuty | amountOnIntDueDuty |
      | true            | 77                   | 71                      | 5469                 | 400000           | 405469             | 400000             |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | interestDueWindow | unpaidAmountWindow | amountOnIntDueWindow | breathingSpaceApplied | reason | description | code |
      | 2024-03-01 | 2024-03-06 | 5            | 2.6          | 35                      | 178               | 500178             | 500000               | false                 |        |             |      |
