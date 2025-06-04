package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.SendStreamingMessageResponse;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.TextPart;
import io.github.a2ap.core.server.EventQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for DemoAgentExecutor
 */
class DemoAgentExecutorTest {
    
    private DemoAgentExecutor agentExecutor;
    private RequestContext taskContext;
    private EventQueue eventQueue;
    
    @BeforeEach
    void setUp() {
        agentExecutor = new DemoAgentExecutor();
        
        Task task = Task.builder()
                .id(UUID.randomUUID().toString())
                .contextId(UUID.randomUUID().toString())
                .status(TaskStatus.builder()
                        .state(TaskState.SUBMITTED)
                        .timestamp(String.valueOf(System.currentTimeMillis()))
                        .build())
                .build();
        
        Message userMessage = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .role("user")
                .parts(List.of(TextPart.builder()
                        .text("请帮我生成一个简单的Java类")
                        .build()))
                .build();
        
        taskContext = RequestContext.builder()
                .task(task)
                .build();
        
        eventQueue = new EventQueue();
    }
    
    @Test
    void testExecute_ShouldEmitMultipleEvents() {
        // 执行代理任务
        agentExecutor.execute(taskContext, eventQueue).subscribe();
        
        // 验证事件流
        Flux<SendStreamingMessageResponse> eventFlux = eventQueue.asFlux();
        
        StepVerifier.create(eventFlux)
                .expectNextMatches(event -> {
                    // 第一个事件应该是工作状态
                    assertThat(event).isInstanceOf(TaskStatusUpdateEvent.class);
                    TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
                    assertThat(statusEvent.getStatus().getState()).isEqualTo(TaskState.WORKING);
                    assertThat(statusEvent.getIsFinal()).isFalse();
                    return true;
                })
                .expectNextCount(8) // 期望还有更多事件（状态更新、产物等）
                .expectNextMatches(event -> {
                    // 最后一个事件应该是完成状态
                    if (event instanceof TaskStatusUpdateEvent) {
                        TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
                        return statusEvent.getStatus().getState() == TaskState.COMPLETED 
                                && statusEvent.getIsFinal();
                    }
                    return false;
                })
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }
    
    @Test
    void testExecute_ShouldEmitArtifacts() {
        // 执行代理任务
        agentExecutor.execute(taskContext, eventQueue).subscribe();
        
        // 验证产物事件
        List<SendStreamingMessageResponse> events = eventQueue.asFlux()
                .collectList()
                .block(Duration.ofSeconds(10));
        
        assertThat(events).isNotNull();
        
        // 检查是否包含产物事件
        long artifactEventCount = events.stream()
                .filter(event -> event instanceof TaskArtifactUpdateEvent)
                .count();
        
        assertThat(artifactEventCount).isGreaterThan(0);
        
        // 检查文本产物
        boolean hasTextArtifact = events.stream()
                .filter(event -> event instanceof TaskArtifactUpdateEvent)
                .map(event -> (TaskArtifactUpdateEvent) event)
                .anyMatch(event -> "text-response".equals(event.getArtifact().getArtifactId()));
        
        assertThat(hasTextArtifact).isTrue();
        
        // 检查代码产物
        boolean hasCodeArtifact = events.stream()
                .filter(event -> event instanceof TaskArtifactUpdateEvent)
                .map(event -> (TaskArtifactUpdateEvent) event)
                .anyMatch(event -> "code-example".equals(event.getArtifact().getArtifactId()));
        
        assertThat(hasCodeArtifact).isTrue();
    }
    
    @Test
    void testCancel_ShouldEmitCancelledStatus() {
        // 执行取消操作
        agentExecutor.cancel("id").subscribe();
        
        // 验证取消事件
        Flux<SendStreamingMessageResponse> eventFlux = eventQueue.asFlux();
        
        StepVerifier.create(eventFlux)
                .expectNextMatches(event -> {
                    assertThat(event).isInstanceOf(TaskStatusUpdateEvent.class);
                    TaskStatusUpdateEvent statusEvent = (TaskStatusUpdateEvent) event;
                    assertThat(statusEvent.getStatus().getState()).isEqualTo(TaskState.CANCELED);
                    assertThat(statusEvent.getIsFinal()).isTrue();
                    return true;
                })
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
    
    @Test
    void testExecute_ShouldCloseQueueWhenComplete() {
        // 执行代理任务
        agentExecutor.execute(taskContext, eventQueue)
                .block(Duration.ofSeconds(10));
        
        // 验证队列已关闭
        assertThat(eventQueue.isClosed()).isTrue();
    }
} 
