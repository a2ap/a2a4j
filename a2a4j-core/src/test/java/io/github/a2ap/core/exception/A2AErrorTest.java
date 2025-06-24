package io.github.a2ap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class A2AErrorTest {

    @Test
    void testDefaultConstructor() {
        A2AError error = new A2AError();
        assertNotNull(error);
        assertNull(error.getMessage());
        assertEquals(0, error.getCode());
        assertNull(error.getData());
        assertNull(error.getTaskId());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Test error message";
        A2AError error = new A2AError(message);
        assertEquals(message, error.getMessage());
        assertEquals(0, error.getCode());
        assertNull(error.getData());
        assertNull(error.getTaskId());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Original cause");
        A2AError error = new A2AError(message, cause);
        assertEquals(message, error.getMessage());
        assertEquals(cause, error.getCause());
        assertEquals(0, error.getCode());
        assertNull(error.getData());
        assertNull(error.getTaskId());
    }

    @Test
    void testConstructorWithMessageCodeAndData() {
        String message = "Test error message";
        int code = 1001;
        Object data = "error data";
        A2AError error = new A2AError(message, code, data);
        assertEquals(message, error.getMessage());
        assertEquals(code, error.getCode());
        assertEquals(data, error.getData());
        assertNull(error.getTaskId());
    }

    @Test
    void testConstructorWithMessageCodeDataAndTaskId() {
        String message = "Test error message";
        int code = 1001;
        Object data = "error data";
        String taskId = "task-123";
        A2AError error = new A2AError(message, code, data, taskId);
        assertEquals(message, error.getMessage());
        assertEquals(code, error.getCode());
        assertEquals(data, error.getData());
        assertEquals(taskId, error.getTaskId());
    }

    @Test
    void testConstructorWithMessageCauseCodeDataAndTaskId() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Original cause");
        int code = 1001;
        Object data = "error data";
        String taskId = "task-123";
        A2AError error = new A2AError(message, cause, code, data, taskId);
        assertEquals(message, error.getMessage());
        assertEquals(cause, error.getCause());
        assertEquals(code, error.getCode());
        assertEquals(data, error.getData());
        assertEquals(taskId, error.getTaskId());
    }

    @Test
    void testSettersAndGetters() {
        A2AError error = new A2AError();
        
        error.setCode(2001);
        error.setData("custom data");
        error.setTaskId("custom-task-id");
        
        assertEquals(2001, error.getCode());
        assertEquals("custom data", error.getData());
        assertEquals("custom-task-id", error.getTaskId());
    }

    @Test
    void testErrorCodeConstants() {
        assertEquals(-32602, A2AError.INVALID_PARAMS);
        assertEquals(-32601, A2AError.METHOD_NOT_FOUND);
        assertEquals(1001, A2AError.TASK_NOT_FOUND);
        assertEquals(1002, A2AError.TASK_CANCELLED);
        assertEquals(1003, A2AError.AGENT_EXECUTION_ERROR);
        assertEquals(1004, A2AError.AUTHENTICATION_ERROR);
        assertEquals(1005, A2AError.AUTHORIZATION_ERROR);
    }

    @Test
    void testBuilder() {
        A2AError error = A2AError.builder()
                .message("Builder test message")
                .code(3001)
                .data("builder data")
                .taskId("builder-task-id")
                .cause(new RuntimeException("Builder cause"))
                .build();
        
        assertEquals("Builder test message", error.getMessage());
        assertEquals(3001, error.getCode());
        assertEquals("builder data", error.getData());
        assertEquals("builder-task-id", error.getTaskId());
        assertNotNull(error.getCause());
        assertEquals("Builder cause", error.getCause().getMessage());
    }

    @Test
    void testBuilderWithPartialData() {
        A2AError error = A2AError.builder()
                .message("Partial message")
                .code(4001)
                .build();
        
        assertEquals("Partial message", error.getMessage());
        assertEquals(4001, error.getCode());
        assertNull(error.getData());
        assertNull(error.getTaskId());
        assertNull(error.getCause());
    }

    @Test
    void testEqualsAndHashCode() {
        A2AError error1 = new A2AError("Test message", 1001, "data", "task-123");
        A2AError error2 = new A2AError("Test message", 1001, "data", "task-123");
        A2AError error3 = new A2AError("Different message", 1001, "data", "task-123");
        
        assertEquals(error1, error2);
        assertNotEquals(error1, error3);
        assertEquals(error1.hashCode(), error2.hashCode());
        assertNotEquals(error1.hashCode(), error3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        A2AError error = new A2AError("Test message", 1001, "data", "task-123");
        assertNotEquals(null, error);
    }

    @Test
    void testEqualsWithDifferentClass() {
        A2AError error = new A2AError("Test message", 1001, "data", "task-123");
        assertNotEquals("string", error);
    }

    @Test
    void testToString() {
        A2AError error = new A2AError("Test message", 1001, "data", "task-123");
        String toString = error.toString();
        
        assertTrue(toString.contains("Test message"));
        assertTrue(toString.contains("1001"));
        assertTrue(toString.contains("data"));
        assertTrue(toString.contains("task-123"));
        assertTrue(toString.contains("A2AError"));
    }

    @Test
    void testToStringWithNullValues() {
        A2AError error = new A2AError();
        String toString = error.toString();
        
        assertTrue(toString.contains("A2AError"));
        assertTrue(toString.contains("code=0"));
    }
} 