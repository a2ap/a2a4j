package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.server.TaskManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the TaskManager interface.
 * This implementation stores all tasks in memory and is suitable for testing
 * and demonstration purposes.
 */
public class InMemoryTaskManager implements TaskManager {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final Map<String, String> taskCallbacks = new ConcurrentHashMap<>();

    @Override
    public Task createTask(Task task) {
        // Generate a unique ID if not provided
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
        }

        // Initialize task status if not provided
        if (task.getStatus() == null) {
            TaskStatus initialStatus = new TaskStatus();
            initialStatus.setState(TaskState.PENDING);
            task.setStatus(initialStatus);
        }

        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public List<Task> getTasksForAgent(String agentId, String role) {
        return tasks.values().stream()
                .filter(task -> {
                    if (role == null) {
                        return task.getSender().equals(agentId) || task.getReceiver().equals(agentId);
                    } else if (role.equalsIgnoreCase("sender")) {
                        return task.getSender().equals(agentId);
                    } else if (role.equalsIgnoreCase("receiver")) {
                        return task.getReceiver().equals(agentId);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateTaskStatus(String taskId, TaskStatus status) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }

        task.setStatus(status);
        tasks.put(taskId, task);

        // Notify callback if registered
        String callbackUrl = taskCallbacks.get(taskId);
        if (callbackUrl != null) {
            // In a real implementation, this would make an HTTP call to the callback URL
            // For simplicity, we're just logging it here
            System.out.println("Notifying callback URL: " + callbackUrl + " for task: " + taskId);
        }

        return true;
    }

    @Override
    public boolean cancelTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }

        TaskStatus cancelledStatus = new TaskStatus();
        cancelledStatus.setState(TaskState.CANCELED);
        task.setStatus(cancelledStatus);
        tasks.put(taskId, task);

        return true;
    }

    @Override
    public boolean deleteTask(String taskId) {
        if (!tasks.containsKey(taskId)) {
            return false;
        }

        tasks.remove(taskId);
        taskCallbacks.remove(taskId);
        return true;
    }

    @Override
    public boolean registerTaskStatusCallback(String taskId, String callbackUrl) {
        if (!tasks.containsKey(taskId)) {
            return false;
        }

        taskCallbacks.put(taskId, callbackUrl);
        return true;
    }
}
