package io.github.a2ap.core.client;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.MessageSendParams;
import reactor.core.publisher.Flux;

/**
 * Interface defining the core functionality of an A2A client.
 * The A2A client is responsible for interacting with an A2A server.
 */
public interface A2AClient {

    /**
     * Get the AgentCard info current in client
     * @return AgentCard
     */
    AgentCard agentCard();
    
    /**
     * Retrieves the AgentCard for the server this client connects to.
     * This is typically fetched from a well-known endpoint.
     * @return AgentCard.
     */
    AgentCard retrieveAgentCard();

    /**
     * Sends a task request to the server (non-streaming).
     * @param params The parameters for the tasks/send method.
     * @return created Task object or null.
     */
    Task sendTask(MessageSendParams params);

    /**
     * Sends a task request and subscribes to streaming updates.
     * Returns a Flux that emits TaskUpdateEvent payloads (either TaskStatusUpdateEvent or TaskArtifactUpdateEvent).
     * @param params The parameters for the tasks/sendSubscribe method.
     * @return A Flux of task update events.
     */
    Flux<SendStreamingMessageResponse> sendTaskSubscribe(MessageSendParams params);

    /**
     * Retrieves the current state of a task.
     * @param params The parameters for the tasks/get method.
     * @return Task object or null.
     */
    Task getTask(TaskQueryParams params);
    
    /**
     * Cancels a currently running task.
     * @param params The parameters for the tasks/cancel method.
     * @return the updated Task object (usually canceled state) or null.
     */
    Task cancelTask(TaskIdParams params);

    /**
     * Sets or updates the push notification config for a task.
     * @param params The parameters for the tasks/pushNotification/set method.
     * @return the confirmed TaskPushNotificationConfig or null.
     */
    TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params);

    /**
     * Retrieves the currently configured push notification config for a task.
     * @param params The parameters for the tasks/pushNotification/get method.
     * @return the TaskPushNotificationConfig or null.
     */
    TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params);

    /**
     * Resubscribes to updates for a task after a potential connection interruption.
     * Returns a Publisher that emits TaskUpdateEvent payloads (either TaskStatusUpdateEvent or TaskArtifactUpdateEvent).
     * @param params The parameters for the tasks/resubscribe method.
     * @return A Flux of task update events.
     */
    Flux<SendStreamingMessageResponse> resubscribeTask(TaskQueryParams params);

    /**
     * Optional: Checks if the server likely supports optional methods based on agent card.
     * This is a client-side heuristic and might not be perfectly accurate.
     * @param capability The capability to check (e.g., 'streaming', 'pushNotifications').
     * @return true if the capability is likely supported.
     */
    Boolean supports(String capability);
}
