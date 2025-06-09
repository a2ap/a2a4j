# A2A4J Server Hello World 样例

这是一个完整的 A2A (Agent2Agent) 协议服务器实现样例，展示了如何使用 A2A4J 框架构建一个功能完整的智能代理服务器。

## 样例特性

- ✅ 完整的 A2A 协议实现
- ✅ JSON-RPC 2.0 同步和流式通信
- ✅ Agent Card 自动发现
- ✅ 多种工件类型生成（文本、代码、摘要）
- ✅ 实时状态更新和进度跟踪
- ✅ Server-Sent Events 流式响应
- ✅ CORS 跨域支持
- ✅ 详细的日志记录

## 快速开始

### 前置要求

- Java 17 或更高版本
- Maven 3.6 或更高版本
- curl 或其他 HTTP 客户端（用于测试）

### 构建项目

```bash
# 克隆仓库（如果还没有）
git clone https://github.com/a2ap/a2a4j.git
cd a2a4j

# 构建整个项目
mvn clean install

# 进入样例目录
cd a2a4j-samples/server-hello-world
```

### 运行服务器

```bash
# 使用 Maven 运行
mvn spring-boot:run

# 或者运行编译后的 JAR
mvn clean package
java -jar target/server-hello-world-*.jar
```

服务器将在 **http://localhost:8089** 启动。

### 验证服务器状态

```bash
# 检查服务器是否运行
curl -X GET http://localhost:8089/actuator/health

# 预期响应
{"status":"UP"}
```

## A2A 协议端点测试

### 1. Agent Card 发现

获取智能体的能力和元数据信息：

```bash
curl -X GET http://localhost:8089/.well-known/agent.json
```

**预期响应示例：**
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

### 2. 同步消息发送

发送消息并等待完整响应：

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
            "text": "请帮我分析一下机器学习的基本概念"
          }
        ]
      }
    },
    "id": "test-1"
  }'
```

**预期响应示例：**
```json
{
	"jsonrpc": "2.0",
	"result": {
		"id": "5c694c92-3860-4a4b-9a21-ab7362e84b0b",
		"contextId": "15334c48-6c91-4536-9cab-43d9f66bcf00",
		"status": {
			"state": "completed",
			"message": {
				"role": "agent",
				"parts": [{
					"type": "text",
					"kind": "text",
					"text": "Task completed successfully! I have generated a detailed response and example code for you."
				}]
			},
			"timestamp": "1749283570371"
		},
		"artifacts": [{
			"artifactId": "text-response",
			"name": "AI Assistant Response",
			"description": "AI generated text reply",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "Here's my analysis of your question:\n\n"
			}, {
				"type": "text",
				"kind": "text",
				"text": "Based on the information provided, I suggest the following approach:\n"
			}, {
				"type": "text",
				"kind": "text",
				"text": "\n\nIf you have any questions, please feel free to ask!"
			}],
			"metadata": {
				"contentType": "text/plain",
				"chunkIndex": 1749283568621,
				"encoding": "utf-8"
			}
		}, {
			"artifactId": "code-example",
			"name": "Example Code",
			"description": "Example Java code generated based on requirements",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "// Example code\npublic class ExampleService {\n\n    public String processRequest(String input) {\n        if (input == null || input.trim().isEmpty()) {\n            return \"Input cannot be empty\";\n        }\n\n        // Process input\n        String processed = input.trim().toLowerCase();\n        return \"Processed result: \" + processed;\n    }\n}\n"
			}],
			"metadata": {
				"contentType": "text/x-java-source",
				"language": "java",
				"filename": "ExampleService.java"
			}
		}, {
			"artifactId": "task-summary",
			"name": "Task Summary",
			"description": "Summary report of this task execution",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "## Task Execution Summary\n\n? User request analysis completed\n? Text response generated\n? Example code provided\n? Task executed successfully\n\nTotal execution time: ~3 seconds\nGenerated content: Text response + Code example"
			}],
			"metadata": {
				"reportType": "summ  mary",
				"contentType": "text/markdown"
			}
		}],
		"history": [{
			"role": "agent",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "Starting to process user request..."
			}]
		}, {
			"role": "agent",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "Analyzing user input..."
			}]
		}, {
			"role": "agent",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "Generating response..."
			}]
		}, {
			"role": "agent",
			"parts": [{
				"type": "text",
				"kind": "text",
				"text": "Task completed successfully! I have generated a detailed response and example code for you."
			}]
		}]
	},
	"id": "test-1"
}
```

### 3. 流式消息发送

发送消息并接收实时更新：

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
            "text": "生成一个简单的 Java 类示例"
          }
        ]
      }
    },
    "id": "stream-1"
  }'
```

**预期流式响应：**

```text
event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"status-update","status":{"state":"working","message":{"role":"agent","parts":[{"type":"text","kind":"text","text":"Starting to process user request..."}]},"timestamp":"1749283741336"},"final":false,"metadata":null},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"status-update","status":{"state":"working","message":{"role":"agent","parts":[{"type":"text","kind":"text","text":"Analyzing user input..."}]},"timestamp":"1749283741340"},"final":false,"metadata":null},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"status-update","status":{"state":"working","message":{"role":"agent","parts":[{"type":"text","kind":"text","text":"Generating response..."}]},"timestamp":"1749283741340"},"final":false,"metadata":null},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"artifact-update","artifact":{"artifactId":"text-response","name":"AI Assistant Response","description":"AI generated text reply","parts":[{"type":"text","kind":"text","text":"Here's my analysis of your question:\n\n"}],"metadata":{"contentType":"text/plain","chunkIndex":1749283739591,"encoding":"utf-8"}},"final":false,"append":false,"lastChunk":false,"metadata":{"artifactType":"text"}},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"artifact-update","artifact":{"artifactId":"text-response","name":"AI Assistant Response","description":"AI generated text reply","parts":[{"type":"text","kind":"text","text":"Based on the information provided, I suggest the following approach:\n"}],"metadata":{"contentType":"text/plain","chunkIndex":1749283739893,"encoding":"utf-8"}},"final":false,"append":true,"lastChunk":false,"metadata":{"artifactType":"text"}},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"artifact-update","artifact":{"artifactId":"code-example","name":"Example Code","description":"Example Java code generated based on requirements","parts":[{"type":"text","kind":"text","text":"// Example code\npublic class ExampleService {\n\n    public String processRequest(String input) {\n        if (input == null || input.trim().isEmpty()) {\n            return \"Input cannot be empty\";\n        }\n\n        // Process input\n        String processed = input.trim().toLowerCase();\n        return \"Processed result: \" + processed;\n    }\n}\n"}],"metadata":{"contentType":"text/x-java-source","language":"java","filename":"ExampleService.java"}},"final":false,"append":false,"lastChunk":true,"metadata":{"artifactType":"code"}},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"artifact-update","artifact":{"artifactId":"text-response","name":"AI Assistant Response","description":"AI generated text reply","parts":[{"type":"text","kind":"text","text":"\n\nIf you have any questions, please feel free to ask!"}],"metadata":{"contentType":"text/plain","chunkIndex":1749283740812,"encoding":"utf-8"}},"final":false,"append":true,"lastChunk":true,"metadata":{"artifactType":"text"}},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"artifact-update","artifact":{"artifactId":"task-summary","name":"Task Summary","description":"Summary report of this task execution","parts":[{"type":"text","kind":"text","text":"## Task Execution Summary\n\n? User request analysis completed\n? Text response generated\n? Exampl  le code provided\n? Task executed successfully\n\nTotal execution time: ~3 seconds\nGenerated content: Text response + Code example"}],"metadata":{"reportType":"summary","contentType":"text/markdown"}},"final"::false,"append":false,"lastChunk":true,"metadata":{"artifactType":"summary"}},"id":"stream-1"}

event:task-update
data:{"jsonrpc":"2.0","result":{"taskId":"43dc70c4-149a-44de-b96f-a28687895da3","contextId":"67318bf5-cbee-491f-8ac7-62164b57bf9e","kind":"status-update","status":{"state":"completed","message":{"role":"agent","parts":[{"type":"text","kind":"text","text":"Task completed successfully! I have generated a detailed response and example code for you."}]},"timestamp":"1749283741347"},"final":true,"metadata":{"artifactsGenerated":4,"executionTime":"3000ms","success":true}},"id":"stream-1"}
```

### 4. 任务状态查询

查询指定任务的当前状态：

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

### 5. 任务取消

取消正在运行的任务：

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

## 高级测试场景

### 测试流式响应处理

使用更复杂的工具来观察流式响应：

```bash
# 使用 httpie 观察流式响应
echo '{
  "jsonrpc": "2.0",
  "method": "message/stream",
  "params": {
    "message": {
      "role": "user",
      "parts": [{"type": "text", "kind": "text", "text": "创建一个数据结构示例"}]
    }
  },
  "id": "advanced-1"
}' | http POST localhost:8089/a2a/server \
  Content-Type:application/json \
  Accept:text/event-stream
```

### 并发请求测试

测试服务器处理多个并发请求的能力：

```bash
# 启动多个并发请求
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
          \"parts\": [{\"type\": \"text\", \"kind\": \"text\", \"text\": \"并发请求 $i\"}]
        }
      },
      \"id\": \"concurrent-$i\"
    }" &
done

# 等待所有请求完成
wait
```

### 错误处理测试

测试各种错误场景：

```bash
# 测试无效的 JSON-RPC 方法
curl -X POST http://localhost:8089/a2a/server \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "invalid/method",
    "params": {},
    "id": "error-1"
  }'

# 测试无效的参数
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

## 代码结构说明

### 核心组件

- **`A2AServerApplication`**: Spring Boot 主应用类，配置 CORS 和应用启动
- **`A2AServerController`**: REST 控制器，实现 A2A 协议端点
- **`DemoAgentExecutor`**: 示例智能体执行器，展示各种事件类型和工件生成

### 执行流程

1. **任务创建**: 收到 `message/send` 或 `message/stream` 请求
2. **状态更新**: 发送 "Starting", "Analyzing", "Generating" 状态
3. **内容生成**: 分块发送文本响应
4. **工件创建**: 生成代码示例和任务摘要
5. **任务完成**: 发送最终完成状态并关闭事件队列

### 配置选项

在 `application.yml` 中可以配置：

```yaml
server:
  port: 8089  # 修改服务器端口

a2a:
  server:
    name: "我的 A2A 智能体"  # 智能体名称
    description: "自定义描述"  # 智能体描述
    capabilities:
      streaming: true  # 是否支持流式响应
      pushNotifications: false  # 是否支持推送通知
```

## 故障排除

### 常见问题

1. **端口被占用**: 修改 `application.yml` 中的 `server.port`
2. **Java 版本不兼容**: 确保使用 Java 17 或更高版本
3. **依赖问题**: 运行 `mvn clean install` 重新构建

### 调试模式

启用详细日志记录：

```yaml
logging:
  level:
    io.github.a2ap: DEBUG
    org.springframework.web: DEBUG
```

### 性能监控

添加 Spring Boot Actuator 端点（需要在配置文件中开启，默认开始 health 断点）：

```bash
# 查看应用信息
curl http://localhost:8089/actuator/info

# 查看健康状态
curl http://localhost:8089/actuator/health

# 查看度量指标
curl http://localhost:8089/actuator/metrics
```

## 扩展开发

### 自定义智能体执行器

创建自己的 `AgentExecutor` 实现：

```java
@Component
public class MyCustomExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        // 实现自定义逻辑
        return Mono.empty();
    }
    
    @Override
    public Mono<Void> cancel(String taskId) {
        // 实现取消逻辑
        return Mono.empty();
    }
}
```

### 添加自定义端点

扩展控制器以支持更多功能：

```java
@RestController
public class CustomController {
    
    @GetMapping("/custom/endpoint")
    public ResponseEntity<String> customEndpoint() {
        return ResponseEntity.ok("Custom response");
    }
}
```

## 生产部署

### Docker 部署

```dockerfile
FROM openjdk:17-jre-slim
COPY target/server-hello-world-*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境配置

```bash
# 生产环境变量
export SERVER_PORT=8089
export A2A_SERVER_NAME="Production A2A Agent"
export LOGGING_LEVEL_ROOT=INFO
```

## 参考资料

- [A2A4J 核心文档](../../a2a4j-core/README.md)
- [Spring Boot Starter 文档](../../a2a4j-spring-boot-starter/a2a4j-server-spring-boot-starter/README.md)
- [A2A 协议规范](https://github.com/a2ap/protocol)
- [JSON-RPC 2.0 规范](https://www.jsonrpc.org/specification)

## 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](../../LICENSE) 文件。  
