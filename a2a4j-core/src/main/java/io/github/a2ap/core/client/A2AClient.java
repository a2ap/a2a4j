package io.github.a2ap.core.client;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.TaskSendParams;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

/**
 * Interface defining the core functionality of an A2A client.
 * The A2A client is responsible for interacting with an A2A server.
 */
public interface A2AClient {

    /**
     * Retrieves the AgentCard for the server this client connects to.
     * This is typically fetched from a well-known endpoint.
     * @return A CompletableFuture resolving to the AgentCard.
     */
    CompletableFuture<AgentCard> agentCard();

    /**
     * Sends a task request to the server (non-streaming).
     * @param params The parameters for the tasks/send method.
     * @return A CompletableFuture resolving to the created Task object or null.
     */
    CompletableFuture<Task> sendTask(TaskSendParams params);

    /**
     * Sends a task request and subscribes to streaming updates.
     * Returns a Publisher that emits TaskStatusUpdateEvent or TaskArtifactUpdateEvent payloads.
     * @param params The parameters for the tasks/sendSubscribe method.
     * @return A Flow.Publisher of task update events.
     */
    Flow.Publisher<TaskStatusUpdateEvent, TaskArtifactUpdateEvent> sendTaskSubscribe(TaskSendParams params);

    /**
     * Retrieves the current state of a task.
     * @param params The parameters for the tasks/get method.
     * @return A CompletableFuture resolving to the Task object or null.
     */
    CompletableFuture<Task> getTask(TaskQueryParams params);

    /**
     * Cancels a currently running task.
     * @param params The parameters for the tasks/cancel method.
     * @return A CompletableFuture resolving to the updated Task object (usually canceled state) or null.
     */
    CompletableFuture<Task> cancelTask(TaskIdParams params);

    /**
     * Sets or updates the push notification config for a task.
     * @param params The parameters for the tasks/pushNotification/set method.
     * @return A CompletableFuture resolving to the confirmed TaskPushNotificationConfig or null.
     */
    CompletableFuture<TaskPushNotificationConfig> setTaskPushNotification(TaskPushNotificationConfig params);

    /**
     * Retrieves the currently configured push notification config for a task.
     * @param params The parameters for the tasks/pushNotification/get method.
     * @return A CompletableFuture resolving to the TaskPushNotificationConfig or null.
     */
    CompletableFuture<TaskPushNotificationConfig> getTaskPushNotification(TaskIdParams params);

    /**
     * Resubscribes to updates for a task after a potential connection interruption.
     * Returns a Publisher that emits TaskStatusUpdateEvent or TaskArtifactUpdateEvent payloads.
     * @param params The parameters for the tasks/resubscribe method.
     * @return A Flow.Publisher of task update events.
     */
    Flow.Publisher<TaskStatusUpdateEvent | TaskArtifactUpdateEvent> resubscribeTask(TaskQueryParams params);

    /**
     * Optional: Checks if the server likely supports optional methods based on agent card.
     * This is a client-side heuristic and might not be perfectly accurate.
     * @param capability The capability to check (e.g., 'streaming', 'pushNotifications').
     * @return A CompletableFuture resolving to true if the capability is likely supported.
     */
    CompletableFuture<Boolean> supports(String capability);
}
