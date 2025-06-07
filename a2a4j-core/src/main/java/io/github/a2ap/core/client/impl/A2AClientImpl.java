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

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.util.JsonUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

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
     * @param agentCard The agent card info.
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
    public Task sendMessage(MessageSendParams taskSendParams) {
        log.info("Sending message to {} with params: {}", this.baseUrl, taskSendParams);
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        try {
            Task responseTask = client.post()
                    .uri("/message/send")
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(taskSendParams).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .map(data -> JsonUtil.fromJson(data, Task.class))
                    .block();
            log.info("Message sent successfully. Received task: {}", responseTask);
            return responseTask;
        } catch (Exception e) {
            log.error("Error sending message to {}: {}", this.baseUrl, e.getMessage(), e);
            return null; 
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> sendMessageStream(MessageSendParams params) {
        log.info("Send stream message for {} from {}", params, this.baseUrl);
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        return client.post()
                .uri("/message/stream")
                .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(params).getBytes(StandardCharsets.UTF_8))))
                .responseContent()
                .asString()
                .map(this::parseServerSentEvent)
                .filter(Objects::nonNull)
                .doOnError(e -> log.error("Error receiving streaming updates for {}: {}", params, e.getMessage(), e))
                .doOnComplete(() -> log.info("Message updates stream completed for {}.", params));
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
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        try {
            Task task = client.post()
                    .uri("/tasks/get")
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(queryParams).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .map(data -> JsonUtil.fromJson(data, Task.class))
                    .block();
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
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        try {
            Task task = client.post()
                    .uri("/tasks/cancel")
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(params).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .map(data -> JsonUtil.fromJson(data, Task.class))
                    .block();
            log.info("Task {} cancelled successfully.", params);
            return task;
        } catch (Exception e) {
            log.error("Error cancelling task {} on {}: {}", params, this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params) {
        log.info("Setting push notification config for task {} on {}", params.getTaskId(), this.baseUrl);
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        try {
            // 构建JSON-RPC请求
            Map<String, Object> jsonRpcRequest = Map.of(
                "jsonrpc", "2.0",
                "method", "tasks/pushNotificationConfig/set",
                "params", params,
                "id", UUID.randomUUID().toString()
            );
            
            String responseData = client.post()
                    .uri("/a2a/server")
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
                };
                Map<String, String> response = JsonUtil.fromJson(responseData, typeRef);
                if (response != null && response.containsKey("result")) {
                    return JsonUtil.fromJson(response.get("result"), TaskPushNotificationConfig.class);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error setting push notification config for task {} on {}: {}", params.getTaskId(), this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params) {
        log.info("Getting push notification config for task {} from {}", params.getId(), this.baseUrl);
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        try {
            // 构建JSON-RPC请求
            Map<String, Object> jsonRpcRequest = Map.of(
                "jsonrpc", "2.0",
                "method", "tasks/pushNotificationConfig/get",
                "params", params,
                "id", UUID.randomUUID().toString()
            );
            
            String responseData = client.post()
                    .uri("/a2a/server")
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
                };
                Map<String, String> response = JsonUtil.fromJson(responseData, typeRef);
                if (response != null && response.containsKey("result")) {
                    return JsonUtil.fromJson(response.get("result"), TaskPushNotificationConfig.class);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting push notification config for task {} from {}: {}", params.getId(), this.baseUrl, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> resubscribeTask(TaskQueryParams params) {
        log.info("Resubscribing to task updates for {} from {}", params.getTaskId(), this.baseUrl);
        HttpClient client = HttpClient.create().baseUrl(this.baseUrl);
        
        // 构建JSON-RPC请求
        Map<String, Object> jsonRpcRequest = Map.of(
            "jsonrpc", "2.0",
            "method", "tasks/resubscribe",
            "params", Map.of("id", params.getTaskId()),
            "id", UUID.randomUUID().toString()
        );
        
        return client.post()
                .uri("/a2a/server")
                .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                .responseContent()
                .asString()
                .map(this::parseServerSentEvent)
                .filter(Objects::nonNull)
                .doOnError(e -> log.error("Error resubscribing to task updates for {}: {}", params.getTaskId(), e.getMessage(), e))
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
        
        // check agent supports
        return switch (capability.toLowerCase()) {
            case "streaming" -> agentCard.getCapabilities().isStreaming();
            case "pushnotifications" -> agentCard.getCapabilities().isPushNotifications();
            default -> false;
        };
    }
    
    private SendStreamingMessageResponse parseServerSentEvent(String eventData) {
        if (StringUtil.isNullOrEmpty(eventData)) {
            return null;
        }
        try {
            // parse sse data from response
            TypeReference<Map<String, String>> typeRef = new TypeReference<>() {
            };
            Map<String, String> jsonRpcResponse = JsonUtil.fromJson(eventData, typeRef);
            
            if (jsonRpcResponse != null && jsonRpcResponse.containsKey("result")) {
                String result = jsonRpcResponse.get("result");
                return JsonUtil.fromJson(result, SendStreamingMessageResponse.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error parsing server-sent event: {}", e.getMessage());
            return null;
        }
    }
}
