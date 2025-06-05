package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.server.TaskStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TaskStore interface.
 * Stores Task and History data in a ConcurrentHashMap.
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
