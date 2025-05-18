package io.github.a2ap.core.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.jsonrpc.JSONRPCError;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskSendParams;
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

    @GetMapping(".well-known/a2a-agent-card")
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
                case "tasks/send":
                    // Params expected: Task object
                    TaskSendParams taskSendParams = objectMapper.convertValue(params, TaskSendParams.class);
                    Task createdTask = a2aServer.handleTask(taskSendParams);
                    response.setResult(createdTask);
                    break;
                case "tasks/get":
                    // Params expected: TaskIdParams { taskId: string }
                    TaskIdParams taskIdParamsGet = objectMapper.convertValue(params, TaskIdParams.class);
                    Task task = a2aServer.getTask(taskIdParamsGet.getTaskId());
                    response.setResult(task);
                    break;
                case "tasks/cancel":
                    // Params expected: TaskIdParams { taskId: string }
                    TaskIdParams taskIdParamsCancel = objectMapper.convertValue(params, TaskIdParams.class);
                    Task cancelledTask = a2aServer.cancelTask(taskIdParamsCancel.getTaskId());
                    response.setResult(cancelledTask);
                    break;
                case "tasks/pushNotification/set":
                    // Params expected: TaskPushNotificationConfig object
                    TaskPushNotificationConfig configToSet = objectMapper.convertValue(params,
                            TaskPushNotificationConfig.class);
                    TaskPushNotificationConfig setResult = a2aServer.setTaskPushNotification(configToSet);
                    response.setResult(setResult);
                    break;
                case "tasks/pushNotification/get":
                    // Params expected: TaskIdParams { taskId: string }
                    TaskIdParams taskIdParamsGetConfig = objectMapper.convertValue(params, TaskIdParams.class);
                    TaskPushNotificationConfig getConfigResult = a2aServer
                            .getTaskPushNotification(taskIdParamsGetConfig.getTaskId());
                    response.setResult(getConfigResult);
                    break;
                default:
                    // Method not found error
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(-32601, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from A2AServerImpl
            response.setError(new JSONRPCError(-32602, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            // Handle other internal errors
            response.setError(new JSONRPCError(-32603, "Internal error", e.getMessage()));
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
                case "tasks/sendSubscribe":
                    // First, create the task
                    Task taskToSend = objectMapper.convertValue(params, Task.class);
                    Task createdTask = a2aServer.handleTask(taskToSend);
                    // Then, subscribe to updates for the created task
                    return a2aServer.subscribeToTaskUpdates(createdTask.getId())
                            .map(taskUpdate -> ServerSentEvent.<Task>builder()
                                    .data(taskUpdate)
                                    .id(taskUpdate.getId())
                                    .event("task-update")
                                    .build());
                case "tasks/resubscribe":
                    // Params expected: TaskIdParams { taskId: string }
                    // Subscribe to updates for the existing task ID
                    TaskIdParams taskIdParamsGet = objectMapper.convertValue(params, TaskIdParams.class);
                    return a2aServer.subscribeToTaskUpdates(taskIdParamsGet.getTaskId())
                            .map(taskUpdate -> ServerSentEvent.<Task>builder()
                                    .data(taskUpdate)
                                    .id(taskUpdate.getId())
                                    .event("task-update")
                                    .build());
                default:
                    // Method not found error
                    log.warn("Unsupported method: {}", method);
                    response.setError(new JSONRPCError(-32601, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from A2AServerImpl
            response.setError(new JSONRPCError(-32602, "Invalid params", e.getMessage()));
        } catch (Exception e) {
            // Handle other internal errors
            response.setError(new JSONRPCError(-32603, "Internal error", e.getMessage()));
            // Log the error with more context
            log.error("Internal error processing method {}.", method, e);
        }
        return Flux.empty();
    }
}
