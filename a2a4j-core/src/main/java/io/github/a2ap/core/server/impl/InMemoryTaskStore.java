package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskAndHistory;
import io.github.a2ap.core.server.TaskStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TaskStore interface.
 * Stores Task and History data in a ConcurrentHashMap.
 */
public class InMemoryTaskStore implements TaskStore {

    private final Map<String, TaskAndHistory> store = new ConcurrentHashMap<>();

    @Override
    public void save(Task task, List<Message> history) {
        store.put(task.getId(), new TaskAndHistory(task, history));
    }

    @Override
    public TaskAndHistory load(String taskId) {
        return store.get(taskId);
    }
}
