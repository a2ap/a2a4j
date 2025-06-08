# A2A4J - Agent2Agent Protocol for Java

[![Maven Central](https://img.shields.io/maven-central/v/io.github.a2ap/a2a4j-parent)](https://search.maven.org/artifact/io.github.a2ap/a2a4j-parent)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java Version](https://img.shields.io/badge/Java-17%2B-green.svg)](https://openjdk.org/projects/jdk/17/)

📖 **[中文文档](README_CN.md)**

A2A4J is a comprehensive Java implementation of the Agent2Agent (A2A) Protocol, providing an open standard for communication and interoperability between independent AI agent systems. Built with Spring Boot integration and reactive programming support, A2A4J enables agents to discover each other's capabilities, collaborate on tasks, and securely exchange information without needing access to each other's internal state.

## 🚀 Features

- ✅ **Complete A2A Protocol Support** - Full implementation of the Agent2Agent specification
- ✅ **JSON-RPC 2.0 Communication** - Standards-based request/response messaging
- ✅ **Server-Sent Events Streaming** - Real-time task updates and streaming responses
- ✅ **Task Lifecycle Management** - Comprehensive task state management and monitoring
- ✅ **Spring Boot Integration** - Easy integration with Spring Boot applications
- ✅ **Reactive Programming Support** - Built on Reactor for scalable, non-blocking operations
- ✅ **Multiple Content Types** - Support for text, files, and structured data exchange
- ⚪️ **Agent Card Discovery** - Dynamic capability discovery mechanism
- ⚪️ **Push Notification Configuration** - Asynchronous task updates via webhooks
- ⚪️ **Enterprise Security** - Authentication and authorization support

## 📋 Prerequisites

- **Java 17+** - Required for running the application
- **Maven 3.6+** - Build tool

## 🏗️ Project Structure

```
a2a4j/
├── a2a4j-bom/                     # A2A4J dependency management
├── a2a4j-core/                    # Core A2A protocol implementation
├── a2a4j-spring-boot-starter/     # Spring Boot auto-configuration
│   ├── a2a4j-server-spring-boot-starter/   # Server-side starter
│   └── a2a4j-client-spring-boot-starter/   # Client-side starter
├── a2a4j-samples/                 # Example implementations
│   └── server-hello-world/        # Hello World server example
├── specification/                 # A2A protocol specification
├── tools/                        # Development tools and configuration
```

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/a2ap/a2a4j.git
cd a2a4j
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Hello World Example

```bash
cd a2a4j-samples/server-hello-world
mvn spring-boot:run
```

The server will start at `http://localhost:8089`.

### 4. Test the Agent

#### Get Agent Card
```bash
curl http://localhost:8089/.well-known/agent.json
```

#### Send a Message
```bash
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "message/send",
    "params": {
      "message": {
        "role": "user",
        "parts": [
          {
            "type": "text",
            "kind": "text",
            "text": "Hello, A2A!"
          }
        ]
      }
    },
    "id": "1"
  }'
```

#### Stream Messages
```bash
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '{
    "jsonrpc": "2.0",
    "method": "message/stream",
    "params": {
      "message": {
        "role": "user",
        "parts": [
          {
            "type": "text",
            "kind": "text",
            "text": "Hello, streaming A2A!"
          }
        ]
      }
    },
    "id": "1"
  }'
```

## 📚 Core Modules

### A2A4J Core (`a2a4j-core`)

The core module provides the fundamental A2A protocol implementation:

- **Models**: Data structures for Agent Cards, Tasks, Messages, and Artifacts
- **Server**: Server-side A2A protocol implementation
- **Client**: Client-side A2A protocol implementation
- **JSON-RPC**: JSON-RPC 2.0 request/response handling
- **Exception Handling**: Comprehensive error management

[📖 View Core Documentation](a2a4j-core/README.md)

### Spring Boot Starters

#### Server Starter (`a2a4j-server-spring-boot-starter`)
Auto-configuration for A2A servers with Spring Boot, providing:
- Automatic endpoint configuration
- Agent Card publishing
- Task management
- SSE streaming support

#### Client Starter (`a2a4j-client-spring-boot-starter`)
Auto-configuration for A2A clients with Spring Boot, providing:
- Agent discovery
- HTTP client configuration
- Reactive client support

### Examples (`a2a4j-samples`)

Complete working examples demonstrating A2A4J usage:
- **Hello World Server**: Basic A2A server implementation
- **Client Examples**: Various client usage patterns

## 🔧 Usage Examples

### Creating an A2A Server

```java
@RestController
public class MyA2AController {

    @Autowired
    private A2AServer a2aServer;
    @Autowired
    private final Dispatcher a2aDispatch;

    @GetMapping(".well-known/agent.json")
    public ResponseEntity<AgentCard> getAgentCard() {
        AgentCard card = a2aServer.getSelfAgentCard();
        return ResponseEntity.ok(card);
    }

    @PostMapping(value = "/a2a/server", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONRPCResponse> handleA2ARequestTask(@RequestBody JSONRPCRequest request) {
        return ResponseEntity.ok(a2aDispatch.dispatch(request));
    }

    @PostMapping(value = "/a2a/server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<JSONRPCResponse>> handleA2ARequestTaskSubscribe(@RequestBody JSONRPCRequest request) {
        return a2aDispatch.dispatchStream(request).map(event -> ServerSentEvent.<JSONRPCResponse>builder()
                .data(event).event("task-update").build());
    }
}

@Component
public class MyAgentExecutor implements AgentExecutor {

    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        // your agent logic code
        TaskStatusUpdateEvent completedEvent = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .status(TaskStatus.builder()
                        .state(TaskState.COMPLETED)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .message(createAgentMessage("Task completed successfully! Hi you."))
                        .build())
                .isFinal(true)
                .metadata(Map.of(
                        "executionTime", "3000ms",
                        "artifactsGenerated", 4,
                        "success", true))
                .build();

        eventQueue.enqueueEvent(completedEvent);
        return Mono.empty();
    }
}
```

### Creating an A2A Client

```java
// Create agent card
AgentCard agentCard = AgentCard.builder()
    .name("Target Agent")
    .url("http://localhost:8089")
    .version("1.0.0")
    .capabilities(AgentCapabilities.builder().streaming(true).build())
    .skills(List.of())
    .build();

// Create client
A2AClient client = new A2AClientImpl(agentCard, new HttpCardResolver());

// Send message
TextPart textPart = new TextPart();
textPart.setText("Hello from Java client!");

Message message = Message.builder()
    .role("user")
    .parts(List.of(textPart))
    .build();

MessageSendParams params = MessageSendParams.builder()
    .message(message)
    .build();

Task result = client.sendTask(params);
System.out.println("Task created: " + result.getId());
```

### Streaming Support

```java
// Send message with streaming
Flux<SendStreamingMessageResponse> stream = client.sendTaskSubscribe(params);

stream.subscribe(
    event -> {
        if (event instanceof TaskStatusUpdateEvent) {
            TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
            System.out.println("Status: " + statusEvent.getStatus().getState());
        } else if (event instanceof TaskArtifactUpdateEvent) {
            TaskArtifactUpdateEvent artifactEvent = (TaskArtifactUpdateEvent) event;
            System.out.println("Artifact: " + artifactEvent.getArtifact().getType());
        }
    },
    error -> System.err.println("Error: " + error.getMessage()),
    () -> System.out.println("Stream completed")
);
```

## 📊 JSON-RPC Methods

### Core Methods
- `message/send` - Send a message and create a task
- `message/stream` - Send a message with streaming updates

### Task Management
- `tasks/get` - Get task status and details
- `tasks/cancel` - Cancel a running task
- `tasks/resubscribe` - Resubscribe to task updates

### Push Notifications
- `tasks/pushNotificationConfig/set` - Configure push notifications
- `tasks/pushNotificationConfig/get` - Get notification configuration


## 📖 Documentation

- [A2A Protocol Specification](specification/specification.md)
- [Core Module Documentation](a2a4j-core/README.md)
- [API Reference](a2a4j-core/API_REFERENCE.md)
- [Hello World Example](a2a4j-samples/server-hello-world/README.md)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🌟 Support

- **Issues**: [GitHub Issues](https://github.com/a2ap/a2a4j/issues)
- **Discussions**: [GitHub Discussions](https://github.com/a2ap/a2a4j/discussions)
- **CI/CD**: [GitHub Actions](https://github.com/a2ap/a2a4j/actions)

## 🔗 Refer Projects

- [A2A Protocol Specification](https://google-a2a.github.io/A2A/specification/)
- [A2A Protocol Website](https://google-a2a.github.io)

---

Built with ❤️ by the A2AP Community
