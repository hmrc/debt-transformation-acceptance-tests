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

#DTD-191: IFS Amounts to be in pennies. Is outstanding

Feature: Get Debt For DRIER case (mvp)

  Scenario: Interest Bearing DRIER debt (MVP)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
    | expectedResponse      |
    | expectedResponse1     |

@ignore
  Scenario: Non Interest Bearing DRIER debt (MVP)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        | DRIER  | NI         | false           | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | expectedResponse      |
      | expectedResponse2     |

  Scenario: DRIER debt Zero Amount Edge Case
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 0      | 2020-12-20 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | expectedResponse      |
      | expectedResponse3     |

# Below scenario currently fails as api returns daily interest of -0.0001. Should negative amounts be possible?
  @ignore
  Scenario: DRIER debt Amount is negative (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | -1     | 2020-12-20 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with
      | expectedResponse      |
      | expectedResponse4     |

  Scenario: DRIER debt Amount non integer (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | \"\"   | 2020-12-20 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/amount

  @ignore
  Scenario: DRIER debt Amount non integer (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 1.2    | 2020-12-20 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/amount

  Scenario: DRIER debt invalid entry in Date Amount (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | d          | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateAmount

  Scenario: DRIER debt empty entry in Date Amount (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 |            | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateAmount

  Scenario: DRIER debt invalid Date Amount (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2021-02-30 | 2021-04-18        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateAmount

  Scenario: DRIER debt invalid entry in dateCalculationTo (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | d                 | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateCalculationTo

  Scenario: DRIER debt empty dateCalculationTo (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 |                   | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateCalculationTo

  Scenario: DRIER debt invalid dateCalculationTo (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-02-30        | DRIER  | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/dateCalculationTo

  Scenario: DRIER debt invalid regime (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        | DRIdER | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/regime

  Scenario: DRIER debt empty regime (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        |        | NI         | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/regime

  Scenario: DRIER debt invalid chargeType (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        | DRIER  | invalid    | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/chargeType

  Scenario: DRIER debt empty chargeType (Edge Case)
    Given a debt item
      | uniqueItemReference | amount | dateAmount | dateCalculationTo | regime | chargeType | interestBearing | numberOfDays | interestRate |
      | UniqueReference1    | 500000 | 2020-12-20 | 2021-04-18        | DRIER  |            | true            | 0            | 1            |
    When the debt item is sent to the ifs service
    Then the ifs service will respond with Invalid DebtCalculationRequest payload: List((/debtItems(0)/chargeType
