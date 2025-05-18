package io.github.a2ap.core.client.impl;

import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.event.TaskUpdateEvent;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.TaskSendParams;
import io.github.a2ap.core.model.TaskStatus;
import java.util.Optional;
import java.util.concurrent.Flow;
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
    
    private final String baseUrl;

    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     *
     * @param cardResolver The CardResolver to use for resolving agent cards.
     * @param targetAgentUrl The URL of the target agent.
     */
    public A2AClientImpl(CardResolver cardResolver, String targetAgentUrl) {
        this.cardResolver = cardResolver;
        this.baseUrl = targetAgentUrl;
    }

    @Override
    public AgentCard agentCard() {
        return null;
    }

    /**
     * Sends a task request to the target agent URL.
     *
     * @param taskSendParams The parameters for sending the task.
     * @return The created Task object received from the agent.
     */
    @Override
    public Task sendTask(TaskSendParams taskSendParams) {
        log.info("Sending task to {} with params: {}", this.baseUrl, taskSendParams);
        WebClient client = WebClient.create(this.baseUrl);
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
            log.error("Error sending task to {}: {}", this.baseUrl, e.getMessage(), e);
            // Depending on requirements, you might want to rethrow or return a specific
            // error indicator
            return null; // Or throw a custom exception
        }
    }

    @Override
    public Flow.Publisher<TaskUpdateEvent> sendTaskSubscribe(TaskSendParams params) {
        return null;
    }

    /**
     * Retrieves a specific task by its ID from the target agent URL.
     *
     * @param queryParams         The query params task to retrieve.
     * @return An Optional containing the Task object if found, otherwise empty.
     */
    @Override
    public Task getTask(TaskQueryParams queryParams) {
        log.info("Getting task {} from {}", queryParams, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            Task task = client.get()
                    .uri("/tasks/" + queryParams.getTaskId())
                    .retrieve()
                    .bodyToMono(Task.class)
                    .block(); // Using block() for simplicity
            log.info("Successfully retrieved task {}: {}", queryParams, task);
            return task;
        } catch (Exception e) {
            log.error("Error getting task {} from {}: {}", queryParams, this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskStatus getTaskStatus(TaskQueryParams queryParams) {
        log.info("Getting task status for {} from {}", queryParams, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            TaskStatus status = client.get()
                    .uri("/tasks/" + queryParams.getTaskId() + "/status")
                    .retrieve()
                    .bodyToMono(TaskStatus.class)
                    .block(); // Using block() for simplicity
            log.info("Successfully retrieved status for task {}: {}", queryParams, status);
            return status;
        } catch (Exception e) {
            log.error("Error getting task status for {} from {}: {}", queryParams, this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Task cancelTask(TaskIdParams params) {
        log.info("Cancelling task {} on {}", params, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            // Assuming the cancel endpoint returns a boolean or a response indicating
            // success
            client.post()
                    .uri("/tasks/" + params.getTaskId() + "/cancel")
                    .retrieve()
                    .toBodilessEntity() // Use toBodilessEntity() if no response body is expected
                    .block(); // Using block() for simplicity
            log.info("Task {} cancelled successfully.", params);
            return null;
        } catch (Exception e) {
            log.error("Error cancelling task {} on {}: {}", params, this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params) {
        return null;
    }

    @Override
    public TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params) {
        return null;
    }

    @Override
    public Flow.Publisher<TaskUpdateEvent> resubscribeTask(TaskQueryParams params) {
        return null;
    }

    @Override
    public Boolean supports(String capability) {
        return null;
    }

    /**
     * Retrieves the result of a specific task by its ID from the target agent URL.
     *
     * @param taskId         The ID of the task whose result to retrieve.
     * @return An Optional containing the task result (as Object) if available,
     *         otherwise empty.
     */
    public Object getTaskResult(String taskId) {
        log.info("Getting task result for {} from {}", taskId, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
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
            log.error("Error getting task result for {} from {}: {}", taskId, this.baseUrl, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Subscribes to streaming updates for a specific task from the target agent
     * URL.
     *
     * @param taskId         The ID of the task to subscribe to updates for.
     * @return A Flux of Task objects representing the updates.
     */
    public Flux<Task> getTaskUpdates(String taskId) {
        log.info("Subscribing to task updates for {} from {}", taskId, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        return client.get()
                .uri("/tasks/" + taskId + "/updates")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Task.class)
                .doOnError(e -> log.error("Error receiving task updates for {}: {}", taskId, e.getMessage(), e))
                .doOnComplete(() -> log.info("Task updates stream completed for {}.", taskId));
    }
}
