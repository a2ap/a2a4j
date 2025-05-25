package io.github.a2ap.core.server;

import io.github.a2ap.core.model.TaskContext;

/**
 * Simplified interface for task storage providers.
 * Stores and retrieves both the task and its full message history together.
 */
public interface TaskStore {

    /**
     * Saves a task and its associated message history.
     * Overwrites existing data if the task ID exists.
     * @param taskContext The task context object to save.
     */
    void save(TaskContext taskContext);

    /**
     * Loads a task and its history by task ID.
     * @param taskId The ID of the task to load.
     * @return an object containing the Task and its history, or null if not found.
     */
    TaskContext load(String taskId);
}
