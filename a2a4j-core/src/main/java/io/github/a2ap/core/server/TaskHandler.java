package io.github.a2ap.core.server;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskContext;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Defines the signature for a task handler function.
 *
 * Handlers are implemented as functions that receive context about the
 * task and the triggering message. They can perform work and emit status
 * or artifact updates as a Flux of Task objects.
 *
 * @param context - The TaskContext object containing task details, cancellation status, and store access.
 * @return {Flux<Task>} - A Flux of updated Task objects representing the updates.
 */
public interface TaskHandler {
    /**
     * Handles a task based on the provided context.
     *
     * @param context The context for the task handling.
     * @return A Flux of updated Task objects.
     */
    Flux<Task> handle(TaskContext context);
}
