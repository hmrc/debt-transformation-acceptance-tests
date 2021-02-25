@Example
Feature: Example Statement of Liability Hello World test
  @Example
  Scenario: Retrieve hello world response
    When a request is made to get response from hello world endpoint
    Then the response code should be 200

  Scenario: Unable to retrieve hello world response
    When a request is made to an invalid endpoint
    Then the response code should be 404