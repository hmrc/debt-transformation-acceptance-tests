Feature: IFS provide details on whether the charges - use the charge reference or ASN and interest bearing

  Scenario Outline: Debt Interest type - use the charge reference or ASN and interest bearing
    Given a debt interest type item
      | mainTrans   | subTrans   |
      | <mainTrans> | <subTrans> |
    And the debt interest type request is sent to the ifs service
    Then the 1st debt interest type response summary will contain
      | mainTrans   | subTrans   | interestBearing   | useChargeReference   |
      | <mainTrans> | <subTrans> | <interestBearing> | <useChargeReference> |
    Examples:
      | mainTrans | subTrans | interestBearing | useChargeReference |
#PAYE
      | 1025      | 1090     | false           | true               |
      | 1030      | 1090     | false           | true               |
      | 1035      | 1090     | false           | true               |
      | 1040      | 1090     | false           | true               |
      | 1045      | 1090     | false           | true               |
      | 2000      | 1000     | true            | false              |
      | 2000      | 1020     | true            | false              |
      | 2000      | 1023     | true            | false              |
      | 2000      | 1026     | true            | false              |
      | 2000      | 1030     | true            | false              |
      | 2000      | 1100     | true            | false              |
      | 2005      | 2000     | false           | true               |
      | 2005      | 2020     | false           | true               |
      | 2005      | 2023     | false           | true               |
      | 2005      | 2026     | false           | true               |
      | 2005      | 2030     | false           | true               |
      | 2005      | 2100     | false           | true               |
      | 2006      | 1106     | true            | false              |
      | 2007      | 1107     | false           | true               |
      | 2030      | 1250     | true            | false              |
      | 2030      | 1260     | true            | false              |
      | 2030      | 1270     | true            | false              |
      | 2030      | 1280     | true            | false              |
      | 2030      | 1290     | true            | false              |
      | 2030      | 1300     | true            | false              |
      | 2030      | 1310     | true            | false              |
      | 2030      | 1320     | true            | false              |
      | 2030      | 1330     | true            | false              |
      | 2030      | 1340     | true            | false              |
      | 2030      | 1350     | true            | false              |
      | 2030      | 1390     | true            | false              |
      | 2030      | 1395     | true            | false              |
      | 2040      | 1000     | true            | true               |
      | 2045      | 2000     | false           | true               |
      | 2045      | 2100     | false           | true               |
      | 2060      | 1020     | true            | false              |
      | 2065      | 2020     | false           | true               |
      | 2090      | 1000     | true            | false              |
      | 2090      | 1020     | true            | false              |
      | 2090      | 1023     | true            | false              |
      | 2090      | 1026     | true            | false              |
      | 2090      | 1100     | true            | false              |
      | 2090      | 1250     | true            | false              |
      | 2090      | 1260     | true            | false              |
      | 2090      | 1270     | true            | false              |
      | 2090      | 1280     | true            | false              |
      | 2090      | 1290     | true            | false              |
      | 2090      | 1300     | true            | false              |
      | 2090      | 1310     | true            | false              |
      | 2090      | 1320     | true            | false              |
      | 2090      | 1330     | true            | false              |
      | 2090      | 1340     | true            | false              |
      | 2090      | 1350     | true            | false              |
      | 2095      | 2000     | false           | true               |
      | 2095      | 2020     | false           | true               |
      | 2095      | 2023     | false           | true               |
      | 2095      | 2026     | false           | true               |
      | 2095      | 2100     | false           | true               |
      | 2100      | 1000     | true            | true              |
      | 2100      | 1023     | true            | true              |
      | 2100      | 1026     | true            | true              |
      | 2100      | 1030     | true            | true              |
      | 2100      | 1100     | true            | true              |
      | 2105      | 2000     | false           | true               |
      | 2105      | 2023     | false           | true               |
      | 2105      | 2026     | false           | true               |
      | 2105      | 2030     | false           | true               |
      | 2105      | 2100     | false           | true               |
      | 2110      | 1090     | false           | true               |
      | 2115      | 1090     | false           | true               |
      | 2120      | 1090     | false           | true               |
      | 2125      | 1090     | false           | true               |
      | 2130      | 1355     | true            | false              |
      | 2135      | 2355     | false           | true               |
      | 2500      | 1090     | true            | true               |
      | 2505      | 2090     | false           | true               |
      | 2510      | 1090     | true            | true               |
      | 2515      | 2090     | false           | true               |
      | 2520      | 1090     | true            | true              |
      | 2525      | 2090     | false           | true               |
      | 2530      | 1090     | true            | true               |
      | 2535      | 2090     | false           | true               |
      | 2540      | 1090     | true            | true               |
      | 2545      | 2090     | false           | true               |
      | 2550      | 1090     | true            | true               |
      | 2555      | 2090     | false           | true               |
      | 2560      | 1090     | true            | true               |
      | 2565      | 2090     | false           | true               |
      | 2570      | 1090     | true            | true               |
      | 2575      | 2090     | false           | true               |
      | 2580      | 1090     | true            | true               |
      | 2585      | 2090     | false           | true               |
      | 2590      | 1090     | true            | true               |
      | 2595      | 2090     | false           | true               |

# VAT
      | 4700      | 1174     | true            | false              |
      | 4620      | 1175     | false           | false              |
      | 4703      | 1090     | true            | false              |
      | 4622      | 1175     | false           | false              |
      | 4704      | 1090     | true            | false              |
      | 4624      | 1175     | false           | false              |
      | 7700      | 1174     | false           | false              |
      | 4748      | 1090     | true            | false              |
      | 4749      | 1175     | false           | false              |
      | 4735      | 1090     | true            | false              |
      | 4682      | 1175     | false           | false              |
      | 7735      | 1090     | false           | false              |
      | 4760      | 1090     | true            | false              |
      | 4693      | 1175     | false           | false              |
      | 7760      | 1090     | false           | false              |
      | 4763      | 1090     | false           | false              |
      | 4766      | 1090     | true            | false              |
      | 4767      | 1175     | false           | false              |
      | 7766      | 1090     | false            | false             |
      | 4745      | 1090     | true            | false              |
      | 4684      | 1175     | false           | false              |
      | 7745      | 1090     | false           | false              |
      | 4770      | 1090     | true            | false              |
      | 4771      | 1175     | false           | false              |
      | 4773      | 1090     | true            | false              |
      | 4774      | 1175     | false           | false              |
      | 4776      | 1090     | true            | false              |
      | 4777      | 1175     | false           | false              |
      | 7776      | 1090     | false           | false              |
      | 4755      | 1090     | true            | false              |
      | 4687      | 1175     | false           | false              |
      | 7755      | 1090     | false           | false              |
      | 4783      | 1090     | true            | false              |
      | 4784      | 1175     | false           | false              |
      | 7783      | 1090     | false           | false              |
      | 4786      | 1090     | false           | false              |
      | 7786      | 1090     | false           | false              |
      | 4765      | 1090     | true            | false              |
      | 4695      | 1175     | false           | false              |
      | 7765      | 1090     | false           | false              |
      | 4775      | 1090     | true            | false              |
      | 4697      | 1175     | false           | false              |
      | 7775      | 1090     | false           | false              |
      | 4790      | 1090     | true            | false              |
      | 4791      | 1175     | false           | false              |
      | 4793      | 1090     | true            | false              |
      | 4794      | 1175     | false           | false              |
      | 4796      | 1090     | false           | false              |
      | 7796      | 1090     | false           | false              |
      | 4799      | 1090     | false           | false              |
      | 7799      | 1090     | false           | false              |
      | 4747      | 1090     | false           | false              |
      | 7747      | 1090     | false           | false              |
      | 4711      | 1174     | false           | false              |


  Scenario Outline:Debt Interest type rule validation
    Given a debt interest type item
      | mainTrans   | subTrans   |
      | <mainTrans> | <subTrans> |
    And the debt interest type request is sent to the ifs service
    Then the ifs service will respond with
      | statusCode | reason                                        | message                                                                             |
      | 400        | Invalid combination of mainTrans and subTrans | Invalid combination of mainTrans and subTrans. No rule found in the configured ones |
    Examples:
      | mainTrans | subTrans |
      | 5330      | 7006     |
      | 5330      | 7010     |
      | 5330      | 7011     |
      | 5350      | 7012     |
      | 5350      | 7014     |
      | 5350      | 7013     |
      | 1085      | 1000     |
      | 1085      | 1020     |
      | 1085      | 1025     |
      | 1085      | 1180     |
      | 1511      | 2000     |
      | 1515      | 1090     |
      | 1520      | 1090     |
      | 1525      | 1000     |
      | 1526      | 2000     |
      | 1530      | 1000     |
      | 1531      | 2000     |
      | 1535      | 1000     |
      | 1536      | 2000     |
      | 1540      | 1000     |
      | 1541      | 2000     |
      | 1545      | 1000     |
      | 1545      | 1090     |
      | 1545      | 2000     |
      | 1546      | 2000     |
      | 2421      | 1150     |
      | 1441      | 1150     |
      | 4618      | 1090     |
      | 3996      | 1091     |
      | 3997      | 2091     |