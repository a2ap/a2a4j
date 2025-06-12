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

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import io.github.a2ap.core.server.QueueManager;
import io.github.a2ap.core.server.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Implementation of the A2AServer interface. This class provides the core functionality
 * for an A2A server.
 */
public class DefaultA2AServer implements A2AServer {

    private static final Logger log = LoggerFactory.getLogger(DefaultA2AServer.class);

    private final TaskManager taskManager;

    private final AgentExecutor agentExecutor;

    private final QueueManager queueManager;

    private final AgentCard a2aServerSelfCard;

    /**
     * Constructs a new A2AServerImpl with the specified components.
     *
     * @param taskManager   The TaskManager to use for task management.
     * @param agentExecutor The AgentExecutor to use for agent execution.
     * @param queueManager  The QueueManager to use for event queue management.
     */
    public DefaultA2AServer(TaskManager taskManager, AgentExecutor agentExecutor, QueueManager queueManager,
                            AgentCard a2aServerSelfCard) {
        this.taskManager = taskManager;
        this.agentExecutor = agentExecutor;
        this.queueManager = queueManager;
        log.info("A2AServerImpl initialized with TaskManager: {}, AgentExecutor: {}, QueueManager: {}",
            taskManager.getClass().getSimpleName(), agentExecutor.getClass().getSimpleName(),
            queueManager.getClass().getSimpleName());

        this.a2aServerSelfCard = a2aServerSelfCard;
    }

    /**
     * Handle the task on the server.
     *
     * @param params The Task params object to handle.
     * @return The Task object with updated status and ID.
     * @throws IllegalArgumentException if the task is invalid
     */
    @Override
    public SendMessageResponse handleMessage(MessageSendParams params) {
        log.info("Attempting to handle the message: {}", params);
        if (params == null || params.getMessage() == null || params.getMessage().getParts() == null
            || params.getMessage().getParts().isEmpty()) {
            log.error("Task handle failed: Task params must have at least one message.");
            throw new IllegalArgumentException("Task params must have at least one message");
        }
        RequestContext taskContext = taskManager.loadOrCreateContext(params);
        Task currentTask = taskContext.getTask();
        log.info("Task request context loaded: {}", taskContext.getTask());

        // Create event queue for this task
        final EventQueue eventQueue = queueManager.create(taskContext.getTaskId());

        // Execute agent and collect final result
        eventQueue.enqueueEvent(currentTask);
        Mono<SendMessageResponse> resultMono = agentExecutor.execute(taskContext, eventQueue)
            .then(eventQueue.asFlux().flatMap(event -> {
                if (event instanceof TaskStatusUpdateEvent) {
                    return taskManager.applyStatusUpdate(currentTask, (TaskStatusUpdateEvent) event);
                } else if (event instanceof TaskArtifactUpdateEvent) {
                    return taskManager.applyArtifactUpdate(currentTask, (TaskArtifactUpdateEvent) event);
                } else {
                    return Mono.just(event);
                }
            }).filter(event -> !(event instanceof Task))
                .cast(SendMessageResponse.class)
                .next()
                .doOnError(e -> log.error("Error in task {} updates stream via handleMessage: {}",
                    taskContext.getTaskId(), e.getMessage(), e))
                .doOnTerminate(() -> {
                    log.debug("Agent execution completed for task: {}", taskContext.getTaskId());
                    queueManager.remove(taskContext.getTaskId());
                }));

        SendMessageResponse response = resultMono.block();
        response = response == null ? currentTask : response;
        log.info("Handle message success: {}", response);
        return response;
    }

    @Override
    public Flux<SendStreamingMessageResponse> handleMessageStream(MessageSendParams params) {
        log.info("Attempting to handle the streaming message: {}", params);
        if (params == null || params.getMessage() == null || params.getMessage().getParts() == null
            || params.getMessage().getParts().isEmpty()) {
            log.error("Streaming handle failed: Task params must have at least one message.");
            throw new IllegalArgumentException("Task params must have at least one message");
        }

        RequestContext taskContext = taskManager.loadOrCreateContext(params);
        Task currentTask = taskContext.getTask();
        log.info("Task request context loaded: {}", taskContext.getTask());

        // Create event queue for this task
        final EventQueue eventQueue = queueManager.create(taskContext.getTaskId());

        // Execute agent and collect final result
        return agentExecutor.execute(taskContext, eventQueue).thenMany(eventQueue.asFlux().doOnNext(event -> {
            if (event instanceof TaskStatusUpdateEvent) {
                taskManager.applyStatusUpdate(currentTask, (TaskStatusUpdateEvent) event).block();
            } else if (event instanceof TaskArtifactUpdateEvent) {
                taskManager.applyArtifactUpdate(currentTask, (TaskArtifactUpdateEvent) event).block();
            }
        }).doOnComplete(() -> log.debug("Task {} updates stream completed via handleMessageStream.",
                taskContext.getTaskId()))
            .doOnError(e -> log.error("Error in task {} updates stream via handleMessageStream: {}",
                taskContext.getTaskId(), e.getMessage(), e))
            .doOnTerminate(() -> {
                log.debug("Agent execution completed for task: {}", taskContext.getTaskId());
                queueManager.remove(taskContext.getTaskId());
            }));
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
        if (taskId == null) {
            throw new IllegalArgumentException("Cancel Task id must not be null");
        }
        log.info("Attempting to cancel task with ID: {}", taskId);

        Task cancelledTask = taskManager.getTask(taskId);

        if (cancelledTask == null) {
            log.warn("Task with ID {} not found for cancellation.", taskId);
            throw new IllegalArgumentException("Cancel Task id not found for cancellation.");
        }

        // Get or create event queue for cancellation
        EventQueue eventQueue = queueManager.get(taskId);
        TaskStatus taskStatus = TaskStatus.builder()
            .state(TaskState.CANCELED)
            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
            .build();
        if (eventQueue != null) {
            TaskStatusUpdateEvent event = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .status(taskStatus)
                .isFinal(true)
                .build();
            eventQueue.enqueueEvent(event);
            eventQueue.close();
        }

        // Execute cancellation
        agentExecutor.cancel(taskId).block();
        taskManager.applyTaskUpdate(cancelledTask, taskStatus).block();
        log.info("Task {} cancelled successfully.", taskId);
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
    public Flux<SendStreamingMessageResponse> subscribeToTaskUpdates(String taskId) {
        log.info("Subscribing to task updates for ID: {}", taskId);

        // check the task
        Task task = taskManager.getTask(taskId);
        if (task == null) {
            log.warn("Task with ID {} not found for subscription.", taskId);
            return Flux.error(new IllegalArgumentException("Task not found: " + taskId));
        }

        // if the task is finish, return
        TaskState state = task.getStatus().getState();
        if (state == TaskState.COMPLETED || state == TaskState.FAILED || state == TaskState.CANCELED
            || state == TaskState.REJECTED) {
            log.info("Task {} is in final state {}, returning final status.", taskId, state);
            TaskStatusUpdateEvent finalEvent = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .status(task.getStatus())
                .isFinal(true)
                .build();
            return Flux.just(finalEvent);
        }

        // for the task is process, try tap the current event queue
        EventQueue eventQueue = queueManager.tap(taskId);
        if (eventQueue != null) {
            log.debug("Task {} is in progress, subscribing to updates via tapped queue.", taskId);
            return eventQueue.asFlux()
                .doOnSubscribe(
                    s -> log.debug("Subscriber attached to task {} updates via subscribeToTaskUpdates.", taskId))
                .doOnComplete(() -> log.debug("Task {} updates stream completed via subscribeToTaskUpdates.", taskId))
                .doOnError(e -> log.error("Error in task {} updates stream via subscribeToTaskUpdates: {}", taskId,
                    e.getMessage(), e));
        } else {
            log.warn("No active event queue found for task {}.", taskId);
            return Flux.empty();
        }
    }

    /**
     * Retrieves the AgentCard for the server itself.
     *
     * @return The AgentCard of the server.
     */
    @Override
    public AgentCard getSelfAgentCard() {
        log.info("Getting self agent card.");
        return a2aServerSelfCard;
    }

}
