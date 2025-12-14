@api @room @smoke
Feature: Room List API Functional Tests

  Background:
    * url 'http://localhost:8080'

  Scenario: Get all rooms - basic test
    Given path '/api/v1/room/list'
    When method get
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    And match response.data[0] contains { name: '#string', showname: '#string' }

  Scenario: Get all rooms - detailed validation
    Given path '/api/v1/room/list'
    When method get
    Then status 200
    And match response == {
      success: true,
      data: '#array',
      status: '#string',
      code: '#string',
      message: '#string',
      timestamp: '#number'
    }
    And match each response.data == {
      id: '#number',
      showname: '#string',
      name: '#string',
      bbet: '#number',
      sbet: '#number',
      maxbuy: '#number',
      minbuy: '#number',
      roomtype: '#string'
    }

  Scenario: Get all rooms - performance test
    Given path '/api/v1/room/list'
    When method get
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    # Response should be fast - under 500ms
    And match response.timings.duration < 500

  Scenario: Get all rooms with query parameters
    Given path '/api/v1/room/list'
    Given param type = 'public'
    When method get
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    # All rooms should be of type 'public'
    And match each response.data == { roomtype: 'public' }

  Scenario: Get specific room details
    Given path '/api/v1/room/beginner'
    When method get
    Then status 200
    And match response.success == true
    And match response.data == {
      id: '#number',
      showname: '初级场',
      name: 'beginner',
      bbet: 20,
      sbet: 10,
      maxbuy: 2000,
      minbuy: 200,
      roomtype: 'public'
    }

  Scenario: Invalid room should return proper error
    Given path '/api/v1/room/nonexistent'
    When method get
    Then status 400
    And match response.success == false
    And match response.message == '#string'
    And match response.data == null

  Scenario: Test room list sorting
    Given path '/api/v1/room/list'
    When method get
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    # Verify that rooms are sorted by some criteria (e.g., by minbuy)
    And def sorted = karate.sort(response.data, 'minbuy')
    And match response.data == sorted

  Scenario: Test room list pagination (if supported)
    Given path '/api/v1/room/list'
    Given param page = 1
    Given param size = 2
    When method get
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    And match response.data.length <= 2
