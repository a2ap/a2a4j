# A2A4J Server Spring Boot Starter API Reference

This document provides specific API reference for the A2A4J Server Spring Boot Starter components.

## Auto-Configuration

### A2aServerAutoConfiguration

The main auto-configuration class that sets up all A2A server components.

```java
@Configuration
@EnableConfigurationProperties(A2aServerProperties.class)
@AutoConfigureAfter(value = {A2aServerProperties.class})
public class A2aServerAutoConfiguration {
    // Auto-configured beans
}
```

#### Auto-configured Beans

| Bean Type | Bean Name | Implementation | Description |
|-----------|-----------|----------------|-------------|
| `QueueManager` | `queueManager` | `InMemoryQueueManager` | Manages task event queues |
| `TaskStore` | `taskStore` | `InMemoryTaskStore` | Stores task data and history |
| `TaskManager` | `taskManager` | `InMemoryTaskManager` | Manages task lifecycle |
| `ObjectMapper` | `objectMapper` | `ObjectMapper` | JSON serialization/deserialization |
| `Dispatcher` | `dispatcher` | `DefaultDispatcher` | Routes JSON-RPC requests |
| `AgentExecutor` | `agentExecutor` | Anonymous no-op | Executes agent logic (override required) |
| `AgentCard` | `a2aServerSelfCard` | Built from properties | Agent metadata |
| `A2AServer` | `a2AServer` | `DefaultA2AServer` | Main server implementation |

All beans are created with `@ConditionalOnMissingBean`, allowing easy override.

## Configuration Properties

### A2aServerProperties

Configuration properties class for A2A server settings.

```java
@ConfigurationProperties(prefix = "a2a.server")
public class A2aServerProperties {
    // Configuration properties
}
```

#### Properties

```yaml
a2a:
  server:
    enabled: true                              # Enable/disable A2A server
    name: "My Agent"                           # Agent name
    description: "Agent description"           # Agent description  
    version: "1.0.0"                          # Agent version
    url: "https://agent.example.com"          # Agent base URL
    capabilities:
      streaming: true                          # Support streaming responses
      push-notifications: false               # Support push notifications
      state-transition-history: true         # Maintain state history
```

#### Configuration Validation

The starter validates configuration at startup:

- `name` - Required for agent identification
- `url` - Required for agent discovery
- `version` - Required for compatibility checking

## Customization Examples

### Override Default Components

#### Custom TaskStore

```java
@Configuration
public class CustomTaskStoreConfig {
    
    @Bean
    @Primary
    public TaskStore customTaskStore(DataSource dataSource) {
        return new JdbcTaskStore(dataSource);
    }
}
```

#### Custom QueueManager

```java
@Configuration  
public class CustomQueueConfig {
    
    @Bean
    @Primary
    public QueueManager redisQueueManager(RedisTemplate<String, Object> redisTemplate) {
        return new RedisQueueManager(redisTemplate);
    }
}
```

#### Custom AgentExecutor

```java
@Component
public class MyCustomAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // Your custom agent logic here
            processMessage(context.getMessage(), context.getTaskId(), eventQueue);
            eventQueue.close();
        });
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.fromRunnable(() -> {
            // Handle cancellation
            handleTaskCancellation(taskId);
        });
    }
    
    private void processMessage(Message message, String taskId, EventQueue eventQueue) {
        // Implementation details
    }
    
    private void handleTaskCancellation(String taskId) {
        // Implementation details  
    }
}
```

### Enhanced AgentCard

```java
@Bean
@Primary
public AgentCard enhancedAgentCard(A2aServerProperties properties) {
    return AgentCard.builder()
        .name(properties.getName())
        .description(properties.getDescription())
        .url(properties.getUrl())
        .version(properties.getVersion())
        .capabilities(AgentCapabilities.builder()
            .streaming(properties.getCapabilities().isStreaming())
            .pushNotifications(properties.getCapabilities().isPushNotifications())
            .stateTransitionHistory(properties.getCapabilities().isStateTransitionHistory())
            .build())
        .skills(List.of(
            AgentSkill.builder()
                .name("text-processing")
                .description("Advanced text processing capabilities")
                .inputModes(List.of("text", "markdown"))
                .outputModes(List.of("text", "html", "json"))
                .build(),
            AgentSkill.builder()
                .name("file-analysis")
                .description("File content analysis")
                .inputModes(List.of("file"))
                .outputModes(List.of("json", "text"))
                .build()
        ))
        .defaultInputModes(List.of("text", "file"))
        .defaultOutputModes(List.of("text", "json", "file"))
        .provider(AgentProvider.builder()
            .name("My Organization")
            .url("https://myorg.example.com")
            .build())
        .build();
}
```

## Spring Boot Integration

### Profiles

Configure different settings for different environments:

```yaml
# application.yml
spring:
  profiles:
    active: development

---
# Development profile  
spring:
  config:
    activate:
      on-profile: development
a2a:
  server:
    url: "http://localhost:8080"
    capabilities:
      streaming: true
      push-notifications: false

---
# Production profile
spring:
  config:
    activate:
      on-profile: production
a2a:
  server:
    url: "https://prod-agent.example.com"
    capabilities:
      streaming: true
      push-notifications: true
```

### Conditional Configuration

Use Spring Boot's conditional annotations:

```java
@Configuration
@ConditionalOnProperty(name = "a2a.server.enabled", havingValue = "true", matchIfMissing = true)
public class A2AEnabledConfig {
    
    @Bean
    @ConditionalOnProperty(name = "a2a.server.capabilities.push-notifications", havingValue = "true")
    public PushNotificationService pushNotificationService() {
        return new WebPushNotificationService();
    }
}
```

### Health Checks

Add custom health indicators:

```java
@Component
@ConditionalOnProperty(name = "management.health.a2a.enabled", havingValue = "true", matchIfMissing = true)
public class A2AHealthIndicator implements HealthIndicator {
    
    private final A2AServer a2aServer;
    
    public A2AHealthIndicator(A2AServer a2aServer) {
        this.a2aServer = a2aServer;
    }
    
    @Override
    public Health health() {
        try {
            AgentCard card = a2aServer.getSelfAgentCard();
            return Health.up()
                .withDetail("agent", card.getName())
                .withDetail("version", card.getVersion())
                .withDetail("capabilities", card.getCapabilities())
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### Metrics

Add custom metrics:

```java
@Component
public class A2AMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter taskCounter;
    private final Timer taskExecutionTimer;
    
    public A2AMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.taskCounter = Counter.builder("a2a.tasks.total")
            .description("Total number of tasks processed")
            .register(meterRegistry);
        this.taskExecutionTimer = Timer.builder("a2a.tasks.execution.time")
            .description("Task execution time")
            .register(meterRegistry);
    }
    
    public void incrementTaskCount() {
        taskCounter.increment();
    }
    
    public Timer.Sample startTaskTimer() {
        return Timer.start(meterRegistry);
    }
}
```

## Testing Support

### Test Configuration

```java
@TestConfiguration
public class A2ATestConfig {
    
    @Bean
    @Primary
    public AgentExecutor testAgentExecutor() {
        return new AgentExecutor() {
            @Override
            public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
                return Mono.fromRunnable(() -> {
                    // Test implementation
                    TaskArtifactUpdateEvent event = TaskArtifactUpdateEvent.builder()
                        .taskId(context.getTaskId())
                        .artifact(Artifact.builder()
                            .type("test")
                            .content("Test response")
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
        };
    }
}
```

### Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(A2ATestConfig.class)
class A2AServerStarterIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldAutoConfigureA2AServer() {
        ResponseEntity<AgentCard> response = restTemplate.getForEntity(
            "/.well-known/agent.json", AgentCard.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isNotNull();
    }
    
    @Test
    void shouldProcessMessageRequests() {
        JSONRPCRequest request = JSONRPCRequest.builder()
            .method("message/send")
            .params(MessageSendParams.builder()
                .message(Message.builder()
                    .role("user")
                    .parts(List.of(TextPart.builder().text("test").build()))
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

## Configuration Metadata

The starter provides IDE support through configuration metadata in `spring-configuration-metadata.json`:

```json
{
  "groups": [
    {
      "name": "a2a.server",
      "type": "io.github.a2ap.server.spring.auto.configuration.A2aServerProperties",
      "description": "Configuration properties for A2A Server."
    }
  ],
  "properties": [
    {
      "name": "a2a.server.name",
      "type": "java.lang.String",
      "description": "The name of the agent."
    }
  ]
}
```

This enables:
- Auto-completion in IDEs
- Property validation
- Documentation tooltips
- Configuration hints

## Advanced Customization

### Custom Auto-Configuration

Create your own auto-configuration that builds upon the starter:

```java
@Configuration
@AutoConfigureAfter(A2aServerAutoConfiguration.class)
@ConditionalOnProperty(name = "myapp.a2a.enhanced", havingValue = "true")
public class EnhancedA2AAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MessageValidator messageValidator() {
        return new DefaultMessageValidator();
    }
    
    @Bean
    @ConditionalOnMissingBean  
    public AgentSecurityManager securityManager() {
        return new JwtAgentSecurityManager();
    }
    
    @Bean
    public AgentExecutor enhancedAgentExecutor(
            AgentExecutor delegate,
            MessageValidator validator,
            AgentSecurityManager securityManager) {
        return new EnhancedAgentExecutor(delegate, validator, securityManager);
    }
}
```

### Custom Starter

Create your own starter that includes this one:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.a2ap</groupId>
        <artifactId>a2a4j-server-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
```

This reference covers all the key aspects of using and customizing the A2A4J Server Spring Boot Starter. For complete examples, see the main [README](README.md). 
