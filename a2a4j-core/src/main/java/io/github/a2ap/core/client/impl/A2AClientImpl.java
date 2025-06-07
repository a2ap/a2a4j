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

package io.github.a2ap.core.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of the A2AClient interface.
 */
public class A2AClientImpl implements A2AClient {

    private static final Logger log = LoggerFactory.getLogger(A2AClientImpl.class);

    private AgentCard agentCard;

    private String baseUrl;

    private final CardResolver cardResolver;

    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     *
     * @param cardResolver The CardResolver to use for resolving agent cards.
     * @param agentCard    The agent card info.
     */
    public A2AClientImpl(AgentCard agentCard, CardResolver cardResolver) {
        this.agentCard = agentCard;
        this.cardResolver = cardResolver;
        this.baseUrl = agentCard.getUrl();
    }

    @Override
    public AgentCard agentCard() {
        if (agentCard != null) {
            return agentCard;
        }
        return retrieveAgentCard();
    }

    @Override
    public AgentCard retrieveAgentCard() {
        log.info("Sending retrieve agent card to {}", this.baseUrl);
        AgentCard card = cardResolver.resolveCard(this.baseUrl);
        this.agentCard = card;
        this.baseUrl = card.getUrl();
        return card;
    }

    /**
     * Sends a task request to the target agent URL.
     *
     * @param taskSendParams The parameters for sending the task.
     * @return The created Task object received from the agent.
     */
    @Override
    public Task sendTask(MessageSendParams taskSendParams) {
        log.info("Sending task to {} with params: {}", this.baseUrl, taskSendParams);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            Task responseTask = client.post()
                    .uri("/tasks/send")
                    .bodyValue(taskSendParams)
                    .retrieve()
                    .bodyToMono(Task.class)
                    .block();
            log.info("Task sent successfully. Received task: {}", responseTask);
            return responseTask;
        } catch (Exception e) {
            log.error("Error sending task to {}: {}", this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> sendTaskSubscribe(MessageSendParams params) {
        log.info("Subscribing to task updates for {} from {}", params, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        return client.get()
                .uri("/tasks/sendSubscribe")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(SendStreamingMessageResponse.class)
                .doOnError(e -> log.error("Error receiving task updates for {}: {}", params, e.getMessage(), e))
                .doOnComplete(() -> log.info("Task updates stream completed for {}.", params));
    }

    /**
     * Retrieves a specific task by its ID from the target agent URL.
     *
     * @param queryParams The query params task to retrieve.
     * @return An Optional containing the Task object if found, otherwise empty.
     */
    @Override
    public Task getTask(TaskQueryParams queryParams) {
        log.info("Getting task {} from {}", queryParams, this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            Task task = client.get().uri("/tasks/" + queryParams.getTaskId()).retrieve().bodyToMono(Task.class).block(); // Using
            // block()
            // for
            // simplicity
            log.info("Successfully retrieved task {}: {}", queryParams, task);
            return task;
        } catch (Exception e) {
            log.error("Error getting task {} from {}: {}", queryParams, this.baseUrl, e.getMessage(), e);
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
                    .uri("/tasks/" + params.getId() + "/cancel")
                    .retrieve()
                    .toBodilessEntity() // Use toBodilessEntity() if no response body is
                    // expected
                    .block(); // Using block() for simplicity
            log.info("Task {} cancelled successfully.", params);
            return null;
        } catch (Exception e) {
            log.error("Error cancelling task {} on {}: {}", params, this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params) {
        log.info("Setting push notification config for task {} on {}", params.getTaskId(), this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            // 构建JSON-RPC请求
            Map<String, Object> jsonRpcRequest = Map.of("jsonrpc", "2.0", "method", "tasks/pushNotificationConfig/set",
                    "params", params, "id", UUID.randomUUID().toString());

            Map<String, Object> response = client.post()
                    .uri("/a2a/server")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonRpcRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("result")) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.convertValue(response.get("result"), TaskPushNotificationConfig.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error setting push notification config for task {} on {}: {}", params.getTaskId(), this.baseUrl,
                    e.getMessage(), e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params) {
        log.info("Getting push notification config for task {} from {}", params.getId(), this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);
        try {
            // 构建JSON-RPC请求
            Map<String, Object> jsonRpcRequest = Map.of("jsonrpc", "2.0", "method", "tasks/pushNotificationConfig/get",
                    "params", params, "id", UUID.randomUUID().toString());

            Map<String, Object> response = client.post()
                    .uri("/a2a/server")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonRpcRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("result")) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.convertValue(response.get("result"), TaskPushNotificationConfig.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting push notification config for task {} from {}: {}", params.getId(), this.baseUrl,
                    e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> resubscribeTask(TaskQueryParams params) {
        log.info("Resubscribing to task updates for {} from {}", params.getTaskId(), this.baseUrl);
        WebClient client = WebClient.create(this.baseUrl);

        // 构建JSON-RPC请求
        Map<String, Object> jsonRpcRequest = Map.of("jsonrpc", "2.0", "method", "tasks/resubscribe", "params",
                Map.of("id", params.getTaskId()), "id", UUID.randomUUID().toString());

        return client.post()
                .uri("/a2a/server")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseServerSentEvent)
                .filter(Objects::nonNull)
                .doOnError(e -> log.error("Error resubscribing to task updates for {}: {}", params.getTaskId(),
                        e.getMessage(), e))
                .doOnComplete(() -> log.info("Task resubscription stream completed for {}.", params.getTaskId()));
    }

    @Override
    public Boolean supports(String capability) {
        if (agentCard == null) {
            agentCard = retrieveAgentCard();
        }

        if (agentCard == null || agentCard.getCapabilities() == null) {
            return false;
        }

        // 检查代理能力
        return switch (capability.toLowerCase()) {
            case "streaming" -> agentCard.getCapabilities().isStreaming();
            case "pushnotifications" -> agentCard.getCapabilities().isPushNotifications();
            default -> false;
        };
    }

    @SuppressWarnings("unchecked")
    private SendStreamingMessageResponse parseServerSentEvent(String eventData) {
        try {
            // 解析SSE数据中的JSON-RPC响应
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonRpcResponse = mapper.readValue(eventData, Map.class);

            if (jsonRpcResponse.containsKey("result")) {
                Object result = jsonRpcResponse.get("result");
                // 根据结果类型创建相应的事件
                return mapper.convertValue(result, SendStreamingMessageResponse.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error parsing server-sent event: {}", e.getMessage());
            return null;
        }
    }

}
