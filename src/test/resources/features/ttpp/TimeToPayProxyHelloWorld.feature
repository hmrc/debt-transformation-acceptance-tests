
Feature: Time To Pay Proxy Hello World test

  Scenario: Retrieve hello world response from time to pay Service
    When a request is made to get response from sol hello world endpoint
    Then the sol response code should be 200
    And the sol hello world response body should be Hello world

  Scenario: Unable to retrieve hello world response from Statement of Liability Service
    When a request is made to an invalid sol endpoint
