package io.github.a2ap.core.server;

/**
 * Exception thrown when attempting to create a queue for a task that already has one.
 */
public class TaskQueueExistsException extends Exception {
    public TaskQueueExistsException(String message) {
        super(message);
    }
} 