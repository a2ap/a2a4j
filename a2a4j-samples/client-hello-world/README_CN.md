# A2A4J Client Hello World 示例（客户端）

这是一个 A2A（Agent2Agent）协议客户端实现示例，演示如何使用 A2A4J 框架与 A2A 服务器进行交互。

## 功能特性

- ✅ A2A 协议客户端实现
- ✅ 支持 JSON-RPC 2.0 同步与流式通信
- ✅ 演示如何向 A2A 服务器发送消息
- ✅ 实时状态更新与进度跟踪
- ✅ 详细日志输出

## 快速开始

### 前置条件

- Java 17 及以上
- Maven 3.6 及以上

### 构建项目

```bash
# 克隆仓库（如尚未克隆）
git clone https://github.com/a2ap/a2a4j.git
cd a2a4j

# 构建整个项目
mvn clean install

# 进入示例目录
cd a2a4j-samples/client-hello-world
```

### 运行客户端

```bash
# 使用Maven运行
mvn spring-boot:run

# 或运行已编译的JAR包
mvn clean package
java -jar target/client-hello-world-*.jar
```

### 示例用法

客户端将尝试连接 A2A 服务器（默认：http://localhost:8089）并发送示例消息。你可以在配置文件中修改消息内容和服务器 URL。

---

更多细节请参考源码及注释。
