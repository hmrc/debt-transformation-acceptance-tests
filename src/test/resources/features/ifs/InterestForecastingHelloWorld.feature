Feature: Interest Forecasting Hello World test

  Scenario: Retrieve hello world response
    When a request is made to get response from ifs hello world endpoint
    Then the ifs response code should be 200
    And the ifs hello world response body should be Hello world

  Scenario: Unable to retrieve hello world response
    When a request is made to an invalid ifs endpoint
    Then the ifs response code should be 404