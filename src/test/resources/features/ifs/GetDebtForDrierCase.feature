#  Assumptions
#  1 Debt item
#  Initial amount and date
#  REGIME == DRIERd
#  Where charge type is DRIER NI, then interest bearing is assumed to be true
#  Where charge type is DRIER HiPG, then interest bearing is assumed to be false
#  NO repayments
#  NO suppression period
#  NO breathing space
#  Date Amount  == Interest start date
#  No outstanding interests to pay
#  When bearing the interest rate is 1%

Feature: Get Debt For DRIER case (mvp)

  Scenario: Interest Bearing DRIER debt (MVP)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | dailyInterest | totalInterest | intRate | totalAmountToPay | totalAmountWithInterest | numberChargeableDays | totalAmountOnWhichInterestDue |
      | 13            | 109           | 1       | 500000           | 500109                  | 8                    | 500000                        |

  Scenario: Non Interest Bearing DRIER debt (MVP)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        | DRIER  | HIPG       | false           |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | dailyInterest | totalInterest | intRate | totalAmountToPay | totalAmountWithInterest | numberChargeableDays | totalAmountOnWhichInterestDue |
      | 0             | 0             | 0       | 500000           | 500000                  | 0                    | 500000                        |

  Scenario: DRIER debt Zero Amount Edge Case
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 0      | 2021-03-01 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | dailyInterest | totalInterest | intRate | totalAmountToPay | totalAmountWithInterest | numberChargeableDays | totalAmountOnWhichInterestDue |
      | 0             | 0             | 1       | 0                | 0                       | 8                    | 0                             |

# Below scenario currently fails as api returns daily interest of -0.0001. Should negative amounts be possible?
  @ignore
  Scenario: DRIER debt Amount is negative (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | -1     | 2021-03-01 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the 1st debt summary will contain
      | dailyInterest | totalInterest | intRate | totalAmountToPay | totalAmountWithInterest | numberChargeableDays | totalAmountOnWhichInterestDue |
      | 0             | 0             | 1       | 0                | 0                       | 8                    | 0                             |

  Scenario: DRIER debt Amount non integer (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | \"\"   | 2021-03-01 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /amount' missing or invalid

  Scenario: DRIER debt Amount non integer (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 1.2    | 2021-03-01 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /amount' missing or invalid

  Scenario: DRIER debt invalid entry in Date Amount (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | d          | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateAmount' missing or invalid

  Scenario: DRIER debt empty entry in Date Amount (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 |            | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateAmount' missing or invalid

  Scenario: DRIER debt invalid Date Amount (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-02-30 | 2021-03-08        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateAmount' missing or invalid

  Scenario: DRIER debt invalid entry in dateCalculationTo (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-08 | d                 | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: DRIER debt empty dateCalculationTo (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-08 |                   | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: DRIER debt invalid dateCalculationTo (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-02-01 | 2021-02-30        | DRIER  | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /dateCalculationTo' missing or invalid

  Scenario: DRIER debt invalid regime (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        | DRIdER | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /regime' missing or invalid

  Scenario: DRIER debt empty regime (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        |        | NI         | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /regime' missing or invalid

  Scenario: DRIER debt invalid chargeType (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        | DRIER  | invalid    | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /chargeType' missing or invalid

  Scenario: DRIER debt empty chargeType (Edge Case)
    Given a debt item
      | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing |
      | 500000 | 2021-03-01 | 2021-03-08        | DRIER  |            | true            |
    And the debt item has no payment history
    When the debt item is sent to the ifs service
    Then the ifs service will respond with /chargeType' missing or invalid
