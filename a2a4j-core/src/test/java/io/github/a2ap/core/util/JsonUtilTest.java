/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a2ap.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void testToJsonWithNull() {
        String result = JsonUtil.toJson(null);
        assertNull(result);
    }

    @Test
    void testToJsonWithSimpleObject() {
        TestObject obj = new TestObject("test", 123);
        String result = JsonUtil.toJson(obj);
        assertNotNull(result);
        assertTrue(result.contains("test"));
        assertTrue(result.contains("123"));
    }

    @Test
    void testToJsonWithComplexObject() {
        Map<String, Object> complexObj = new HashMap<>();
        complexObj.put("string", "value");
        complexObj.put("number", 42);
        complexObj.put("boolean", true);
        complexObj.put("null", null);

        String result = JsonUtil.toJson(complexObj);
        assertNotNull(result);
        assertTrue(result.contains("value"));
        assertTrue(result.contains("42"));
        assertTrue(result.contains("true"));
    }

    @Test
    void testFromJsonWithNullString() {
        TestObject result = JsonUtil.fromJson(null, TestObject.class);
        assertNull(result);
    }

    @Test
    void testFromJsonWithEmptyString() {
        TestObject result = JsonUtil.fromJson("", TestObject.class);
        assertNull(result);
    }

    @Test
    void testFromJsonWithValidJson() {
        String json = "{\"name\":\"test\",\"value\":123}";
        TestObject result = JsonUtil.fromJson(json, TestObject.class);
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(123, result.getValue());
    }

    @Test
    void testFromJsonWithInvalidJson() {
        String invalidJson = "invalid json";
        TestObject result = JsonUtil.fromJson(invalidJson, TestObject.class);
        assertNull(result);
    }

    @Test
    void testFromJsonWithTypeReference() {
        String json = "[{\"name\":\"test1\",\"value\":1},{\"name\":\"test2\",\"value\":2}]";
        TypeReference<List<TestObject>> typeRef = new TypeReference<List<TestObject>>() {};
        List<TestObject> result = JsonUtil.fromJson(json, typeRef);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getName());
        assertEquals("test2", result.get(1).getName());
    }

    @Test
    void testFromJsonToJsonNode() {
        String json = "{\"name\":\"test\",\"value\":123,\"nested\":{\"key\":\"value\"}}";
        JsonNode result = JsonUtil.fromJson(json);
        assertNotNull(result);
        assertEquals("test", result.get("name").asText());
        assertEquals(123, result.get("value").asInt());
        assertTrue(result.has("nested"));
    }

    @Test
    void testFromJsonToJsonNodeWithNull() {
        JsonNode result = JsonUtil.fromJson(null);
        assertNull(result);
    }

    @Test
    void testIsJsonStrWithNull() {
        assertFalse(JsonUtil.isJsonStr(null));
    }

    @Test
    void testIsJsonStrWithEmptyString() {
        assertFalse(JsonUtil.isJsonStr(""));
    }

    @Test
    void testIsJsonStrWithWhitespace() {
        assertFalse(JsonUtil.isJsonStr("   "));
    }

    @Test
    void testIsJsonStrWithValidObjectJson() {
        assertTrue(JsonUtil.isJsonStr("{\"key\":\"value\"}"));
    }

    @Test
    void testIsJsonStrWithValidArrayJson() {
        assertTrue(JsonUtil.isJsonStr("[1,2,3]"));
    }

    @Test
    void testIsJsonStrWithInvalidJson() {
        assertFalse(JsonUtil.isJsonStr("invalid json"));
    }

    @Test
    void testIsJsonStrWithNonJsonString() {
        assertFalse(JsonUtil.isJsonStr("just a string"));
    }

    @Test
    void testIsJsonStrWithWhitespacePadding() {
        assertTrue(JsonUtil.isJsonStr("  {\"key\":\"value\"}  "));
    }

    @Test
    void testDateTimeSerialization() {
        LocalDateTime now = LocalDateTime.now();
        String json = JsonUtil.toJson(now);
        assertNotNull(json);
        
        LocalDateTime deserialized = JsonUtil.fromJson(json, LocalDateTime.class);
        assertNotNull(deserialized);
        assertEquals(now.getYear(), deserialized.getYear());
        assertEquals(now.getMonth(), deserialized.getMonth());
        assertEquals(now.getDayOfMonth(), deserialized.getDayOfMonth());
    }

    @Test
    void testMapSerialization() {
        Map<String, Object> map = new HashMap<>();
        map.put("string", "value");
        map.put("integer", 42);
        map.put("double", 3.14);
        map.put("boolean", true);

        String json = JsonUtil.toJson(map);
        assertNotNull(json);

        Map<String, Object> deserialized = JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {});
        assertNotNull(deserialized);
        assertEquals("value", deserialized.get("string"));
        assertEquals(42, deserialized.get("integer"));
        assertEquals(3.14, deserialized.get("double"));
        assertEquals(true, deserialized.get("boolean"));
    }

    static class TestObject {
        private String name;
        private int value;

        public TestObject() {}

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
} 
