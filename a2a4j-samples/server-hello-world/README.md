# A2A4J Server Hello World Sample

This is a complete A2A (Agent2Agent) protocol server implementation sample that demonstrates how to build a fully functional intelligent agent server using the A2A4J framework.

## Sample Features

- ✅ Complete A2A protocol implementation
- ✅ JSON-RPC 2.0 synchronous and streaming communication
- ✅ Automatic Agent Card discovery
- ✅ Multiple artifact type generation (text, code, summaries)
- ✅ Real-time status updates and progress tracking
- ✅ Server-Sent Events streaming responses
- ✅ CORS cross-origin support
- ✅ Detailed logging

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- curl or other HTTP client (for testing)

### Build Project

```bash
# Clone repository (if you haven't already)
git clone https://github.com/a2ap/a2a4j.git
cd a2a4j

# Build entire project
mvn clean install

# Navigate to sample directory
cd a2a4j-samples/server-hello-world
```

### Run Server

```bash
# Run with Maven
mvn spring-boot:run

# Or run compiled JAR
mvn clean package
java -jar target/server-hello-world-*.jar
```

The server will start at **http://localhost:8089**.

### Verify Server Status

```bash
# Check if server is running
curl -X GET http://localhost:8089/actuator/health

# Expected response
{"status":"UP"}
```

## A2A Protocol Endpoint Testing

### 1. Agent Card Discovery

Get agent capabilities and metadata information:

```bash
curl -X GET http://localhost:8089/.well-known/agent.json
```

**Expected Response Example:**
```json
{
  "name": "A2A Java Server",
  "description": "A sample A2A agent implemented in Java",
  "version": "1.0.0",
  "url": "http://localhost:8089",
  "capabilities": {
    "streaming": true,
    "pushNotifications": false,
    "stateTransitionHistory": true
  },
  "skills": [],
  "defaultInputModes": ["text"],
  "defaultOutputModes": ["text"]
}
```

### 2. Synchronous Message Sending

Send a message and wait for complete response:

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
            "text": "Please help me analyze basic machine learning concepts"
          }
        ]
      }
    },
    "id": "test-1"
  }'
```

**Expected Response Example:**
```json
{
  "jsonrpc": "2.0",
  "result": {
    "taskId": "task-abc123",
    "contextId": "ctx-456789",
    "status": {
      "state": "CREATED",
      "timestamp": "1703123456789"
    }
  },
  "id": "test-1"
}
```

### 3. Streaming Message Sending

Send a message and receive real-time updates:

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
            "text": "Generate a simple Java class example"
          }
        ]
      }
    },
    "id": "stream-1"
  }'
```

**Expected Streaming Response:**
```
event: task-update
data: {"jsonrpc":"2.0","result":{"taskId":"task-xyz","status":{"state":"WORKING","message":"Starting to process user request..."}},"id":"stream-1"}

event: task-update
data: {"jsonrpc":"2.0","result":{"taskId":"task-xyz","status":{"state":"WORKING","message":"Analyzing user input..."}},"id":"stream-1"}

event: task-update
data: {"jsonrpc":"2.0","result":{"taskId":"task-xyz","artifact":{"artifactId":"text-response","name":"AI Assistant Response","parts":[{"type":"text","text":"Here's my analysis of your question:\n\n"}]}},"id":"stream-1"}

event: task-update
data: {"jsonrpc":"2.0","result":{"taskId":"task-xyz","artifact":{"artifactId":"code-example","name":"Example Code","parts":[{"type":"text","text":"// Example code\npublic class ExampleService {\n..."}]}},"id":"stream-1"}

event: task-update
data: {"jsonrpc":"2.0","result":{"taskId":"task-xyz","status":{"state":"COMPLETED","message":"Task completed successfully!"}},"id":"stream-1"}
```

### 4. Task Status Query

Query the current status of a specific task:

```bash
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tasks/get",
    "params": {
      "taskId": "task-abc123"
    },
    "id": "get-task-1"
  }'
```

### 5. Task Cancellation

Cancel a running task:

```bash
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tasks/cancel",
    "params": {
      "taskId": "task-abc123"
    },
    "id": "cancel-1"
  }'
```

## Advanced Testing Scenarios

### Test Streaming Response Handling

Use more sophisticated tools to observe streaming responses:

```bash
# Use httpie to observe streaming responses
echo '{
  "jsonrpc": "2.0",
  "method": "message/stream",
  "params": {
    "message": {
      "role": "user",
      "parts": [{"type": "text", "kind": "text", "text": "Create a data structure example"}]
    }
  },
  "id": "advanced-1"
}' | http POST localhost:8089/a2a/server \
  Content-Type:application/json \
  Accept:text/event-stream
```

### Concurrent Request Testing

Test the server's ability to handle multiple concurrent requests:

```bash
# Start multiple concurrent requests
for i in {1..5}; do
  curl -X POST http://localhost:8089/a2a/server \
    -H "Content-Type: application/json" \
    -H "Accept: text/event-stream" \
    -d "{
      \"jsonrpc\": \"2.0\",
      \"method\": \"message/stream\",
      \"params\": {
        \"message\": {
          \"role\": \"user\",
          \"parts\": [{\"type\": \"text\", \"kind\": \"text\", \"text\": \"Concurrent request $i\"}]
        }
      },
      \"id\": \"concurrent-$i\"
    }" &
done

# Wait for all requests to complete
wait
```

### Error Handling Testing

Test various error scenarios:

```bash
# Test invalid JSON-RPC method
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "invalid/method",
    "params": {},
    "id": "error-1"
  }'

# Test invalid parameters
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "message/send",
    "params": {
      "invalidParam": "value"
    },
    "id": "error-2"
  }'
```

## Code Structure Explanation

### Core Components

- **`A2aServerApplication`**: Spring Boot main application class, configures CORS and application startup
- **`A2aServerController`**: REST controller implementing A2A protocol endpoints
- **`DemoAgentExecutor`**: Sample agent executor demonstrating various event types and artifact generation

### Execution Flow

1. **Task Creation**: Receives `message/send` or `message/stream` request
2. **Status Updates**: Sends "Starting", "Analyzing", "Generating" statuses
3. **Content Generation**: Sends text response in chunks
4. **Artifact Creation**: Generates code examples and task summaries
5. **Task Completion**: Sends final completion status and closes event queue

### Configuration Options

Configure in `application.yml`:

```yaml
server:
  port: 8089  # Modify server port

a2a:
  server:
    name: "My A2A Agent"  # Agent name
    description: "Custom description"  # Agent description
    capabilities:
      streaming: true  # Whether to support streaming responses
      pushNotifications: false  # Whether to support push notifications
```

## Troubleshooting

### Common Issues

1. **Port in use**: Modify `server.port` in `application.yml`
2. **Java version incompatible**: Ensure using Java 17 or higher
3. **Dependency issues**: Run `mvn clean install` to rebuild

### Debug Mode

Enable detailed logging:

```yaml
logging:
  level:
    io.github.a2ap: DEBUG
    org.springframework.web: DEBUG
```

### Performance Monitoring

Add Spring Boot Actuator endpoints:

```bash
# View application info
curl http://localhost:8089/actuator/info

# View health status
curl http://localhost:8089/actuator/health

# View metrics
curl http://localhost:8089/actuator/metrics
```

## Extension Development

### Custom Agent Executor

Create your own `AgentExecutor` implementation:

```java
@Component
public class MyCustomExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        // Implement custom logic
        return Mono.empty();
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        // Implement cancellation logic
        return Mono.empty();
    }
}
```

### Add Custom Endpoints

Extend controller to support more functionality:

```java
@RestController
public class CustomController {
    
    @GetMapping("/custom/endpoint")
    public ResponseEntity<String> customEndpoint() {
        return ResponseEntity.ok("Custom response");
    }
}
```

## Production Deployment

### Docker Deployment

```dockerfile
FROM openjdk:17-jre-slim
COPY target/server-hello-world-*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Configuration

```bash
# Production environment variables
export SERVER_PORT=8080
export A2A_SERVER_NAME="Production A2A Agent"
export LOGGING_LEVEL_ROOT=INFO
```

## References

- [A2A4J Core Documentation](../../a2a4j-core/README.md)
- [Spring Boot Starter Documentation](../../a2a4j-spring-boot-starter/a2a4j-server-spring-boot-starter/README.md)
- [A2A Protocol Specification](https://github.com/a2ap/protocol)
- [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)

## License

This project is licensed under the Apache License 2.0 - see [LICENSE](../../LICENSE) file for details. 
