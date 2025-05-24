package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskAndHistory;
import io.github.a2ap.core.server.TaskStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the TaskStore interface.
 * Stores Task and History data in a ConcurrentHashMap.
 */
public class InMemoryTaskStore implements TaskStore {

    private final Map<String, TaskAndHistory> store = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Void> save(Task task, List<Message> history) {
        // Store copies to prevent internal mutation if caller reuses objects
        Task taskCopy = new Task(task); // Assuming Task has a copy constructor or similar
        List<Message> historyCopy = history.stream()
                .map(Message::new) // Assuming Message has a copy constructor or similar
                .collect(Collectors.toList());

        store.put(task.getId(), new TaskAndHistory(taskCopy, historyCopy));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<TaskAndHistory> load(String taskId) {
        TaskAndHistory entry = store.get(taskId);
        // Return copies to prevent external mutation
        if (entry != null) {
            Task taskCopy = new Task(entry.getTask()); // Assuming Task has a copy constructor or similar
            List<Message> historyCopy = entry.getHistory().stream()
                    .map(Message::new) // Assuming Message has a copy constructor or similar
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(new TaskAndHistory(taskCopy, historyCopy));
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}
