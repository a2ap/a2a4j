package io.github.a2ap.core.server;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskAndHistory;
import java.util.List;

/**
 * Simplified interface for task storage providers.
 * Stores and retrieves both the task and its full message history together.
 */
public interface TaskStore {

    /**
     * Saves a task and its associated message history.
     * Overwrites existing data if the task ID exists.
     * @param task The task object to save.
     * @param history The list of message history associated with the task.
     */
    void save(Task task, List<Message> history);

    /**
     * Loads a task and its history by task ID.
     * @param taskId The ID of the task to load.
     * @return an object containing the Task and its history, or null if not found.
     */
    TaskAndHistory load(String taskId);
}
