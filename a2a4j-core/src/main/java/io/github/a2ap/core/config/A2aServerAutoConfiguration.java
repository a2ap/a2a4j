package io.github.a2ap.core.config;

import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(A2aServerProperties.class)
public class A2aServerAutoConfiguration {
    
    @Bean(name = "a2aServerSelfCard")
    public AgentCard a2aServerSelfCard(final A2aServerProperties a2aServerProperties) {
        // Create and return a default AgentCard instance
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
}
