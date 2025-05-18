package io.github.a2ap.core.event;

/**
 * Interface for task update events.
 */
public interface TaskUpdateEvent {
    /**
     * Gets the task ID associated with this event.
     * @return The task ID.
     */
    String getTaskId();
}
