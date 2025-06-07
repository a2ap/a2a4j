# A2A4J Server Spring Boot Starter

This Spring Boot Starter provides automatic configuration for A2A (Agent2Agent) protocol servers, making it easy to integrate A2A functionality into Spring Boot applications.

## Features

- ✅ **Zero Configuration**: Auto-configures all A2A server components out of the box
- ✅ **Flexible Overrides**: Override any component with custom implementations
- ✅ **Property-based Configuration**: Configure agent metadata and capabilities via application properties
- ✅ **Production Ready**: Provides sensible defaults with options for customization
- ✅ **Spring Boot Integration**: Seamlessly integrates with Spring Boot's configuration system

## Quick Start

### 1. Add Dependency

Add the starter to your Spring Boot project:

```xml
<dependency>
    <groupId>io.github.a2ap</groupId>
    <artifactId>a2a4j-server-spring-boot-starter</artifactId>
    <version>${a2a4j.version}</version>
</dependency>
```

### 2. Configure Properties

Configure your agent in `application.yml`:

```yaml
a2a:
  server:
    name: "My A2A Agent"
    description: "A powerful A2A agent"
    version: "1.0.0"
    url: "https://my-agent.example.com"
    capabilities:
      streaming: true
      push-notifications: false
      state-transition-history: true
```

Or in `application.properties`:

```properties
a2a.server.name=My A2A Agent
a2a.server.description=A powerful A2A agent
a2a.server.version=1.0.0
a2a.server.url=https://my-agent.example.com
a2a.server.capabilities.streaming=true
a2a.server.capabilities.push-notifications=false
a2a.server.capabilities.state-transition-history=true
```

### 3. Implement Agent Logic

Create a custom `AgentExecutor` to define your agent's behavior:

```java
@Component
public class MyAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // Extract the message from context
            Message message = context.getMessage();
            
            // Process the message and generate response
            String responseText = "Hello! You said: " + 
                extractTextFromMessage(message);
            
            // Publish status update
            TaskStatusUpdateEvent statusEvent = TaskStatusUpdateEvent.builder()
                .taskId(context.getTaskId())
                .status(TaskStatus.builder()
                    .state(TaskState.RUNNING)
                    .statusText("Processing request")
                    .build())
                .build();
            eventQueue.enqueueEvent(statusEvent);
            
            // Publish response artifact
            TaskArtifactUpdateEvent artifactEvent = TaskArtifactUpdateEvent.builder()
                .taskId(context.getTaskId())
                .artifact(Artifact.builder()
                    .type("text")
                    .content(responseText)
                    .build())
                .append(false)
                .build();
            eventQueue.enqueueEvent(artifactEvent);
            
            // Mark task as completed
            TaskStatusUpdateEvent completeEvent = TaskStatusUpdateEvent.builder()
                .taskId(context.getTaskId())
                .status(TaskStatus.builder()
                    .state(TaskState.COMPLETED)
                    .statusText("Request completed")
                    .build())
                .build();
            eventQueue.enqueueEvent(completeEvent);
            
            // Close the event queue
            eventQueue.close();
        });
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.fromRunnable(() -> {
            // Handle task cancellation logic
            System.out.println("Cancelling task: " + taskId);
        });
    }
    
    private String extractTextFromMessage(Message message) {
        return message.getParts().stream()
            .filter(TextPart.class::isInstance)
            .map(TextPart.class::cast)
            .map(TextPart::getText)
            .collect(Collectors.joining(" "));
    }
}
```

### 4. Run Your Application

```java
@SpringBootApplication
public class MyAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAgentApplication.class, args);
    }
}
```

Your A2A server will be available at the configured URL with the following endpoints:

- `GET /.well-known/agent.json` - Agent Card discovery
- `POST /a2a/server` - JSON-RPC endpoint for synchronous and streaming requests

## Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `a2a.server.enabled` | `boolean` | `true` | Whether the A2A server is enabled |
| `a2a.server.name` | `string` | | The name of the agent |
| `a2a.server.description` | `string` | | A description of what the agent does |
| `a2a.server.version` | `string` | | The version of the agent |
| `a2a.server.url` | `string` | | The base URL where the agent can be reached |
| `a2a.server.capabilities.streaming` | `boolean` | `true` | Whether the agent supports streaming responses |
| `a2a.server.capabilities.push-notifications` | `boolean` | `false` | Whether the agent supports push notifications |
| `a2a.server.capabilities.state-transition-history` | `boolean` | `true` | Whether the agent maintains state transition history |

## Auto-configured Components

The starter automatically configures the following beans (all can be overridden):

### Core Components

- **`QueueManager`**: Manages event queues for tasks (default: `InMemoryQueueManager`)
- **`TaskStore`**: Stores task data and history (default: `InMemoryTaskStore`)
- **`TaskManager`**: Manages task lifecycle (default: `InMemoryTaskManager`)
- **`AgentExecutor`**: Executes agent logic (default: no-op implementation)
- **`Dispatcher`**: Routes JSON-RPC requests (default: `DefaultDispatcher`)
- **`A2AServer`**: Main server implementation (default: `DefaultA2AServer`)

### Supporting Components

- **`ObjectMapper`**: For JSON serialization/deserialization
- **`AgentCard`**: Agent metadata based on configuration properties

## Customization

### Override Components

You can override any auto-configured component by providing your own bean:

```java
@Configuration
public class CustomA2AConfig {
    
    @Bean
    public TaskStore taskStore() {
        // Return your custom implementation
        return new DatabaseTaskStore();
    }
    
    @Bean
    public QueueManager queueManager() {
        // Return your custom implementation
        return new RedisQueueManager();
    }
}
```

### Custom Agent Card

Provide a custom agent card with additional metadata:

```java
@Bean
@Primary
public AgentCard customAgentCard() {
    return AgentCard.builder()
        .name("Advanced Agent")
        .description("An advanced A2A agent with custom capabilities")
        .url("https://advanced-agent.example.com")
        .version("2.0.0")
        .capabilities(AgentCapabilities.builder()
            .streaming(true)
            .pushNotifications(true)
            .stateTransitionHistory(true)
            .build())
        .skills(List.of(
            AgentSkill.builder()
                .name("text-processing")
                .description("Process and analyze text content")
                .build(),
            AgentSkill.builder()
                .name("data-analysis")
                .description("Analyze data and generate insights")
                .build()
        ))
        .defaultInputModes(List.of("text", "file", "json"))
        .defaultOutputModes(List.of("text", "file", "json", "image"))
        .build();
}
```

## Advanced Usage

### Production Configuration

For production deployments, consider implementing persistent storage:

```java
@Configuration
@Profile("production")
public class ProductionConfig {
    
    @Bean
    public TaskStore taskStore(DataSource dataSource) {
        return new JdbcTaskStore(dataSource);
    }
    
    @Bean
    public QueueManager queueManager(RedisTemplate<String, Object> redisTemplate) {
        return new RedisQueueManager(redisTemplate);
    }
}
```

### Custom Endpoints

Add custom endpoints to your agent:

```java
@RestController
@RequestMapping("/api/agent")
public class AgentController {
    
    private final A2AServer a2aServer;
    
    public AgentController(A2AServer a2aServer) {
        this.a2aServer = a2aServer;
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "running",
            "agent", a2aServer.getSelfAgentCard().getName()
        ));
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String taskId) {
        Task task = a2aServer.getTask(taskId);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
}
```

### Security Configuration

Add security to your agent:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/.well-known/**").permitAll()
                .requestMatchers("/a2a/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        
        return http.build();
    }
}
```

## Testing

### Unit Testing

Test your agent executor:

```java
@ExtendWith(MockitoExtension.class)
class MyAgentExecutorTest {
    
    @Mock
    private EventQueue eventQueue;
    
    @InjectMocks
    private MyAgentExecutor agentExecutor;
    
    @Test
    void testExecute() {
        // Given
        RequestContext context = RequestContext.builder()
            .taskId("test-task")
            .message(Message.builder()
                .role("user")
                .parts(List.of(TextPart.builder()
                    .text("Hello, agent!")
                    .build()))
                .build())
            .build();
        
        // When
        StepVerifier.create(agentExecutor.execute(context, eventQueue))
            .verifyComplete();
        
        // Then
        verify(eventQueue, atLeastOnce()).enqueueEvent(any());
        verify(eventQueue).close();
    }
}
```

### Integration Testing

Test the complete A2A server:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class A2AServerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testAgentCard() {
        ResponseEntity<AgentCard> response = restTemplate.getForEntity(
            "/.well-known/agent.json", AgentCard.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("My A2A Agent");
    }
    
    @Test
    void testMessageSend() {
        JSONRPCRequest request = JSONRPCRequest.builder()
            .method("message/send")
            .params(MessageSendParams.builder()
                .message(Message.builder()
                    .role("user")
                    .parts(List.of(TextPart.builder()
                        .text("Hello!")
                        .build()))
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

## Examples

### Echo Agent

A simple echo agent that returns the input message:

```java
@Component
public class EchoAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            String inputText = extractTextFromMessage(context.getMessage());
            
            TaskArtifactUpdateEvent event = TaskArtifactUpdateEvent.builder()
                .taskId(context.getTaskId())
                .artifact(Artifact.builder()
                    .type("text")
                    .content("Echo: " + inputText)
                    .build())
                .build();
            
            eventQueue.enqueueEvent(event);
            eventQueue.close();
        });
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.empty();
    }
    
    private String extractTextFromMessage(Message message) {
        return message.getParts().stream()
            .filter(TextPart.class::isInstance)
            .map(TextPart.class::cast)
            .map(TextPart::getText)
            .collect(Collectors.joining(" "));
    }
}
```

### File Processing Agent

An agent that processes uploaded files:

```java
@Component
public class FileProcessingAgent implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // Extract file parts
            List<FilePart> fileParts = context.getMessage().getParts().stream()
                .filter(FilePart.class::isInstance)
                .map(FilePart.class::cast)
                .toList();
            
            if (fileParts.isEmpty()) {
                publishError(context.getTaskId(), "No files provided", eventQueue);
                return;
            }
            
            // Process each file
            for (FilePart filePart : fileParts) {
                processFile(context.getTaskId(), filePart, eventQueue);
            }
            
            eventQueue.close();
        });
    }
    
    private void processFile(String taskId, FilePart filePart, EventQueue eventQueue) {
        String fileName = filePart.getFile().getName();
        String mimeType = filePart.getFile().getMimeType();
        
        TaskArtifactUpdateEvent event = TaskArtifactUpdateEvent.builder()
            .taskId(taskId)
            .artifact(Artifact.builder()
                .type("analysis")
                .content(Map.of(
                    "fileName", fileName,
                    "mimeType", mimeType,
                    "status", "processed"
                ))
                .build())
            .build();
        
        eventQueue.enqueueEvent(event);
    }
    
    private void publishError(String taskId, String error, EventQueue eventQueue) {
        TaskStatusUpdateEvent event = TaskStatusUpdateEvent.builder()
            .taskId(taskId)
            .status(TaskStatus.builder()
                .state(TaskState.FAILED)
                .statusText(error)
                .build())
            .build();
        
        eventQueue.enqueueEvent(event);
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.empty();
    }
}
```

## Troubleshooting

### Common Issues

1. **Agent card not accessible**: Ensure the `url` property is correctly configured
2. **Custom executor not used**: Make sure your executor is annotated with `@Component`
3. **Configuration not applied**: Check that properties are under the `a2a.server` prefix

### Debug Logging

Enable debug logging for A2A components:

```yaml
logging:
  level:
    io.github.a2ap: DEBUG
    org.springframework.boot.autoconfigure: DEBUG
```

## Contributing

Contributions are welcome! Please read the [Contributing Guide](../../CONTRIBUTING.md) for details.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../../LICENSE) file for details. 
