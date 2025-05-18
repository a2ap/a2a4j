package io.github.a2ap.core.server;

import io.github.a2ap.core.event.TaskUpdateEvent;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskStatus;
import reactor.core.publisher.Flux;

/**
 * Interface defining the core functionality of an A2A server.
 * The A2A server is responsible for managing tasks and agent interactions.
 */
public interface A2AServer {

    /**
     * Registers an agent with the server.
     * 
     * @param agentCard The agent card containing information about the agent
     * @return true if registration was successful, false otherwise
     */
    boolean registerAgent(AgentCard agentCard);

    /**
     * Handle send task.
     * 
     * @param params The task params to send
     * @return The task with a generated ID
     */
    Task handleTask(TaskSendParams params);

    /**
     * Handle send task streaming.
     * @param params The task params to send
     * @return Streaming events
     */
    Flux<TaskUpdateEvent> handleSubscribeTask(TaskSendParams params);

    /**
     * Gets a task by its ID.
     *
     * @param taskId The ID of the task
     * @return The Task object or null if not found
     */
    Task getTask(String taskId);

    /**
     * Updates the status of a task.
     * 
     * @param taskId The ID of the task
     * @param status The new status of the task
     * @return true if the update was successful, false otherwise
     */
    boolean updateTaskStatus(String taskId, TaskStatus status);

    /**
     * Cancels a task.
     *
     * @param taskId The ID of the task to cancel
     * @return The updated Task object (usually in a cancelled state) or null if not
     *         found or cancellation failed
     */
    Task cancelTask(String taskId);

    /**
     * Sets or updates the push notification configuration for a task.
     *
     * @param config The push notification configuration
     * @return The confirmed TaskPushNotificationConfig or null if setting failed
     */
    TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig config);

    /**
     * Retrieves the push notification configuration for a task.
     *
     * @param taskId The ID of the task
     * @return The TaskPushNotificationConfig or null if not found
     */
    TaskPushNotificationConfig getTaskPushNotification(String taskId);

    /**
     * Retrieves the AgentCard for this server.
     *
     * @return The server's AgentCard
     */
    AgentCard getSelfAgentCard();

    /**
     * Subscribes to updates for a specific task.
     *
     * @param taskId The ID of the task to subscribe to
     * @return A Flux of Task objects representing updates
     */
    Flux<Task> subscribeToTaskUpdates(String taskId);
}
