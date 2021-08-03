Feature: Payment plan frequency calculation for 1 debt 1 duty with no initial payment

  Scenario: Payment plan calculation instalment - Single payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Single           | 2021-06-01     | 1530      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 101462          | 39           | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 7           | 10000           | 2.6     |
      | 2        | 2021-06-02     | 10000     | debtId       | 90000   | 6           | 20000           | 2.6     |
      | 3        | 2021-06-03     | 10000     | debtId       | 80000   | 5           | 30000           | 2.6     |
      | 4        | 2021-06-04     | 10000     | debtId       | 70000   | 4           | 40000           | 2.6     |
      | 5        | 2021-06-05     | 10000     | debtId       | 60000   | 4           | 50000           | 2.6     |
      | 6        | 2021-06-06     | 10000     | debtId       | 50000   | 3           | 60000           | 2.6     |
      | 7        | 2021-06-07     | 10000     | debtId       | 40000   | 2           | 70000           | 2.6     |
      | 8        | 2021-06-08     | 10000     | debtId       | 30000   | 2           | 80000           | 2.6     |
      | 9        | 2021-06-09     | 10000     | debtId       | 20000   | 1           | 90000           | 2.6     |
      | 10       | 2021-06-10     | 10000     | debtId       | 10000   | 0           | 100000          | 2.6     |
      | 11       | 2021-06-11     | 1462      | debtId       | 0       | 0           | 101462          | 2.6     |


  Scenario: Payment plan calculation instalment - monthly payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Monthly          | 2021-06-01     | 1525      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 102617          | 1194         | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 213         | 10000           | 2.6     |
      | 2        | 2021-07-01     | 10000     | debtId       | 90000   | 198         | 20000           | 2.6     |
      | 3        | 2021-08-01     | 10000     | debtId       | 80000   | 176         | 30000           | 2.6     |
      | 4        | 2021-09-01     | 10000     | debtId       | 70000   | 149         | 40000           | 2.6     |
      | 5        | 2021-10-01     | 10000     | debtId       | 60000   | 132         | 50000           | 2.6     |
      | 6        | 2021-11-01     | 10000     | debtId       | 50000   | 106         | 60000           | 2.6     |
      | 7        | 2021-12-01     | 10000     | debtId       | 40000   | 88          | 70000           | 2.6     |
      | 8        | 2022-01-01     | 10000     | debtId       | 30000   | 66          | 80000           | 2.6     |
      | 9        | 2022-02-01     | 10000     | debtId       | 20000   | 39          | 90000           | 2.6     |
      | 10       | 2022-03-01     | 10000     | debtId       | 10000   | 22          | 100000          | 2.6     |
      | 11       | 2022-04-01     | 2617      | debtId       | 0       | 0           | 102617          | 2.6     |


  Scenario: Payment plan calculation instalment - weekly payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Weekly           | 2021-06-01     | 1525      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 101697          | 274          | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 49          | 10000           | 2.6     |
      | 2        | 2021-06-08     | 10000     | debtId       | 90000   | 44          | 20000           | 2.6     |
      | 3        | 2021-06-15     | 10000     | debtId       | 80000   | 39          | 30000           | 2.6     |
      | 4        | 2021-06-22     | 10000     | debtId       | 70000   | 34          | 40000           | 2.6     |
      | 5        | 2021-06-29     | 10000     | debtId       | 60000   | 29          | 50000           | 2.6     |
      | 6        | 2021-07-06     | 10000     | debtId       | 50000   | 24          | 60000           | 2.6     |
      | 7        | 2021-07-13     | 10000     | debtId       | 40000   | 19          | 70000           | 2.6     |
      | 8        | 2021-07-20     | 10000     | debtId       | 30000   | 14          | 80000           | 2.6     |
      | 9        | 2021-07-27     | 10000     | debtId       | 20000   | 9           | 90000           | 2.6     |
      | 10       | 2021-08-03     | 10000     | debtId       | 10000   | 4           | 100000          | 2.6     |
      | 11       | 2021-08-10     | 1697      | debtId       | 0       | 0           | 101697          | 2.6     |


  Scenario: Payment plan calculation instalment - 2-Weekly payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | 2-Weekly         | 2021-06-01     | 1530      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 101971          | 548          | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 99          | 10000           | 2.6     |
      | 2        | 2021-06-15     | 10000     | debtId       | 90000   | 89          | 20000           | 2.6     |
      | 3        | 2021-06-29     | 10000     | debtId       | 80000   | 79          | 30000           | 2.6     |
      | 4        | 2021-07-13     | 10000     | debtId       | 70000   | 69          | 40000           | 2.6     |
      | 5        | 2021-07-27     | 10000     | debtId       | 60000   | 59          | 50000           | 2.6     |
      | 6        | 2021-08-10     | 10000     | debtId       | 50000   | 49          | 60000           | 2.6     |
      | 7        | 2021-08-24     | 10000     | debtId       | 40000   | 39          | 70000           | 2.6     |
      | 8        | 2021-09-07     | 10000     | debtId       | 30000   | 29          | 80000           | 2.6     |
      | 9        | 2021-09-21     | 10000     | debtId       | 20000   | 19          | 90000           | 2.6     |
      | 10       | 2021-10-05     | 10000     | debtId       | 10000   | 9           | 100000          | 2.6     |
      | 11       | 2021-10-19     | 1971      | debtId       | 0       | 0           | 101971          | 2.6     |


  Scenario: Payment plan calculation instalment - end of payment interest due higher than amount due

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Monthly          | 2021-06-01     | 1525      | 1000     | 9542            |
    When the payment plan detail is sent to the ifs service

    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 12                       | 110736          | 1194         | 9542            |

    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 213         | 10000           | 2.6     |
      | 2        | 2021-07-01     | 10000     | debtId       | 90000   | 198         | 20000           | 2.6     |
      | 3        | 2021-08-01     | 10000     | debtId       | 80000   | 176         | 30000           | 2.6     |
      | 4        | 2021-09-01     | 10000     | debtId       | 70000   | 149         | 40000           | 2.6     |
      | 5        | 2021-10-01     | 10000     | debtId       | 60000   | 132         | 50000           | 2.6     |
      | 6        | 2021-11-01     | 10000     | debtId       | 50000   | 106         | 60000           | 2.6     |
      | 7        | 2021-12-01     | 10000     | debtId       | 40000   | 88          | 70000           | 2.6     |
      | 8        | 2022-01-01     | 10000     | debtId       | 30000   | 66          | 80000           | 2.6     |
      | 9        | 2022-02-01     | 10000     | debtId       | 20000   | 39          | 90000           | 2.6     |
      | 10       | 2022-03-01     | 10000     | debtId       | 10000   | 22          | 100000          | 2.6     |
      | 11       | 2022-04-01     | 10000     | debtId       | 0       | 0           | 110000          | 2.6     |
      | 12       | 2022-05-01     | 736       | debtId       | 0       | 0           | 110736          | 2.6     |

  Scenario: Payment plan calculation instalment - weekly payment frequency with end of month instalment start Date

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Weekly           | 2021-06-30     | 1525      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 101697          | 274          | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-30     | 10000     | debtId       | 100000  | 49          | 10000           | 2.6     |
      | 2        | 2021-07-07     | 10000     | debtId       | 90000   | 44          | 20000           | 2.6     |
      | 3        | 2021-07-14     | 10000     | debtId       | 80000   | 39          | 30000           | 2.6     |
      | 4        | 2021-07-21     | 10000     | debtId       | 70000   | 34          | 40000           | 2.6     |
      | 5        | 2021-07-28     | 10000     | debtId       | 60000   | 29          | 50000           | 2.6     |
      | 6        | 2021-08-04     | 10000     | debtId       | 50000   | 24          | 60000           | 2.6     |
      | 7        | 2021-08-11     | 10000     | debtId       | 40000   | 19          | 70000           | 2.6     |
      | 8        | 2021-08-18     | 10000     | debtId       | 30000   | 14          | 80000           | 2.6     |
      | 9        | 2021-08-25     | 10000     | debtId       | 20000   | 9           | 90000           | 2.6     |
      | 10       | 2021-09-01     | 10000     | debtId       | 10000   | 4           | 100000          | 2.6     |
      | 11       | 2021-09-08     | 1697      | debtId       | 0       | 0           | 101697          | 2.6     |


  Scenario: Payment plan calculation instalment - 4-Weekly payment frequency with end of month instalment start Date

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | 4-Weekly         | 2021-06-01    | 1530      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 102519          | 1096         | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2021-06-01     | 10000     | debtId       | 100000  | 199         | 10000           | 2.6     |
      | 2        | 2021-06-29     | 10000     | debtId       | 90000   | 179         | 20000           | 2.6     |
      | 3        | 2021-07-27     | 10000     | debtId       | 80000   | 159         | 30000           | 2.6     |
      | 4        | 2021-08-24     | 10000     | debtId       | 70000   | 139         | 40000           | 2.6     |
      | 5        | 2021-09-21     | 10000     | debtId       | 60000   | 119         | 50000           | 2.6     |
      | 6        | 2021-10-19     | 10000     | debtId       | 50000   | 99          | 60000           | 2.6     |
      | 7        | 2021-11-16     | 10000     | debtId       | 40000   | 79          | 70000           | 2.6     |
      | 8        | 2021-12-14     | 10000     | debtId       | 30000   | 59          | 80000           | 2.6     |
      | 9        | 2022-01-11     | 10000     | debtId       | 20000   | 39          | 90000           | 2.6     |
      | 10       | 2022-02-08     | 10000     | debtId       | 10000   | 19          | 100000          | 2.6     |
      | 11       | 2022-03-08     | 2519      | debtId       | 0       | 0           | 102519          | 2.6     |


  Scenario: Payment plan calculation instalment - Monthly payment frequency instalment Date starts in non leap year to Leap year

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Monthly          | 2019-12-31     | 1530      | 1000     | 9542            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 12                       | 111027          | 1485         | 9542            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2019-12-31     | 10000     | debtId       | 100000  | 275         | 10000           | 3.25    |
      | 2        | 2020-01-31     | 10000     | debtId       | 90000   | 231         | 20000           | 3.25    |
      | 3        | 2020-02-29     | 10000     | debtId       | 80000   | 220         | 30000           | 3.25    |
      | 4        | 2020-03-31     | 10000     | debtId       | 70000   | 186         | 40000           | 3.25    |
      | 5        | 2020-04-30     | 10000     | debtId       | 60000   | 165         | 50000           | 3.25    |
      | 6        | 2020-05-31     | 10000     | debtId       | 50000   | 133         | 60000           | 3.25    |
      | 7        | 2020-06-30     | 10000     | debtId       | 40000   | 110         | 70000           | 3.25    |
      | 8        | 2020-07-31     | 10000     | debtId       | 30000   | 82          | 80000           | 3.25    |
      | 9        | 2020-08-31     | 10000     | debtId       | 20000   | 53          | 90000           | 3.25    |
      | 10       | 2020-09-30     | 10000     | debtId       | 10000   | 27          | 100000          | 3.25    |
      | 11       | 2020-10-31     | 10000     | debtId       | 0       | 0           | 110000          | 2.6     |
      | 12       | 2020-11-30     | 1027      | debtId       | 0       | 0           | 111027          | 2.6     |


  Scenario: Payment plan calculation instalment - HalfYearly payment frequency instalment Date starts in non leap year to Leap year

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | HalfYearly       | 2019-12-31     | 1525      | 1000     | 3538            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 12                       | 112466          | 8928         | 3538            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2019-12-31     | 10000     | debtId       | 100000  | 1616        | 10000           | 3.25    |
      | 2        | 2020-06-30     | 10000     | debtId       | 90000   | 1470        | 20000           | 3.25    |
      | 3        | 2020-12-31     | 10000     | debtId       | 80000   | 1285        | 30000           | 3.25    |
      | 4        | 2021-06-30     | 10000     | debtId       | 70000   | 1146        | 40000           | 3.25    |
      | 5        | 2021-12-31     | 10000     | debtId       | 60000   | 966         | 50000           | 3.25    |
      | 6        | 2022-06-30     | 10000     | debtId       | 50000   | 819         | 60000           | 3.25    |
      | 7        | 2022-12-31     | 10000     | debtId       | 40000   | 644         | 70000           | 3.25    |
      | 8        | 2023-06-30     | 10000     | debtId       | 30000   | 491         | 80000           | 3.25    |
      | 9        | 2023-12-31     | 10000     | debtId       | 20000   | 323         | 90000           | 3.25    |
      | 10       | 2024-06-30     | 10000     | debtId       | 10000   | 163         | 100000          | 3.25    |
      | 11       | 2024-12-31     | 10000     | debtId       | 0       | 0           | 110000          | 2.6     |
      | 12       | 2025-06-30     | 2466      | debtId       | 0       | 0           | 112466          | 2.6     |


  Scenario: Payment plan calculation instalment - Annually payment frequency instalment Date starts in non leap year to Leap year

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Annually         | 2019-12-31     | 1525      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 12                       | 119284          | 17861        | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2019-12-31     | 10000     | debtId       | 100000  | 3250        | 10000           | 3.25    |
      | 2        | 2020-12-31     | 10000     | debtId       | 90000   | 2917        | 20000           | 3.25    |
      | 3        | 2021-12-31     | 10000     | debtId       | 80000   | 2600        | 30000           | 3.25    |
      | 4        | 2022-12-31     | 10000     | debtId       | 70000   | 2275        | 40000           | 3.25    |
      | 5        | 2023-12-31     | 10000     | debtId       | 60000   | 1950        | 50000           | 3.25    |
      | 6        | 2024-12-31     | 10000     | debtId       | 50000   | 1620        | 60000           | 3.25    |
      | 7        | 2025-12-31     | 10000     | debtId       | 40000   | 1300        | 70000           | 3.25    |
      | 8        | 2026-12-31     | 10000     | debtId       | 30000   | 975         | 80000           | 3.25    |
      | 9        | 2027-12-31     | 10000     | debtId       | 20000   | 650         | 90000           | 3.25    |
      | 10       | 2028-12-31     | 10000     | debtId       | 10000   | 324         | 100000          | 3.25    |
      | 11       | 2029-12-31     | 10000     | debtId       | 0       | 0           | 110000          | 2.6     |
      | 12       | 2030-12-31     | 9284      | debtId       | 0       | 0           | 119284          | 2.6     |


  Scenario: Payment plan calculation instalment - Quarterly payment frequency with end of Leap year instalment Date

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | Quarterly        | 2020-01-15     | 1525      | 1000     | 1423            |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      | totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      | 11                       | 107999          | 4461         | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2020-01-15     | 10000     | debtId       | 100000  | 808         | 10000           | 3.25    |
      | 2        | 2020-04-15     | 10000     | debtId       | 90000   | 727         | 20000           | 3.25    |
      | 3        | 2020-07-15     | 10000     | debtId       | 80000   | 653         | 30000           | 3.25    |
      | 4        | 2020-10-15     | 10000     | debtId       | 70000   | 571         | 40000           | 3.25    |
      | 5        | 2021-01-15     | 10000     | debtId       | 60000   | 480         | 50000           | 3.25    |
      | 6        | 2021-04-15     | 10000     | debtId       | 50000   | 405         | 60000           | 3.25    |
      | 7        | 2021-07-15     | 10000     | debtId       | 40000   | 327         | 70000           | 3.25    |
      | 8        | 2021-10-15     | 10000     | debtId       | 30000   | 245         | 80000           | 3.25    |
      | 9        | 2022-01-15     | 10000     | debtId       | 20000   | 160         | 90000           | 3.25    |
      | 10       | 2022-04-15     | 10000     | debtId       | 10000   | 81          | 100000          | 3.25    |
      | 11       | 2022-07-15     | 5884      | debtId       | 0       | 0           | 105884          | 2.6     |



