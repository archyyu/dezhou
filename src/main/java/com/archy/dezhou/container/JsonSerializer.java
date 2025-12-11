package com.archy.dezhou.container;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Modern JSON-based replacement for ASObjectSerializer
 * Uses FastJSON for high-performance serialization
 */
public class JsonSerializer {
    
    /**
     * Private constructor - utility class
     */
    private JsonSerializer() {
        // Prevent instantiation
    }

    /**
     * Serialize JsonObjectWrapper to JSON string
     * @param wrapper JsonObjectWrapper to serialize
     * @return JSON string representation
     */
    public static String serialize(JsonObjectWrapper wrapper) {
        if (wrapper == null) {
            return "{}";
        }
        return wrapper.toJSONString();
    }

    /**
     * Serialize JsonObjectWrapper to pretty JSON string (for debugging)
     * @param wrapper JsonObjectWrapper to serialize
     * @return Pretty JSON string representation
     */
    public static String serializePretty(JsonObjectWrapper wrapper) {
        if (wrapper == null) {
            return "{}";
        }
        return JSON.toJSONString(wrapper.toJSONObject(), 
                                SerializerFeature.PrettyFormat,
                                SerializerFeature.WriteMapNullValue,
                                SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * Deserialize JSON string to JsonObjectWrapper
     * @param jsonString JSON string to deserialize
     * @return JsonObjectWrapper instance
     */
    public static JsonObjectWrapper deserialize(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new JsonObjectWrapper();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            return new JsonObjectWrapper(jsonObject);
        } catch (Exception e) {
            // Return empty object on parse failure (similar to original behavior)
            return new JsonObjectWrapper();
        }
    }

    /**
     * Deserialize JSON string to JsonObjectWrapper with error handling
     * @param jsonString JSON string to deserialize
     * @param throwOnError Whether to throw exception on parse error
     * @return JsonObjectWrapper instance
     * @throws RuntimeException if throwOnError is true and parsing fails
     */
    public static JsonObjectWrapper deserialize(String jsonString, boolean throwOnError) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new JsonObjectWrapper();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            return new JsonObjectWrapper(jsonObject);
        } catch (Exception e) {
            if (throwOnError) {
                throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
            }
            return new JsonObjectWrapper();
        }
    }

    /**
     * Create a standard response object
     * @param cmd Command string
     * @return JsonObjectWrapper with standard response structure
     */
    public static JsonObjectWrapper createResponse(String cmd) {
        return new JsonObjectWrapper()
            .put("_cmd", cmd)
            .put("timestamp", System.currentTimeMillis())
            .put("success", true);
    }

    /**
     * Create an error response object
     * @param cmd Command string
     * @param errorMessage Error message
     * @return JsonObjectWrapper with error response structure
     */
    public static JsonObjectWrapper createErrorResponse(String cmd, String errorMessage) {
        return new JsonObjectWrapper()
            .put("_cmd", cmd)
            .put("timestamp", System.currentTimeMillis())
            .put("success", false)
            .put("error", errorMessage);
    }

    /**
     * Deep clone a JsonObjectWrapper
     * @param source Source object to clone
     * @return Deep copy of the source object
     */
    public static JsonObjectWrapper deepClone(JsonObjectWrapper source) {
        if (source == null) {
            return null;
        }
        String jsonString = source.toJSONString();
        return deserialize(jsonString);
    }

    /**
     * Merge two JsonObjectWrappers
     * @param target Target object (will be modified)
     * @param source Source object to merge from
     * @return Merged target object
     */
    public static JsonObjectWrapper merge(JsonObjectWrapper target, JsonObjectWrapper source) {
        if (target == null || source == null) {
            return target != null ? target : source;
        }
        
        JSONObject targetJson = target.toJSONObject();
        JSONObject sourceJson = source.toJSONObject();
        
        // Deep merge - source values override target values
        for (Map.Entry<String, Object> entry : sourceJson.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof JSONObject && targetJson.containsKey(key)) {
                Object targetValue = targetJson.get(key);
                if (targetValue instanceof JSONObject) {
                    // Recursive merge for nested objects
                    JSONObject merged = new JSONObject();
                    merged.putAll((JSONObject) targetValue);
                    merged.putAll((JSONObject) value);
                    targetJson.put(key, merged);
                } else {
                    targetJson.put(key, value);
                }
            } else {
                targetJson.put(key, value);
            }
        }
        
        return new JsonObjectWrapper(targetJson);
    }

    /**
     * Convert legacy JsonObjectWrapper to JsonObjectWrapper
     * @param legacyObj Legacy JsonObjectWrapper
     * @return Modern JsonObjectWrapper
     */
    public static JsonObjectWrapper fromLegacy(com.archy.dezhou.container.JsonObjectWrapper legacyObj) {
        if (legacyObj == null) {
            return new JsonObjectWrapper();
        }
        
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        
        // Convert all keys
        for (Object keyObj : legacyObj.keySet()) {
            String key = keyObj.toString();
            Object value = legacyObj.get(key);
            
            if (value instanceof com.archy.dezhou.container.JsonObjectWrapper) {
                // Recursive conversion for nested objects
                wrapper.putObj(key, fromLegacy((com.archy.dezhou.container.JsonObjectWrapper) value));
            } else {
                wrapper.put(key, value);
            }
        }
        
        return wrapper;
    }

    /**
     * Convert JsonObjectWrapper to legacy JsonObjectWrapper
     * @param wrapper Modern JsonObjectWrapper
     * @return Legacy JsonObjectWrapper
     */
    public static com.archy.dezhou.container.JsonObjectWrapper toLegacy(JsonObjectWrapper wrapper) {
        if (wrapper == null) {
            return new com.archy.dezhou.container.JsonObjectWrapper();
        }
        
        com.archy.dezhou.container.JsonObjectWrapper legacyObj = new com.archy.dezhou.container.JsonObjectWrapper();
        JSONObject jsonObject = wrapper.toJSONObject();
        
        // Convert all entries
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof JSONObject) {
                // Recursive conversion for nested objects
                legacyObj.put(key, toLegacy(new JsonObjectWrapper((JSONObject) value)));
            } else if (value instanceof Number) {
                legacyObj.putNumber(key, ((Number) value).doubleValue());
            } else if (value instanceof Boolean) {
                legacyObj.putBool(key, (Boolean) value);
            } else {
                legacyObj.put(key, value);
            }
        }
        
        return legacyObj;
    }
}