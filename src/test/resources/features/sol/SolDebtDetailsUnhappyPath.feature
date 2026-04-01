Feature: Statement of liability Unhappy Path (Service Errors)

  Scenario: Send error message where no debt items are provided when SoL is called - DTD-545
    Given a request to sol with no debt items provided
    When a debt statement of liability is requested
    Then the sol response code should be 400
    And the sol service will respond with {"reason":"Could not parse body due to requirement failed: Debts which are mandatory, are missing","message":"Invalid Json"}
