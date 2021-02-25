Feature: Example Statement of Liability Hello World test

  Scenario: Retrieve hello world response
    When a request is made to get response from sol hello world endpoint
    Then the sol response code should be 200
    And the sol hello world response body should be Hello world

  Scenario: Unable to retrieve hello world response
    When a request is made to an invalid sol endpoint
    Then the sol response code should be 404