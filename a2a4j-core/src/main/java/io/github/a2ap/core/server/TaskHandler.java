package io.github.a2ap.core.server;

import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskUpdate;
import reactor.core.publisher.Flux;

/**
 * Defines the signature for a task handler function.
 * Handlers are implemented as functions that receive context about the
 * task and the triggering message. They can perform work and emit status
 * or artifact updates as a Flux of Task objects.
 */
public interface TaskHandler {
    /**
     * Handles a task based on the provided context.
     *
     * @param context The context for the task handling.
     * @return A Flux of updated Task TaskUpdate objects.
     */
    Flux<TaskUpdate> handle(TaskContext context);
}
