#Assumptions
#
#Customer reference = see format from ARC
#Debt amount = 1,200
#Debt ID = see format from ARC
#Duty ID = see format from ARC
#Main Trans = 1545 (interest bearing)
#Sub Trans = 1000
#Original debt amount = 1,200
#Payment amount = 200
#Payment date = 25/08/2020
#Interest start date = 05/04/2020
#Quote type = Duration
#Instalment date = 01/06/2021
#Instalment amount = 100
#Frequency = monthly
#Duration = Not known as IFS will calculate
#No initial payment
#No suppressions
#No BS
#Type of payment plan = Time to pay - not relevant for IFS to do calculation

Feature: Payment plan frequency calculation for 1 debt 1 duty with no initial payment


  Scenario: Payment plan calculation instalment - monthly payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | monthly          | 2021-06-01     | 1525      | 1000     | 1423                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |11                       | 102617          | 1194         | 1423            |
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
      | debtId | 100000     | 10000            | weekly           | 2021-06-01     | 1525      | 1000     | 1423                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |11                       | 101697          | 274          | 1423            |
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


  Scenario: Payment plan calculation instalment - biweekly payment frequency

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | biweekly         | 2021-06-01     | 1530      | 1000     | 1423                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |11                       | 101971          | 548          | 1423            |
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


  Scenario: Payment plan calculation instalment - end of payement interest due higher than amount due

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | monthly          | 2021-06-01     | 1525      | 1000     | 9542                |
    When the payment plan detail is sent to the ifs service

    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |12                       | 110736          | 1194         | 9542            |

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
      | debtId | 100000     | 10000            | weekly           | 2021-06-30     | 1525      | 1000     | 1423                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |11                       | 101697          | 274          | 1423            |
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

  Scenario: Payment plan calculation instalment - Monthly payment frequency instalment Date starts in non leap year to Leap year

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | monthly          | 2019-12-31     | 1530      | 1000     | 9542                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |12                       | 111027          | 1485         | 9542            |
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


  Scenario: Payment plan calculation instalment - weekly payment frequency with end of Leap year instalment Date

    Given debt payment plan details
      | debtId | debtAmount | instalmentAmount | paymentFrequency | instalmentDate | mainTrans | subTrans | interestAccrued |
      | debtId | 100000     | 10000            | weekly           | 2020-02-01     | 1525      | 1000     | 1423                |
    When the payment plan detail is sent to the ifs service
    Then ifs returns payment frequency summary
      |totalNumberOfInstalments | expectedPayment | totalPlanInt | interestAccrued |
      |11                       | 101764          | 341          | 1423            |
    Then ifs service return the following payment plan calculation instalment
      | serialNo | paymentDueDate | amountDue | uniqueDebtId | balance | interestDue | totalPaidAmount | intRate |
      | 1        | 2020-02-01     | 10000     | debtId       | 100000  | 62          | 10000           | 3.25    |
      | 2        | 2020-02-08     | 10000     | debtId       | 90000   | 55          | 20000           | 3.25    |
      | 3        | 2020-02-15     | 10000     | debtId       | 80000   | 49          | 30000           | 3.25    |
      | 4        | 2020-02-22     | 10000     | debtId       | 70000   | 43          | 40000           | 3.25    |
      | 5        | 2020-02-29     | 10000     | debtId       | 60000   | 37          | 50000           | 3.25    |
      | 6        | 2020-03-07     | 10000     | debtId       | 50000   | 31          | 60000           | 3.25    |
      | 7        | 2020-03-14     | 10000     | debtId       | 40000   | 24          | 70000           | 3.25    |
      | 8        | 2020-03-21     | 10000     | debtId       | 30000   | 18          | 80000           | 3.25    |
      | 9        | 2020-03-28     | 10000     | debtId       | 20000   | 12          | 90000           | 3.25    |
      | 10       | 2020-04-04     | 10000     | debtId       | 10000   | 6           | 100000          | 3.25    |
      | 11       | 2020-04-11     | 1764      | debtId       | 0       | 0           | 101764          | 2.6     |



