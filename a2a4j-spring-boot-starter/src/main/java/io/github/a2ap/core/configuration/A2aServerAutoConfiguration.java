package io.github.a2ap.core.configuration;

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

@Configuration
@EnableConfigurationProperties(A2aServerProperties.class)
@AutoConfigureAfter(value = {A2aServerProperties.class})
public class A2aServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public QueueManager queueManager() {
        return new InMemoryQueueManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskStore taskStore() {
        return new InMemoryTaskStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskManager taskManager(TaskStore taskStore) {
        return new InMemoryTaskManager(taskStore);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public Dispatcher dispatcher(A2AServer a2aServer, ObjectMapper objectMapper) {
        return new DefaultDispatcher(a2aServer, objectMapper);
    }
    
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
    
    @Bean
    @ConditionalOnMissingBean
    public A2AServer a2AServer(TaskManager taskManager, AgentExecutor agentExecutor, QueueManager queueManager, AgentCard agentCard) {
        return new DefaultA2AServer(taskManager, agentExecutor, queueManager, agentCard);
    }
} 
