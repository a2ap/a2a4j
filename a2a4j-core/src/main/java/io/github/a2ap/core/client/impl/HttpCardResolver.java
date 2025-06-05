package io.github.a2ap.core.client.impl;

import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.AgentCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * An implementation of {@link CardResolver} that fetches AgentCard from a URL
 * via HTTP.
 */
@Component
public class HttpCardResolver implements CardResolver {

    private static final Logger log = LoggerFactory.getLogger(HttpCardResolver.class);

    @Override
    public AgentCard resolveCard(String agentIdentifier) {
        // Assuming agentIdentifier is a URL for now
        WebClient client = WebClient.create(agentIdentifier);
        try {
            AgentCard responseCard = client.get()
                    .uri("/.well-known/agent.json")
                    .retrieve()
                    .bodyToMono(AgentCard.class)
                    .block();
            log.info("Retrieve agent card {} successfully. Info: {}", agentIdentifier, responseCard);
            return responseCard;
        } catch (Exception e) {
            log.error("Error sending task to {}: {}", agentIdentifier, e.getMessage(), e);
            return null;
        }
    }
}
