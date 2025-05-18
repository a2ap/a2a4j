package io.github.a2ap.core.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.AgentCard;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * An implementation of {@link CardResolver} that fetches AgentCard from a URL
 * via HTTP.
 */
public class HttpCardResolver implements CardResolver {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpCardResolver() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CompletableFuture<AgentCard> resolveCard(String agentIdentifier) {
        // Assuming agentIdentifier is a URL for now
        try {
            URI uri = new URI(agentIdentifier);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::parseAgentCard);

        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private AgentCard parseAgentCard(String json) {
        try {
            return objectMapper.readValue(json, AgentCard.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AgentCard JSON", e);
        }
    }
}
