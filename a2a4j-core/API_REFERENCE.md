# A2A4J API Reference

This document provides detailed API reference for the A2A4J library.

## Table of Contents

- [Core Interfaces](#core-interfaces)
- [Model Classes](#model-classes)
- [Server Components](#server-components)
- [Client Components](#client-components)
- [JSON-RPC Support](#json-rpc-support)
- [Exception Handling](#exception-handling)

## Core Interfaces

### A2AServer

Main server interface defining core functionality.

```java
public interface A2AServer {
    SendMessageResponse handleMessage(MessageSendParams params);
    Flux<SendStreamingMessageResponse> handleMessageStream(MessageSendParams params);
    Task getTask(String taskId);
    Task cancelTask(String taskId);
    TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig config);
    TaskPushNotificationConfig getTaskPushNotification(String taskId);
    AgentCard getSelfAgentCard();
    Flux<SendStreamingMessageResponse> subscribeToTaskUpdates(String taskId);
}
```

### A2AClient

Main client interface for server communication.

```java
public interface A2AClient {
    AgentCard agentCard();
    AgentCard retrieveAgentCard();
    Task sendTask(MessageSendParams params);
    Flux<SendStreamingMessageResponse> sendTaskSubscribe(MessageSendParams params);
    Task getTask(TaskQueryParams params);
    Task cancelTask(TaskIdParams params);
    TaskPushNotificationConfig setTaskPushNotification(TaskPushNotificationConfig params);
    TaskPushNotificationConfig getTaskPushNotification(TaskIdParams params);
    Flux<SendStreamingMessageResponse> resubscribeTask(TaskQueryParams params);
    Boolean supports(String capability);
}
```

## Model Classes

### AgentCard

Represents an agent's capabilities and metadata.

```java
AgentCard agentCard = AgentCard.builder()
    .id("unique-agent-id")
    .name("My Agent")
    .description("A sample A2A agent")
    .url("https://agent.example.com")
    .version("1.0.0")
    .documentationUrl("https://agent.example.com/docs")
    .capabilities(AgentCapabilities.builder()
        .streaming(true)
        .pushNotifications(true)
        .build())
    .defaultInputModes(List.of("text", "file"))
    .defaultOutputModes(List.of("text", "file", "json"))
    .skills(List.of(
        AgentSkill.builder()
            .name("text-processing")
            .description("Process text content")
            .build()
    ))
    .build();
```

### Task

Represents a task and its execution state.

```java
Task task = Task.builder()
    .id("task-123")
    .contextId("context-456")
    .status(TaskStatus.builder()
        .state(TaskState.RUNNING)
        .progress(0.5f)
        .statusText("Processing...")
        .build())
    .artifacts(List.of(
        Artifact.builder()
            .type("text")
            .content("Generated content")
            .build()
    ))
    .metadata(Map.of("key", "value"))
    .build();
```

### Message

Represents messages exchanged between agents.

```java
Message message = Message.builder()
    .messageId("msg-123")
    .taskId("task-123")
    .contextId("context-456")
    .role("user")
    .parts(List.of(
        TextPart.builder()
            .text("Hello, A2A!")
            .build(),
        FilePart.builder()
            .file(FileWithUri.builder()
                .uri("https://example.com/file.pdf")
                .mimeType("application/pdf")
                .build())
            .build()
    ))
    .metadata(Map.of("timestamp", System.currentTimeMillis()))
    .build();
```

### Part Types

#### TextPart

```java
TextPart textPart = TextPart.builder()
    .text("Hello, world!")
    .build();
```

#### FilePart

```java
// File with URI
FilePart filePart = FilePart.builder()
    .file(FileWithUri.builder()
        .uri("https://example.com/document.pdf")
        .mimeType("application/pdf")
        .size(1024L)
        .build())
    .build();

// File with bytes
FilePart filePartWithBytes = FilePart.builder()
    .file(FileWithBytes.builder()
        .name("document.pdf")
        .mimeType("application/pdf")
        .bytes(fileBytes)
        .build())
    .build();
```

#### DataPart

```java
DataPart dataPart = DataPart.builder()
    .data(Map.of(
        "type", "json",
        "content", Map.of("key", "value")
    ))
    .build();
```

## Server Components

### TaskManager

Interface for managing tasks in the A2A system.

```java
public interface TaskManager {
    RequestContext loadOrCreateContext(MessageSendParams params);
    Task getTask(String taskId);
    Mono<Task> applyTaskUpdate(Task task, List<TaskUpdate> taskUpdates);
    Mono<Task> applyTaskUpdate(Task task, TaskUpdate update);
    Mono<Task> applyStatusUpdate(Task task, TaskStatusUpdateEvent event);
    Mono<Task> applyArtifactUpdate(Task task, TaskArtifactUpdateEvent event);
    void registerTaskNotification(TaskPushNotificationConfig config);
    TaskPushNotificationConfig getTaskNotification(String taskId);
}
```

### AgentExecutor

Contains the core agent logic.

```java
@Component
public class MyAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // Your agent logic here
            
            // Publish status update
            TaskStatusUpdateEvent statusEvent = TaskStatusUpdateEvent.builder()
                .taskId(context.getTaskId())
                .status(TaskStatus.builder()
                    .state(TaskState.RUNNING)
                    .progress(0.5f)
                    .statusText("Processing...")
                    .build())
                .build();
            eventQueue.enqueueEvent(statusEvent);
            
            // Publish artifact
            TaskArtifactUpdateEvent artifactEvent = TaskArtifactUpdateEvent.builder()
                .taskId(context.getTaskId())
                .artifact(Artifact.builder()
                    .type("text")
                    .content("Generated response")
                    .build())
                .append(false)
                .build();
            eventQueue.enqueueEvent(artifactEvent);
        });
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.fromRunnable(() -> {
            // Handle task cancellation
        });
    }
}
```

### EventQueue

Event queue for A2A responses from agent.

```java
EventQueue eventQueue = new EventQueue();

// Enqueue events
eventQueue.enqueueEvent(statusUpdateEvent);
eventQueue.enqueueEvent(artifactUpdateEvent);

// Get as Flux
Flux<SendStreamingMessageResponse> flux = eventQueue.asFlux();

// Create child queue
EventQueue childQueue = eventQueue.tap();

// Close queue
eventQueue.close();
```

## Client Components

### CardResolver

Interface for resolving AgentCard information.

```java
@Component
public class HttpCardResolver implements CardResolver {
    
    @Override
    public AgentCard resolveCard(String agentIdentifier) {
        // Fetch agent card from well-known endpoint
        WebClient client = WebClient.create(agentIdentifier);
        return client.get()
            .uri("/.well-known/agent.json")
            .retrieve()
            .bodyToMono(AgentCard.class)
            .block();
    }
}
```

### Client Usage Examples

```java
// Create client
AgentCard targetAgent = AgentCard.builder()
    .url("https://target-agent.example.com")
    .build();

A2AClient client = new A2AClientImpl(targetAgent, new HttpCardResolver());

// Send message
MessageSendParams params = MessageSendParams.builder()
    .message(Message.builder()
        .role("user")
        .parts(List.of(TextPart.builder()
            .text("Hello!")
            .build()))
        .build())
    .build();

Task task = client.sendTask(params);

// Stream messages
Flux<SendStreamingMessageResponse> stream = client.sendTaskSubscribe(params);
stream.subscribe(
    event -> {
        if (event instanceof TaskStatusUpdateEvent) {
            // Handle status update
        } else if (event instanceof TaskArtifactUpdateEvent) {
            // Handle artifact update
        }
    },
    error -> System.err.println("Error: " + error),
    () -> System.out.println("Stream completed")
);

// Get task status
Task currentTask = client.getTask(TaskQueryParams.builder()
    .id(task.getId())
    .build());

// Cancel task
Task cancelledTask = client.cancelTask(TaskIdParams.builder()
    .id(task.getId())
    .build());
```

## JSON-RPC Support

### JSONRPCRequest

```java
JSONRPCRequest request = JSONRPCRequest.builder()
    .method("message/send")
    .params(MessageSendParams.builder()
        .message(message)
        .build())
    .id("request-123")
    .build();
```

### JSONRPCResponse

```java
JSONRPCResponse response = new JSONRPCResponse();
response.setId("request-123");
response.setResult(task);
// or
response.setError(new JSONRPCError(
    JSONRPCError.INVALID_PARAMS,
    "Invalid parameters",
    "Missing required field: message"
));
```

### Dispatcher

Routes JSON-RPC requests to appropriate handlers.

```java
@Component
public class DefaultDispatcher implements Dispatcher {
    
    @Override
    public JSONRPCResponse dispatch(JSONRPCRequest request) {
        // Handle synchronous requests
        switch (request.getMethod()) {
            case "message/send":
                // Handle message send
                break;
            case "tasks/get":
                // Handle task get
                break;
            // ... other methods
        }
    }
    
    @Override
    public Flux<JSONRPCResponse> dispatchStream(JSONRPCRequest request) {
        // Handle streaming requests
        switch (request.getMethod()) {
            case "message/stream":
                // Handle streaming message
                break;
            case "tasks/resubscribe":
                // Handle resubscribe
                break;
        }
    }
}
```

## Exception Handling

### A2AError

```java
try {
    Task task = client.sendTask(params);
} catch (A2AError e) {
    switch (e.getCode()) {
        case A2AError.INVALID_PARAMS:
            System.err.println("Invalid parameters: " + e.getMessage());
            break;
        case A2AError.TASK_NOT_FOUND:
            System.err.println("Task not found: " + e.getTaskId());
            break;
        case A2AError.AUTHENTICATION_ERROR:
            System.err.println("Authentication failed");
            break;
        default:
            System.err.println("Unknown error: " + e.getMessage());
            break;
    }
}
```

### Creating A2AError

```java
A2AError error = A2AError.builder()
    .message("Task execution failed")
    .code(A2AError.AGENT_EXECUTION_ERROR)
    .taskId("task-123")
    .data(Map.of("details", "Agent timeout"))
    .build();

throw error;
```

## Configuration Examples

### Spring Boot Configuration

```java
@Configuration
public class A2AConfig {
    
    @Bean
    public A2AServer a2aServer(TaskManager taskManager, AgentExecutor agentExecutor) {
        return new DefaultA2AServer(taskManager, agentExecutor);
    }
    
    @Bean
    public Dispatcher dispatcher(A2AServer a2aServer, ObjectMapper objectMapper) {
        return new DefaultDispatcher(a2aServer, objectMapper);
    }
    
    @Bean
    public TaskManager taskManager() {
        return new InMemoryTaskManager();
    }
    
    @Bean
    public AgentExecutor agentExecutor() {
        return new MyCustomAgentExecutor();
    }
}
```

### WebClient Configuration

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("a2a-pool")
            .maxConnections(100)
            .maxIdleTime(Duration.ofSeconds(30))
            .build();
            
        HttpClient httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
```

This API reference covers the main interfaces and classes in the A2A4J library. For more detailed examples and usage patterns, please refer to the main [README](README_EN.md) and the test code in the repository. 
