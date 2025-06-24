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

class TaskTest {

    @Test
    void testDefaultConstructor() {
        Task task = new Task();
        assertNotNull(task);
        assertNull(task.getId());
        assertNull(task.getContextId());
        assertNull(task.getStatus());
        assertNull(task.getArtifacts());
        assertNull(task.getHistory());
        assertNull(task.getMetadata());
        assertEquals("task", task.getKind());
    }

    @Test
    void testConstructorWithParameters() {
        String id = "task-123";
        String contextId = "context-456";
        TaskStatus status = TaskStatus.builder().state(TaskState.WORKING).build();
        List<Artifact> artifacts = Arrays.asList(new Artifact());
        List<Message> history = Arrays.asList(new Message());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        Task task = new Task(id, contextId, status, artifacts, history, metadata);
        
        assertEquals(id, task.getId());
        assertEquals(contextId, task.getContextId());
        assertEquals(status, task.getStatus());
        assertEquals(artifacts, task.getArtifacts());
        assertEquals(history, task.getHistory());
        assertEquals(metadata, task.getMetadata());
        assertEquals("task", task.getKind());
    }

    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        
        task.setId("task-123");
        task.setContextId("context-456");
        task.setStatus(TaskStatus.COMPLETED);
        
        List<Artifact> artifacts = Arrays.asList(new Artifact());
        task.setArtifacts(artifacts);
        
        List<Message> history = Arrays.asList(new Message());
        task.setHistory(history);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        task.setMetadata(metadata);
        
        task.setKind("custom-task");
        
        assertEquals("task-123", task.getId());
        assertEquals("context-456", task.getContextId());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(artifacts, task.getArtifacts());
        assertEquals(history, task.getHistory());
        assertEquals(metadata, task.getMetadata());
        assertEquals("custom-task", task.getKind());
    }

    @Test
    void testBuilder() {
        List<Artifact> artifacts = Arrays.asList(new Artifact());
        List<Message> history = Arrays.asList(new Message());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");

        Task task = Task.builder()
                .id("builder-task-123")
                .contextId("builder-context-456")
                .status(TaskStatus.COMPLETED)
                .artifacts(artifacts)
                .history(history)
                .metadata(metadata)
                .build();

        assertEquals("builder-task-123", task.getId());
        assertEquals("builder-context-456", task.getContextId());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(artifacts, task.getArtifacts());
        assertEquals(history, task.getHistory());
        assertEquals(metadata, task.getMetadata());
        assertEquals("task", task.getKind());
    }

    @Test
    void testBuilderWithPartialData() {
        Task task = Task.builder()
                .id("partial-task-123")
                .status(TaskStatus.builder().state(TaskState.SUBMITTED).build())
                .build();

        assertEquals("partial-task-123", task.getId());
        assertEquals(TaskState.SUBMITTED, task.getStatus().getState());
        assertNull(task.getContextId());
        assertNull(task.getArtifacts());
        assertNull(task.getHistory());
        assertNull(task.getMetadata());
    }

    @Test
    void testEqualsAndHashCode() {
        Task task1 = Task.builder()
                .id("task-123")
                .contextId("context-456")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();

        Task task2 = Task.builder()
                .id("task-123")
                .contextId("context-456")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();

        Task task3 = Task.builder()
                .id("different-task-123")
                .contextId("context-456")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();

        assertEquals(task1, task2);
        assertNotEquals(task1, task3);
        assertEquals(task1.hashCode(), task2.hashCode());
        assertNotEquals(task1.hashCode(), task3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        Task task = Task.builder()
                .id("task-123")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();
        assertNotEquals(null, task);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Task task = Task.builder()
                .id("task-123")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();
        assertNotEquals("string", task);
    }

    @Test
    void testToString() {
        Task task = Task.builder()
                .id("task-123")
                .contextId("context-456")
                .status(TaskStatus.builder().state(TaskState.WORKING).build())
                .build();

        String toString = task.toString();
        
        assertTrue(toString.contains("task-123"));
        assertTrue(toString.contains("context-456"));
        assertTrue(toString.contains("WORKING"));
        assertTrue(toString.contains("Task"));
    }

    @Test
    void testToStringWithNullValues() {
        Task task = new Task();
        String toString = task.toString();
        
        assertTrue(toString.contains("Task"));
        assertTrue(toString.contains("id=null"));
        assertTrue(toString.contains("kind=task"));
    }

    @Test
    void testTaskStatusValues() {
        // Test TaskStatus constants
        assertEquals(TaskState.COMPLETED, TaskStatus.COMPLETED.getState());
        assertEquals(TaskState.CANCELED, TaskStatus.CANCELLED.getState());
    }

    @Test
    void testTaskStateValues() {
        // Test all TaskState enum values
        assertEquals("submitted", TaskState.SUBMITTED.getValue());
        assertEquals("working", TaskState.WORKING.getValue());
        assertEquals("input-required", TaskState.INPUT_REQUIRED.getValue());
        assertEquals("completed", TaskState.COMPLETED.getValue());
        assertEquals("failed", TaskState.FAILED.getValue());
        assertEquals("canceled", TaskState.CANCELED.getValue());
        assertEquals("rejected", TaskState.REJECTED.getValue());
        assertEquals("auth-required", TaskState.AUTH_REQUIRED.getValue());
        assertEquals("unknown", TaskState.UNKNOWN.getValue());
    }

    @Test
    void testTaskWithArtifacts() {
        Artifact artifact1 = new Artifact();
        artifact1.setArtifactId("artifact-1");
        artifact1.setName("Test Artifact 1");
        
        Artifact artifact2 = new Artifact();
        artifact2.setArtifactId("artifact-2");
        artifact2.setName("Test Artifact 2");
        
        List<Artifact> artifacts = Arrays.asList(artifact1, artifact2);
        
        Task task = Task.builder()
                .id("artifacts-task")
                .artifacts(artifacts)
                .build();
        
        assertEquals(artifacts, task.getArtifacts());
        assertEquals(2, task.getArtifacts().size());
        assertEquals("artifact-1", task.getArtifacts().get(0).getArtifactId());
        assertEquals("artifact-2", task.getArtifacts().get(1).getArtifactId());
    }

    @Test
    void testTaskWithHistory() {
        Message message1 = new Message();
        message1.setMessageId("message-1");
        message1.setRole("user");
        
        Message message2 = new Message();
        message2.setMessageId("message-2");
        message2.setRole("agent");
        
        List<Message> history = Arrays.asList(message1, message2);
        
        Task task = Task.builder()
                .id("history-task")
                .history(history)
                .build();
        
        assertEquals(history, task.getHistory());
        assertEquals(2, task.getHistory().size());
        assertEquals("message-1", task.getHistory().get(0).getMessageId());
        assertEquals("message-2", task.getHistory().get(1).getMessageId());
    }

    @Test
    void testTaskWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        metadata.put("category", "test");
        metadata.put("tags", Arrays.asList("tag1", "tag2"));
        metadata.put("config", Map.of("timeout", 30, "retries", 3));
        
        Task task = Task.builder()
                .id("metadata-task")
                .metadata(metadata)
                .build();
        
        assertEquals(metadata, task.getMetadata());
        assertEquals("high", task.getMetadata().get("priority"));
        assertEquals("test", task.getMetadata().get("category"));
    }

    @Test
    void testTaskKindDefaultValue() {
        Task task = new Task();
        assertEquals("task", task.getKind());
        
        Task builtTask = Task.builder().id("test").build();
        assertEquals("task", builtTask.getKind());
    }

    @Test
    void testTaskKindCustomValue() {
        Task task = new Task();
        task.setKind("custom-task-type");
        assertEquals("custom-task-type", task.getKind());
    }

    @Test
    void testTaskWithNullArtifacts() {
        Task task = Task.builder()
                .id("null-artifacts-task")
                .artifacts(null)
                .build();
        
        assertNull(task.getArtifacts());
    }

    @Test
    void testTaskWithEmptyArtifacts() {
        Task task = Task.builder()
                .id("empty-artifacts-task")
                .artifacts(Arrays.asList())
                .build();
        
        assertNotNull(task.getArtifacts());
        assertEquals(0, task.getArtifacts().size());
    }

    @Test
    void testTaskWithNullHistory() {
        Task task = Task.builder()
                .id("null-history-task")
                .history(null)
                .build();
        
        assertNull(task.getHistory());
    }

    @Test
    void testTaskWithEmptyHistory() {
        Task task = Task.builder()
                .id("empty-history-task")
                .history(Arrays.asList())
                .build();
        
        assertNotNull(task.getHistory());
        assertEquals(0, task.getHistory().size());
    }

    @Test
    void testTaskWithNullMetadata() {
        Task task = Task.builder()
                .id("null-metadata-task")
                .metadata(null)
                .build();
        
        assertNull(task.getMetadata());
    }

    @Test
    void testTaskWithEmptyMetadata() {
        Task task = Task.builder()
                .id("empty-metadata-task")
                .metadata(new HashMap<>())
                .build();
        
        assertNotNull(task.getMetadata());
        assertEquals(0, task.getMetadata().size());
    }
} 
