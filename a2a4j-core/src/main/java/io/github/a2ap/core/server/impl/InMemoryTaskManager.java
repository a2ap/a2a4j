package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.Part;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * In-memory implementation of the TaskManager interface.
 * This implementation stores all tasks in memory and is suitable for testing
 * and demonstration purposes.
 */
@Slf4j
@Component
public class InMemoryTaskManager implements TaskManager {
    
    private final Map<String, Sinks.Many<Task>> taskUpdateSinks = new ConcurrentHashMap<>();
    private final TaskStore taskStore = new InMemoryTaskStore();
    private final Map<String, TaskPushNotificationConfig> notificationConfigMap = new ConcurrentHashMap<>();

    @Override
    public TaskContext loadOrCreateTask(MessageSendParams params) {
        String taskId = params.getMessage().getTaskId();
        taskId = taskId == null ? UUID.randomUUID().toString() : taskId;
        TaskContext taskContext = taskStore.load(taskId);
        if (taskContext == null) {
            // create the new one take
            Task task = Task.builder()
                    .id(taskId)
                    .status(TaskStatus.builder()
                            .state(TaskState.SUBMITTED)
                            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                            .build())
                    .contextId(UUID.randomUUID().toString())
                    .metadata(params.getMetadata())
                    .artifacts(new LinkedList<>())
                    .build();
            List<Message> initMessages = new LinkedList<>();
            initMessages.add(params.getMessage());
            taskContext = TaskContext.builder()
                    .task(task).history(initMessages)
                    .userMessage(params.getMessage())
                    .build();
            log.info("Create new message task: {}", taskContext);
        } else {
            TaskState taskState = taskContext.getTask().getStatus().getState();
            if (taskState == TaskState.COMPLETED || taskState == TaskState.FAILED || taskState == TaskState.CANCELED || taskState == TaskState.REJECTED) {
                log.warn("Received message for task {} already in final state {}. Handling as new submission (keeping history)", taskId, taskState);
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.SUBMITTED)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(taskContext, taskStatusUpdate).block();
            } else if (taskState == TaskState.INPUT_REQUIRED || taskState == TaskState.AUTH_REQUIRED) {
                log.info("Received message while {}, changing task {} state to 'working'", taskState, taskId);
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.WORKING)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(taskContext, taskStatusUpdate).block();
            } else if (taskState == TaskState.WORKING) {
                log.info("Received message while task {} already 'working'. Proceeding.", taskId);
            } else {
                log.info("receiving task {} another message might be odd, but proceed.", taskId);
            }
        }
        return taskContext;
    }

    @Override
    public Task getTask(String taskId) {
        TaskContext taskContext = taskStore.load(taskId);
        if (taskContext == null) {
            return null;
        } else {
            return taskContext.getTask();
        }
    }

    @Override
    public Task cancelTask(String taskId) {
        TaskContext taskContext = taskStore.load(taskId);
        if (taskContext == null) {
            throw new IllegalArgumentException("Cancel Task with ID " + taskId + " not found.");
        }
        // todo Emit the cancelled task and complete the sink
        Sinks.Many<Task> sink = taskUpdateSinks.get(taskId);
        if (sink != null) {
            sink.tryEmitNext(taskContext.getTask());
            sink.tryEmitComplete();
            taskUpdateSinks.remove(taskId);
            log.debug("Emitted cancelled task {} and completed/removed sink.", taskId);
        } else {
            log.warn("No active sink found for task {}. Cannot emit cancellation update.", taskId);
        }
        TaskStatus cancelledStatus = new TaskStatus();
        cancelledStatus.setState(TaskState.CANCELED);
        return taskContext.getTask();
    }

    @Override
    public Mono<TaskContext> applyTaskUpdate(TaskContext taskContext, List<TaskUpdate> taskUpdates) {
        if (taskUpdates == null || taskUpdates.isEmpty()) {
            return Mono.just(taskContext);
        }
        Task task = taskContext.getTask();
        for (TaskUpdate taskUpdate : taskUpdates) {
            if (taskUpdate instanceof TaskStatus taskStatus) {
                log.info("apply task {} updated with status {}", taskContext.getTask().getId(), taskStatus);
                taskStatus.setTimestamp(String.valueOf(Instant.now().toEpochMilli()));
                task.setStatus(taskStatus);
                // If the update includes an agent message, add it to history
                if (taskStatus.getMessage() != null && Objects.equals(taskStatus.getMessage().getRole(), "agent")) {
                    List<Message> history = taskContext.getHistory() == null ? new LinkedList<>() : taskContext.getHistory();
                    history.add(taskStatus.getMessage());
                    taskContext.setHistory(history);
                }
            } else if (taskUpdate instanceof Artifact artifact) {
                log.info("apply task {} updated with artifact {}", taskContext.getTask().getId(), artifact);
                List<Artifact> artifacts = task.getArtifacts();
//                Optional<Artifact> previousArtifactOption = artifacts.stream().filter(item -> 
//                        Objects.equals(item.getIndex(), artifact.getIndex()) 
//                                || Objects.equals(item.getName(), artifact.getName())).findFirst();
//                if (previousArtifactOption.isPresent()) {
//                    Artifact previousArtifact = previousArtifactOption.get();
//                    int previousIndex = artifacts.indexOf(previousArtifact);
//                    if (artifact.getAppend() != null && artifact.getAppend()) {
//                        // combine pre and current
//                        if (StringUtils.hasText(artifact.getDescription())) {
//                            previousArtifact.setDescription(artifact.getDescription());
//                        }
//                        if (Objects.nonNull(artifact.getLastChunk())) {
//                            previousArtifact.setLastChunk(artifact.getLastChunk());
//                        }
//                        if (artifact.getMetadata() != null && !artifact.getMetadata().isEmpty()) {
//                            Map<String, Object> metadata = previousArtifact.getMetadata() == null ? new HashMap<>(8) : previousArtifact.getMetadata();
//                            metadata.putAll(artifact.getMetadata());
//                            previousArtifact.setMetadata(metadata);
//                        }
//                        if (artifact.getParts() != null && !artifact.getParts().isEmpty()) {
//                            List<Part> parts = previousArtifact.getParts() == null ? new LinkedList<>() : previousArtifact.getParts();
//                            parts.addAll(artifact.getParts());
//                            previousArtifact.setParts(parts);
//                        }
//                        artifacts.set(previousIndex, previousArtifact);
//                    } else {
//                        artifacts.set(previousIndex, artifact);
//                    }
//                } else {
//                    artifacts.add(artifact);
//                }
            } else {
                log.error("Received taskUpdate {} but not a TaskUpdate {}", taskUpdate, taskUpdate.getClass());
            }
        }
        taskStore.save(taskContext);
        return Mono.just(taskContext);
    }

    @Override
    public Mono<TaskContext> applyTaskUpdate(TaskContext taskContext, TaskUpdate update) {
        return applyTaskUpdate(taskContext, Stream.of(update).collect(Collectors.toList()));
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
