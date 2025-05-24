package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.event.TaskArtifactUpdateEvent;
import io.github.a2ap.core.event.TaskStatusUpdateEvent;
import io.github.a2ap.core.event.TaskUpdateEvent;
import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.TaskHandler;
import io.github.a2ap.core.server.TaskManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the A2AServer interface.
 * This class provides the core functionality for an A2A server.
 */
@Slf4j
@Component
public class A2AServerImpl implements A2AServer {
    
    private final TaskManager taskManager;
    private final TaskHandler taskHandler;
    
    /**
     * Constructs a new A2AServerImpl with the specified TaskStore and TaskHandler.
     *
     * @param taskStore The TaskStore to use for task management.
     * @param taskHandler The TaskHandler to use for task processing.
     */
    public A2AServerImpl(TaskHandler taskHandler, TaskManager taskManager) {
        this.taskHandler = taskHandler;
        this.taskManager = taskManager;
        log.info("A2AServerImpl initialized with TaskManager: {} and TaskHandler: {}",
                taskManager.getClass().getSimpleName(), taskHandler.getClass().getSimpleName());
    }

    /**
     * Handle the task on the server.
     *
     * @param params The Task params object to handle.
     * @return The Task object with updated status and ID.
     * @throws IllegalArgumentException if the task is invalid 
     */
    @Override
    public Task handleTask(TaskSendParams params) {
        log.info("Attempting to handle the task: {}", params);
        if (params == null || params.getId() == null || params.getSessionId() == null) {
            log.error("Task handle failed: Task params must have a id and session id.");
            throw new IllegalArgumentException("Task params must have a id and session id");
        }
        TaskContext taskContext = taskManager.loadOrCreateTask(params);
        log.info("Task context loaded: {}", taskContext.getTask());
        Flux<TaskUpdate> taskFlux = taskHandler.handle(taskContext);
        List<TaskUpdate> taskUpdates = taskFlux.collectList().block();
        Mono<Task> taskMono = taskManager.applyTaskUpdate(taskUpdates);
        Task task = taskMono.block();
        log.info("Task handle success: {}", task);
        return task;
    }

    @Override
    public Flux<TaskUpdateEvent> handleSubscribeTask(TaskSendParams params) {
        log.info("Attempting to handle and subscribe to task: {}", params);
        TaskContext taskContext = taskManager.loadOrCreateTask(params);
        log.info("Task context loaded: {}", taskContext.getTask());
        return taskHandler.handle(taskContext).map(update -> {
             Task updateTask = taskManager.applyTaskUpdate(update).block();
             TaskUpdateEvent updateEvent = null;
             if (update instanceof TaskStatus) {
                 // todo some check status and 
                 boolean isComplete = ((TaskStatus) update).getState() == TaskState.COMPLETED;
                 updateEvent = TaskStatusUpdateEvent.builder()
                         .id(updateTask.getId())
                         .status((TaskStatus) update)
                         .isFinal(isComplete)
                         .build();
             } else if (update instanceof Artifact) {
                 // todo some
                 updateEvent = TaskArtifactUpdateEvent.builder()
                         .id(updateTask.getId())
                         .artifact((Artifact) update)
                         .build();
             } else {
                 // todo 
             }
             return updateEvent;
        }).doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates via handleSubscribeTask.", taskContext.getTask().getId()))
                .doOnComplete(() -> log.debug("Task {} updates stream completed via handleSubscribeTask.", taskContext.getTask().getId()))
                .doOnError(e -> log.error("Error in task {} updates stream via handleSubscribeTask: {}", taskContext.getTask().getId(), e.getMessage(), e));
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return The Task object if found, otherwise null.
     */
    @Override
    public Task getTask(String taskId) {
        log.info("Getting task with ID: {}", taskId);
        Task task = taskManager.getTask(taskId);
        if (task != null) {
            log.debug("Found task {}: {}", taskId, task);
        } else {
            log.warn("Task with ID {} not found.", taskId);
        }
        return task;
    }

    /**
     * Cancels a task.
     *
     * @param taskId The ID of the task to cancel.
     * @return The cancelled Task object if successful, otherwise null.
     */
    @Override
    public Task cancelTask(String taskId) {
        log.info("Attempting to cancel task with ID: {}", taskId);
        // Use taskStore to cancel task
        Task cancelledTask = taskManager.cancelTask(taskId);
        if (cancelledTask != null) {
            log.info("Task {} cancelled successfully.", taskId);
        } else {
            log.warn("Failed to cancel task with ID {}. Task not found or already completed/failed.", taskId);
        }
        return cancelledTask;
    }

    /**
     * Sets or updates the push notification configuration for a task.
     *
     * @param config The TaskPushNotificationConfig to set.
     * @return The set TaskPushNotificationConfig if successful, otherwise null.
     */
    @Override
    public TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig config) {
        log.info("Attempting to set push notification config for task: {}",
                config != null ? config.getTaskId() : "null");
        if (config == null || config.getTaskId() == null || config.getTaskId().isEmpty()) {
            log.warn("Failed to set push notification config: Invalid config provided.");
            return null; // Invalid config
        }
        taskManager.registerTaskNotification(config);
        log.info("Push notification config set for task {}.", config.getTaskId());
        return config; // Return the set config
    }

    /**
     * Retrieves the push notification configuration for a task.
     *
     * @param taskId The ID of the task.
     * @return The TaskPushNotificationConfig if found, otherwise null.
     */
    @Override
    public TaskPushNotificationConfig getTaskPushNotification(String taskId) {
        log.info("Getting push notification config for task ID: {}", taskId);
        TaskPushNotificationConfig config = taskManager.getTaskNotification(taskId);
        if (config != null) {
            log.debug("Found push notification config for task {}: {}", taskId, config);
        } else {
            log.warn("Push notification config not found for task ID {}.", taskId);
        }
        return config;
    }

    /**
     * Subscribes to streaming updates for a task.
     *
     * @param taskId The ID of the task to subscribe to.
     * @return A Flux of Task objects representing the updates.
     */
    @Override
    public Flux<TaskUpdateEvent> subscribeToTaskUpdates(String taskId) {
        log.info("Subscribing to task updates for ID: {}", taskId);
        return Flux.empty();
//        Sinks.Many<Task> sink = taskUpdateSinks.computeIfAbsent(taskId,
//                id -> {
//                    log.debug("Creating new sink for task {}.", id);
//                    return Sinks.many().multicast().onBackpressureBuffer();
//                });
//        log.debug("Returning Flux for task {} updates.", taskId);
//        return sink.asFlux()
//                .doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates.", taskId))
//                .doOnComplete(() -> log.debug("Task {} updates stream completed.", taskId))
//                .doOnError(e -> log.error("Error in task {} updates stream: {}", taskId, e.getMessage(), e));
    }

    /**
     * Retrieves the AgentCard for the server itself.
     *
     * @return The AgentCard of the server.
     */
    @Override
    public AgentCard getSelfAgentCard() {
        log.info("Getting self agent card.");
        // TODO: Provide actual agent card information
        AgentCard selfCard = AgentCard.builder()
                .name("Example Java Agent")
                .description("A sample A2A agent implemented in Java.")
                .url("http://localhost:8080/a2a") // Example URL
                .version("1.0.0")
                // Placeholder capabilities - replace with actual capabilities
                .capabilities(new AgentCapabilities())
                .skills(List.of()) // No skills defined yet
                .build();
        log.debug("Returning self agent card: {}", selfCard);
        return selfCard;
    }
}
