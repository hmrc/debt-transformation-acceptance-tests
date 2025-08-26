Feature: Get Debt For all the SUPPORTED REGIMES

  Scenario Outline: Interest Bearing TPSS MainTrans SubTrans
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 35                      | 249                  | 2.6     | 500000           | 500249             | 7                    | 500000             | false                 |
    Examples:
      | mainTrans | subTrans |
      | 1525      | 1000     |
      | 1530      | 1000     |
      | 1535      | 1000     |
      | 1540      | 1000     |
      | 1545      | 1000     |
      | 1545      | 1090     |
      | 1545      | 2000     |

  Scenario Outline: Non Interest Bearing TPSS MainTrans and SubTrans
    Given the current set of rules
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator   |
      | false           | 0                       | 0                    | 0       | 500000           | 500000             | 0                    | 500000             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 5330      | 7006     | false                 |
      | 5330      | 7010     | false                 |
      | 5330      | 7011     | false                 |
      | 5350      | 7012     | false                 |
      | 5350      | 7014     | false                 |
      | 5350      | 7013     | false                 |
      | 1085      | 1000     | false                 |
      | 1085      | 1020     | false                 |
      | 1085      | 1025     | false                 |
      | 1085      | 1180     | false                 |
      | 1511      | 2000     | true                  |
      | 1515      | 1090     | false                 |
      | 1520      | 1090     | false                 |
      | 1526      | 2000     | true                  |
      | 1531      | 2000     | true                  |
      | 1536      | 2000     | true                  |
      | 1541      | 2000     | true                  |
      | 1546      | 2000     | true                  |
      | 2421      | 1150     | false                 |
      | 1441      | 1150     | false                 |

  Scenario Outline: Non Interest Bearing PAYE MainTrans and SubTrans
    Given the current set of rules
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator   |
      | false           | 0                       | 0                    | 0       | 500000           | 500000             | 0                    | 500000             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 2005      | 2000     | true                  |
      | 2005      | 2020     | true                  |
      | 2005      | 2023     | true                  |
      | 2005      | 2026     | true                  |
      | 2005      | 2030     | true                  |
      | 2005      | 2100     | true                  |
      | 2007      | 1107     | true                  |
      | 2045      | 2000     | true                  |
      | 2045      | 2100     | true                  |
      | 2065      | 2020     | true                  |
      | 2095      | 2000     | true                  |
      | 2095      | 2020     | true                  |
      | 2095      | 2023     | true                  |
      | 2095      | 2026     | true                  |
      | 2095      | 2100     | true                  |
      | 2105      | 2000     | true                  |
      | 2105      | 2023     | true                  |
      | 2105      | 2026     | true                  |
      | 2105      | 2030     | true                  |
      | 2105      | 2100     | true                  |
      | 2135      | 2355     | true                  |
      | 2505      | 2090     | true                  |
      | 2515      | 2090     | true                  |
      | 2525      | 2090     | true                  |
      | 2535      | 2090     | true                  |
      | 2545      | 2090     | true                  |
      | 2555      | 2090     | true                  |
      | 2565      | 2090     | true                  |
      | 2575      | 2090     | true                  |
      | 2585      | 2090     | true                  |
      | 2595      | 2090     | true                  |
      | 1025      | 1090     | false                 |
      | 1030      | 1090     | false                 |
      | 1035      | 1090     | false                 |
      | 1040      | 1090     | false                 |
      | 1045      | 1090     | false                 |
      | 2110      | 1090     | false                 |
      | 2115      | 1090     | false                 |
      | 2120      | 1090     | false                 |
      | 2125      | 1090     | false                 |

  Scenario Outline: Interest Bearing Employer PAYE charges
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator |
      | true            | 35                      | 249                  | 2.6     | 500000           | 500249             | 7                    | 500000             | false                 |
    Examples:
      | mainTrans | subTrans |
      | 2000      | 1000     |
      | 2000      | 1020     |
      | 2000      | 1023     |
      | 2000      | 1026     |
      | 2000      | 1030     |
      | 2000      | 1100     |
      | 2006      | 1106     |
      | 2030      | 1250     |
      | 2030      | 1260     |
      | 2030      | 1270     |
      | 2030      | 1280     |
      | 2030      | 1290     |
      | 2030      | 1300     |
      | 2030      | 1310     |
      | 2030      | 1320     |
      | 2030      | 1330     |
      | 2030      | 1340     |
      | 2030      | 1350     |
      | 2030      | 1390     |
      | 2030      | 1395     |
      | 2040      | 1000     |
      | 2060      | 1020     |
      | 2090      | 1000     |
      | 2090      | 1020     |
      | 2090      | 1023     |
      | 2090      | 1026     |
      | 2090      | 1100     |
      | 2090      | 1250     |
      | 2090      | 1260     |
      | 2090      | 1270     |
      | 2090      | 1280     |
      | 2090      | 1290     |
      | 2090      | 1300     |
      | 2090      | 1310     |
      | 2090      | 1320     |
      | 2090      | 1330     |
      | 2090      | 1340     |
      | 2090      | 1350     |
      | 2100      | 1000     |
      | 2100      | 1023     |
      | 2100      | 1026     |
      | 2100      | 1030     |
      | 2100      | 1100     |
      | 2130      | 1355     |
      | 2500      | 1090     |
      | 2510      | 1090     |
      | 2520      | 1090     |
      | 2530      | 1090     |
      | 2540      | 1090     |
      | 2550      | 1090     |
      | 2560      | 1090     |
      | 2570      | 1090     |
      | 2580      | 1090     |
      | 2590      | 1090     |

  Scenario: MainTrans (1525) debt empty subTrans (7006)
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-03-01        | 2021-03-08          | 1525      | 7006     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid combination of mainTrans and subTrans. No rule found in the configured ones

  Scenario Outline: Interest Bearing VAT charges
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | totalAmountIntDuty | interestOnlyIndicator   |
      | true            | 35                      | 500249             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 4700      | 1174     | false                 |
      | 4760      | 1090     | false                 |
      | 4766      | 1090     | false                 |
      | 4745      | 1090     | false                 |
      | 4770      | 1090     | false                 |
      | 4773      | 1090     | false                 |
      | 4776      | 1090     | false                 |
      | 4755      | 1090     | false                 |
      | 4783      | 1090     | false                 |
      | 4765      | 1090     | false                 |
      | 4775      | 1090     | false                 |
      | 4790      | 1090     | false                 |
      | 4793      | 1090     | false                 |
      | 4748      | 1090     | false                 |
      | 4703      | 1090     | false                 |
      | 4704      | 1090     | false                 |
      | 4735      | 1090     | false                 |
      | 3996      | 1091     | false                 |

  Scenario Outline: Non Interest Bearing VAT Charges
    Given the current set of rules
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator   |
      | false           | 0                       | 0                    | 0       | 500000           | 500000             | 0                    | 500000             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 7700      | 1174     | false                 |
      | 7747      | 1090     | false                 |
      | 7760      | 1090     | false                 |
      | 7766      | 1090     | false                 |
      | 7735      | 1090     | false                 |
      | 7745      | 1090     | false                 |
      | 7776      | 1090     | false                 |
      | 7755      | 1090     | false                 |
      | 7783      | 1090     | false                 |
      | 7786      | 1090     | false                 |
      | 7765      | 1090     | false                 |
      | 7775      | 1090     | false                 |
      | 7796      | 1090     | false                 |
      | 7799      | 1090     | false                 |
      | 4749      | 1175     | true                  |
      | 4620      | 1175     | true                  |
      | 4622      | 1175     | true                  |
      | 4624      | 1175     | true                  |
      | 4682      | 1175     | true                  |
      | 4684      | 1175     | true                  |
      | 4687      | 1175     | true                  |
      | 4693      | 1175     | true                  |
      | 4695      | 1175     | true                  |
      | 4767      | 1175     | true                  |
      | 4697      | 1175     | true                  |
      | 4777      | 1175     | true                  |
      | 4784      | 1175     | true                  |
      | 4791      | 1175     | true                  |
      | 4794      | 1175     | true                  |
      | 4774      | 1175     | true                  |
      | 4771      | 1175     | true                  |
      | 4618      | 1090     | false                 |
      | 3997      | 2091     | true                  |
      | 4763      | 1090     | false                 |
      | 4796      | 1090     | false                 |
      | 4799      | 1090     | false                 |
      | 4747      | 1090     | false                 |
      | 4711      | 1174     | false                 |
      | 4786      | 1090     | false                 |

  Scenario Outline: Interest Bearing SA charges
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | totalAmountIntDuty | interestOnlyIndicator   |
      | true            | 35                      | 500249             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 4920      | 1553     | false                 |
      | 4930      | 1553     | false                 |
      | 4910      | 1553     | false                 |
      | 4940      | 1090     | false                 |
      | 4950      | 1090     | false                 |
      | 4960      | 1090     | false                 |
      | 4970      | 1090     | false                 |
      | 4980      | 1090     | false                 |
      | 4990      | 1090     | false                 |
      | 5010      | 1090     | false                 |
      | 5020      | 1090     | false                 |
      | 5030      | 1090     | false                 |
      | 5040      | 1090     | false                 |
      | 5050      | 1553     | false                 |
      | 5060      | 1553     | false                 |
      | 5070      | 1553     | false                 |
      | 5080      | 1090     | false                 |
      | 5090      | 1553     | false                 |
      | 5100      | 1553     | false                 |
      | 5110      | 1090     | false                 |
      | 5120      | 1090     | false                 |
      | 5130      | 1090     | false                 |
      | 5140      | 1090     | false                 |
      | 5150      | 1090     | false                 |
      | 5160      | 1090     | false                 |
      | 5170      | 1090     | false                 |
      | 5180      | 1553     | false                 |
      | 5190      | 1553     | false                 |
      | 5200      | 1553     | false                 |
      | 5210      | 1553     | false                 |

  Scenario Outline: Non Interest Bearing SA Charges
    Given the current set of rules
    And a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | interestDueDutyTotal | intRate | unpaidAmountDuty | totalAmountIntDuty | numberChargeableDays | amountOnIntDueDuty | interestOnlyIndicator   |
      | false           | 0                       | 0                    | 0       | 500000           | 500000             | 0                    | 500000             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 6010      | 1554     | true                  |
      | 4955      | 2090     | true                  |
      | 4965      | 2090     | true                  |
      | 4975      | 2090     | true                  |
      | 4985      | 2090     | true                  |
      | 4995      | 2090     | true                  |
      | 5015      | 2090     | true                  |
      | 5025      | 2090     | true                  |
      | 5035      | 2090     | true                  |
      | 5045      | 2090     | true                  |
      | 5055      | 1554     | true                  |
      | 5065      | 1554     | true                  |
      | 5075      | 1554     | true                  |
      | 5085      | 2090     | true                  |
      | 5095      | 1554     | true                  |
      | 5105      | 1554     | true                  |
      | 5115      | 2090     | true                  |
      | 5125      | 2090     | true                  |
      | 5135      | 2090     | true                  |
      | 5145      | 2090     | true                  |
      | 5155      | 2090     | true                  |
      | 5165      | 2090     | true                  |
      | 5175      | 2090     | true                  |
      | 5185      | 1554     | true                  |
      | 5195      | 1554     | true                  |
      | 5205      | 1554     | true                  |
      | 5215      | 1554     | true                  |
      | 5071      | 1553     | false                 |
      | 5073      | 1553     | false                 |
      | 6010      | 1555     | true                  |


  Scenario Outline: SA SSTTP Debts
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | totalAmountIntDuty | interestOnlyIndicator   |
      | true            | 35                      | 500249             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 4920      | 1553     | false                 |
      | 4930      | 1553     | false                 |
      | 4910      | 1553     | false                 |
      | 5140      | 1090     | false                 |
      | 5150      | 1090     | false                 |
      | 5030      | 1090     | false                 |
      | 5040      | 1090     | false                 |
      | 5060      | 1553     | false                 |
      | 5070      | 1553     | false                 |
      | 5100      | 1553     | false                 |
      | 5090      | 1553     | false                 |
      | 5180      | 1553     | false                 |
      | 5190      | 1553     | false                 |
      | 5200      | 1553     | false                 |
      | 5210      | 1553     | false                 |
      | 5160      | 1090     | false                 |
      | 5170      | 1090     | false                 |
      | 5080      | 1090     | false                 |
      | 4940      | 1090     | false                 |
      | 4980      | 1090     | false                 |
      | 4950      | 1090     | false                 |
      | 4990      | 1090     | false                 |
      | 4960      | 1090     | false                 |
      | 5010      | 1090     | false                 |
      | 4970      | 1090     | false                 |
      | 5020      | 1090     | false                 |
      | 5110      | 1090     | false                 |
      | 5120      | 1090     | false                 |
      | 5130      | 1090     | false                 |
      | 4910      | 1005     | false                 |
      | 4910      | 1007     | false                 |
      | 4910      | 1008     | false                 |
      | 4910      | 1009     | false                 |
      | 4910      | 1010     | false                 |
      | 4910      | 1011     | false                 |
      | 4910      | 1012     | false                 |
      | 4910      | 1015     | false                 |
      | 4910      | 1042     | false                 |
      | 4910      | 1044     | false                 |
      | 4910      | 1046     | false                 |
      | 4910      | 1047     | false                 |
      | 4910      | 1060     | false                 |
      | 4910      | 1096     | false                 |
      | 4910      | 1100     | false                 |
      | 4910      | 2195     | false                 |
      | 4910      | 2200     | false                 |
      | 4910      | 2205     | false                 |
      | 4910      | 2210     | false                 |
      | 4920      | 1005     | false                 |
      | 4920      | 1007     | false                 |
      | 4920      | 1008     | false                 |
      | 4920      | 1009     | false                 |
      | 4920      | 1010     | false                 |
      | 4920      | 1011     | false                 |
      | 4920      | 1012     | false                 |
      | 4920      | 1015     | false                 |
      | 4930      | 1005     | false                 |
      | 4930      | 1007     | false                 |
      | 4930      | 1008     | false                 |
      | 4930      | 1009     | false                 |
      | 4930      | 1010     | false                 |
      | 4930      | 1011     | false                 |
      | 4930      | 1012     | false                 |
      | 4930      | 1015     | false                 |
      | 4000      | 1005     | false                 |
      | 4000      | 1007     | false                 |
      | 4000      | 1008     | false                 |
      | 4000      | 1009     | false                 |
      | 4000      | 1042     | false                 |
      | 4000      | 1044     | false                 |
      | 4000      | 1046     | false                 |
      | 4000      | 1047     | false                 |
      | 4000      | 1060     | false                 |
      | 4000      | 1100     | false                 |
      | 4001      | 1005     | false                 |
      | 4001      | 1007     | false                 |
      | 4001      | 1008     | false                 |
      | 4001      | 1009     | false                 |
      | 4001      | 1010     | false                 |
      | 4001      | 1011     | false                 |
      | 4001      | 1012     | false                 |
      | 4001      | 1015     | false                 |
      | 4001      | 1042     | false                 |
      | 4001      | 1044     | false                 |
      | 4001      | 1046     | false                 |
      | 4001      | 1047     | false                 |
      | 4001      | 1060     | false                 |
      | 4001      | 1100     | false                 |
      | 4002      | 1085     | false                 |
      | 4002      | 1090     | false                 |
      | 4002      | 1095     | false                 |
      | 4003      | 1005     | false                 |
      | 4003      | 1007     | false                 |
      | 4003      | 1008     | false                 |
      | 4003      | 1009     | false                 |
      | 4003      | 1010     | false                 |
      | 4003      | 1011     | false                 |
      | 4003      | 1012     | false                 |
      | 4003      | 1015     | false                 |
      | 4003      | 1042     | false                 |
      | 4003      | 1044     | false                 |
      | 4003      | 1046     | false                 |
      | 4003      | 1047     | false                 |
      | 4003      | 1060     | false                 |
      | 4003      | 1100     | false                 |
      | 4915      | 1005     | false                 |
      | 4915      | 1005     | false                 |
      | 4915      | 1007     | false                 |
      | 4915      | 1008     | false                 |
      | 4915      | 1009     | false                 |
      | 4915      | 1011     | false                 |
      | 4915      | 1012     | false                 |
      | 4915      | 1015     | false                 |
      | 4915      | 1042     | false                 |
      | 4915      | 1044     | false                 |
      | 4915      | 1047     | false                 |
      | 4915      | 2195     | false                 |
      | 4915      | 2200     | false                 |
      | 4915      | 2205     | false                 |
      | 4915      | 2210     | false                 |
      | 4915      | 1060     | false                 |
      | 4915      | 1096     | false                 |
      | 4911      | 1005     | false                 |
      | 4911      | 1007     | false                 |
      | 4911      | 1008     | false                 |
      | 4911      | 1009     | false                 |
      | 4911      | 1011     | false                 |
      | 4911      | 1012     | false                 |
      | 4911      | 1015     | false                 |
      | 4913      | 1005     | false                 |
      | 4913      | 1005     | false                 |
      | 4913      | 1007     | false                 |
      | 4913      | 1008     | false                 |
      | 4913      | 1009     | false                 |
      | 4913      | 1011     | false                 |
      | 4913      | 1012     | false                 |
      | 4913      | 1015     | false                 |
      | 4027      | 1080     | false                 |
      | 4028      | 1085     | false                 |
      | 4028      | 1090     | false                 |
      | 4028      | 1095     | false                 |
      | 4029      | 1085     | false                 |
      | 4029      | 1090     | false                 |
      | 4029      | 1095     | false                 |
      | 4031      | 1085     | false                 |
      | 4031      | 1090     | false                 |
      | 4031      | 1095     | false                 |
      | 4032      | 1085     | false                 |
      | 4032      | 1090     | false                 |
      | 4032      | 1095     | false                 |
      | 4033      | 1085     | false                 |
      | 4033      | 1090     | false                 |
      | 4033      | 1095     | false                 |

  Scenario Outline: SA SSTTP Debts - Non Interest bearing
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans   | subTrans   |
      | 500000         | 2021-03-01        | 2021-03-08          | <mainTrans> | <subTrans> |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | totalAmountIntDuty | interestOnlyIndicator   |
      | false           | 0                       | 500000             | <interestOnlyIndicator> |
    Examples:
      | mainTrans | subTrans | interestOnlyIndicator |
      | 5071      | 1553     | false                 |
      | 5073      | 1553     | false                 |
      | 6010      | 1560     | true                  |
      | 6010      | 1565     | true                  |
      | 6010      | 1570     | true                  |
      | 6010      | 1575     | true                  |
      | 6010      | 1580     | true                  |
      | 6010      | 1585     | true                  |
      | 6010      | 1590     | true                  |
      | 6010      | 1595     | true                  |
      | 6010      | 1600     | true                  |
      | 6010      | 1605     | true                  |
      | 6010      | 1610     | true                  |
      | 6010      | 1680     | true                  |
      | 6010      | 1685     | true                  |
      | 4026      | 2090     | true                  |
      | 4026      | 2095     | true                  |
      | 4026      | 2096     | true                  |
      | 6010      | 1554     | true                  |
      | 4941      | 2090     | true                  |
      | 6010      | 1611     | true                  |
      | 6010      | 2090     | true                  |
      | 6010      | 2095     | true                  |
      | 6010      | 2096     | true                  |

  Scenario: Simple assessment (SIA) SSTTP Debts - Non Interest bearing
    Given a debt item
      | originalAmount | interestStartDate | interestRequestedTo | mainTrans | subTrans |
      | 500000         | 2021-03-01        | 2021-03-08          | 4530      | 3190     |
    And the debt item has no payment history
    And no breathing spaces have been applied to the debt item
    And no post codes have been provided for the customer
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | interestBearing | interestDueDailyAccrual | totalAmountIntDuty | interestOnlyIndicator |
      | false           | 0                       | 500000             | false                 |