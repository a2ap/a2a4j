# A2A4J - Agent2Agent Protocol Java Implementation

A2A4J 是 Agent2Agent (A2A) 协议的 Java 实现，提供了完整的服务器端和客户端支持。

📖 **[English Documentation](README_EN.md)**

## 功能特性

- ✅ 完整的 A2A 协议支持
- ✅ JSON-RPC 2.0 通信
- ✅ Server-Sent Events 流式处理
- ✅ Agent Card 发现机制
- ✅ 任务生命周期管理
- ✅ 推送通知配置
- ✅ Spring Boot 集成
- ✅ 响应式编程支持

## 快速开始

### 环境要求

- Java 17+
- Spring Boot 3.2+
- Maven 3.6+

### 运行服务器

```bash
mvn spring-boot:run
```

服务器将在 `http://localhost:8080` 启动。

### Agent Card 访问

```bash
curl http://localhost:8080/.well-known/agent.json
```

### 发送消息

```bash
curl -X POST http://localhost:8080/a2a/server \
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

## 支持的JSON-RPC方法

### 核心方法

- `message/send` - 发送消息并创建任务
- `message/stream` - 发送消息并订阅流式更新

### 任务管理

- `tasks/get` - 获取任务状态
- `tasks/cancel` - 取消任务
- `tasks/resubscribe` - 重新订阅任务更新

### 推送通知

- `tasks/pushNotificationConfig/set` - 设置推送通知配置
- `tasks/pushNotificationConfig/get` - 获取推送通知配置

## 客户端使用示例

```java
// 创建客户端
AgentCard agentCard = AgentCard.builder()
    .name("Target Agent")
    .url("http://localhost:8080")
    .version("1.0.0")
    .capabilities(AgentCapabilities.builder().streaming(true).build())
    .skills(List.of())
    .build();

A2AClient client = new A2AClientImpl(agentCard, new HttpCardResolver());

// 发送消息
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
```

## 架构设计

```
a2a4j-core/
├── src/main/java/io/github/a2ap/core/
│   ├── model/          # 数据模型
│   ├── server/         # 服务器端实现
│   ├── client/         # 客户端实现
│   ├── jsonrpc/        # JSON-RPC支持
│   └── exception/      # 异常处理
└── src/test/java/      # 测试代码
```

## 测试

```bash
mvn test
```

## 详细文档

要了解更多详细信息，包括配置、安全、性能优化等，请参考：

- 📖 **[完整英文文档](README_EN.md)** - 包含详细的使用指南、架构说明和最佳实践
- 🚀 [快速开始指南](docs/quick-start.md)
- 🔧 [配置指南](docs/configuration.md)
- 🔐 [安全指南](docs/security.md)

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个实现。

## 许可证

Apache-2.0 license  
