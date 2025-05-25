package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.server.TaskStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TaskStore interface.
 * Stores Task and History data in a ConcurrentHashMap.
 */
public class InMemoryTaskStore implements TaskStore {

    private final Map<String, TaskContext> store = new ConcurrentHashMap<>();

    @Override
    public void save(TaskContext taskContext) {
        store.put(taskContext.getTask().getId(), taskContext);
    }

    @Override
    public TaskContext load(String taskId) {
        return store.get(taskId);
    }
}
