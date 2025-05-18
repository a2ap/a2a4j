package io.github.a2ap.core.server;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskStatus;
import java.util.List;

/**
 * Interface for managing tasks in the A2A system.
 * The TaskManager is responsible for handling the lifecycle and state of tasks.
 */
public interface TaskManager {

    /**
     * Creates a new task.
     * 
     * @param task The task to create
     * @return The created task with a generated ID
     */
    Task createTask(Task task);

    /**
     * Gets a task by its ID.
     * 
     * @param taskId The ID of the task
     * @return The task with the specified ID, or null if not found
     */
    Task getTask(String taskId);

    /**
     * Gets all tasks for a specific agent (either as sender or receiver).
     * 
     * @param agentId The ID of the agent
     * @param role    The role of the agent ("sender" or "receiver"), or null for
     *                both
     * @return A list of tasks for the specified agent
     */
    List<Task> getTasksForAgent(String agentId, String role);

    /**
     * Updates the status of a task.
     * 
     * @param taskId The ID of the task
     * @param status The new status of the task
     * @return true if the update was successful, false otherwise
     */
    boolean updateTaskStatus(String taskId, TaskStatus status);

    /**
     * Cancels a task.
     * 
     * @param taskId The ID of the task to cancel
     * @return true if the task was successfully cancelled, false otherwise
     */
    boolean cancelTask(String taskId);

    /**
     * Deletes a task.
     * 
     * @param taskId The ID of the task to delete
     * @return true if the task was successfully deleted, false otherwise
     */
    boolean deleteTask(String taskId);

    /**
     * Registers a callback for task status updates.
     * 
     * @param taskId      The ID of the task
     * @param callbackUrl The URL to call when the task status changes
     * @return true if the callback was successfully registered, false otherwise
     */
    boolean registerTaskStatusCallback(String taskId, String callbackUrl);
}
