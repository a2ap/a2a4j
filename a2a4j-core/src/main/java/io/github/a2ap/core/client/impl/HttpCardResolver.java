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

package io.github.a2ap.core.client.impl;

import io.github.a2ap.core.client.CardResolver;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClient;

/**
 * HTTP-based implementation of {@link CardResolver} for discovering agent capabilities.
 * 
 * This implementation follows the A2A protocol standard for agent discovery by fetching
 * AgentCard information from a well-known HTTP endpoint. It assumes the agent identifier
 * is a base URL and attempts to retrieve the agent card from the standard location.
 * 
 * Discovery process:
 * 1. Treats the agent identifier as a base URL
 * 2. Appends the well-known path "/.well-known/agent.json"
 * 3. Performs an HTTP GET request to retrieve the agent card
 * 4. Deserializes the JSON response into an AgentCard object
 * 
 * The implementation uses Reactor Netty's HttpClient for non-blocking HTTP operations
 * and includes proper error handling and logging for troubleshooting discovery issues.
 * 
 * This resolver is suitable for agents that expose their capabilities through standard
 * HTTP endpoints following the A2A protocol conventions.
 */
public class HttpCardResolver implements CardResolver {

    private static final Logger log = LoggerFactory.getLogger(HttpCardResolver.class);
    
    private final String baseUrl;
    
    private final HttpClient httpClient;

    public HttpCardResolver(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.create().baseUrl(this.baseUrl);
    }

    @Override
    public AgentCard resolveCard() {
        // Assuming agentIdentifier is a URL for now
        log.info("Retrieve agent card to {}", this.baseUrl);
        try {
            AgentCard responseCard = this.httpClient
                    .get()
                    .uri("/.well-known/agent.json")
                    .responseContent()
                    .aggregate()
                    .asString()
                    .map(data -> JsonUtil.fromJson(data, AgentCard.class))
                    .block();
            log.info("Retrieve agent card {} successfully. Info: {}", this.baseUrl, responseCard);
            return responseCard;
        } catch (Exception e) {
            log.error("Error sending task to {}: {}", this.baseUrl, e.getMessage(), e);
            return null;
        }
    }
}
