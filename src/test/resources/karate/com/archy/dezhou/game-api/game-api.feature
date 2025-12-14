@api @game
Feature: Game API Tests
  Background:
    * url 'http://localhost:8080'

  Scenario: Get game state for a room
    Given path '/api/v1/game/testRoom/state'
    When method get
    Then status 200
    And match response == { success: true, data: '#present' }

  Scenario: Get player game status
    Given path '/api/v1/game/testRoom/players/123/status'
    When method get
    Then status 200
    And match response == { success: true, data: '#present' }

  Scenario: Invalid room ID should return error
    Given path '/api/v1/game/invalidRoom/state'
    When method get
    Then status 400
    And match response == { success: false, message: 'RoomNotFound' }

  Scenario: Invalid user ID should return error
    Given path '/api/v1/game/testRoom/players/999999/status'
    When method get
    Then status 400
    And match response == { success: false, message: 'UserNotFound' }
