package com.archy.dezhou.container;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

/**
 * Modern JSON-based replacement for JsonObjectWrapper
 * Uses FastJSON for high-performance JSON operations
 */
public class JsonObjectWrapper {
    
    private JSONObject jsonObject;

    /**
     * Default constructor
     */
    public JsonObjectWrapper() {
        this.jsonObject = new JSONObject();
    }

    /**
     * Constructor from existing JSONObject
     */
    public JsonObjectWrapper(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Constructor from Map
     */
    public JsonObjectWrapper(Map<String, Object> map) {
        this.jsonObject = new JSONObject(map);
    }

    /**
     * Put methods - String key
     */
    public JsonObjectWrapper put(String key, Object value) {
        jsonObject.put(key, value);
        return this;
    }

    public JsonObjectWrapper putNumber(String key, Number value) {
        jsonObject.put(key, value);
        return this;
    }

    public JsonObjectWrapper putBool(String key, Boolean value) {
        jsonObject.put(key, value);
        return this;
    }

    public JsonObjectWrapper putString(String key, String value) {
        jsonObject.put(key, value);
        return this;
    }

    /**
     * Put methods - Integer key (for compatibility)
     */
    public JsonObjectWrapper put(int key, Object value) {
        return put(String.valueOf(key), value);
    }

    public JsonObjectWrapper putNumber(int key, Number value) {
        return putNumber(String.valueOf(key), value);
    }

    public JsonObjectWrapper putBool(int key, Boolean value) {
        return putBool(String.valueOf(key), value);
    }

    public JsonObjectWrapper putString(int key, String value) {
        return putString(String.valueOf(key), value);
    }

    /**
     * Get methods
     */
    public Object get(String key) {
        return jsonObject.get(key);
    }

    public Object get(int key) {
        return get(String.valueOf(key));
    }

    public String getString(String key) {
        return jsonObject.getString(key);
    }

    public String getString(int key) {
        return getString(String.valueOf(key));
    }

    public Number getNumber(String key) {
        return jsonObject.getBigDecimal(key);
    }

    public Number getNumber(int key) {
        return getNumber(String.valueOf(key));
    }

    public Boolean getBool(String key) {
        return jsonObject.getBoolean(key);
    }

    public Boolean getBool(int key) {
        return getBool(String.valueOf(key));
    }

    /**
     * Nested object methods
     */
    public JsonObjectWrapper getObj(String key) {
        JSONObject nested = jsonObject.getJSONObject(key);
        return nested != null ? new JsonObjectWrapper(nested) : null;
    }

    public JsonObjectWrapper getObj(int key) {
        return getObj(String.valueOf(key));
    }

    public JsonObjectWrapper putObj(String key, JsonObjectWrapper wrapper) {
        jsonObject.put(key, wrapper.jsonObject);
        return this;
    }

    public JsonObjectWrapper putObj(int key, JsonObjectWrapper wrapper) {
        return putObj(String.valueOf(key), wrapper);
    }

    /**
     * Collection methods
     */
    public JsonObjectWrapper putCollection(String key, Iterable<?> collection) {
        jsonObject.put(key, collection);
        return this;
    }

    public JsonObjectWrapper putCollection(int key, Iterable<?> collection) {
        return putCollection(String.valueOf(key), collection);
    }

    /**
     * Map methods
     */
    public JsonObjectWrapper putMap(String key, Map<?, ?> map) {
        jsonObject.put(key, map);
        return this;
    }

    public JsonObjectWrapper putMap(int key, Map<?, ?> map) {
        return putMap(String.valueOf(key), map);
    }

    /**
     * Utility methods
     */
    public int size() {
        return jsonObject.size();
    }

    public boolean containsKey(String key) {
        return jsonObject.containsKey(key);
    }

    public boolean containsKey(int key) {
        return containsKey(String.valueOf(key));
    }

    public JsonObjectWrapper remove(String key) {
        jsonObject.remove(key);
        return this;
    }

    public JsonObjectWrapper remove(int key) {
        return remove(String.valueOf(key));
    }

    /**
     * Convert to underlying JSONObject
     */
    public JSONObject toJSONObject() {
        return jsonObject;
    }

    /**
     * Convert to JSON string
     */
    public String toJSONString() {
        return jsonObject.toJSONString();
    }

    /**
     * Convert to pretty JSON string (for debugging)
     */
    public String toPrettyString() {
        return jsonObject.toJSONString();
    }

    /**
     * Create from JSON string
     */
    public static JsonObjectWrapper fromJSONString(String jsonString) {
        return new JsonObjectWrapper(JSONObject.parseObject(jsonString));
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}