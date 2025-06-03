package io.github.a2ap.core.server;

/**
 * Interface for managing event queues for tasks.
 * 
 * This is the Java equivalent of Python's QueueManager.
 */
public interface QueueManager {
    
    /**
     * Creates or retrieves the main event queue for a task.
     *
     * @param taskId The ID of the task.
     * @return The EventQueue for the task.
     * @throws TaskQueueExistsException if the queue already exists and cannot be created again.
     */
    EventQueue create(String taskId) throws TaskQueueExistsException;
    
    /**
     * Taps into an existing task's event queue to create a child queue.
     *
     * @param taskId The ID of the task.
     * @return A new EventQueue that receives events from the main task queue,
     *         or null if no queue exists for this task.
     */
    EventQueue tap(String taskId);
    
    /**
     * Retrieves the main event queue for a task without creating it.
     *
     * @param taskId The ID of the task.
     * @return The EventQueue for the task, or null if it doesn't exist.
     */
    EventQueue get(String taskId);
    
    /**
     * Removes and closes the event queue for a task.
     *
     * @param taskId The ID of the task.
     */
    void remove(String taskId);
} 