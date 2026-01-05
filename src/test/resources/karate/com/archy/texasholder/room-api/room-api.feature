@api @room
Feature: Room API Tests
  Background:
    * url 'http://localhost:8080'

  Scenario: Get room list
    Given path '/api/v1/room/list'
    When method get
    Then status 200
    And match response == { success: true, data: '#array' }

  Scenario: Get room details
    Given path '/api/v1/room/beginner'
    When method get
    Then status 200
    And match response == { success: true, data: '#present' }

  Scenario: Invalid room should return error
    Given path '/api/v1/room/nonexistent'
    When method get
    Then status 400
    And match response == { success: false, message: '#string' }

  Scenario: Test room list with query parameters
    Given path '/api/v1/room/list'
    Given param type = 'public'
    When method get
    Then status 200
    And match response == { success: true, data: '#array' }
