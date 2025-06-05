package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.Part;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
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
                
                // Initialize artifacts list if it doesn't exist
                if (artifacts == null) {
                    artifacts = new LinkedList<>();
                    task.setArtifacts(artifacts);
                }
                
                String artifactId = artifact.getArtifactId();
                
                // Find existing artifact with the same ID
                Artifact existingArtifact = null;
                int existingArtifactIndex = -1;
                for (int i = 0; i < artifacts.size(); i++) {
                    Artifact art = artifacts.get(i);
                    if (art.getArtifactId() != null && art.getArtifactId().equals(artifactId)) {
                        existingArtifact = art;
                        existingArtifactIndex = i;
                        break;
                    }
                }
                
                // Since we don't have append information from the raw Artifact,
                // we default to replacing/adding the artifact
                if (existingArtifactIndex != -1) {
                    // Replace the existing artifact entirely with the new data
                    log.debug("Replacing artifact at id {} for task {}", artifactId, task.getId());
                    artifacts.set(existingArtifactIndex, artifact);
                } else {
                    // Add the new artifact since no artifact with this ID exists yet
                    log.debug("Adding new artifact with id {} for task {}", artifactId, task.getId());
                    artifacts.add(artifact);
                }
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
    public Mono<Task> applyStatusUpdate(Task task, TaskStatusUpdateEvent event) {
        log.info("apply task {} updated with status event {}", task.getId(), event);
        
        TaskStatus taskStatus = event.getStatus();
        if (taskStatus != null) {
            log.info("apply task {} updated with status {}", task.getId(), taskStatus);
            taskStatus.setTimestamp(String.valueOf(Instant.now().toEpochMilli()));
            task.setStatus(taskStatus);
            
            // If the update includes an agent message, add it to history
            if (taskStatus.getMessage() != null && Objects.equals(taskStatus.getMessage().getRole(), "agent")) {
                List<Message> history = task.getHistory() == null ? new LinkedList<>() : task.getHistory();
                history.add(taskStatus.getMessage());
                task.setHistory(history);
            }
        } else {
            log.warn("Received TaskStatusUpdateEvent for task {} but status is null", task.getId());
        }
        
        taskStore.save(task);
        return Mono.just(task);
    }

    /**
     * Apply artifact update with append support from TaskArtifactUpdateEvent
     * @param task The task to update
     * @param event The TaskArtifactUpdateEvent containing artifact and append information
     * @return Updated task
     */
    public Mono<Task> applyArtifactUpdate(Task task, TaskArtifactUpdateEvent event) {
        log.info("apply task {} updated with artifact event {}", task.getId(), event);
        
        List<Artifact> artifacts = task.getArtifacts();
        
        // Initialize artifacts list if it doesn't exist
        if (artifacts == null) {
            artifacts = new LinkedList<>();
            task.setArtifacts(artifacts);
        }
        
        Artifact newArtifactData = event.getArtifact();
        String artifactId = newArtifactData.getArtifactId();
        boolean appendParts = event.getAppend() != null ? event.getAppend() : false;
        
        // Find existing artifact with the same ID
        Artifact existingArtifact = null;
        int existingArtifactIndex = -1;
        for (int i = 0; i < artifacts.size(); i++) {
            Artifact art = artifacts.get(i);
            if (art.getArtifactId() != null && art.getArtifactId().equals(artifactId)) {
                existingArtifact = art;
                existingArtifactIndex = i;
                break;
            }
        }
        
        if (!appendParts) {
            // This represents the first chunk for this artifact ID.
            if (existingArtifactIndex != -1) {
                // Replace the existing artifact entirely with the new data
                log.debug("Replacing artifact at id {} for task {}", artifactId, task.getId());
                artifacts.set(existingArtifactIndex, newArtifactData);
            } else {
                // Add the new artifact since no artifact with this ID exists yet
                log.debug("Adding new artifact with id {} for task {}", artifactId, task.getId());
                artifacts.add(newArtifactData);
            }
        } else if (existingArtifact != null) {
            // Append new parts to the existing artifact's part list
            log.debug("Appending parts to artifact id {} for task {}", artifactId, task.getId());
            if (existingArtifact.getParts() != null && newArtifactData.getParts() != null) {
                List<Part> parts = new LinkedList<>(existingArtifact.getParts());
                parts.addAll(newArtifactData.getParts());
                existingArtifact.setParts(parts);
            }
        } else {
            // We received a chunk to append, but we don't have an existing artifact.
            // we will ignore this chunk
            log.warn("Received append=true for nonexistent artifact id {} in task {}. Ignoring chunk.", 
                    artifactId, task.getId());
        }
        
        taskStore.save(task);
        return Mono.just(task);
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
