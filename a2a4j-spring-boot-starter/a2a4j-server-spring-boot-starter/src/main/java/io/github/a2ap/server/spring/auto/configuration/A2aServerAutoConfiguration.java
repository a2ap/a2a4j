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

package io.github.a2ap.server.spring.auto.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.Dispatcher;
import io.github.a2ap.core.server.EventQueue;
import io.github.a2ap.core.server.QueueManager;
import io.github.a2ap.core.server.impl.DefaultA2AServer;
import io.github.a2ap.core.server.impl.DefaultDispatcher;
import io.github.a2ap.core.server.impl.InMemoryQueueManager;
import io.github.a2ap.core.server.TaskManager;
import io.github.a2ap.core.server.TaskStore;
import io.github.a2ap.core.server.impl.InMemoryTaskManager;
import io.github.a2ap.core.server.impl.InMemoryTaskStore;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Auto-configuration for A2A Server components.
 * 
 * This configuration class automatically sets up all the necessary beans for running
 * an A2A server when the starter is included in a Spring Boot application.
 * 
 * <p>The configuration provides default implementations for all core A2A server components:
 * <ul>
 * <li>{@link QueueManager} - Manages event queues for tasks</li>
 * <li>{@link TaskStore} - Stores task data and history</li>
 * <li>{@link TaskManager} - Manages task lifecycle</li>
 * <li>{@link AgentExecutor} - Executes agent logic (default no-op implementation)</li>
 * <li>{@link Dispatcher} - Routes JSON-RPC requests</li>
 * <li>{@link A2AServer} - Main server implementation</li>
 * <li>{@link AgentCard} - Agent metadata and capabilities</li>
 * </ul>
 * 
 * <p>All beans are created with {@link ConditionalOnMissingBean} annotation,
 * allowing users to override any component by providing their own implementation.
 * 
 * @see A2aServerProperties
 * @see A2AServer
 */
@Configuration
@EnableConfigurationProperties(A2aServerProperties.class)
@AutoConfigureAfter(value = {A2aServerProperties.class})
public class A2aServerAutoConfiguration {

    /**
     * Creates a default in-memory queue manager for managing task event queues.
     * This implementation stores queues in memory and is suitable for development
     * and single-instance deployments.
     * 
     * @return A new InMemoryQueueManager instance
     */
    @Bean
    @ConditionalOnMissingBean
    public QueueManager queueManager() {
        return new InMemoryQueueManager();
    }

    /**
     * Creates a default in-memory task store for persisting task data.
     * This implementation stores tasks in memory and is suitable for development
     * and testing. For production use, consider providing a persistent implementation.
     * 
     * @return A new InMemoryTaskStore instance
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskStore taskStore() {
        return new InMemoryTaskStore();
    }

    /**
     * Creates a default task manager for handling task lifecycle operations.
     * The task manager uses the provided task store for persistence.
     * 
     * @param taskStore The task store to use for task persistence
     * @return A new InMemoryTaskManager instance
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskManager taskManager(TaskStore taskStore) {
        return new InMemoryTaskManager(taskStore);
    }
    
    /**
     * Creates a default Jackson ObjectMapper for JSON serialization/deserialization.
     * This is used by the dispatcher for converting JSON-RPC parameters.
     * 
     * @return A new ObjectMapper instance with default configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    /**
     * Creates a default dispatcher for routing JSON-RPC requests to appropriate handlers.
     * The dispatcher handles both synchronous and streaming requests.
     * 
     * @param a2aServer The A2A server to delegate operations to
     * @param objectMapper The ObjectMapper for parameter conversion
     * @return A new DefaultDispatcher instance
     */
    @Bean
    @ConditionalOnMissingBean
    public Dispatcher dispatcher(A2AServer a2aServer, ObjectMapper objectMapper) {
        return new DefaultDispatcher(a2aServer, objectMapper);
    }
    
    /**
     * Creates a default no-op agent executor.
     * This implementation immediately closes the event queue and returns empty results.
     * 
     * <p><strong>Important:</strong> This is a placeholder implementation.
     * Production applications should provide their own {@link AgentExecutor}
     * implementation that contains the actual agent logic.
     * 
     * @return A no-op AgentExecutor implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public AgentExecutor agentExecutor() {
        return new AgentExecutor() {
            @Override
            public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
                eventQueue.close();
                return Mono.empty();
            }

            @Override
            public Mono<Void> cancel(String taskId) {
                return Mono.empty();
            }
        };
    }

    /**
     * Creates an agent card based on configuration properties.
     * The agent card contains metadata about the agent's capabilities and endpoints.
     * 
     * @param a2aServerProperties The configuration properties for the A2A server
     * @return An AgentCard configured with the provided properties
     */
    @Bean(name = "a2aServerSelfCard")
    @ConditionalOnMissingBean
    public AgentCard agentCard(final A2aServerProperties a2aServerProperties) {
        return AgentCard.builder()
                .name(a2aServerProperties.getName())
                .url(a2aServerProperties.getUrl())
                .version(a2aServerProperties.getVersion())
                .description(a2aServerProperties.getDescription())
                .capabilities(AgentCapabilities.builder()
                        .streaming(a2aServerProperties.getCapabilities().isStreaming())
                        .pushNotifications(a2aServerProperties.getCapabilities().isPushNotifications())
                        .stateTransitionHistory(a2aServerProperties.getCapabilities().isStateTransitionHistory())
                        .build())
                .build();
    }
    
    /**
     * Creates the main A2A server implementation.
     * This server orchestrates all the components to provide complete A2A protocol support.
     * 
     * @param taskManager The task manager for handling task operations
     * @param agentExecutor The agent executor containing the core logic
     * @param queueManager The queue manager for event handling
     * @param agentCard The agent card with server metadata
     * @return A new DefaultA2AServer instance
     */
    @Bean
    @ConditionalOnMissingBean
    public A2AServer a2AServer(TaskManager taskManager, AgentExecutor agentExecutor, QueueManager queueManager, AgentCard agentCard) {
        return new DefaultA2AServer(taskManager, agentExecutor, queueManager, agentCard);
    }
} 
