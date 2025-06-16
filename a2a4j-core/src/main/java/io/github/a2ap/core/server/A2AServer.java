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

package io.github.a2ap.core.server;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import reactor.core.publisher.Flux;

/**
 * Interface defining the core functionality of an A2A server.
 * The A2A server is responsible for managing tasks and agent interactions.
 */
public interface A2AServer {

    /**
     * Handle send message task.
     *
     * @param params The task params to send
     * @return SendMessageResponse The task or Message
     */
    SendMessageResponse handleMessage(MessageSendParams params);

    /**
     * Handle send task streaming.
     *
     * @param params The task params to send
     * @return Streaming events
     */
    Flux<SendStreamingMessageResponse> handleMessageStream(MessageSendParams params);

    /**
     * Gets a task by its ID.
     *
     * @param taskId The ID of the task
     * @return The Task object or null if not found
     */
    Task getTask(String taskId);

    /**
     * Cancels a task.
     *
     * @param taskId The ID of the task to cancel
     * @return The updated Task object (usually in a cancelled state) or null if not
     * found or cancellation failed
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
     * Retrieves the authenticated extended AgentCard for this server.
     * This method should return a potentially more detailed version of the Agent Card
     * after the client has authenticated. Only available if the agent supports
     * authenticated extended cards.
     *
     * @return The server's authenticated extended AgentCard, or null if not supported
     */
    AgentCard getAuthenticatedExtendedCard();

    /**
     * Subscribes to updates for a specific task.
     *
     * @param taskId The ID of the task to subscribe to
     * @return A Flux of Task objects representing updates
     */
    Flux<SendStreamingMessageResponse> subscribeToTaskUpdates(String taskId);
}
