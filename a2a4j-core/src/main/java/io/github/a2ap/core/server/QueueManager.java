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

/**
 * Interface for managing event queues for tasks.
 * <p>
 * This is the Java equivalent of Python's QueueManager.
 */
public interface QueueManager {

    /**
     * Creates or retrieves the main event queue for a task.
     *
     * @param taskId The ID of the task.
     * @return The EventQueue for the task.
     */
    EventQueue create(String taskId);

    /**
     * Taps into an existing task's event queue to create a child queue.
     *
     * @param taskId The ID of the task.
     * @return A new EventQueue that receives events from the main task queue, or null if
     * no queue exists for this task.
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
