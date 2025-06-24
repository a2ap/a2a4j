package io.github.a2ap.core.jsonrpc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonRpcResponseTest {

    @Test
    void testDefaultConstructor() {
        JSONRPCResponse response = new JSONRPCResponse();
        assertNotNull(response);
        assertEquals("2.0", response.getJsonrpc());
        assertNull(response.getId());
        assertNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithId() {
        String id = "response-123";
        JSONRPCResponse response = new JSONRPCResponse(id);
        
        assertEquals("2.0", response.getJsonrpc());
        assertEquals(id, response.getId());
        assertNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithIdAndResult() {
        String id = "response-123";
        Object result = "success";
        JSONRPCResponse response = new JSONRPCResponse(id, result);
        
        assertEquals("2.0", response.getJsonrpc());
        assertEquals(id, response.getId());
        assertEquals(result, response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithIdAndError() {
        String id = "response-456";
        JSONRPCError error = JSONRPCError.builder()
                .code(-32601)
                .message("Method not found")
                .build();
        JSONRPCResponse response = new JSONRPCResponse(id, error);
        
        assertEquals("2.0", response.getJsonrpc());
        assertEquals(id, response.getId());
        assertNull(response.getResult());
        assertEquals(error, response.getError());
    }

    @Test
    void testSettersAndGetters() {
        JSONRPCResponse response = new JSONRPCResponse();
        
        response.setId("response-789");
        response.setResult("operation completed");
        
        JSONRPCError error = JSONRPCError.builder()
                .code(-32602)
                .message("Invalid params")
                .build();
        response.setError(error);
        
        assertEquals("2.0", response.getJsonrpc());
        assertEquals("response-789", response.getId());
        assertNull(response.getResult()); // Error clears result
        assertEquals(error, response.getError());
    }

    @Test
    void testMutualExclusionOfResultAndError() {
        JSONRPCResponse response = new JSONRPCResponse();
        
        // Set result first
        Object result = "result value";
        response.setResult(result);
        assertEquals(result, response.getResult());
        assertNull(response.getError());
        
        // Set error - should clear result
        JSONRPCError error = JSONRPCError.builder()
                .code(-32601)
                .message("Method not found")
                .build();
        response.setError(error);
        assertNull(response.getResult());
        assertEquals(error, response.getError());
        
        // Set result again - should clear error
        response.setResult(result);
        assertEquals(result, response.getResult());
        assertNull(response.getError());
    }

    @Test
    void testEqualsAndHashCode() {
        JSONRPCResponse response1 = new JSONRPCResponse("response-123", "success");
        JSONRPCResponse response2 = new JSONRPCResponse("response-123", "success");
        JSONRPCResponse response3 = new JSONRPCResponse("different-response-123", "success");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        JSONRPCResponse response = new JSONRPCResponse("response-123", "success");
        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        JSONRPCResponse response = new JSONRPCResponse("response-123", "success");
        assertNotEquals("string", response);
    }

    @Test
    void testToString() {
        JSONRPCResponse response = new JSONRPCResponse("response-123", "success");

        String toString = response.toString();
        
        assertTrue(toString.contains("response-123"));
        assertTrue(toString.contains("success"));
        assertTrue(toString.contains("2.0"));
        assertTrue(toString.contains("JSONRPCResponse"));
    }

    @Test
    void testToStringWithNullValues() {
        JSONRPCResponse response = new JSONRPCResponse();
        String toString = response.toString();
        
        assertTrue(toString.contains("JSONRPCResponse"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("result=null"));
    }

    @Test
    void testJsonRpcVersion() {
        JSONRPCResponse response = new JSONRPCResponse();
        assertEquals("2.0", response.getJsonrpc());
        
        JSONRPCResponse responseWithId = new JSONRPCResponse("test");
        assertEquals("2.0", responseWithId.getJsonrpc());
    }

    @Test
    void testResponseWithComplexResult() {
        Map<String, Object> complexResult = new HashMap<>();
        complexResult.put("string", "value");
        complexResult.put("number", 42);
        complexResult.put("boolean", true);
        complexResult.put("array", new Object[]{1, 2, 3});
        
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested", "value");
        complexResult.put("object", nestedMap);
        
        JSONRPCResponse response = new JSONRPCResponse("complex-response", complexResult);
        
        assertEquals(complexResult, response.getResult());
        assertEquals("value", ((Map<String, Object>) response.getResult()).get("string"));
        assertEquals(42, ((Map<String, Object>) response.getResult()).get("number"));
        assertEquals(true, ((Map<String, Object>) response.getResult()).get("boolean"));
    }

    @Test
    void testResponseWithError() {
        JSONRPCError error = JSONRPCError.builder()
                .code(-32601)
                .message("Method not found")
                .data("Additional error information")
                .build();
        
        JSONRPCResponse response = new JSONRPCResponse("error-response", error);
        
        assertEquals(error, response.getError());
        assertEquals(Integer.valueOf(-32601), response.getError().getCode());
        assertEquals("Method not found", response.getError().getMessage());
        assertEquals("Additional error information", response.getError().getData());
    }

    @Test
    void testResponseWithNullResult() {
        JSONRPCResponse response = new JSONRPCResponse("null-result-response", null);
        
        assertNull(response.getResult());
    }

    @Test
    void testResponseWithNullError() {
        JSONRPCResponse response = new JSONRPCResponse("null-error-response", (JSONRPCError) null);
        
        assertNull(response.getError());
    }

    @Test
    void testResponseWithNumericId() {
        JSONRPCResponse response = new JSONRPCResponse("123", "numeric id result");
        
        assertEquals("123", response.getId());
    }

    @Test
    void testResponseWithNullId() {
        JSONRPCResponse response = new JSONRPCResponse();
        response.setId(null);
        
        assertNull(response.getId());
    }

    @Test
    void testResponseWithSpecialCharacters() {
        String specialId = "response with spaces and special chars: !@#$%^&*()";
        String specialResult = "result_with_underscores_and-dashes";
        
        JSONRPCResponse response = new JSONRPCResponse(specialId, specialResult);
        
        assertEquals(specialId, response.getId());
        assertEquals(specialResult, response.getResult());
        assertTrue(response.toString().contains(specialId));
        assertTrue(response.toString().contains(specialResult));
    }

    @Test
    void testResponseWithUnicodeCharacters() {
        String unicodeId = "response with unicode: 中文 Español Français";
        String unicodeResult = "result_with_unicode_结果";
        
        JSONRPCResponse response = new JSONRPCResponse(unicodeId, unicodeResult);
        
        assertEquals(unicodeId, response.getId());
        assertEquals(unicodeResult, response.getResult());
        assertTrue(response.toString().contains(unicodeId));
        assertTrue(response.toString().contains(unicodeResult));
    }

    @Test
    void testResponseWithBooleanResult() {
        JSONRPCResponse response = new JSONRPCResponse("boolean-response", true);
        
        assertEquals(true, response.getResult());
    }

    @Test
    void testResponseWithNumberResult() {
        JSONRPCResponse response = new JSONRPCResponse("number-response", 42.5);
        
        assertEquals(42.5, response.getResult());
    }

    @Test
    void testResponseWithArrayResult() {
        Object[] arrayResult = {"item1", "item2", "item3"};
        
        JSONRPCResponse response = new JSONRPCResponse("array-response", arrayResult);
        
        assertEquals(arrayResult, response.getResult());
    }
} 