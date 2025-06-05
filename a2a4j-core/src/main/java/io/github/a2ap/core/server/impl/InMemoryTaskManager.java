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

package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * In-memory implementation of the TaskManager interface.
 * This implementation stores all tasks in memory and is suitable for testing
 * and demonstration purposes.
 */
@Component
public class InMemoryTaskManager implements TaskManager {

    private static final Logger log = LoggerFactory.getLogger(InMemoryTaskManager.class);

    private final Map<String, Sinks.Many<Task>> taskUpdateSinks = new ConcurrentHashMap<>();
    private final TaskStore taskStore = new InMemoryTaskStore();
    private final Map<String, TaskPushNotificationConfig> notificationConfigMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> contextTaskIdMap = new ConcurrentHashMap<>();

    @Override
    public RequestContext loadOrCreateContext(MessageSendParams params) {
        String taskId = params.getMessage().getTaskId();
        taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
        String contextId = params.getMessage().getContextId();
        contextId = contextId == null ? UUID.randomUUID().toString() : contextId;
        RequestContext.RequestContextBuilder contextBuilder = RequestContext.builder()
                .taskId(taskId).contextId(contextId).request(params);
        Task currentTask = taskStore.load(taskId);
        if (currentTask == null) {
            // create the new one take
            currentTask = Task.builder()
                    .id(taskId)
                    .contextId(contextId)
                    .status(TaskStatus.builder()
                            .state(TaskState.SUBMITTED)
                            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                            .build())
                    .metadata(params.getMetadata())
                    .artifacts(new LinkedList<>())
                    .history(new LinkedList<>())
                    .build();
            log.info("Create new message task: {}", currentTask);
        } else {
            TaskState taskState = currentTask.getStatus().getState();
            if (taskState == TaskState.COMPLETED || taskState == TaskState.FAILED || taskState == TaskState.CANCELED
                    || taskState == TaskState.REJECTED) {
                log.warn(
                        "Received message for task {} already in final state {}. Handling as new submission (keeping history)",
                        taskId, taskState);
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.SUBMITTED)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(currentTask, taskStatusUpdate).block();
            } else if (taskState == TaskState.INPUT_REQUIRED || taskState == TaskState.AUTH_REQUIRED) {
                log.info("Received message while {}, changing task {} state to 'working'", taskState, taskId);
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.WORKING)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(currentTask, taskStatusUpdate).block();
            } else if (taskState == TaskState.WORKING) {
                log.info("Received message while task {} already 'working'. Proceeding.", taskId);
            } else {
                log.info("receiving task {} another message might be odd, but proceed.", taskId);
            }
        }
        contextBuilder.task(currentTask);
        Set<String> relatedTaskIds = contextTaskIdMap.computeIfAbsent(contextId, k -> new HashSet<>());
        relatedTaskIds.add(taskId);
        final String currentTaskId = taskId;
        List<Task> relatedTasksList = relatedTaskIds.stream().map(id -> {
            if (Objects.equals(id, currentTaskId)) {
                return null;
            } else {
                return taskStore.load(id);
            }
        }).filter(Objects::nonNull).toList();
        contextBuilder.relatedTasks(relatedTasksList);
        return contextBuilder.build();
    }

    @Override
    public Task getTask(String taskId) {
        return taskStore.load(taskId);
    }

    @Override
    public Task cancelTask(String taskId) {
        Task task = taskStore.load(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Cancel Task with ID " + taskId + " not found.");
        }
        // todo Emit the cancelled task and complete the sink
        Sinks.Many<Task> sink = taskUpdateSinks.get(taskId);
        if (sink != null) {
            sink.tryEmitNext(task);
            sink.tryEmitComplete();
            taskUpdateSinks.remove(taskId);
            log.debug("Emitted cancelled task {} and completed/removed sink.", taskId);
        } else {
            log.warn("No active sink found for task {}. Cannot emit cancellation update.", taskId);
        }
        TaskStatus cancelledStatus = new TaskStatus();
        cancelledStatus.setState(TaskState.CANCELED);
        return task;
    }

    @Override
    public Mono<Task> applyTaskUpdate(Task task, List<TaskUpdate> taskUpdates) {
        if (taskUpdates == null || taskUpdates.isEmpty()) {
            return Mono.just(task);
        }
        for (TaskUpdate taskUpdate : taskUpdates) {
            if (taskUpdate instanceof TaskStatus taskStatus) {
                log.info("apply task {} updated with status {}", task.getId(), taskStatus);
                taskStatus.setTimestamp(String.valueOf(Instant.now().toEpochMilli()));
                task.setStatus(taskStatus);
                // If the update includes an agent message, add it to history
                if (taskStatus.getMessage() != null && Objects.equals(taskStatus.getMessage().getRole(), "agent")) {
                    List<Message> history = task.getHistory() == null ? new LinkedList<>() : task.getHistory();
                    history.add(taskStatus.getMessage());
                    task.setHistory(history);
                }
            } else if (taskUpdate instanceof Artifact artifact) {
                log.info("apply task {} updated with artifact {}", task.getId(), artifact);
                List<Artifact> artifacts = task.getArtifacts();
                // Optional<Artifact> previousArtifactOption = artifacts.stream().filter(item ->
                // Objects.equals(item.getIndex(), artifact.getIndex())
                // || Objects.equals(item.getName(), artifact.getName())).findFirst();
                // if (previousArtifactOption.isPresent()) {
                // Artifact previousArtifact = previousArtifactOption.get();
                // int previousIndex = artifacts.indexOf(previousArtifact);
                // if (artifact.getAppend() != null && artifact.getAppend()) {
                // // combine pre and current
                // if (StringUtils.hasText(artifact.getDescription())) {
                // previousArtifact.setDescription(artifact.getDescription());
                // }
                // if (Objects.nonNull(artifact.getLastChunk())) {
                // previousArtifact.setLastChunk(artifact.getLastChunk());
                // }
                // if (artifact.getMetadata() != null && !artifact.getMetadata().isEmpty()) {
                // Map<String, Object> metadata = previousArtifact.getMetadata() == null ? new
                // HashMap<>(8) : previousArtifact.getMetadata();
                // metadata.putAll(artifact.getMetadata());
                // previousArtifact.setMetadata(metadata);
                // }
                // if (artifact.getParts() != null && !artifact.getParts().isEmpty()) {
                // List<Part> parts = previousArtifact.getParts() == null ? new LinkedList<>() :
                // previousArtifact.getParts();
                // parts.addAll(artifact.getParts());
                // previousArtifact.setParts(parts);
                // }
                // artifacts.set(previousIndex, previousArtifact);
                // } else {
                // artifacts.set(previousIndex, artifact);
                // }
                // } else {
                // artifacts.add(artifact);
                // }
            } else {
                log.error("Received taskUpdate {} but not a TaskUpdate {}", taskUpdate, taskUpdate.getClass());
            }
        }
        taskStore.save(task);
        return Mono.just(task);
    }

    @Override
    public Mono<Task> applyTaskUpdate(Task task, TaskUpdate update) {
        return applyTaskUpdate(task, Stream.of(update).collect(Collectors.toList()));
    }

    @Override
    public void registerTaskNotification(TaskPushNotificationConfig config) {
        notificationConfigMap.put(config.getTaskId(), config);
    }

    @Override
    public TaskPushNotificationConfig getTaskNotification(String taskId) {
        return notificationConfigMap.get(taskId);
    }
}
