package io.github.a2ap.core.client.impl;

import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskStatus;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Implementation of the A2AClient interface.
 */
@Component
public class A2AClientImpl implements A2AClient {

    private static final Logger log = LoggerFactory.getLogger(A2AClientImpl.class);

    private final CardResolver cardResolver;

    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     *
     * @param cardResolver The CardResolver to use for resolving agent cards.
     */
    public A2AClientImpl(CardResolver cardResolver) {
        this.cardResolver = cardResolver;
    }

    /**
     * Sends a task request to the target agent URL.
     *
     * @param taskSendParams The parameters for sending the task.
     * @param targetAgentUrl The URL of the target agent.
     * @return The created Task object received from the agent.
     */
    @Override
    public Task sendTask(TaskSendParams taskSendParams, String targetAgentUrl) {
        log.info("Sending task to {} with params: {}", targetAgentUrl, taskSendParams);
        WebClient client = WebClient.create(targetAgentUrl);
        try {
            Task responseTask = client.post()
                    .uri("/tasks")
                    .bodyValue(taskSendParams)
                    .retrieve()
                    .bodyToMono(Task.class)
                    .block(); // Using block() for simplicity in this example
            log.info("Task sent successfully. Received task: {}", responseTask);
            return responseTask;
        } catch (Exception e) {
            log.error("Error sending task to {}: {}", targetAgentUrl, e.getMessage(), e);
            // Depending on requirements, you might want to rethrow or return a specific
            // error indicator
            return null; // Or throw a custom exception
        }
    }

    /**
     * Retrieves a specific task by its ID from the target agent URL.
     *
     * @param taskId         The ID of the task to retrieve.
     * @param targetAgentUrl The URL of the target agent.
     * @return An Optional containing the Task object if found, otherwise empty.
     */
    @Override
    public Optional<Task> getTask(String taskId, String targetAgentUrl) {
        log.info("Getting task {} from {}", taskId, targetAgentUrl);
        WebClient client = WebClient.create(targetAgentUrl);
        try {
            Task task = client.get()
                    .uri("/tasks/" + taskId)
                    .retrieve()
                    .bodyToMono(Task.class)
                    .block(); // Using block() for simplicity
            log.info("Successfully retrieved task {}: {}", taskId, task);
            return Optional.ofNullable(task);
        } catch (Exception e) {
            log.error("Error getting task {} from {}: {}", taskId, targetAgentUrl, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves the status of a specific task by its ID from the target agent URL.
     *
     * @param taskId         The ID of the task whose status to retrieve.
     * @param targetAgentUrl The URL of the target agent.
     * @return The TaskStatus of the task, or null if an error occurred or task not
     *         found.
     */
    @Override
    public TaskStatus getTaskStatus(String taskId, String targetAgentUrl) {
        log.info("Getting task status for {} from {}", taskId, targetAgentUrl);
        WebClient client = WebClient.create(targetAgentUrl);
        try {
            TaskStatus status = client.get()
                    .uri("/tasks/" + taskId + "/status")
                    .retrieve()
                    .bodyToMono(TaskStatus.class)
                    .block(); // Using block() for simplicity
            log.info("Successfully retrieved status for task {}: {}", taskId, status);
            return status;
        } catch (Exception e) {
            log.error("Error getting task status for {} from {}: {}", taskId, targetAgentUrl, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Cancels a specific task by its ID on the target agent URL.
     *
     * @param taskId         The ID of the task to cancel.
     * @param targetAgentUrl The URL of the target agent.
     * @return true if the cancellation request was successful, false otherwise.
     */
    @Override
    public boolean cancelTask(String taskId, String targetAgentUrl) {
        log.info("Cancelling task {} on {}", taskId, targetAgentUrl);
        WebClient client = WebClient.create(targetAgentUrl);
        try {
            // Assuming the cancel endpoint returns a boolean or a response indicating
            // success
            client.post()
                    .uri("/tasks/" + taskId + "/cancel")
                    .retrieve()
                    .toBodilessEntity() // Use toBodilessEntity() if no response body is expected
                    .block(); // Using block() for simplicity
            log.info("Task {} cancelled successfully.", taskId);
            return true; // Assuming success if no exception is thrown
        } catch (Exception e) {
            log.error("Error cancelling task {} on {}: {}", taskId, targetAgentUrl, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Retrieves the result of a specific task by its ID from the target agent URL.
     *
     * @param taskId         The ID of the task whose result to retrieve.
     * @param targetAgentUrl The URL of the target agent.
     * @return An Optional containing the task result (as Object) if available,
     *         otherwise empty.
     */
    @Override
    public Optional<Object> getTaskResult(String taskId, String targetAgentUrl) {
        log.info("Getting task result for {} from {}", taskId, targetAgentUrl);
        WebClient client = WebClient.create(targetAgentUrl);
        try {
            // Assuming the result endpoint returns an Object or a specific result type
            Object result = client.get()
                    .uri("/tasks/" + taskId + "/result")
                    .retrieve()
                    .bodyToMono(Object.class) // Or a more specific class if known
                    .block(); // Using block() for simplicity
            log.info("Successfully retrieved result for task {}: {}", taskId, result);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.error("Error getting task result for {} from {}: {}", taskId, targetAgentUrl, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Subscribes to streaming updates for a specific task from the target agent
     * URL.
     *
     * @param taskId         The ID of the task to subscribe to updates for.
     * @param targetAgentUrl The URL of the target agent.
     * @return A Flux of Task objects representing the updates.
     */
    @Override
    public Flux<Task> getTaskUpdates(String taskId, String targetAgentUrl) {
        log.info("Subscribing to task updates for {} from {}", taskId, targetAgentUrl);
        WebClient client = WebClient.create(targetAgentUrl);
        return client.get()
                .uri("/tasks/" + taskId + "/updates")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Task.class)
                .doOnError(e -> log.error("Error receiving task updates for {}: {}", taskId, e.getMessage(), e))
                .doOnComplete(() -> log.info("Task updates stream completed for {}.", taskId));
    }
}
