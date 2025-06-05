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
    void save(Task task);

    /**
     * Loads a task and its history by task ID.
     * @param taskId The ID of the task to load.
     * @return an object containing the Task and its history, or null if not found.
     */
    Task load(String taskId);
}
