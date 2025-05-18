package io.github.a2ap.core.server.controller;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskIdParams;
import io.github.a2ap.core.model.TaskPushNotificationConfig;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.server.A2AServer;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/") // Base path for A2A endpoints
public class A2AServerController {

    private final A2AServer a2aServer;

    @Autowired
    public A2AServerController(A2AServer a2aServer) {
        this.a2aServer = a2aServer;
    }

    @GetMapping(".well-known/a2a-agent-card")
    public CompletableFuture<ResponseEntity<AgentCard>> getAgentCard() {
        // Assuming A2AServer has a method to get its own AgentCard
        // This might need to be implemented or retrieved from configuration
        // For now, returning a placeholder or fetching from a known source
        // A more complete implementation would involve the server knowing its own card.
        // Let's assume A2AServerImpl has a method like getSelfAgentCard()
        // Or we might need to register the server's own card somewhere.
        // For simplicity, let's assume the server instance itself can provide it.
        // This part needs clarification on how the server's own card is managed.
        // Placeholder: Returning null for now, needs proper implementation.
        return CompletableFuture.completedFuture(ResponseEntity.ok(null)); // TODO: Implement fetching server's own
                                                                           // AgentCard
    }

    @PostMapping("tasks/create")
    public CompletableFuture<ResponseEntity<Task>> createTask(@RequestBody Task task) {
        try {
            Task createdTask = a2aServer.createTask(task);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body(createdTask));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null)); // Handle validation
                                                                                              // errors
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture
                    .completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)); // Handle
                                                                                                          // other
                                                                                                          // errors
        }
    }

    @GetMapping("tasks/get")
    public CompletableFuture<ResponseEntity<Task>> getTask(@RequestParam String taskId) {
        // Assuming TaskQueryParams is simple and only contains taskId
        // If TaskQueryParams is more complex, might need @RequestBody POST
        TaskQueryParams params = new TaskQueryParams();
        params.setTaskId(taskId);

        // A2AServerImpl currently returns TaskStatus, not Task object for getTaskStatus
        // The client expects a Task object for getTask.
        // This indicates a mismatch between client and server interfaces.
        // The server should ideally return the full Task object.
        // Let's adjust the server interface or implementation to return Task.
        // For now, returning null as a placeholder.
        // TODO: Adjust A2AServer interface/impl to return Task for getTask
        return CompletableFuture.completedFuture(ResponseEntity.ok(null)); // Placeholder
    }

    @PostMapping("tasks/cancel")
    public CompletableFuture<ResponseEntity<Task>> cancelTask(@RequestBody TaskIdParams params) {
        // Assuming TaskIdParams is simple and contains taskId
        // A2AServerImpl's cancelTask returns boolean, client expects Task.
        // Mismatch here as well. Server should ideally return the updated Task.
        // TODO: Adjust A2AServer interface/impl to return Task for cancelTask
        boolean success = a2aServer.cancelTask(params.getTaskId());
        if (success) {
            // Need to fetch the updated task to return to client
            // This requires a getTask method on A2AServer that returns Task
            // For now, returning a placeholder.
            return CompletableFuture.completedFuture(ResponseEntity.ok(null)); // Placeholder
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // Task
                                                                                                              // not
                                                                                                              // found
                                                                                                              // or
                                                                                                              // couldn't
                                                                                                              // be
                                                                                                              // cancelled
        }
    }

    @PostMapping("tasks/pushNotification/set")
    public CompletableFuture<ResponseEntity<TaskPushNotificationConfig>> setTaskPushNotification(
            @RequestBody TaskPushNotificationConfig params) {
        // A2AServerImpl's subscribeToTaskUpdates takes taskId and callbackUrl, not
        // TaskPushNotificationConfig object.
        // Mismatch here. The server interface needs to be updated to match the client's
        // model.
        // TODO: Adjust A2AServer interface/impl to handle TaskPushNotificationConfig
        // For now, returning a placeholder.
        return CompletableFuture.completedFuture(ResponseEntity.ok(null)); // Placeholder
    }

    @GetMapping("tasks/pushNotification/get")
    public CompletableFuture<ResponseEntity<TaskPushNotificationConfig>> getTaskPushNotification(
            @RequestParam String taskId) {
        // Assuming TaskIdParams is simple and contains taskId
        // A2AServerImpl does not have a method to get push notification config.
        // Mismatch here. Server needs a method to retrieve this config.
        // TODO: Add getTaskPushNotification method to A2AServer interface/impl
        // For now, returning a placeholder.
        return CompletableFuture.completedFuture(ResponseEntity.ok(null)); // Placeholder
    }

    // TODO: Implement /tasks/sendSubscribe and /tasks/resubscribe using SSE
    // This will require using Spring WebFlux's Server-Sent Events capabilities.
    // This is more complex and will be addressed in a future step.

    // Helper method to handle potential mismatches or additional logic
    // private Task convertTaskStatusToTask(String taskId, TaskStatus status) { ...
    // }
}
