@DTD-2140 @DTD-2244 @DTD-2273 @DTD-2274
Feature: Breathing Space

  @DTD-2244 @DTD-2273 @DTD-2274
  Scenario: Interest Bearing. Single debt with breathing space and no payment history (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2019-01-03      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 44                   | 3872                 | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 87                   | 44                      | 3872                 | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2018-12-16 | 2019-01-02 | 17           | 3.25         | 44                      | 500756             | false                 |
      | 2019-01-03 | 2019-02-03 | 32           | 0.0          | 0                       | 500000             | true                  |
      | 2019-02-04 | 2019-04-14 | 70           | 3.25         | 44                      | 503116             | false                 |

  @DTD-2244
  Scenario: 2 debts with breathing space. No payment history (Scenario 1 - step 6) (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-01-31        | 2022-05-15          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-03-01      | 2022-04-29    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-01-31        | 2022-05-15          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-03-01      | 2022-04-29    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 8                    | 100356         |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 44                   | 4                       | 50178              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 3                       | 50075              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 4                       | 50032              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 50000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 50000              | true                  |
      | 2022-04-30 | 2022-05-15 | 16           | 3.25         | 4                       | 50071              | false                 |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 44                   | 4                       | 50178              |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 3                       | 50075              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 4                       | 50032              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 50000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 50000              | true                  |
      | 2022-04-30 | 2022-05-15 | 16           | 3.25         | 4                       | 50071              | false                 |

  @DTD-2140 @DTD-2243
  Scenario: Single debt with breathing space AND payment history (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-04-06        | 2022-04-29          | 4920      | 1553     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 20000         | 2022-04-24  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-04-10      | 2022-04-20    |
    And no breathing spaces have been applied to the debt item
    And the debt item has no payment history
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 2                    | 30044          |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 19                   | 2                       | 30044              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-04-06 | 2022-04-09 | 3            | 3.25         | 1                       | 20005              | false                 |
      | 2022-04-10 | 2022-04-20 | 11           | 0.0          | 0                       | 20000              | true                  |
      | 2022-04-21 | 2022-04-24 | 4            | 3.25         | 1                       | 20007              | false                 |
      | 2022-04-06 | 2022-04-09 | 3            | 3.25         | 2                       | 30008              | false                 |
      | 2022-04-10 | 2022-04-20 | 11           | 0.0          | 0                       | 30000              | true                  |
      | 2022-04-21 | 2022-04-29 | 9            | 3.25         | 2                       | 30024              | false                 |

  @DTD-2140 @DTD-2243
  Scenario: 2 debts one with a breathing space and payment history plus a late payment debt (Scenario 1, Step 7) (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-01-31        | 2022-06-10          | 4920      | 1553     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 25000         | 2022-05-30  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-03-01      | 2022-04-29    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 1500           | 2034-11-12        | 2022-06-10          | 1520      | 1090     | false           |
    And no breathing spaces have been applied to the debt item
    And the debt item has no payment history
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 2                    | 26771          |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 129                  | 2                       | 25271              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 1                       | 25037              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 2                       | 25016              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-30 | 2022-05-23 | 24           | 3.25         | 2                       | 25053              | false                 |
      | 2022-05-24 | 2022-05-30 | 7            | 3.5          | 2                       | 25016              | false                 |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 1                       | 25037              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 2                       | 25016              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-30 | 2022-05-23 | 24           | 3.25         | 2                       | 25053              | false                 |
      | 2022-05-24 | 2022-06-10 | 18           | 3.5          | 2                       | 25043              | false                 |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 0                    | 0                       | 1500               |

  @DTD-2140 @DTD-2243
  Scenario: 1 debt with a payment and 2 breathing spaces (incl an open ended BS), 1 late payment debt, 3rd debt with BS (Scenario 2, Step 4) (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-01-31        | 2022-06-19          | 4920      | 1553     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 25000         | 2022-05-30  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-03-01      | 2022-04-29    |
      | 2022-06-01      | 2034-06-17    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 1500           | 2034-11-12        | 2022-06-10          | 1520      | 1090     | false           |
    And no breathing spaces have been applied to the debt item
    And the debt item has no payment history
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 50000          | 2022-07-30        | 2022-08-10          | 4920      | 1553     | true            |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-08-01      | 2034-06-17    |
    And the debt item has no payment history
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 0                    | 76752          |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 119                  | 0                       | 25247              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 1                       | 25037              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 2                       | 25016              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-30 | 2022-05-23 | 24           | 3.25         | 2                       | 25053              | false                 |
      | 2022-05-24 | 2022-05-30 | 7            | 3.5          | 2                       | 25016              | false                 |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 1                       | 25037              | false                 |
      | 2022-02-21 | 2022-02-28 | 8            | 3.0          | 2                       | 25016              | false                 |
      | 2022-03-01 | 2022-04-04 | 35           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-05 | 2022-04-29 | 25           | 0.0          | 0                       | 25000              | true                  |
      | 2022-04-30 | 2022-05-23 | 24           | 3.25         | 2                       | 25053              | false                 |
      | 2022-05-24 | 2022-05-31 | 8            | 3.5          | 2                       | 25019              | false                 |
      | 2022-06-01 | 2022-06-19 | 19           | 0.0          | 0                       | 25000              | true                  |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 0                    | 0                       | 1500               |
    And the 3rd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 1                    | 0                       | 50005              |
    And the 3rd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-07-30 | 2022-07-31 | 1            | 3.75         | 5                       | 50005              | false                 |
      | 2022-08-01 | 2022-08-10 | 10           | 0.0          | 0                       | 50000              | true                  |

  @DTD-2140
  Scenario: Customer makes payment whilst in an active Breathing Space period (Scenario 4) (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 25000          | 2022-01-31        | 2022-08-01          | 4920      | 1553     | true            |
    And the debt item has payment history
      | paymentAmount | paymentDate |
      | 10000         | 2022-07-01  |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-06-01      | 2022-07-30    |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 15000          | 2022-05-30        | 2022-08-01          | 4920      | 1553     | true            |
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-06-01      | 2022-07-30    |
    And the debt item has no payment history
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | amountIntTotal |
      | 2                    | 30258          |
    And the 1st debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 242                  | 1                       | 15254              |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 0                       | 10015              | false                 |
      | 2022-02-21 | 2022-04-04 | 43           | 3.0          | 0                       | 10035              | false                 |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 0                       | 10043              | false                 |
      | 2022-05-24 | 2022-05-31 | 8            | 3.5          | 0                       | 10007              | false                 |
      | 2022-06-01 | 2022-07-01 | 31           | 0.0          | 0                       | 10000              | true                  |
      | 2022-01-31 | 2022-02-20 | 20           | 2.75         | 1                       | 15022              | false                 |
      | 2022-02-21 | 2022-04-04 | 43           | 3.0          | 1                       | 15053              | false                 |
      | 2022-04-05 | 2022-05-23 | 49           | 3.25         | 1                       | 15065              | false                 |
      | 2022-05-24 | 2022-05-31 | 8            | 3.5          | 1                       | 15011              | false                 |
      | 2022-06-01 | 2022-07-04 | 34           | 0.0          | 0                       | 15000              | true                  |
      | 2022-07-05 | 2022-07-30 | 26           | 0.0          | 0                       | 15000              | true                  |
      | 2022-07-31 | 2022-08-01 | 2            | 3.75         | 1                       | 15003              | false                 |
    And the 2nd debt summary will contain
      | numberChargeableDays | interestDueDailyAccrual | totalAmountIntDuty |
      | 3                    | 1                       | 15004              |
    And the 2nd debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-05-30 | 2022-05-31 | 1            | 3.5          | 1                       | 15001              | false                 |
      | 2022-06-01 | 2022-07-04 | 34           | 0.0          | 0                       | 15000              | true                  |
      | 2022-07-05 | 2022-07-30 | 26           | 0.0          | 0                       | 15000              | true                  |
      | 2022-07-31 | 2022-08-01 | 2            | 3.75         | 1                       | 15003              | false                 |

  @DTD-2167 @DTD-2244
  Scenario: Interest Bearing. Breathing space that starts before the interest start date (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2017-01-03      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 44                   | 3116                 | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 70                   | 44                      | 3872                 | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2018-12-16 | 2019-02-03 | 49           | 0.0          | 0                       | 500000             | true                  |
      | 2019-02-04 | 2019-04-14 | 70           | 3.25         | 44                      | 503116             | false                 |

  @DTD-2167 @DTD-2244
  Scenario: Interest Bearing. Breathing space that starts before the interest start date and ends after the interest end date (VAT)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 4766      | 1090     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2017-01-03      | 2019-05-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 0                    | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 0                    | 0                       | 3872                 | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2018-12-16 | 2019-04-14 | 119          | 0.0          | 0                       | 500000             | true                  |

  @DTD-2168 @DTD-2244
  Scenario: Interest Bearing. Breathing space that starts same day as interest start date (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2018-12-16      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 44                   | 3116                 | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 70                   | 44                      | 3872                 | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2018-12-16 | 2019-02-03 | 49           | 0.0          | 0                       | 500000             | true                  |
      | 2019-02-04 | 2019-04-14 | 70           | 3.25         | 44                      | 503116             | false                 |

  @DTD-2168 @DTD-2244
  Scenario: Non Interest Bearing. Breathing space that starts same day as interest start date (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2018-12-16        | 2019-04-14          | 5071      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2018-12-16      | 2019-02-03    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 0                    | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | false           | 0                    | 0                       | 0                    | 500000           |

  @DTD-2371
  Scenario: Breathing space that ends same day as interest requested
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2024-01-01        | 2024-01-10          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2024-01-04      | 2024-01-10    |
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 177                  | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 2                    | 0                       | 177                  | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2024-01-01 | 2024-01-03 | 2            | 6.5          | 88                      | 500177             | false                 |
      | 2024-01-04 | 2024-01-10 | 7            | 0.0          | 0                       | 500000             | true                  |

  @DTD-2371
  Scenario: Breathing space that ends same day as interest requested to with a suppression(SA)
    Given suppression data has been created
      | reason | description | enabled | fromDate   | toDate     |
      | POLICY | COVID       | true    | 2024-02-01 | 2024-05-04 |
    And suppression rules have been created
      | ruleId | postCode | suppressionIds |
      | 1      | TW3      | 1              |
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2024-01-01        | 2024-01-10          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2024-01-04      | 2024-01-10    |
    And the customer has post codes
      | postCode | postCodeDate |
      | TW3 4QQ  | 2019-07-06   |
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 177                  | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 2                    | 0                       | 177                  | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2024-01-01 | 2024-01-03 | 2            | 6.5          | 88                      | 500177             | false                 |
      | 2024-01-04 | 2024-01-10 | 7            | 0.0          | 0                       | 500000             | true                  |

  @DTD-2371
  Scenario: Interest Bearing. Breathing space that ends same day as interest requested to. Breathing space includes interest rate change(SA)
    Given  a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2022-01-01        | 2022-01-10          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2022-01-04      | 2022-01-10    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 71                   | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 2                    | 0                       | 72                   | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2022-01-01 | 2022-01-03 | 2            | 2.6          | 35                      | 500071             | false                 |
      | 2022-01-04 | 2022-01-06 | 3            | 0.0          | 0                       | 500000             | true                  |
      | 2022-01-07 | 2022-01-10 | 4            | 0.0          | 0                       | 500000             | true                  |

  @DTD-2371
  Scenario: Interest Bearing. 2 breathing spaces. First ends same day as interest requested to (SA)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-01-01        | 2021-01-10          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-01-04      | 2021-01-10    |
      | 2021-03-01      | 2021-03-10    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 0                    | 71                   | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 2                    | 0                       | 72                   | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2021-01-01 | 2021-01-03 | 2            | 2.6          | 35                      | 500071             | false                 |
      | 2021-01-04 | 2021-01-10 | 7            | 0.0          | 0                       | 500000             | true                  |

  @DTD-2351
  Scenario: Interest Bearing. Overlapping breathing spaces should be merged into 1 calculation window. No interest rate changes (SA)
    Given  a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans | interestBearing |
      | 500000         | 2021-01-01        | 2021-01-10          | 4920      | 1553     | true            |
    And the debt item has no payment history
    And the debt item has breathing spaces applied
      | debtRespiteFrom | debtRespiteTo |
      | 2021-01-04      | 2021-01-07    |
      | 2021-01-07      | 2021-01-09    |
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service wilL return a total debts summary of
      | combinedDailyAccrual | interestDueCallTotal | unpaidAmountTotal |
      | 35                   | 106                  | 500000            |
    And the 1st debt summary will contain
      | interestBearing | numberChargeableDays | interestDueDailyAccrual | interestDueDutyTotal | unpaidAmountDuty |
      | true            | 3                    | 35                      | 106                  | 500000           |
    And the 1st debt summary will have calculation windows
      | periodFrom | periodTo   | numberOfDays | interestRate | interestDueDailyAccrual | unpaidAmountWindow | breathingSpaceApplied |
      | 2021-01-01 | 2021-01-03 | 2            | 2.6          | 35                      | 500071             | false                 |
      | 2021-01-04 | 2021-01-09 | 6            | 0.0          | 0                       | 500000             | true                  |
      | 2021-01-10 | 2021-01-10 | 1            | 2.6          | 35                      | 500035             | false                 |
