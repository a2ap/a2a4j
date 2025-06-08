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

import io.github.a2ap.core.client.A2aClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * Default implementation of the A2aClient interface providing comprehensive A2A protocol client functionality.
 * 
 * This implementation offers a complete HTTP-based client for interacting with A2A protocol-compliant
 * agents. It handles all aspects of the client-side A2A communication including agent discovery,
 * task management, streaming operations, and push notification configuration.
 * 
 * Key features:
 * - JSON-RPC 2.0 based communication over HTTP
 * - Automatic agent card resolution and caching
 * - Support for both synchronous and streaming message operations
 * - Comprehensive task lifecycle management (send, get, cancel, resubscribe)
 * - Push notification configuration management
 * - Built-in capability detection and validation
 * - Robust error handling and logging
 * 
 * Communication protocol:
 * - Uses Reactor Netty HttpClient for non-blocking HTTP operations
 * - All requests follow JSON-RPC 2.0 specification
 * - Streaming responses are handled via Server-Sent Events (SSE)
 * - Automatic request ID generation for proper correlation
 * 
 * Supported A2A methods:
 * - "message/send": Send messages and create tasks
 * - "message/stream": Send messages with streaming updates
 * - "tasks/get": Retrieve task information
 * - "tasks/cancel": Cancel ongoing tasks
 * - "tasks/pushNotificationConfig/set": Configure push notifications
 * - "tasks/pushNotificationConfig/get": Retrieve push notification settings
 * - "tasks/resubscribe": Resubscribe to task updates
 * 
 * The client maintains agent card information for efficient communication and provides
 * capability checking to ensure operations are supported by the target agent.
 * 
 * Thread safety: This implementation is thread-safe and can be used concurrently
 * across multiple threads.
 */
public class DefaultA2aClient implements A2aClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultA2aClient.class);
    
    private AgentCard agentCard;
    
    private final CardResolver cardResolver;
    
    private final HttpClient client;

    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     * 
     * @param cardResolver The CardResolver to use for resolving agent cards.
     */
    public DefaultA2aClient(CardResolver cardResolver) {
        this.cardResolver = cardResolver;
        this.agentCard = this.retrieveAgentCard();
        this.client = HttpClient.create().baseUrl(this.agentCard.getUrl());
    }

    /**
     * Constructs a new A2aClient with the agent card info
     * @param agentCard agent card info
     */
    public DefaultA2aClient(AgentCard agentCard) {
        this.agentCard = agentCard;
        this.client = HttpClient.create().baseUrl(this.agentCard.getUrl());
        this.cardResolver = null;
    }
    
    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     *
     * @param cardResolver The CardResolver to use for resolving agent cards.
     * @param agentCard The agent card info.
     */
    public DefaultA2aClient(AgentCard agentCard, CardResolver cardResolver) {
        this.agentCard = agentCard;
        this.cardResolver = cardResolver;
        this.client = HttpClient.create().baseUrl(this.agentCard.getUrl());
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
        if (this.cardResolver != null) {
            AgentCard card = cardResolver.resolveCard();
            this.agentCard = card;
            return card;        
        } else {
            log.warn("Retrieving agent card error due the card resolver is null, use the cache agent card {}", this.agentCard.getName());
            return this.agentCard;
        }
    }

    /**
     * Sends a task request to the target agent URL.
     *
     * @param taskSendParams The parameters for sending the task.
     * @return The created Task object received from the agent.
     */
    @Override
    public Task sendMessage(MessageSendParams taskSendParams) {
        log.info("Sending message to {} with params: {}", this.agentCard.getName(), taskSendParams);
        try {
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("message/send")
                    .params(taskSendParams)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client.post()
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        log.error("JSON-RPC error when sending message: code={}, message={}, data={}", 
                                response.getError().getCode(), 
                                response.getError().getMessage(), 
                                response.getError().getData());
                        return null;
                    }
                    if (response.getResult() != null) {
                        Task responseTask = JsonUtil.fromJson(JsonUtil.toJson(response.getResult()), Task.class);
                        log.info("Message sent successfully. Received task: {}", responseTask);
                        return responseTask;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error sending message to {}: {}", this.agentCard.getName(), e.getMessage(), e);
            return null; 
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> sendMessageStream(MessageSendParams params) {
        log.info("Send stream message for {} from {}", params, this.agentCard.getName());
        
        JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                .method("message/stream")
                .params(params)
                .id(UUID.randomUUID().toString())
                .build();
        
        return client.post()
                .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
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
        log.info("Getting task {} from {}", queryParams, this.agentCard.getName());
        try {
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("tasks/get")
                    .params(queryParams)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client.post()
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        log.error("JSON-RPC error when getting task: code={}, message={}, data={}", 
                                response.getError().getCode(), 
                                response.getError().getMessage(), 
                                response.getError().getData());
                        return null;
                    }
                    if (response.getResult() != null) {
                        Task task = JsonUtil.fromJson(JsonUtil.toJson(response.getResult()), Task.class);
                        log.info("Successfully retrieved task {}: {}", queryParams, task);
                        return task;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting task {} from {}: {}", queryParams, this.agentCard.getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Task cancelTask(TaskIdParams params) {
        log.info("Cancelling task {} on {}", params, this.agentCard.getName());
        try {
            // 构建JSON-RPC请求
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("tasks/cancel")
                    .params(params)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client.post()
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        log.error("JSON-RPC error when cancelling task: code={}, message={}, data={}", 
                                response.getError().getCode(), 
                                response.getError().getMessage(), 
                                response.getError().getData());
                        return null;
                    }
                    if (response.getResult() != null) {
                        Task task = JsonUtil.fromJson(JsonUtil.toJson(response.getResult()), Task.class);
                        log.info("Task {} cancelled successfully.", params);
                        return task;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error cancelling task {} on {}: {}", params, this.agentCard.getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params) {
        log.info("Setting push notification config for task {} on {}", params.getTaskId(), this.agentCard.getName());
        try {
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("tasks/pushNotificationConfig/set")
                    .params(params)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client.post()
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        log.error("JSON-RPC error when setting push notification config: code={}, message={}, data={}", 
                                response.getError().getCode(), 
                                response.getError().getMessage(), 
                                response.getError().getData());
                        return null;
                    }
                    if (response.getResult() != null) {
                        return JsonUtil.fromJson(JsonUtil.toJson(response.getResult()), TaskPushNotificationConfig.class);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error setting push notification config for task {} on {}: {}", params.getTaskId(), this.agentCard.getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params) {
        log.info("Getting push notification config for task {} from {}", params.getId(), this.agentCard.getName());
        try {
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("tasks/pushNotificationConfig/get")
                    .params(params)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client.post()
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        log.error("JSON-RPC error when getting push notification config: code={}, message={}, data={}", 
                                response.getError().getCode(), 
                                response.getError().getMessage(), 
                                response.getError().getData());
                        return null;
                    }
                    if (response.getResult() != null) {
                        return JsonUtil.fromJson(JsonUtil.toJson(response.getResult()), TaskPushNotificationConfig.class);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting push notification config for task {} from {}: {}", params.getId(), this.agentCard.getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Flux<SendStreamingMessageResponse> resubscribeTask(TaskQueryParams params) {
        log.info("Resubscribing to task updates for {} from {}", params.getTaskId(), this.agentCard.getName());
        
        JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                .method("tasks/resubscribe")
                .params(Map.of("id", params.getTaskId()))
                .id(UUID.randomUUID().toString())
                .build();
        
        return client.post()
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
            JSONRPCResponse jsonRpcResponse = JsonUtil.fromJson(eventData, JSONRPCResponse.class);
            
            if (jsonRpcResponse != null) {
                if (jsonRpcResponse.getError() != null) {
                    log.error("JSON-RPC error in server-sent event: code={}, message={}, data={}", 
                            jsonRpcResponse.getError().getCode(), 
                            jsonRpcResponse.getError().getMessage(), 
                            jsonRpcResponse.getError().getData());
                    return null;
                }
                if (jsonRpcResponse.getResult() != null) {
                    return JsonUtil.fromJson(JsonUtil.toJson(jsonRpcResponse.getResult()), SendStreamingMessageResponse.class);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error parsing server-sent event: {}", e.getMessage());
            return null;
        }
    }
}
