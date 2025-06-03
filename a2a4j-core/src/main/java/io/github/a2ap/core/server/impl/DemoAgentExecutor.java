package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.TaskContext;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.TextPart;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Demo implementation of AgentExecutor that simulates various types of events
 * during task execution, including status updates, progress reports, and artifact generation.
 */
@Slf4j
@Component
public class DemoAgentExecutor implements AgentExecutor {
    
    @Override
    public Mono<Void> execute(TaskContext context, EventQueue eventQueue) {
        String taskId = context.getTask().getId();
        String contextId = context.getTask().getContextId();
        log.info("Demo agent starting execution for task: {}", taskId);
        
        return Mono.fromRunnable(() -> {
            // 1. 发送任务开始状态
            sendWorkingStatus(taskId, contextId, eventQueue, "开始处理用户请求...");
        })
        .then(Mono.delay(Duration.ofMillis(500)))
        .then(Mono.fromRunnable(() -> {
            // 2. 发送分析阶段状态
            sendWorkingStatus(taskId, contextId, eventQueue, "正在分析用户输入内容...");
        }))
        .then(Mono.delay(Duration.ofSeconds(1)))
        .then(Mono.fromRunnable(() -> {
            // 3. 发送处理进度状态
            sendWorkingStatus(taskId, contextId, eventQueue, "正在生成响应内容...");
        }))
        .then(Mono.delay(Duration.ofMillis(800)))
        .then(Mono.fromRunnable(() -> {
            // 4. 发送第一个文本产物（分块）
            sendTextArtifact(taskId, contextId, eventQueue, "text-response", 
                    "AI 助手回复", "这是我对您问题的分析：\n\n", false, false);
        }))
        .then(Mono.delay(Duration.ofMillis(300)))
        .then(Mono.fromRunnable(() -> {
            // 5. 继续发送文本产物（分块）
            sendTextArtifact(taskId, contextId, eventQueue, "text-response", 
                    "AI 助手回复", "根据您提供的信息，我建议采用以下方案：\n", true, false);
        }))
        .then(Mono.delay(Duration.ofMillis(500)))
        .then(Mono.fromRunnable(() -> {
            // 6. 发送代码产物
            sendCodeArtifact(taskId, contextId, eventQueue);
        }))
        .then(Mono.delay(Duration.ofMillis(400)))
        .then(Mono.fromRunnable(() -> {
            // 7. 完成文本产物（最后一块）
            sendTextArtifact(taskId, contextId, eventQueue, "text-response", 
                    "AI 助手回复", "\n\n如果您有任何问题，请随时告诉我！", true, true);
        }))
        .then(Mono.delay(Duration.ofMillis(300)))
        .then(Mono.fromRunnable(() -> {
            // 8. 发送总结产物
            sendSummaryArtifact(taskId, contextId, eventQueue);
        }))
        .then(Mono.delay(Duration.ofMillis(200)))
        .then(Mono.fromRunnable(() -> {
            // 9. 发送最终完成状态
            sendCompletedStatus(taskId, contextId, eventQueue);
            eventQueue.close();
            log.info("Demo agent completed task: {}", taskId);
        }))
        .then();
    }
    
    @Override
    public Mono<Void> cancel(TaskContext context, EventQueue eventQueue) {
        String taskId = context.getTask().getId();
        String contextId = context.getTask().getContextId();
        log.info("Demo agent cancelling task: {}", taskId);
        
        return Mono.fromRunnable(() -> {
            // 发送取消状态
            TaskStatusUpdateEvent cancelledEvent = TaskStatusUpdateEvent.builder()
                    .taskId(taskId)
                    .contextId(contextId)
                    .status(TaskStatus.builder()
                            .state(TaskState.CANCELED)
                            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                            .message(createAgentMessage("任务已被用户取消"))
                            .build())
                    .isFinal(true)
                    .build();
            eventQueue.enqueueEvent(cancelledEvent);
            eventQueue.close();
            
            log.info("Demo agent cancelled task: {}", taskId);
        });
    }
    
    /**
     * 发送工作中状态更新
     */
    private void sendWorkingStatus(String taskId, String contextId, EventQueue eventQueue, String statusMessage) {
        TaskStatusUpdateEvent workingEvent = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .status(TaskStatus.builder()
                        .state(TaskState.WORKING)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .message(createAgentMessage(statusMessage))
                        .build())
                .isFinal(false)
                .build();
        
        eventQueue.enqueueEvent(workingEvent);
        log.debug("Sent working status for task {}: {}", taskId, statusMessage);
    }
    
    /**
     * 发送文本产物更新
     */
    private void sendTextArtifact(String taskId, String contextId, EventQueue eventQueue, 
                                  String artifactId, String name, String content, 
                                  boolean append, boolean lastChunk) {
        Artifact artifact = Artifact.builder()
                .artifactId(artifactId)
                .name(name)
                .description("AI 生成的文本回复")
                .parts(List.of(TextPart.builder()
                        .text(content)
                        .build()))
                .metadata(Map.of(
                        "contentType", "text/plain",
                        "encoding", "utf-8",
                        "chunkIndex", System.currentTimeMillis()
                ))
                .build();
        
        TaskArtifactUpdateEvent artifactEvent = TaskArtifactUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .artifact(artifact)
                .append(append)
                .lastChunk(lastChunk)
                .isFinal(false)
                .metadata(Map.of("artifactType", "text"))
                .build();
        
        eventQueue.enqueueEvent(artifactEvent);
        log.debug("Sent text artifact for task {}: {} chars, append={}, lastChunk={}", 
                taskId, content.length(), append, lastChunk);
    }
    
    /**
     * 发送代码产物
     */
    private void sendCodeArtifact(String taskId, String contextId, EventQueue eventQueue) {
        String codeContent = """
                // 示例代码
                public class ExampleService {
                    
                    public String processRequest(String input) {
                        if (input == null || input.trim().isEmpty()) {
                            return "输入不能为空";
                        }
                        
                        // 处理输入
                        String processed = input.trim().toLowerCase();
                        return "处理结果: " + processed;
                    }
                }
                """;
        
        Artifact artifact = Artifact.builder()
                .artifactId("code-example")
                .name("示例代码")
                .description("根据需求生成的示例Java代码")
                .parts(List.of(TextPart.builder()
                        .text(codeContent)
                        .build()))
                .metadata(Map.of(
                        "contentType", "text/x-java-source",
                        "language", "java",
                        "filename", "ExampleService.java"
                ))
                .build();
        
        TaskArtifactUpdateEvent artifactEvent = TaskArtifactUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .artifact(artifact)
                .append(false)
                .lastChunk(true)
                .isFinal(false)
                .metadata(Map.of("artifactType", "code"))
                .build();
        
        eventQueue.enqueueEvent(artifactEvent);
        log.debug("Sent code artifact for task {}", taskId);
    }
    
    /**
     * 发送总结产物
     */
    private void sendSummaryArtifact(String taskId, String contextId, EventQueue eventQueue) {
        Artifact artifact = Artifact.builder()
                .artifactId("task-summary")
                .name("任务总结")
                .description("本次任务执行的总结报告")
                .parts(List.of(TextPart.builder()
                        .text("## 任务执行总结\n\n" +
                                "✅ 已完成用户请求分析\n" +
                                "✅ 已生成文本回复\n" +
                                "✅ 已提供示例代码\n" +
                                "✅ 任务执行成功\n\n" +
                                "总执行时间: 约3秒\n" +
                                "生成内容: 文本回复 + 代码示例")
                        .build()))
                .metadata(Map.of(
                        "contentType", "text/markdown",
                        "reportType", "summary"
                ))
                .build();
        
        TaskArtifactUpdateEvent artifactEvent = TaskArtifactUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .artifact(artifact)
                .append(false)
                .lastChunk(true)
                .isFinal(false)
                .metadata(Map.of("artifactType", "summary"))
                .build();
        
        eventQueue.enqueueEvent(artifactEvent);
        log.debug("Sent summary artifact for task {}", taskId);
    }
    
    /**
     * 发送完成状态
     */
    private void sendCompletedStatus(String taskId, String contextId, EventQueue eventQueue) {
        TaskStatusUpdateEvent completedEvent = TaskStatusUpdateEvent.builder()
                .taskId(taskId)
                .contextId(contextId)
                .status(TaskStatus.builder()
                        .state(TaskState.COMPLETED)
                        .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                        .message(createAgentMessage("任务已成功完成！我已经为您生成了详细的回复和示例代码。"))
                        .build())
                .isFinal(true)
                .metadata(Map.of(
                        "executionTime", "3000ms",
                        "artifactsGenerated", 4,
                        "success", true
                ))
                .build();
        
        eventQueue.enqueueEvent(completedEvent);
        log.debug("Sent completed status for task {}", taskId);
    }
    
    /**
     * 创建代理消息
     */
    private Message createAgentMessage(String content) {
        return Message.builder()
                .role("agent")
                .parts(List.of(TextPart.builder()
                        .text(content)
                        .build()))
                .build();
    }
}
