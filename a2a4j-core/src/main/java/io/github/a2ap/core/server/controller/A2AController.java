package io.github.a2ap.core.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.server.A2AServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Spring Boot Controller to handle A2A protocol JSON-RPC requests.
 */
@RestController
public class A2AController {

    private final A2AServer a2aServer;
    private final ObjectMapper objectMapper;

    @Autowired
    public A2AController(A2AServer a2aServer, ObjectMapper objectMapper) {
        this.a2aServer = a2aServer;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/a2a") // Define the endpoint path
    public ResponseEntity<JSONRPCResponse> handleA2ARequest(@RequestBody JSONRPCRequest request) {
        JSONRPCResponse response = new JSONRPCResponse();
        response.setJsonrpc("2.0");
        response.setId(request.getId()); // Echo the request ID

        String method = request.getMethod();
        Object params = request.getParams(); // params is typically a JSON object or array

        try {
            switch (method) {
                case "agentCard":
                    // No params expected for agentCard
                    Object agentCardResult = a2aServer.getSelfAgentCard();
                    response.setResult(agentCardResult);
                    break;
                case "tasks/send":
                    // Params expected: Task object
                    Task taskToSend = objectMapper.convertValue(params, Task.class);
                    Task createdTask = a2aServer.createTask(taskToSend);
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
                // TODO: Add cases for streaming methods (tasks/sendSubscribe,
                // tasks/resubscribe)
                default:
                    // Method not found error
                    response.setError(new JSONRPCResponse.Error(-32601, "Method not found",
                            "Method '" + method + "' not supported"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from A2AServerImpl
            response.setError(new JSONRPCResponse.Error(-32602, "Invalid params", e.getMessage()));
        } catch (JsonProcessingException e) {
            // Handle JSON parsing errors
            response.setError(
                    new JSONRPCResponse.Error(-32700, "Parse error", "Invalid JSON-RPC parameters: " + e.getMessage()));
        } catch (Exception e) {
            // Handle other internal errors
            response.setError(new JSONRPCResponse.Error(-32603, "Internal error", e.getMessage()));
            // Log the error with more context
            System.err.println("Internal error processing method '" + method + "': " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    // TODO: Add endpoint for streaming methods (e.g., tasks/sendSubscribe,
    // tasks/resubscribe)
    // This will likely require Server-Sent Events (SSE) or WebSockets.
    // Spring WebFlux might be needed for reactive streaming.

    @PostMapping(value = "/a2a/tasks/sendSubscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Task>> sendTaskAndSubscribe(@RequestBody Task task) {
        // First, create the task
        Task createdTask = a2aServer.createTask(task);
        // Then, subscribe to updates for the created task
        return a2aServer.subscribeToTaskUpdates(createdTask.getId())
                .map(taskUpdate -> ServerSentEvent.<Task>builder()
                        .data(taskUpdate)
                        .id(taskUpdate.getId())
                        .event("task-update")
                        .build());
    }

    @GetMapping(value = "/a2a/tasks/resubscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Task>> resubscribeToTask(@RequestParam String taskId) {
        // Subscribe to updates for the existing task ID
        return a2aServer.subscribeToTaskUpdates(taskId)
                .map(taskUpdate -> ServerSentEvent.<Task>builder()
                        .data(taskUpdate)
                        .id(taskUpdate.getId())
                        .event("task-update")
                        .build());
    }
}
