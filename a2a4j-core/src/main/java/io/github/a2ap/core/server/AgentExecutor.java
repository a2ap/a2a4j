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

import io.github.a2ap.core.model.RequestContext;
import reactor.core.publisher.Mono;

/**
 * Agent Executor interface.
 * <p>
 * Implementations of this interface contain the core logic of the agent, executing tasks
 * based on requests and publishing updates to an event queue.
 * <p>
 * This is the Java equivalent of Python's AgentExecutor using Reactor.
 */
public interface AgentExecutor {

    /**
     * Execute the agent's logic for a given request context.
     * <p>
     * The agent should read necessary information from the `context` and publish `Task`
     * or `Message` events, or `TaskStatusUpdateEvent` / `TaskArtifactUpdateEvent` to the
     * `eventQueue`. This method should return once the agent's execution for this request
     * is complete or yields control (e.g., enters an input-required state).
     *
     * @param context    The request context containing the message, task ID, etc.
     * @param eventQueue The queue to publish events to.
     * @return A Mono that completes when the agent execution is finished.
     */
    Mono<Void> execute(RequestContext context, EventQueue eventQueue);

    /**
     * Request the agent to cancel an ongoing task.
     * <p>
     * The agent should attempt to stop the task identified by the task_id in the context
     * and publish a `TaskStatusUpdateEvent` with state `TaskState.canceled` to the
     * `eventQueue`.
     *
     * @param taskId The task ID to cancel.
     * @return A Mono that completes when the cancellation is processed.
     */
    Mono<Void> cancel(String taskId);

}
