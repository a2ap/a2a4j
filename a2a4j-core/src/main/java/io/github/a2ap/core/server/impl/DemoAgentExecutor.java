/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.TaskArtifactUpdateEvent;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskState;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskStatusUpdateEvent;
import io.github.a2ap.core.model.TextPart;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Demo implementation of AgentExecutor that simulates various types of events
 * during task execution, including status updates, progress reports, and
 * artifact generation.
 */
@Component
public class DemoAgentExecutor implements AgentExecutor {

        private static final Logger log = LoggerFactory.getLogger(DemoAgentExecutor.class);

        @Override
        public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
                String taskId = context.getTask().getId();
                String contextId = context.getTask().getContextId();
                log.info("Demo agent starting execution for task: {}", taskId);

                return Mono.fromRunnable(() -> {
                        // 1. Send task start status
                        sendWorkingStatus(taskId, contextId, eventQueue, "Starting to process user request...");
                })
                                .then(Mono.delay(Duration.ofMillis(500)))
                                .then(Mono.fromRunnable(() -> {
                                        // 2. Send analysis phase status
                                        sendWorkingStatus(taskId, contextId, eventQueue, "Analyzing user input...");
                                }))
                                .then(Mono.delay(Duration.ofSeconds(1)))
                                .then(Mono.fromRunnable(() -> {
                                        // 3. Send processing progress status
                                        sendWorkingStatus(taskId, contextId, eventQueue, "Generating response...");
                                }))
                                .then(Mono.delay(Duration.ofMillis(800)))
                                .then(Mono.fromRunnable(() -> {
                                        // 4. Send first text artifact (chunk)
                                        sendTextArtifact(taskId, contextId, eventQueue, "text-response",
                                                        "AI Assistant Response", "Here's my analysis of your question:\n\n", false, false);
                                }))
                                .then(Mono.delay(Duration.ofMillis(300)))
                                .then(Mono.fromRunnable(() -> {
                                        // 5. Continue sending text artifact (chunk)
                                        sendTextArtifact(taskId, contextId, eventQueue, "text-response",
                                                        "AI Assistant Response", "Based on the information provided, I suggest the following approach:\n", true, false);
                                }))
                                .then(Mono.delay(Duration.ofMillis(500)))
                                .then(Mono.fromRunnable(() -> {
                                        // 6. Send code artifact
                                        sendCodeArtifact(taskId, contextId, eventQueue);
                                }))
                                .then(Mono.delay(Duration.ofMillis(400)))
                                .then(Mono.fromRunnable(() -> {
                                        // 7. Complete text artifact (last chunk)
                                        sendTextArtifact(taskId, contextId, eventQueue, "text-response",
                                                        "AI Assistant Response", "\n\nIf you have any questions, please feel free to ask!", true, true);
                                }))
                                .then(Mono.delay(Duration.ofMillis(300)))
                                .then(Mono.fromRunnable(() -> {
                                        // 8. Send summary artifact
                                        sendSummaryArtifact(taskId, contextId, eventQueue);
                                }))
                                .then(Mono.delay(Duration.ofMillis(200)))
                                .then(Mono.fromRunnable(() -> {
                                        // 9. Send final completion status
                                        sendCompletedStatus(taskId, contextId, eventQueue);
                                        eventQueue.close();
                                        log.info("Demo agent completed task: {}", taskId);
                                }))
                                .then();
        }

        @Override
        public Mono<Void> cancel(String taskId) {
                log.info("Demo agent cancelling task: {}", taskId);
                // TODO: Implement cancellation logic
                return Mono.empty();
        }

        /**
         * Send working status update
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
         * Send text artifact update
         */
        private void sendTextArtifact(String taskId, String contextId, EventQueue eventQueue,
                        String artifactId, String name, String content,
                        boolean append, boolean lastChunk) {
                Artifact artifact = Artifact.builder()
                                .artifactId(artifactId)
                                .name(name)
                                .description("AI generated text reply")
                                .parts(List.of(TextPart.builder()
                                                .text(content)
                                                .build()))
                                .metadata(Map.of(
                                                "contentType", "text/plain",
                                                "encoding", "utf-8",
                                                "chunkIndex", System.currentTimeMillis()))
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
         * Send code artifact
         */
        private void sendCodeArtifact(String taskId, String contextId, EventQueue eventQueue) {
                String codeContent = """
                                // Example code
                                public class ExampleService {

                                    public String processRequest(String input) {
                                        if (input == null || input.trim().isEmpty()) {
                                            return "Input cannot be empty";
                                        }

                                        // Process input
                                        String processed = input.trim().toLowerCase();
                                        return "Processed result: " + processed;
                                    }
                                }
                                """;

                Artifact artifact = Artifact.builder()
                                .artifactId("code-example")
                                .name("Example Code")
                                .description("Example Java code generated based on requirements")
                                .parts(List.of(TextPart.builder()
                                                .text(codeContent)
                                                .build()))
                                .metadata(Map.of(
                                                "contentType", "text/x-java-source",
                                                "language", "java",
                                                "filename", "ExampleService.java"))
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
         * Send summary artifact
         */
        private void sendSummaryArtifact(String taskId, String contextId, EventQueue eventQueue) {
                Artifact artifact = Artifact.builder()
                                .artifactId("task-summary")
                                .name("Task Summary")
                                .description("Summary report of this task execution")
                                .parts(List.of(TextPart.builder()
                                                .text("## Task Execution Summary\n\n" +
                                                                "✅ User request analysis completed\n" +
                                                                "✅ Text response generated\n" +
                                                                "✅ Example code provided\n" +
                                                                "✅ Task executed successfully\n\n" +
                                                                "Total execution time: ~3 seconds\n" +
                                                                "Generated content: Text response + Code example")
                                                .build()))
                                .metadata(Map.of(
                                                "contentType", "text/markdown",
                                                "reportType", "summary"))
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
         * Send completion status
         */
        private void sendCompletedStatus(String taskId, String contextId, EventQueue eventQueue) {
                TaskStatusUpdateEvent completedEvent = TaskStatusUpdateEvent.builder()
                                .taskId(taskId)
                                .contextId(contextId)
                                .status(TaskStatus.builder()
                                                .state(TaskState.COMPLETED)
                                                .timestamp(String.valueOf(Instant.now().toEpochMilli()))
                                                .message(createAgentMessage("Task completed successfully! I have generated a detailed response and example code for you."))
                                                .build())
                                .isFinal(true)
                                .metadata(Map.of(
                                                "executionTime", "3000ms",
                                                "artifactsGenerated", 4,
                                                "success", true))
                                .build();

                eventQueue.enqueueEvent(completedEvent);
                log.debug("Sent completed status for task {}", taskId);
        }

        /**
         * Create agent message
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
