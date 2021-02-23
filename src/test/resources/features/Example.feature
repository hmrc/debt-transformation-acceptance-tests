@Example

Feature: Example Feature file using Cucumber

  Scenario: Retrieve customers contact information
    When a request is made to get customer contact information
    Then the response code should be 200

  Scenario: Unable to retrieve customer contact information when providing invalid information
    When an invalid request is made to get customer contact information
    Then the response code should be 400
    And I am returned an invalid VRN response


