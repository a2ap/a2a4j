# A2A4J - Agent2Agent Java协议实现

[![Maven Central](https://img.shields.io/maven-central/v/io.github.a2ap/a2a4j-parent)](https://search.maven.org/artifact/io.github.a2ap/a2a4j-parent)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java Version](https://img.shields.io/badge/Java-17%2B-green.svg)](https://openjdk.org/projects/jdk/17/)

📖 **[English Documentation](README.md)**

A2A4J 是 Agent2Agent (A2A) 协议的全面 Java 实现，为独立 AI 智能体系统之间的通信和互操作性提供开放标准。基于 Spring Boot 集成和响应式编程支持构建，A2A4J 使智能体能够发现彼此的能力、协作完成任务，并安全地交换信息，而无需访问彼此的内部状态。

## 🚀 功能特性

- ✅ **完整的 A2A 协议支持** - Agent2Agent 规范的完整实现
- ✅ **JSON-RPC 2.0 通信** - 基于标准的请求/响应消息传递
- ✅ **Server-Sent Events 流式处理** - 实时任务更新和流式响应
- ✅ **任务生命周期管理** - 全面的任务状态管理和监控
- ✅ **Spring Boot 集成** - 与 Spring Boot 应用程序轻松集成
- ✅ **响应式编程支持** - 基于 Reactor 构建，可扩展的非阻塞操作
- ✅ **多种内容类型** - 支持文本、文件和结构化数据交换
- ⚪️ **推送通知配置** - 通过 webhooks 进行异步任务更新
- ⚪️ **Agent Card 发现机制** - 动态能力发现机制
- ⚪️ **企业级安全** - 身份验证和授权支持

## 📋 环境要求

- **Java 17+** - 运行应用程序所需
- **Maven 3.6+** - 构建工具

## 🏗️ 项目结构

```
a2a4j/
├── a2a4j-bom/                     # 依赖管理
├── a2a4j-core/                    # 核心 A2A 协议实现
├── a2a4j-spring-boot-starter/     # Spring Boot 自动配置
│   ├── a2a4j-server-spring-boot-starter/   # 服务器端启动器
│   └── a2a4j-client-spring-boot-starter/   # 客户端启动器
├── a2a4j-samples/                 # 示例实现
│   └── server-hello-world/        # Hello World 服务器示例
├── specification/                 # A2A 协议规范
├── tools/                        # 开发工具和配置
```

## 🚀 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/a2ap/a2a4j.git
cd a2a4j
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 运行 Hello World 示例

```bash
cd a2a4j-samples/server-hello-world
mvn spring-boot:run
```

服务器将在 `http://localhost:8089` 启动。

### 4. 测试智能体

#### 获取 Agent Card
```bash
curl http://localhost:8089/.well-known/agent.json
```

#### 发送消息
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
            "text": "你好，A2A！"
          }
        ]
      }
    },
    "id": "1"
  }'
```

#### 流式消息
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
            "text": "你好，流式 A2A！"
          }
        ]
      }
    },
    "id": "1"
  }'
```

## 📚 核心模块

### A2A4J 核心模块 (`a2a4j-core`)

核心模块提供基础的 A2A 协议实现：

- **模型**: Agent Cards、Tasks、Messages 和 Artifacts 的数据结构
- **服务器**: 服务器端 A2A 协议实现
- **客户端**: 客户端 A2A 协议实现
- **JSON-RPC**: JSON-RPC 2.0 请求/响应处理
- **异常处理**: 全面的错误管理

[📖 查看核心文档](a2a4j-core/README_CN.md)

### Spring Boot 启动器

#### 服务器启动器 (`a2a4j-server-spring-boot-starter`)
为 A2A 服务器提供 Spring Boot 自动配置，包括：
- 自动端点配置
- Agent Card 发布
- 任务管理
- SSE 流式处理支持

#### 客户端启动器 (`a2a4j-client-spring-boot-starter`)
为 A2A 客户端提供 Spring Boot 自动配置，包括：
- 智能体发现
- HTTP 客户端配置
- 响应式客户端支持

### 示例 (`a2a4j-samples`)

演示 A2A4J 使用方法的完整工作示例：
- **Hello World 服务器**: 基础 A2A 服务器实现
- **客户端示例**: 各种客户端使用模式

## 🔧 使用示例

### 创建 A2A 服务器

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
        // 你的智能体逻辑
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

### 创建 A2A 客户端

```java
// 创建 agent card
AgentCard agentCard = AgentCard.builder()
    .name("目标智能体")
    .url("http://localhost:8089")
    .version("1.0.0")
    .capabilities(AgentCapabilities.builder().streaming(true).build())
    .skills(List.of())
    .build();

// 创建客户端
A2AClient client = new A2AClientImpl(agentCard, new HttpCardResolver());

// 发送消息
TextPart textPart = new TextPart();
textPart.setText("来自 Java 客户端的问候！");

Message message = Message.builder()
    .role("user")
    .parts(List.of(textPart))
    .build();

MessageSendParams params = MessageSendParams.builder()
    .message(message)
    .build();

Task result = client.sendTask(params);
System.out.println("任务已创建: " + result.getId());
```

### 流式处理支持

```java
// 发送流式消息
Flux<SendStreamingMessageResponse> stream = client.sendTaskSubscribe(params);

stream.subscribe(
    event -> {
        if (event instanceof TaskStatusUpdateEvent) {
            TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
            System.out.println("状态: " + statusEvent.getStatus().getState());
        } else if (event instanceof TaskArtifactUpdateEvent) {
            TaskArtifactUpdateEvent artifactEvent = (TaskArtifactUpdateEvent) event;
            System.out.println("产物: " + artifactEvent.getArtifact().getType());
        }
    },
    error -> System.err.println("错误: " + error.getMessage()),
    () -> System.out.println("流处理完成")
);
```

## 📊 JSON-RPC 方法

### 核心方法
- `message/send` - 发送消息并创建任务
- `message/stream` - 发送消息并获取流式更新

### 任务管理
- `tasks/get` - 获取任务状态和详情
- `tasks/cancel` - 取消运行中的任务
- `tasks/resubscribe` - 重新订阅任务更新

### 推送通知
- `tasks/pushNotificationConfig/set` - 配置推送通知
- `tasks/pushNotificationConfig/get` - 获取通知配置


## 📖 文档

- [A2A 协议规范](specification/specification.md)
- [核心模块文档](a2a4j-core/README_CN.md)
- [API 参考](a2a4j-core/API_REFERENCE.md)
- [Hello World 示例](a2a4j-samples/server-hello-world/README_CN.md)

## 🤝 贡献

我们欢迎贡献！请查看我们的[贡献指南](CONTRIBUTING.md)了解详情。

1. Fork 仓库
2. 创建功能分支: `git checkout -b feature/my-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送到分支: `git push origin feature/my-feature`
5. 提交 Pull Request

## 📄 许可证

本项目根据 Apache License 2.0 许可 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🌟 支持

- **问题反馈**: [GitHub Issues](https://github.com/a2ap/a2a4j/issues)
- **讨论**: [GitHub Discussions](https://github.com/a2ap/a2a4j/discussions)
- **CI/CD**: [GitHub Actions](https://github.com/a2ap/a2a4j/actions)

## 🔗 参考来自

- [A2A 协议规范](https://google-a2a.github.io/A2A/specification/)
- [A2A 协议官网](https://google-a2a.github.io)

---

由 A2AP 社区用 ❤️ 构建
