package io.github.a2ap.core.server;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * Interface for managing tasks in the A2A system.
 * The TaskManager is responsible for handling the lifecycle and state of tasks.
 */
public interface TaskManager {

    /**
     * Load or create a new task.
     * 
     * @param params The task param to create
     * @return The created task with a generated ID
     */
    TaskContext loadOrCreateTask(TaskSendParams params);

    /**
     * Gets a task by its ID.
     * 
     * @param taskId The ID of the task
     * @return The task with the specified ID, or null if not found
     */
    Task getTask(String taskId);

    /**
     * Cancels a task.
     * 
     * @param taskId The ID of the task to cancel
     * @return true if the task was successfully cancelled, false otherwise
     */
    Task cancelTask(String taskId);

    /**
     * apply take update for task
     * @param taskContext TaskContext
     * @param taskUpdates taskUpdate TaskStatus or Artifact update
     * @return mono of task
     */
    Mono<TaskContext> applyTaskUpdate(TaskContext taskContext, List<TaskUpdate> taskUpdates);

    /**
     * apply task update for task
     * @param taskContext TaskContext
     * @param update tash update TaskStatus or Artifact update
     * @return mono of task
     */
    Mono<TaskContext> applyTaskUpdate(TaskContext taskContext, TaskUpdate update);

    /**
     * register task notification config
     * @param config notification config
     */
    void registerTaskNotification(TaskPushNotificationConfig config);

    /**
     * get task notification config
     * @param taskId task id
     * @return notification config 
     */
    TaskPushNotificationConfig getTaskNotification(String taskId);
}
