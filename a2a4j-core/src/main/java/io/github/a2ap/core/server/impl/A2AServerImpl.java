package io.github.a2ap.core.server.impl;

package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.TaskContext;
import io.github.a2ap.core.server.TaskHandler;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import io.github.a2ap.core.server.TaskStore.TaskAndHistory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Implementation of the A2AServer interface.
 * This class provides the core functionality for an A2A server.
 */
@Slf4j
public class A2AServerImpl implements A2AServer {

    private final TaskStore taskStore;
    private final TaskManager taskManager;
    private final TaskHandler taskHandler;
    private final Map<String, AgentCard> agents = new ConcurrentHashMap<>();
    // Map to hold Sinks for each streaming task ID to push updates
    private final Map<String, Sinks.Many<Task>> taskUpdateSinks = new ConcurrentHashMap<>();

    /**
     * Constructs a new A2AServerImpl with the specified TaskStore and TaskHandler.
     *
     * @param taskStore The TaskStore to use for task management.
     * @param taskHandler The TaskHandler to use for task processing.
     */
    public A2AServerImpl(TaskStore taskStore, TaskHandler taskHandler, TaskManager taskManager) {
        this.taskStore = taskStore;
        this.taskHandler = taskHandler;
        this.taskManager = taskManager;
        log.info("A2AServerImpl initialized with TaskStore: {} and TaskHandler: {}",
                taskStore.getClass().getSimpleName(), taskHandler.getClass().getSimpleName());
    }

    /**
     * Registers an agent with the server.
     *
     * @param agentCard The AgentCard of the agent to register.
     * @return true if registration was successful, false otherwise.
     */
    @Override
    public boolean registerAgent(AgentCard agentCard) {
        log.info("Attempting to register agent: {}", agentCard);
        if (agentCard == null || agentCard.getId() == null || agentCard.getId().isEmpty()) {
            log.warn("Agent registration failed: Invalid AgentCard provided.");
            return false;
        }
        agents.put(agentCard.getId(), agentCard);
        log.info("Agent registered successfully: {}", agentCard.getId());
        return true;
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
        Flux<Task> taskFlux = taskHandler.handle(taskContext);
        taskFlux.collectList().block();

        // Asynchronously handle the task
        CompletableFuture.runAsync(() -> {
            try {
                TaskContext taskContext = new TaskContext(createdTask, this, taskStore);
                taskHandler.handleTask(taskContext);
                // TaskHandler is expected to update task status via A2AServer methods
            } catch (Exception e) {
                log.error("Error handling task {}: {}", createdTask.getId(), e.getMessage(), e);
                // Optionally update task status to failed
                updateTaskStatus(createdTask.getId(), TaskStatus.builder().state(TaskState.FAILED).message("Error during processing: " + e.getMessage()).build());
            }
        });

        return createdTask;
    }

    @Override
    public Flux<Task> handleSubscribeTask(TaskSendParams params) {
        log.info("Attempting to handle and subscribe to task: {}", params);

        // Validate params (similar to handleTask)
        if (params == null || params.getSender() == null || params.getReceiver() == null) {
            log.error("Subscription failed: Task params must have a sender and receiver.");
            throw new IllegalArgumentException("Task params must have a sender and receiver");
        }

        // Check if sender and receiver are registered
        if (!agents.containsKey(params.getSender().getId())) {
            log.error("Subscription failed: Sender agent '{}' is not registered.", params.getSender().getId());
            throw new IllegalArgumentException("Sender agent is not registered");
        }

        if (!agents.containsKey(params.getReceiver().getId())) {
            log.error("Subscription failed: Receiver agent '{}' is not registered.", params.getReceiver().getId());
            throw new IllegalArgumentException("Receiver agent is not registered");
        }

        // Create task using TaskStore (or get existing if logic allows)
        // For simplicity, let's assume this always creates a new task for now, similar to handleTask
        Task createdTask = taskStore.createTask(params);
        log.info("Task created successfully with ID: {} for subscription.", createdTask.getId());

        // Get or create the sink for the task ID
        Sinks.Many<Task> sink = taskUpdateSinks.computeIfAbsent(createdTask.getId(),
                id -> Sinks.many().multicast().onBackpressureBuffer());
        log.debug("Returning Flux for task {} updates for subscription.", createdTask.getId());

        // Asynchronously handle the task (same logic as handleTask)
        CompletableFuture.runAsync(() -> {
            try {
                TaskContext taskContext = new TaskContext(createdTask, this, taskStore);
                taskHandler.handleTask(taskContext);
                // TaskHandler is expected to update task status via A2AServer methods
            } catch (Exception e) {
                log.error("Error handling task {} for subscription: {}", createdTask.getId(), e.getMessage(), e);
                // Optionally update task status to failed
                updateTaskStatus(createdTask.getId(), TaskStatus.builder().state(TaskState.FAILED).message("Error during processing: " + e.getMessage()).build());
            }
        });

        // Emit the initial task state to the sink for the new subscriber
        sink.tryEmitNext(createdTask);

        return sink.asFlux()
                .doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates via handleSubscribeTask.", createdTask.getId()))
                .doOnComplete(() -> log.debug("Task {} updates stream completed via handleSubscribeTask.", createdTask.getId()))
                .doOnError(e -> log.error("Error in task {} updates stream via handleSubscribeTask: {}", createdTask.getId(), e.getMessage(), e));
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
     * Updates the status of a task.
     *
     * @param taskId The ID of the task to update.
     * @param status The new status for the task.
     * @return true if the status was updated successfully, false otherwise.
     */
    @Override
    public boolean updateTaskStatus(String taskId, TaskStatus status) {
        log.info("Attempting to update status for task {}: {}", taskId, status);
        // Use taskStore to update status
        boolean success = taskStore.updateTaskStatus(taskId, status);
        if (success) {
            log.info("Task {} status updated successfully to {}.", taskId, status);
            // Get the updated task and emit it to the corresponding sink
            Task updatedTask = taskStore.getTask(taskId);
            Sinks.Many<Task> sink = taskUpdateSinks.get(taskId);
            if (sink != null) {
                sink.tryEmitNext(updatedTask);
                log.debug("Emitted updated task {} to sink.", taskId);
                // If the task is completed or cancelled, complete the sink
                if (updatedTask != null && (updatedTask.getStatus().getState() == TaskState.COMPLETED
                        || updatedTask.getStatus().getState() == TaskState.CANCELED)) {
                    sink.tryEmitComplete();
                    taskUpdateSinks.remove(taskId);
                    log.debug("Completed and removed sink for task {}.", taskId);
                }
            } else {
                log.warn("No active sink found for task {}. Cannot emit update.", taskId);
            }
        } else {
            log.warn("Failed to update status for task {}.", taskId);
        }
        return success;
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
        Task cancelledTask = taskStore.cancelTask(taskId);
        if (cancelledTask != null) {
            log.info("Task {} cancelled successfully.", taskId);
            // Emit the cancelled task and complete the sink
            Sinks.Many<Task> sink = taskUpdateSinks.get(taskId);
            if (sink != null) {
                sink.tryEmitNext(cancelledTask);
                sink.tryEmitComplete();
                taskUpdateSinks.remove(taskId);
                log.debug("Emitted cancelled task {} and completed/removed sink.", taskId);
            } else {
                log.warn("No active sink found for task {}. Cannot emit cancellation update.", taskId);
            }
        } else {
            log.warn("Failed to cancel task with ID {}. Task not found or already completed/failed.", taskId);
        }
        return cancelledTask;
    }

    /**
     * Retrieves the AgentCard for a specific agent ID.
     *
     * @param agentId The ID of the agent.
     * @return The AgentCard if found, otherwise null.
     */
    @Override
    public AgentCard getAgentInfo(String agentId) {
        log.info("Getting agent info for ID: {}", agentId);
        AgentCard agentCard = agents.get(agentId);
        if (agentCard != null) {
            log.debug("Found agent info for {}: {}", agentId, agentCard);
        } else {
            log.warn("Agent with ID {} not found.", agentId);
        }
        return agentCard;
    }

    // Need a mechanism to store and retrieve push notification configurations.
    // A simple Map can be used for now.
    private final Map<String, TaskPushNotificationConfig> pushNotificationConfigs = new ConcurrentHashMap<>();

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

        pushNotificationConfigs.put(config.getTaskId(), config);
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
        TaskPushNotificationConfig config = pushNotificationConfigs.get(taskId);
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
    public Flux<Task> subscribeToTaskUpdates(String taskId) {
        log.info("Subscribing to task updates for ID: {}", taskId);
        // Get the sink for the task ID or create a new one if it doesn't exist (for
        // resubscribe)
        // Note: A more robust implementation might handle resubscribe differently,
        // e.g., replaying recent events
        Sinks.Many<Task> sink = taskUpdateSinks.computeIfAbsent(taskId,
                id -> {
                    log.debug("Creating new sink for task {}.", id);
                    return Sinks.many().multicast().onBackpressureBuffer();
                });
        log.debug("Returning Flux for task {} updates.", taskId);
        return sink.asFlux()
                .doOnSubscribe(s -> log.debug("Subscriber attached to task {} updates.", taskId))
                .doOnComplete(() -> log.debug("Task {} updates stream completed.", taskId))
                .doOnError(e -> log.error("Error in task {} updates stream: {}", taskId, e.getMessage(), e));
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
