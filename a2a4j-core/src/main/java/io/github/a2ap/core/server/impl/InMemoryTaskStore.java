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

package io.github.a2ap.core.server.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.server.TaskStore;

/**
 * In-memory implementation of the TaskStore interface. Stores Task and History data in a
 * ConcurrentHashMap.
 */
public class InMemoryTaskStore implements TaskStore {

	private final Map<String, Task> store = new ConcurrentHashMap<>();

	@Override
	public void save(Task task) {
		store.put(task.getId(), task);
	}

	@Override
	public Task load(String taskId) {
		return store.get(taskId);
	}

}
