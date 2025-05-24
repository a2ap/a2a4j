package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    public TaskContext loadOrCreateTask(TaskSendParams params) {
        TaskContext taskContext = taskStore.load(params.getId());
        if (taskContext == null) {
            // create the new one take
            Task task = Task.builder()
                    .id(params.getId())
                    .status(TaskStatus.builder()
                            .state(TaskState.SUBMITTED)
                            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                            .build())
                    .sessionId(params.getSessionId())
                    .metadata(params.getMetadata())
                    .artifacts(new LinkedList<>())
                    .build();
            List<Message> initMessages = new LinkedList<>();
            initMessages.add(params.getMessage());
            taskContext = TaskContext.builder()
                    .task(task).history(initMessages)
                    .userMessage(params.getMessage())
                    .build();
            log.info("Create new task: {}", taskContext);
        } else {
            TaskState taskState = taskContext.getTask().getStatus().getState();
            if (taskState == TaskState.COMPLETED || taskState == TaskState.FAILED || taskState == TaskState.CANCELED) {
                log.warn("Received message for task {} already in final state {}. Handling as new submission (keeping history)", params.getId(), taskState);
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.SUBMITTED)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(taskContext, taskStatusUpdate).block();
            } else if (taskState == TaskState.INPUT_REQUIRED) {
                log.info("Received message while 'input-required', changing task {} state to 'working'", params.getId());
                TaskStatus taskStatusUpdate = TaskStatus.builder()
                        .state(TaskState.WORKING)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .build();
                applyTaskUpdate(taskContext, taskStatusUpdate).block();
            } else if (taskState == TaskState.WORKING) {
                log.info("Received message while task {} already 'working'. Proceeding.", params.getId());
            } else {
                log.info("receiving task {} another message might be odd, but proceed.", params.getId());
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
            } else if (taskUpdate instanceof Artifact artifact) {
                log.info("apply task {} updated with artifact {}", taskContext.getTask().getId(), artifact);
                // todo
            } else {
                log.error("Received taskUpdate {} but not a TaskUpdate {}", taskUpdate, taskUpdate.getClass());
            }
        }
        taskStore.save(taskContext.getTask(), taskContext.getHistory());
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
