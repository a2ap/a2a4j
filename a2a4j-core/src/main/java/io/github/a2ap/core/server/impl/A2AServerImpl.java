package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import io.github.a2ap.core.server.QueueManager;
import io.github.a2ap.core.server.TaskHandler;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskQueueExistsException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private final AgentExecutor agentExecutor;
    private final QueueManager queueManager;
    private final ConcurrentMap<String, Mono<Void>> runningAgents = new ConcurrentHashMap<>();
    
    /**
     * Constructs a new A2AServerImpl with the specified components.
     *
     * @param taskHandler The TaskHandler to use for task processing.
     * @param taskManager The TaskManager to use for task management.
     * @param agentExecutor The AgentExecutor to use for agent execution.
     * @param queueManager The QueueManager to use for event queue management.
     */
    public A2AServerImpl(TaskHandler taskHandler, TaskManager taskManager, 
                        AgentExecutor agentExecutor, QueueManager queueManager) {
        this.taskHandler = taskHandler;
        this.taskManager = taskManager;
        this.agentExecutor = agentExecutor;
        this.queueManager = queueManager;
        log.info("A2AServerImpl initialized with TaskManager: {}, TaskHandler: {}, AgentExecutor: {}, QueueManager: {}",
                taskManager.getClass().getSimpleName(), taskHandler.getClass().getSimpleName(),
                agentExecutor.getClass().getSimpleName(), queueManager.getClass().getSimpleName());
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
        TaskContext taskContext = taskManager.loadOrCreateTask(params);
        log.info("Task context loaded: {}", taskContext.getTask());
        
        // Create event queue for this task
        EventQueue tempQueue1;
        try {
            tempQueue1 = queueManager.create(taskContext.getTask().getId());
        } catch (TaskQueueExistsException e) {
            tempQueue1 = queueManager.get(taskContext.getTask().getId());
        }
        final EventQueue eventQueue = tempQueue1;
        
        // Execute agent and collect final result
        Mono<Task> resultMono = agentExecutor.execute(taskContext, eventQueue)
                .then(eventQueue.asFlux()
                        .filter(event -> event instanceof TaskStatusUpdateEvent && ((TaskStatusUpdateEvent) event).getIsFinal())
                        .cast(TaskStatusUpdateEvent.class)
                        .next()
                        .map(event -> {
                            TaskContext updatedContext = taskManager.applyTaskUpdate(taskContext, event.getStatus()).block();
                            return updatedContext.getTask();
                        }));
        
        Task task = resultMono.block();
        log.info("Task handle success: {}", task);
        return task;
    }

    @Override
    public Flux<SendStreamingMessageResponse> handleMessageStream(MessageSendParams params) {
        log.info("Attempting to handle and subscribe to task: {}", params);
        final TaskContext taskContext = taskManager.loadOrCreateTask(params);
        log.info("Task context loaded: {}", taskContext.getTask());
        
        String taskId = taskContext.getTask().getId();
        
        // Create or get event queue for this task
        EventQueue tempQueue;
        try {
            tempQueue = queueManager.create(taskId);
        } catch (TaskQueueExistsException e) {
            tempQueue = queueManager.get(taskId);
        }
        final EventQueue eventQueue = tempQueue;
        
        // Start agent execution if not already running
        runningAgents.computeIfAbsent(taskId, id -> {
            log.debug("Starting agent execution for task: {}", id);
            return agentExecutor.execute(taskContext, eventQueue)
                    .doOnTerminate(() -> {
                        log.debug("Agent execution completed for task: {}", id);
                        runningAgents.remove(id);
                        queueManager.remove(id);
                    })
                    .cache(); // Cache to prevent multiple executions
        });
        
        // Return the event stream
        return eventQueue.asFlux()
                .doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates via handleMessageStream.", taskId))
                .doOnComplete(() -> log.debug("Task {} updates stream completed via handleMessageStream.", taskId))
                .doOnError(e -> log.error("Error in task {} updates stream via handleMessageStream: {}", taskId, e.getMessage(), e));
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
        
        TaskContext taskContext = TaskContext.builder()
                .task(taskManager.getTask(taskId))
                .build();
        
        if (taskContext.getTask() == null) {
            log.warn("Task with ID {} not found for cancellation.", taskId);
            return null;
        }
        
        // Get or create event queue for cancellation
        EventQueue eventQueue = queueManager.get(taskId);
        if (eventQueue == null) {
            try {
                eventQueue = queueManager.create(taskId);
            } catch (TaskQueueExistsException e) {
                eventQueue = queueManager.get(taskId);
            }
        }
        
        // Cancel the running agent if exists
        Mono<Void> runningAgent = runningAgents.remove(taskId);
        if (runningAgent != null) {
            // Cancel the running execution
            log.debug("Cancelling running agent for task: {}", taskId);
        }
        
        // Execute cancellation
        Task cancelledTask = agentExecutor.cancel(taskContext, eventQueue)
                .then(eventQueue.asFlux()
                        .filter(event -> event instanceof TaskStatusUpdateEvent && ((TaskStatusUpdateEvent) event).getIsFinal())
                        .cast(TaskStatusUpdateEvent.class)
                        .next()
                        .map(event -> {
                            TaskContext updatedContext = taskManager.applyTaskUpdate(taskContext, event.getStatus()).block();
                            return updatedContext.getTask();
                        }))
                .block();
        
        if (cancelledTask != null) {
            log.info("Task {} cancelled successfully.", taskId);
        } else {
            log.warn("Failed to cancel task with ID {}.", taskId);
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
    public Flux<SendStreamingMessageResponse> subscribeToTaskUpdates(String taskId) {
        log.info("Subscribing to task updates for ID: {}", taskId);
        
        // 检查任务是否存在
        Task task = taskManager.getTask(taskId);
        if (task == null) {
            log.warn("Task with ID {} not found for subscription.", taskId);
            return Flux.error(new IllegalArgumentException("Task not found: " + taskId));
        }
        
        // 如果任务已经完成，返回最终状态
        TaskState state = task.getStatus().getState();
        if (state == TaskState.COMPLETED || state == TaskState.FAILED || 
            state == TaskState.CANCELED || state == TaskState.REJECTED) {
            log.info("Task {} is in final state {}, returning final status.", taskId, state);
            TaskStatusUpdateEvent finalEvent = TaskStatusUpdateEvent.builder()
                    .taskId(taskId)
                    .status(task.getStatus())
                    .isFinal(true)
                    .build();
            return Flux.just(finalEvent);
        }
        
        // 对于正在进行的任务，尝试tap到现有的事件队列
        EventQueue eventQueue = queueManager.tap(taskId);
        if (eventQueue != null) {
            log.debug("Task {} is in progress, subscribing to updates via tapped queue.", taskId);
            return eventQueue.asFlux()
                    .doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates via subscribeToTaskUpdates.", taskId))
                    .doOnComplete(() -> log.debug("Task {} updates stream completed via subscribeToTaskUpdates.", taskId))
                    .doOnError(e -> log.error("Error in task {} updates stream via subscribeToTaskUpdates: {}", taskId, e.getMessage(), e));
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
        // TODO: Provide actual agent card information
        AgentCard selfCard = AgentCard.builder()
                .name("Example Java Agent")
                .description("A sample A2A agent implemented in Java.")
                .url("http://localhost:8080/a2a/server") 
                .version("1.0.0")
                // Placeholder capabilities - replace with actual capabilities
                .capabilities(new AgentCapabilities())
                .skills(List.of())
                .build();
        log.debug("Returning self agent card: {}", selfCard);
        return selfCard;
    }
}
