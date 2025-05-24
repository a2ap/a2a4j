package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * In-memory implementation of the TaskManager interface.
 * This implementation stores all tasks in memory and is suitable for testing
 * and demonstration purposes.
 */
@Slf4j
@Component
public class InMemoryTaskManager implements TaskManager {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<Task>> taskUpdateSinks = new ConcurrentHashMap<>();
    private final TaskStore taskStore = new InMemoryTaskStore();
    private final Map<String, TaskPushNotificationConfig> notificationConfigMap = new ConcurrentHashMap<>();

    @Override
    public TaskContext loadOrCreateTask(TaskSendParams params) {
        // Generate a unique ID if not provided
        
        return null;
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
                        return task.getSender().getId().equals(agentId) || task.getReceiver().getId().equals(agentId);
                    } else if (role.equalsIgnoreCase("sender")) {
                        return task.getSender().getId().equals(agentId);
                    } else if (role.equalsIgnoreCase("receiver")) {
                        return task.getReceiver().getId().equals(agentId);
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
        TaskPushNotificationConfig config = notificationConfigMap.get(taskId);
        if (config != null) {
            // todo
            log.info("Notifying callback for task {} with config {}", taskId, config);
        }

        return true;
    }

    @Override
    public Task cancelTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Cancel Task with ID " + taskId + " not found.");
        }
        // Emit the cancelled task and complete the sink
        Sinks.Many<Task> sink = taskUpdateSinks.get(taskId);
        if (sink != null) {
            sink.tryEmitNext(task);
            sink.tryEmitComplete();
            taskUpdateSinks.remove(taskId);
            log.debug("Emitted cancelled task {} and completed/removed sink.", taskId);
        } else {
            log.warn("No active sink found for task {}. Cannot emit cancellation update.", taskId);
        }
        TaskStatus cancelledStatus = new TaskStatus();
        cancelledStatus.setState(TaskState.CANCELED);
        task.setStatus(cancelledStatus);
        tasks.put(taskId, task);
        return task;
    }

    @Override
    public Task deleteTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Delete Task with ID " + taskId + " not found.");
        }

        tasks.remove(taskId);
        notificationConfigMap.remove(taskId);
        // todo 
        return task;
    }

    @Override
    public Mono<Task> applyTaskUpdate(List<TaskUpdate> taskUpdates) {
        return null;
    }

    @Override
    public Mono<Task> applyTaskUpdate(TaskUpdate update) {
        return applyTaskUpdate(Stream.of(update).collect(Collectors.toList()));
    }

    @Override
    public void registerTaskNotification(TaskPushNotificationConfig config) {
        notificationConfigMap.put(config.getTaskId(), config);
    }

    @Override
    public TaskPushNotificationConfig getTaskNotification(String taskId) {
        return notificationConfigMap.get(taskId);
    }
}
