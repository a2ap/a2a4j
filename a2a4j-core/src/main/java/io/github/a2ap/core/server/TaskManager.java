/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a2ap.core.server;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
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
    RequestContext loadOrCreateContext(MessageSendParams params);

    /**
     * Gets a task by its ID.
     * 
     * @param taskId The ID of the task
     * @return The task with the specified ID, or null if not found
     */
    Task getTask(String taskId);

    /**
     * apply take update for task
     * @param taskContext TaskContext
     * @param taskUpdates taskUpdate TaskStatus or Artifact update
     * @return mono of task
     */
    Mono<Task> applyTaskUpdate(Task task, List<TaskUpdate> taskUpdates);

    /**
     * apply task update for task
     * @param taskContext TaskContext
     * @param update tash update TaskStatus or Artifact update
     * @return mono of task
     */
    Mono<Task> applyTaskUpdate(Task task, TaskUpdate update);

    /**
     * Apply status update with append support from TaskStatusUpdateEvent
     * @param task The task to update
     * @param event The TaskStatusUpdateEvent containing status information
     * @return Updated task
     */
    Mono<Task> applyStatusUpdate(Task task, TaskStatusUpdateEvent event);
    
    /**
     * Apply artifact update with append support from TaskArtifactUpdateEvent
     * @param task The task to update
     * @param event The TaskArtifactUpdateEvent containing artifact and append information
     * @return Updated task
     */
    Mono<Task> applyArtifactUpdate(Task task, TaskArtifactUpdateEvent event);

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
