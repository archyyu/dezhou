# JSON Migration Guide for Dezhou Poker Server

## 🎯 Overview

This guide provides a **step-by-step migration** from the legacy XML-based `JsonObjectWrapper`/`ASObjectSerializer` system to modern JSON-based communication for the Dezhou Poker Server.

## 🚀 Migration Strategy

### Phase 1: Implement JSON Alternatives ✅

#### **1. JsonObjectWrapper** (`src/main/java/com/archy/dezhou/container/JsonObjectWrapper.java`)
- **Purpose**: Modern replacement for `JsonObjectWrapper`
- **Features**:
  - Type-safe JSON operations
  - Nested object support
  - Collection and map handling
  - Compatible API with legacy code

#### **2. JsonSerializer** (`src/main/java/com/archy/dezhou/container/JsonSerializer.java`)
- **Purpose**: Modern replacement for `ASObjectSerializer`
- **Features**:
  - FastJSON-based serialization
  - Pretty printing for debugging
  - Error handling options
  - Legacy compatibility methods

### Phase 2: Update Core Game Logic

#### **Example: Room.java Migration**

**Before (Legacy XML):**
```java
public JsonObjectWrapper playerSitDown(int seatId, Player player, int cb) {
    JsonObjectWrapper response = new JsonObjectWrapper();
    JsonObjectWrapper userAobj = new JsonObjectWrapper();
    
    // ... game logic ...
    
    response.put("_cmd", ConstList.CMD_SITDOWN);
    response.put("user", userAobj);
    response.put("issit", "no");
    
    return response;
}
```

**After (Modern JSON):**
```java
public JsonObjectWrapper playerSitDown(int seatId, Player player, int cb) {
    JsonObjectWrapper response = JsonSerializer.createResponse(ConstList.CMD_SITDOWN);
    JsonObjectWrapper userObj = new JsonObjectWrapper();
    
    // ... game logic ...
    
    userObj.put("uid", player.getUid());
    userObj.putNumber("sid", seatId);
    userObj.putNumber("cm", player.getRmoney());
    userObj.put("un", player.getAccount());
    
    response.put("user", userObj);
    response.put("issit", "no");
    
    return response;
}
```

### Phase 3: Update REST Controllers

#### **Example: GameRestController Migration**

**Before:**
```java
@PostMapping("/process")
public ResponseEntity<ResponseDto> processRequest(@RequestBody RequestDto requestDto) {
    ResponseDto response = new ResponseDto();
    gameController.process(requestDto, response);
    return ResponseEntity.ok(response);
}
```

**After (JSON-enhanced):**
```java
@PostMapping("/process")
public ResponseEntity<JsonObjectWrapper> processRequest(@RequestBody RequestDto requestDto) {
    try {
        // Process request using modern JSON
        JsonObjectWrapper response = gameService.processModern(requestDto);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        JsonObjectWrapper errorResponse = JsonSerializer.createErrorResponse(
            "ERROR", "Error processing request: " + e.getMessage()
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
```

### Phase 4: Update Game Controller

#### **Example: GameController Migration**

**Before:**
```java
public void process(RequestDto requestDto, ResponseDto response) {
    String function = requestDto.getFn();
    
    if("login".equals(function)){
        handleLogin(requestDto, response);
    }
    // ... other functions ...
}

private void handleLogin(RequestDto requestDto, ResponseDto response) {
    // Legacy handling
    response.setData("Login successful");
}
```

**After (Modern JSON):**
```java
public JsonObjectWrapper processModern(RequestDto requestDto) {
    String function = requestDto.getFn();
    
    if("login".equals(function)){
        return handleLoginModern(requestDto);
    }
    // ... other functions ...
    
    return JsonSerializer.createErrorResponse("ERROR", "Unknown function: " + function);
}

private JsonObjectWrapper handleLoginModern(RequestDto requestDto) {
    JsonObjectWrapper response = JsonSerializer.createResponse("LOGIN");
    
    // Modern handling with database
    User user = userService.authenticate(requestDto.getData());
    
    if (user != null) {
        response.put("success", true);
        response.put("user", createUserJson(user));
    } else {
        response.put("success", false);
        response.put("error", "Invalid credentials");
    }
    
    return response;
}

private JsonObjectWrapper createUserJson(User user) {
    return new JsonObjectWrapper()
        .put("uid", user.getUid())
        .put("account", user.getAccount())
        .put("allmoney", user.getAllmoney())
        .put("roommoney", user.getRoommoney());
}
```

## 🔧 API Compatibility Layer

### **Hybrid Approach (Recommended)**

```java
@Component
public class GameService {
    
    @Autowired
    private GameController gameController;
    
    // Modern JSON endpoint
    public JsonObjectWrapper processModern(RequestDto requestDto) {
        // Convert to modern JSON processing
        ResponseDto legacyResponse = new ResponseDto();
        gameController.process(requestDto, legacyResponse);
        
        // Convert legacy response to modern JSON
        return convertToModernJson(legacyResponse);
    }
    
    private JsonObjectWrapper convertToModernJson(ResponseDto legacyResponse) {
        JsonObjectWrapper modernResponse = new JsonObjectWrapper();
        
        if (legacyResponse.isSuccess()) {
            modernResponse.put("success", true);
            modernResponse.put("data", legacyResponse.getData());
        } else {
            modernResponse.put("success", false);
            modernResponse.put("error", legacyResponse.getErrorMessage());
        }
        
        return modernResponse;
    }
}
```

## 📊 Response Format Comparison

### **Legacy XML Format**
```xml
<dataObj>
    <var n='_cmd' t='s'>CMD_SITDOWN</var>
    <var n='issit' t='s'>no</var>
    <obj o='user' t='a'>
        <var n='uid' t='s'>123</var>
        <var n='sid' t='n'>1</var>
        <var n='cm' t='n'>1000</var>
    </obj>
</dataObj>
```

### **Modern JSON Format**
```json
{
    "_cmd": "CMD_SITDOWN",
    "issit": "no",
    "user": {
        "uid": "123",
        "sid": 1,
        "cm": 1000
    },
    "timestamp": 1634567890123,
    "success": true
}
```

## 🎯 Key Benefits of JSON Migration

### **1. Performance Improvements**
- **FastJSON** is significantly faster than NanoXML
- Smaller payload sizes (JSON vs XML)
- Better memory efficiency

### **2. Modern Frontend Compatibility**
- Works with **React, Vue, Angular, Mobile apps**
- Standardized format understood by all modern clients
- Easy to debug and inspect

### **3. Better Error Handling**
- Proper exception handling
- Structured error responses
- Validation capabilities

### **4. Schema Evolution**
- Easy to add new fields
- Backward compatibility
- Versioning support

## 🛠️ Migration Steps

### **Step 1: Add JSON Dependencies**
```xml
<!-- Already in pom.xml -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.83</version>
</dependency>
```

### **Step 2: Create JSON Classes**
```bash
# Already created:
# - JsonObjectWrapper.java
# - JsonSerializer.java
```

### **Step 3: Update Game Logic**
```java
// Gradually replace JsonObjectWrapper with JsonObjectWrapper
// Start with new features, then migrate existing ones
```

### **Step 4: Update REST API**
```java
// Add new JSON endpoints alongside existing ones
// Use @RequestMapping with different paths or headers
```

### **Step 5: Frontend Migration**
```javascript
// Update frontend to use JSON instead of XML
// Example with React:
fetch('/api/game/process', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(requestData)
})
.then(response => response.json())
.then(data => {
    // Handle JSON response
    console.log(data._cmd, data.user);
});
```

## 🔄 Backward Compatibility

### **Dual Protocol Support**
```java
@RestController
@RequestMapping("/api/game")
public class GameRestController {
    
    // Modern JSON endpoint
    @PostMapping(value = "/process", consumes = "application/json")
    public ResponseEntity<JsonObjectWrapper> processJson(@RequestBody RequestDto requestDto) {
        return ResponseEntity.ok(gameService.processModern(requestDto));
    }
    
    // Legacy XML endpoint (for backward compatibility)
    @PostMapping(value = "/process", consumes = "text/xml")
    public ResponseEntity<String> processXml(@RequestBody String xmlData) {
        JsonObjectWrapper legacyRequest = ASObjectSerializer.deserialize(xmlData);
        JsonObjectWrapper legacyResponse = gameController.processLegacy(legacyRequest);
        return ResponseEntity.ok(ASObjectSerializer.serialize(legacyResponse));
    }
}
```

## 🧪 Testing Strategy

### **1. Unit Tests**
```java
@Test
public void testJsonSerialization() {
    JsonObjectWrapper obj = new JsonObjectWrapper()
        .put("name", "test")
        .putNumber("value", 123);
    
    String json = JsonSerializer.serialize(obj);
    JsonObjectWrapper deserialized = JsonSerializer.deserialize(json);
    
    assertEquals("test", deserialized.getString("name"));
    assertEquals(123, deserialized.getNumber("value").intValue());
}
```

### **2. Integration Tests**
```java
@Test
public void testGameEndpoint() throws Exception {
    RequestDto request = new RequestDto();
    request.setFn("login");
    
    mockMvc.perform(post("/api/game/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
}
```

### **3. Performance Tests**
```java
@Benchmark
public void benchmarkJsonSerialization() {
    JsonObjectWrapper obj = createComplexGameState();
    String json = JsonSerializer.serialize(obj);
    JsonSerializer.deserialize(json);
}
```

## 📈 Deployment Plan

### **Phase 1: Development**
- Implement JSON classes ✅
- Create compatibility layer
- Update game logic
- Write tests

### **Phase 2: Testing**
- Unit testing
- Integration testing
- Performance benchmarking
- End-to-end testing

### **Phase 3: Staging**
- Deploy to staging environment
- Test with real clients
- Monitor performance
- Gather feedback

### **Phase 4: Production**
- Blue-green deployment
- Feature flags for gradual rollout
- Monitor error rates
- Rollback plan ready

## 🎉 Success Criteria

1. ✅ **JSON classes implemented and tested**
2. ⚠️ **Game logic migrated to JSON** (in progress)
3. ❌ **REST API updated for JSON** (next step)
4. ❌ **Frontend updated for JSON** (future)
5. ❌ **Legacy XML deprecated** (final step)

## 🚀 Next Steps

### **Immediate Actions**
1. **Update GameController** to use JsonObjectWrapper
2. **Update Room class** methods to return JsonObjectWrapper
3. **Add JSON endpoints** to GameRestController
4. **Write comprehensive tests** for new JSON functionality

### **Future Enhancements**
1. **Add OpenAPI/Swagger documentation** for JSON API
2. **Implement API versioning** for backward compatibility
3. **Add request/response validation** using JSON Schema
4. **Implement caching** for frequent game state requests

## 📋 Summary

The JSON migration provides:
- **Modern API** for web and mobile clients
- **Better performance** with FastJSON
- **Easier debugging** with human-readable JSON
- **Future-proof** architecture
- **Gradual migration** path with backward compatibility

**Status**: JSON infrastructure is ready! 🎉 Next step is to migrate the game logic to use the new JSON classes.

Would you like me to provide specific code examples for migrating particular game functions or create a step-by-step migration plan for a specific component?