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

package io.github.a2ap.core.server.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.jsonrpc.JSONRPCError;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * Default implementation of the Dispatcher interface.
 * This implementation routes JSON-RPC requests to the appropriate A2A server methods
 * and handles both synchronous and streaming operations.
 * 
 * Supported methods include:
 * - message/send: Send a message and create a task
 * - message/stream: Send a message and subscribe to streaming updates
 * - tasks/get: Retrieve task information
 * - tasks/cancel: Cancel a running task
 * - tasks/pushNotificationConfig/set: Set push notification configuration
 * - tasks/pushNotificationConfig/get: Get push notification configuration
 * - tasks/resubscribe: Resubscribe to task updates
 */
public class DefaultDispatcher implements Dispatcher {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultDispatcher.class);

    private final A2AServer a2aServer;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a new DefaultDispatcher.
     * 
     * @param a2aServer The A2A server instance to delegate operations to
     * @param objectMapper The Jackson ObjectMapper for parameter conversion
     */
    public DefaultDispatcher(A2AServer a2aServer, ObjectMapper objectMapper) {
        this.a2aServer = a2aServer;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation supports the following methods:
     * - message/send
     * - tasks/get
     * - tasks/cancel
     * - tasks/pushNotificationConfig/set
     * - tasks/pushNotificationConfig/get
     */
    @Override
    public JSONRPCResponse dispatch(JSONRPCRequest request) {
        JSONRPCResponse response = new JSONRPCResponse();
        response.setId(request.getId());
        String method = request.getMethod();
        Object params = request.getParams();
        
        try {
            switch (method) {
                case "message/send":
                    MessageSendParams taskSendParams = objectMapper.convertValue(params, MessageSendParams.class);
                    SendMessageResponse messageResponse = a2aServer.handleMessage(taskSendParams);
                    response.setResult(messageResponse);
                    break;
                case "tasks/get":
                    TaskIdParams taskIdParamsGet = objectMapper.convertValue(params, TaskIdParams.class);
                    Task task = a2aServer.getTask(taskIdParamsGet.getId());
                    response.setResult(task);
                    break;
                case "tasks/cancel":
                    TaskIdParams taskIdParamsCancel = objectMapper.convertValue(params, TaskIdParams.class);
                    Task cancelledTask = a2aServer.cancelTask(taskIdParamsCancel.getId());
                    response.setResult(cancelledTask);
                    break;
                case "tasks/pushNotificationConfig/set":
                    TaskPushNotificationConfig configToSet = objectMapper.convertValue(params,
                            TaskPushNotificationConfig.class);
                    TaskPushNotificationConfig setResult = a2aServer.setTaskPushNotification(configToSet);
                    response.setResult(setResult);
                    break;
                case "tasks/pushNotificationConfig/get":
                    TaskIdParams taskIdParamsGetConfig = objectMapper.convertValue(params, TaskIdParams.class);
                    TaskPushNotificationConfig getConfigResult = a2aServer
                            .getTaskPushNotification(taskIdParamsGetConfig.getId());
                    response.setResult(getConfigResult);
                    break;
                default:
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(JSONRPCError.METHOD_NOT_FOUND, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            response.setError(new JSONRPCError(JSONRPCError.INVALID_PARAMS, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            response.setError(new JSONRPCError(JSONRPCError.INTERNAL_ERROR, "Internal error", e.getMessage()));
            log.error("Internal error processing method {}.", method, e);
        }
        return response;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation supports the following streaming methods:
     * - message/stream
     * - tasks/resubscribe
     */
    @Override
    public Flux<JSONRPCResponse> dispatchStream(JSONRPCRequest request) {
        JSONRPCResponse response = new JSONRPCResponse();
        response.setId(request.getId());
        String method = request.getMethod();
        Object params = request.getParams();
        
        try {
            switch (method) {
                case "message/stream":
                    MessageSendParams taskSendParams = objectMapper.convertValue(params, MessageSendParams.class);
                    return a2aServer.handleMessageStream(taskSendParams).map(event -> {
                        response.setResult(event);
                        return response;
                    });
                case "tasks/resubscribe":
                    TaskIdParams taskIdParamsGet = objectMapper.convertValue(params, TaskIdParams.class);
                    return a2aServer.subscribeToTaskUpdates(taskIdParamsGet.getId())
                            .map(event -> {
                                response.setResult(event);
                                return response;
                            });
                default:
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(JSONRPCError.METHOD_NOT_FOUND, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            response.setError(new JSONRPCError(JSONRPCError.INVALID_REQUEST, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            response.setError(new JSONRPCError(JSONRPCError.INTERNAL_ERROR, "Internal error", e.getMessage()));
            log.error("Internal error processing method {}.", method, e);
        }
        return Flux.just(response);
    }
} 
