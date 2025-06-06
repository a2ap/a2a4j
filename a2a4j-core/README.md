# A2A4J - Agent2Agent Protocol Java Implementation

A2A4J is a Java implementation of the Agent2Agent (A2A) protocol, providing comprehensive server-side and client-side support for agent communication.

## Features

- ✅ Complete A2A protocol support
- ✅ JSON-RPC 2.0 communication
- ✅ Server-Sent Events streaming
- ✅ Agent Card discovery mechanism
- ✅ Task lifecycle management
- ✅ Push notification configuration
- ✅ Spring Boot integration
- ✅ Reactive programming support

## Quick Start

### Prerequisites

- Java 17+
- Spring Boot 3.2+
- Maven 3.6+

### Running the Server

```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8089`.

### Accessing Agent Card

```bash
curl http://localhost:8089/.well-known/agent.json
```

### Sending a Message

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

### Streaming Messages

```bash
curl -X POST http://localhost:8089/a2a/server/stream \
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

## Supported JSON-RPC Methods

### Core Methods

- `message/send` - Send a message and create a task
- `message/stream` - Send a message and subscribe to streaming updates

### Task Management

- `tasks/get` - Get task status
- `tasks/cancel` - Cancel a task
- `tasks/resubscribe` - Resubscribe to task updates

### Push Notifications

- `tasks/pushNotificationConfig/set` - Set push notification configuration
- `tasks/pushNotificationConfig/get` - Get push notification configuration

## Client Usage Example

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

### Streaming Example

```java
// Send message with streaming
Flux<SendStreamingMessageResponse> stream = client.sendTaskSubscribe(params);

stream.subscribe(
    event -> {
        if (event instanceof TaskStatusUpdateEvent) {
            TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
            System.out.println("Status update: " + statusEvent.getStatus().getState());
        } else if (event instanceof TaskArtifactUpdateEvent) {
            TaskArtifactUpdateEvent artifactEvent = (TaskArtifactUpdateEvent) event;
            System.out.println("Artifact update: " + artifactEvent.getArtifact().getType());
        }
    },
    error -> System.err.println("Error: " + error.getMessage()),
    () -> System.out.println("Stream completed")
);
```

## Architecture

```
a2a4j-core/
├── src/main/java/io/github/a2ap/core/
│   ├── model/          # Data models and DTOs
│   │   ├── AgentCard.java
│   │   ├── Task.java
│   │   ├── Message.java
│   │   ├── Part.java (TextPart, FilePart, DataPart)
│   │   ├── Artifact.java
│   │   └── TaskStatus.java
│   ├── server/         # Server-side implementation
│   │   ├── A2AServer.java
│   │   ├── Dispatcher.java
│   │   ├── TaskManager.java
│   │   ├── AgentExecutor.java
│   │   └── impl/       # Implementation classes
│   ├── client/         # Client-side implementation
│   │   ├── A2AClient.java
│   │   ├── CardResolver.java
│   │   └── impl/       # Implementation classes
│   ├── jsonrpc/        # JSON-RPC support
│   │   ├── JSONRPCRequest.java
│   │   ├── JSONRPCResponse.java
│   │   └── JSONRPCError.java
│   └── exception/      # Exception handling
│       └── A2AError.java
└── src/test/java/      # Test code
```

### Core Components

#### Server Components

- **A2AServer**: Main server interface defining core functionality
- **Dispatcher**: Routes JSON-RPC requests to appropriate handlers
- **TaskManager**: Manages task lifecycle and state
- **AgentExecutor**: Contains the core agent logic
- **EventQueue**: Handles event publishing and streaming

#### Client Components

- **A2AClient**: Main client interface for server communication
- **CardResolver**: Resolves agent cards from servers
- **HttpCardResolver**: HTTP-based implementation of card resolution

#### Models

- **AgentCard**: Represents agent capabilities and metadata
- **Task**: Represents a task and its execution state
- **Message**: Represents messages exchanged between agents
- **Part**: Base class for message parts (text, file, data)
- **Artifact**: Represents task artifacts and outputs

## Configuration

### Application Properties

```properties
# Server configuration
server.port=8089

# Agent card configuration
a2a.agent.name=My A2A Agent
a2a.agent.version=1.0.0
a2a.agent.description=A sample A2A agent
a2a.agent.documentation-url=https://example.com/docs

# Capabilities
a2a.agent.capabilities.streaming=true
a2a.agent.capabilities.push-notifications=true

# Default input/output modes
a2a.agent.default-input-modes=text,file
a2a.agent.default-output-modes=text,file,json
```

### Custom Agent Executor

```java
@Component
public class MyAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // Your agent logic here
            TaskStatusUpdateEvent statusEvent = TaskStatusUpdateEvent.builder()
                .taskId(context.getTaskId())
                .status(TaskStatus.builder()
                    .state(TaskState.RUNNING)
                    .build())
                .build();
            
            eventQueue.publish(statusEvent);
            
            // Process the message and generate response
            // Publish artifacts or final status
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

## Testing

```bash
# Run all tests
mvn test

# Run with specific profile
mvn test -Pintegration-tests

# Run with coverage
mvn test jacoco:report
```

### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class A2AIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testMessageSend() {
        JSONRPCRequest request = JSONRPCRequest.builder()
            .method("message/send")
            .params(MessageSendParams.builder()
                .message(Message.builder()
                    .role("user")
                    .parts(List.of(new TextPart("Hello")))
                    .build())
                .build())
            .id("test-1")
            .build();
            
        ResponseEntity<JSONRPCResponse> response = restTemplate.postForEntity(
            "/a2a/server", request, JSONRPCResponse.class);
            
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isNotNull();
    }
}
```

## Security

### Authentication

The A2A protocol supports various authentication schemes:

```java
AgentAuthentication auth = AgentAuthentication.builder()
    .type("bearer")
    .bearerToken("your-token-here")
    .build();

AgentCard agentCard = AgentCard.builder()
    .name("Secure Agent")
    .url("https://secure-agent.example.com")
    .authentication(auth)
    .build();
```

### Security Schemes

```java
Map<String, SecurityScheme> securitySchemes = Map.of(
    "bearer", SecurityScheme.builder()
        .type("http")
        .scheme("bearer")
        .bearerFormat("JWT")
        .build()
);

AgentCard agentCard = AgentCard.builder()
    .securitySchemes(securitySchemes)
    .security(List.of(Map.of("bearer", List.of())))
    .build();
```

## Error Handling

The library provides comprehensive error handling:

```java
try {
    Task task = client.sendTask(params);
} catch (A2AError e) {
    switch (e.getCode()) {
        case A2AError.INVALID_PARAMS:
            // Handle invalid parameters
            break;
        case A2AError.TASK_NOT_FOUND:
            // Handle task not found
            break;
        default:
            // Handle other errors
            break;
    }
}
```

## Performance Considerations

### Connection Pooling

```java
// Configure HTTP client for better performance
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
```

### Streaming Optimization

```java
// Configure streaming buffer sizes
@ConfigurationProperties("a2a.streaming")
public class StreamingConfig {
    private int bufferSize = 8192;
    private Duration flushInterval = Duration.ofMillis(100);
    
    // getters and setters
}
```

## Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

### Development Setup

1. Clone the repository
2. Install Java 17+
3. Run `mvn clean install`
4. Import into your IDE

### Code Style

We use [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Please format your code accordingly.

## Roadmap

- [ ] WebSocket support for real-time communication
- [ ] GraphQL integration
- [ ] Kubernetes operator for agent deployment
- [ ] Performance monitoring and metrics
- [ ] Enhanced security features
- [ ] Multi-tenant support

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- The A2A protocol specification contributors
- Spring Boot and Project Reactor teams
- Jackson JSON processing library
- All contributors to this project

## Support

- [Documentation](https://github.com/a2ap/a2a4j/wiki)
- [Issue Tracker](https://github.com/a2ap/a2a4j/issues)
- [Discussions](https://github.com/a2ap/a2a4j/discussions)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/a2a4j) 
