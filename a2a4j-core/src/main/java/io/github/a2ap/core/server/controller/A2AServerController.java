package io.github.a2ap.core.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.jsonrpc.JSONRPCError;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.SendMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.server.A2AServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Spring Boot Controller to handle A2A protocol JSON-RPC requests.
 */
@Slf4j
@RestController
public class A2AServerController {

    private final A2AServer a2aServer;
    
    private final ObjectMapper objectMapper;

    @Autowired
    public A2AServerController(A2AServer a2aServer, ObjectMapper objectMapper) {
        this.a2aServer = a2aServer;
        this.objectMapper = objectMapper;
    }

    @GetMapping(".well-known/agent.json")
    public ResponseEntity<AgentCard> getAgentCard() {
        AgentCard card = a2aServer.getSelfAgentCard();
        return ResponseEntity.ok(card);
    }

    @PostMapping(value = "/a2a/server", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONRPCResponse> handleA2ARequestTask(@RequestBody JSONRPCRequest request) {
        JSONRPCResponse response = new JSONRPCResponse();
        // Echo the request ID
        response.setId(request.getId());
        // params is typically a JSON object or array
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
                    // Method not found error
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(JSONRPCError.METHOD_NOT_FOUND, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from A2AServerImpl
            response.setError(new JSONRPCError(JSONRPCError.INVALID_PARAMS, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            // Handle other internal errors
            response.setError(new JSONRPCError(JSONRPCError.INTERNAL_ERROR, "Internal error", e.getMessage()));
            // Log the error with more context
            log.error("Internal error processing method {}.", method, e);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/a2a/server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<JSONRPCResponse>> handleA2ARequestTaskSubscribe(@RequestBody JSONRPCRequest request) {
        JSONRPCResponse response = new JSONRPCResponse();
        // Echo the request ID
        response.setId(request.getId());
        // params is typically a JSON object or array
        String method = request.getMethod();
        Object params = request.getParams();
        try {
            switch (method) {
                case "message/stream":
                    MessageSendParams taskSendParams = objectMapper.convertValue(params, MessageSendParams.class);
                    return a2aServer.handleMessageStream(taskSendParams).map(event -> {
                        response.setResult(event);
                        return ServerSentEvent.<JSONRPCResponse>builder()
                                .data(response).event("task-update").build();
                    });
                case "tasks/resubscribe":
                    // Params expected: TaskIdParams { taskId: string }
                    // Subscribe to updates for the existing task ID
                    TaskIdParams taskIdParamsGet = objectMapper.convertValue(params, TaskIdParams.class);
                    return a2aServer.subscribeToTaskUpdates(taskIdParamsGet.getId())
                            .map(event -> {
                                response.setResult(event);
                                return ServerSentEvent.<JSONRPCResponse>builder()
                                        .data(response)
                                        .event("task-update")
                                        .build();
                            });
                default:
                    // Method not found error
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(JSONRPCError.METHOD_NOT_FOUND, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from A2AServerImpl
            response.setError(new JSONRPCError(JSONRPCError.INVALID_REQUEST, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            // Handle other internal errors
            response.setError(new JSONRPCError(JSONRPCError.INTERNAL_ERROR, "Internal error", e.getMessage()));
            // Log the error with more context
            log.error("Internal error processing method {}.", method, e);
        }
        return Flux.empty();
    }
}
