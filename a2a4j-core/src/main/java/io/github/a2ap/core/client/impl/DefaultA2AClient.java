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

import com.fasterxml.jackson.databind.JsonNode;
import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.exception.A2AError;
import io.github.a2ap.core.jsonrpc.JSONRPCError;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.util.JsonUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * Default implementation of the A2AClient interface providing comprehensive A2A protocol client functionality.
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
public class DefaultA2AClient implements A2AClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultA2AClient.class);

    private AgentCard agentCard;
    
    private final CardResolver cardResolver;
    
    private final HttpClient client;

    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     * 
     * @param cardResolver The CardResolver to use for resolving agent cards.
     */
    public DefaultA2AClient(CardResolver cardResolver) {
        this.cardResolver = cardResolver;
        this.agentCard = this.retrieveAgentCard();
        this.client = HttpClient.create();
    }

    /**
     * Constructs a new A2aClient with the agent card info
     * @param agentCard agent card info
     */
    public DefaultA2AClient(AgentCard agentCard) {
        this.agentCard = agentCard;
        this.client = HttpClient.create();
        this.cardResolver = null;
    }
    
    /**
     * Constructs a new A2AClientImpl with the specified CardResolver.
     *
     * @param cardResolver The CardResolver to use for resolving agent cards.
     * @param agentCard The agent card info.
     */
    public DefaultA2AClient(AgentCard agentCard, CardResolver cardResolver) {
        this.agentCard = agentCard;
        this.cardResolver = cardResolver;
        this.client = HttpClient.create();
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
    public SendMessageResponse sendMessage(MessageSendParams taskSendParams) throws A2AError {
        log.info("Sending message to {} with params: {}", this.agentCard.getName(), taskSendParams);
        try {
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("message/send")
                    .params(taskSendParams)
                    .id(UUID.randomUUID().toString())
                    .build();
            String responseData = client
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                    })
                    .post()
                    .uri(this.agentCard.getUrl())
                    .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                    .responseContent()
                    .aggregate()
                    .asString()
                    .block();
            
            if (responseData != null) {
                JSONRPCResponse response = JsonUtil.fromJson(responseData, JSONRPCResponse.class);
                if (response != null) {
                    if (response.getError() != null) {
                        JSONRPCError error = response.getError();
                        log.error("JSON-RPC error when sending message: code={}, message={}, data={}",
                                error.getCode(),
                                error.getMessage(),
                                error.getData());
                        throw new A2AError(error.getMessage(), error.getCode(), error.getData());
                    }
                    if (response.getResult() != null) {
                        String jsonStr = JsonUtil.toJson(response.getResult());
                        JsonNode jsonNode = JsonUtil.fromJson(jsonStr);
                        if (jsonNode != null && jsonNode.has("kind")) {
                            SendMessageResponse messageResponse = null;
                            String kind = jsonNode.get("kind").asText();
                            if ("message".equals(kind)) {
                                messageResponse = JsonUtil.fromJson(jsonStr, Message.class);
                            } else if ("task".equals(kind)) {
                                messageResponse = JsonUtil.fromJson(jsonStr, Task.class);
                            } else {
                                log.error("Unknown json-rpc kind: {}", kind);
                            }
                            if (messageResponse != null) {
                                log.info("Message sent successfully. Received response: {}", messageResponse);
                                return messageResponse;
                            }
                        }
                    }
                }
            }
            throw new A2AError("response data is null");
        } catch (Exception e) {
            log.error("Error sending message to {}: {}", this.agentCard.getName(), e.getMessage(), e);
            throw new A2AError(e.getMessage(), e); 
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
        
        return client
                .headers(headers -> {
                    headers.add("Content-Type", "application/json");
                    headers.add("Accept", "text/event-stream");
                })
                .post()
                .uri(this.agentCard.getUrl())
                .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                .responseContent()
                .asString()
                .scan("", (accumulator, chunk) -> {
                    // Accumulate SSE data until complete events are found
                    return accumulator + chunk;
                })
                .flatMap(this::parseSseChunks)
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
            
            String responseData = client
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                    })
                    .post()
                    .uri(this.agentCard.getUrl())
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
            // Build JSON-RPC request
            JSONRPCRequest jsonRpcRequest = JSONRPCRequest.builder()
                    .method("tasks/cancel")
                    .params(params)
                    .id(UUID.randomUUID().toString())
                    .build();
            
            String responseData = client
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                    })
                    .post()
                    .uri(this.agentCard.getUrl())
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
            
            String responseData = client
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                    })
                    .post()
                    .uri(this.agentCard.getUrl())
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
            
            String responseData = client
                    .headers(headers -> {
                        headers.add("Content-Type", "application/json");
                    })
                    .post()
                    .uri(this.agentCard.getUrl())
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
        
        return client
                .headers(headers -> {
                    headers.add("Content-Type", "application/json");
                    headers.add("Accept", "text/event-stream");
                })
                .post()
                .uri(this.agentCard.getUrl())
                .send(Mono.just(Unpooled.wrappedBuffer(JsonUtil.toJson(jsonRpcRequest).getBytes(StandardCharsets.UTF_8))))
                .responseContent()
                .asString()
                .scan("", (accumulator, chunk) -> {
                    // Accumulate SSE data until complete events are found
                    return accumulator + chunk;
                })
                .flatMap(this::parseSseChunks)
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
            // Parse SSE format data
            String jsonData = extractJsonFromSSE(eventData);
            if (StringUtil.isNullOrEmpty(jsonData)) {
                return null;
            }
            
            JSONRPCResponse jsonRpcResponse = JsonUtil.fromJson(jsonData, JSONRPCResponse.class);
            
            if (jsonRpcResponse != null) {
                if (jsonRpcResponse.getError() != null) {
                    log.error("JSON-RPC error in server-sent event: code={}, message={}, data={}", 
                            jsonRpcResponse.getError().getCode(), 
                            jsonRpcResponse.getError().getMessage(), 
                            jsonRpcResponse.getError().getData());
                    return null;
                }
                if (jsonRpcResponse.getResult() != null) {
                    String result = JsonUtil.toJson(jsonRpcResponse.getResult());
                    JsonNode jsonNode = JsonUtil.fromJson(result);
                    if (jsonNode != null && jsonNode.has("kind")) {
                        String kind = jsonNode.get("kind").asText();
                        if ("task".equals(kind)) {
                            return JsonUtil.fromJson(result, Task.class);
                        } else if ("message".equals(kind)) {
                            return JsonUtil.fromJson(result, Message.class);
                        } else if ("artifact-update".equals(kind)) {
                            return JsonUtil.fromJson(result, TaskArtifactUpdateEvent.class);
                        } else if ("status-update".equals(kind)) {
                            return JsonUtil.fromJson(result, TaskStatusUpdateEvent.class);
                        } else {
                            log.error("Unknown event kind: {}", kind);
                        }
                    } else {
                        log.error("Can not parse server-sent event: {}", jsonRpcResponse);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error parsing server-sent event: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse accumulated SSE data chunks and extract complete SSE events
     * 
     * @param accumulatedData accumulated SSE data
     * @return parsed SendStreamingMessageResponse stream
     */
    private Flux<SendStreamingMessageResponse> parseSseChunks(String accumulatedData) {
        if (StringUtil.isNullOrEmpty(accumulatedData)) {
            return Flux.empty();
        }
        
        List<SendStreamingMessageResponse> events = new ArrayList<>();
        
        // handle the sse single message
        SendStreamingMessageResponse response = parseServerSentEvent(accumulatedData);
        if (response != null) {
            events.add(response);
        }
        
        return Flux.fromIterable(events);
    }

    /**
     * Extract JSON content from SSE format data
     * 
     * @param sseData SSE format data
     * @return extracted JSON string
     */
    private String extractJsonFromSSE(String sseData) {
        if (StringUtil.isNullOrEmpty(sseData)) {
            return null;
        }
        
        // Split SSE data by lines
        String[] lines = sseData.split("\n");
        StringBuilder jsonData = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            // Process data: lines
            if (line.startsWith("data:")) {
                String dataContent = line.substring(5); // Remove "data:" prefix
                jsonData.append(dataContent);
            }
            // Ignore other SSE fields like event:, id:, retry:, etc.
        }
        
        String result = jsonData.toString().trim();
        return result.isEmpty() ? null : result;
    }
}
