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

package io.github.a2ap.core.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {

    @Test
    void testDefaultConstructor() {
        Message message = new Message();
        assertNotNull(message);
        assertNull(message.getMessageId());
        assertNull(message.getTaskId());
        assertNull(message.getContextId());
        assertNull(message.getRole());
        assertNull(message.getParts());
        assertNull(message.getMetadata());
        assertEquals("message", message.getKind());
    }

    @Test
    void testConstructorWithParameters() {
        String messageId = "msg-123";
        String taskId = "task-456";
        String contextId = "context-789";
        String role = "user";
        List<Part> parts = Arrays.asList(new TextPart("Hello"));
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");
        String kind = "custom-message";

        Message message = new Message(messageId, taskId, contextId, role, parts, metadata, kind);
        
        assertEquals(messageId, message.getMessageId());
        assertEquals(taskId, message.getTaskId());
        assertEquals(contextId, message.getContextId());
        assertEquals(role, message.getRole());
        assertEquals(parts, message.getParts());
        assertEquals(metadata, message.getMetadata());
        assertEquals(kind, message.getKind());
    }

    @Test
    void testSettersAndGetters() {
        Message message = new Message();
        
        message.setMessageId("msg-123");
        message.setTaskId("task-456");
        message.setContextId("context-789");
        message.setRole("agent");
        
        List<Part> parts = Arrays.asList(new TextPart("Response"));
        message.setParts(parts);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        message.setMetadata(metadata);
        
        message.setKind("custom-message");
        
        assertEquals("msg-123", message.getMessageId());
        assertEquals("task-456", message.getTaskId());
        assertEquals("context-789", message.getContextId());
        assertEquals("agent", message.getRole());
        assertEquals(parts, message.getParts());
        assertEquals(metadata, message.getMetadata());
        assertEquals("custom-message", message.getKind());
    }

    @Test
    void testBuilder() {
        List<Part> parts = Arrays.asList(new TextPart("Builder message"));
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "builder");

        Message message = Message.builder()
                .messageId("builder-msg-123")
                .taskId("builder-task-456")
                .contextId("builder-context-789")
                .role("user")
                .parts(parts)
                .metadata(metadata)
                .kind("builder-message")
                .build();

        assertEquals("builder-msg-123", message.getMessageId());
        assertEquals("builder-task-456", message.getTaskId());
        assertEquals("builder-context-789", message.getContextId());
        assertEquals("user", message.getRole());
        assertEquals(parts, message.getParts());
        assertEquals(metadata, message.getMetadata());
        assertEquals("builder-message", message.getKind());
    }

    @Test
    void testBuilderWithPartialData() {
        Message message = Message.builder()
                .messageId("partial-msg-123")
                .role("agent")
                .build();

        assertEquals("partial-msg-123", message.getMessageId());
        assertEquals("agent", message.getRole());
        assertNull(message.getTaskId());
        assertNull(message.getContextId());
        assertNull(message.getParts());
        assertNull(message.getMetadata());
        assertEquals("message", message.getKind());
    }

    @Test
    void testEqualsAndHashCode() {
        Message message1 = Message.builder()
                .messageId("msg-123")
                .taskId("task-456")
                .role("user")
                .build();

        Message message2 = Message.builder()
                .messageId("msg-123")
                .taskId("task-456")
                .role("user")
                .build();

        Message message3 = Message.builder()
                .messageId("different-msg-123")
                .taskId("task-456")
                .role("user")
                .build();

        assertEquals(message1, message2);
        assertNotEquals(message1, message3);
        assertEquals(message1.hashCode(), message2.hashCode());
        assertNotEquals(message1.hashCode(), message3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        Message message = Message.builder()
                .messageId("msg-123")
                .role("user")
                .build();
        assertNotEquals(null, message);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Message message = Message.builder()
                .messageId("msg-123")
                .role("user")
                .build();
        assertNotEquals("string", message);
    }

    @Test
    void testToString() {
        Message message = Message.builder()
                .messageId("msg-123")
                .taskId("task-456")
                .role("user")
                .build();

        String toString = message.toString();
        
        assertTrue(toString.contains("msg-123"));
        assertTrue(toString.contains("task-456"));
        assertTrue(toString.contains("user"));
        assertTrue(toString.contains("Message"));
    }

    @Test
    void testToStringWithNullValues() {
        Message message = new Message();
        String toString = message.toString();
        
        assertTrue(toString.contains("Message"));
        assertTrue(toString.contains("messageId=null"));
        assertTrue(toString.contains("kind=message"));
    }

    @Test
    void testMessageWithTextParts() {
        TextPart textPart1 = new TextPart("Hello");
        TextPart textPart2 = new TextPart("World");
        
        List<Part> parts = Arrays.asList(textPart1, textPart2);
        
        Message message = Message.builder()
                .messageId("text-msg")
                .role("user")
                .parts(parts)
                .build();
        
        assertEquals(parts, message.getParts());
        assertEquals(2, message.getParts().size());
        assertTrue(message.getParts().get(0) instanceof TextPart);
        assertTrue(message.getParts().get(1) instanceof TextPart);
        assertEquals("Hello", ((TextPart) message.getParts().get(0)).getText());
        assertEquals("World", ((TextPart) message.getParts().get(1)).getText());
    }

    @Test
    void testMessageWithFileParts() {
        FileWithUri fileContent = new FileWithUri("test.txt", "text/plain", "https://example.com/test.txt");
        
        FilePart filePart = new FilePart(fileContent);
        
        List<Part> parts = Arrays.asList(filePart);
        
        Message message = Message.builder()
                .messageId("file-msg")
                .role("agent")
                .parts(parts)
                .build();
        
        assertEquals(parts, message.getParts());
        assertEquals(1, message.getParts().size());
        assertTrue(message.getParts().get(0) instanceof FilePart);
        assertEquals("test.txt", ((FilePart) message.getParts().get(0)).getFile().getName());
        assertEquals("text/plain", ((FilePart) message.getParts().get(0)).getFile().getMimeType());
    }

    @Test
    void testMessageWithDataParts() {
        DataPart dataPart = new DataPart();
        dataPart.setData("base64-encoded-data");
        
        List<Part> parts = Arrays.asList(dataPart);
        
        Message message = Message.builder()
                .messageId("data-msg")
                .role("agent")
                .parts(parts)
                .build();
        
        assertEquals(parts, message.getParts());
        assertEquals(1, message.getParts().size());
        assertTrue(message.getParts().get(0) instanceof DataPart);
        assertEquals("base64-encoded-data", ((DataPart) message.getParts().get(0)).getData());
    }

    @Test
    void testMessageWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        metadata.put("category", "test");
        metadata.put("tags", Arrays.asList("tag1", "tag2"));
        metadata.put("config", Map.of("timeout", 30, "retries", 3));
        
        Message message = Message.builder()
                .messageId("metadata-msg")
                .role("user")
                .metadata(metadata)
                .build();
        
        assertEquals(metadata, message.getMetadata());
        assertEquals("high", message.getMetadata().get("priority"));
        assertEquals("test", message.getMetadata().get("category"));
    }

    @Test
    void testMessageKindDefaultValue() {
        Message message = new Message();
        assertEquals("message", message.getKind());
        
        Message builtMessage = Message.builder().messageId("test").build();
        assertEquals("message", builtMessage.getKind());
    }

    @Test
    void testMessageKindCustomValue() {
        Message message = new Message();
        message.setKind("custom-message-type");
        assertEquals("custom-message-type", message.getKind());
    }

    @Test
    void testMessageWithNullParts() {
        Message message = Message.builder()
                .messageId("null-parts-msg")
                .role("user")
                .parts(null)
                .build();
        
        assertNull(message.getParts());
    }

    @Test
    void testMessageWithEmptyParts() {
        Message message = Message.builder()
                .messageId("empty-parts-msg")
                .role("user")
                .parts(Arrays.asList())
                .build();
        
        assertNotNull(message.getParts());
        assertEquals(0, message.getParts().size());
    }

    @Test
    void testMessageWithNullMetadata() {
        Message message = Message.builder()
                .messageId("null-metadata-msg")
                .role("user")
                .metadata(null)
                .build();
        
        assertNull(message.getMetadata());
    }

    @Test
    void testMessageWithEmptyMetadata() {
        Message message = Message.builder()
                .messageId("empty-metadata-msg")
                .role("user")
                .metadata(new HashMap<>())
                .build();
        
        assertNotNull(message.getMetadata());
        assertEquals(0, message.getMetadata().size());
    }

    @Test
    void testMessageRoleValues() {
        Message userMessage = Message.builder()
                .messageId("user-msg")
                .role("user")
                .build();
        assertEquals("user", userMessage.getRole());
        
        Message agentMessage = Message.builder()
                .messageId("agent-msg")
                .role("agent")
                .build();
        assertEquals("agent", agentMessage.getRole());
        
        Message assistantMessage = Message.builder()
                .messageId("assistant-msg")
                .role("assistant")
                .build();
        assertEquals("assistant", assistantMessage.getRole());
    }

    @Test
    void testMessageWithMixedParts() {
        TextPart textPart = new TextPart("Text content");
        FileWithUri fileContent = new FileWithUri("document.pdf", "application/pdf", "https://example.com/doc.pdf");
        FilePart filePart = new FilePart(fileContent);
        DataPart dataPart = new DataPart();
        dataPart.setData("binary-data");
        
        List<Part> mixedParts = Arrays.asList(textPart, filePart, dataPart);
        
        Message message = Message.builder()
                .messageId("mixed-msg")
                .role("agent")
                .parts(mixedParts)
                .build();
        
        assertEquals(3, message.getParts().size());
        assertTrue(message.getParts().get(0) instanceof TextPart);
        assertTrue(message.getParts().get(1) instanceof FilePart);
        assertTrue(message.getParts().get(2) instanceof DataPart);
    }
} 
