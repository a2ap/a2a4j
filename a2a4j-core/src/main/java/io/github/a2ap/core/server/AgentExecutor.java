package io.github.a2ap.core.server;

import io.github.a2ap.core.model.TaskContext;
import reactor.core.publisher.Mono;

/**
 * Agent Executor interface.
 *
 * Implementations of this interface contain the core logic of the agent,
 * executing tasks based on requests and publishing updates to an event queue.
 * 
 * This is the Java equivalent of Python's AgentExecutor using Reactor.
 */
public interface AgentExecutor {
    
    /**
     * Execute the agent's logic for a given request context.
     *
     * The agent should read necessary information from the `context` and
     * publish `Task` or `Message` events, or `TaskStatusUpdateEvent` /
     * `TaskArtifactUpdateEvent` to the `eventQueue`. This method should
     * return once the agent's execution for this request is complete or
     * yields control (e.g., enters an input-required state).
     *
     * @param context The request context containing the message, task ID, etc.
     * @param eventQueue The queue to publish events to.
     * @return A Mono that completes when the agent execution is finished.
     */
    Mono<Void> execute(TaskContext context, EventQueue eventQueue);
    
    /**
     * Request the agent to cancel an ongoing task.
     *
     * The agent should attempt to stop the task identified by the task_id
     * in the context and publish a `TaskStatusUpdateEvent` with state
     * `TaskState.canceled` to the `eventQueue`.
     *
     * @param context The request context containing the task ID to cancel.
     * @param eventQueue The queue to publish the cancellation status update to.
     * @return A Mono that completes when the cancellation is processed.
     */
    Mono<Void> cancel(TaskContext context, EventQueue eventQueue);
} 