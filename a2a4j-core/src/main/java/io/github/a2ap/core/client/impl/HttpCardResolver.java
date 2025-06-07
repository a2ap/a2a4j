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
 * An implementation of {@link CardResolver} that fetches AgentCard from a URL
 * via HTTP.
 */
public class HttpCardResolver implements CardResolver {

    private static final Logger log = LoggerFactory.getLogger(HttpCardResolver.class);
    
    @Override
    public AgentCard resolveCard(String agentIdentifier) {
        // Assuming agentIdentifier is a URL for now
        HttpClient client = HttpClient.create();
        try {
            AgentCard responseCard = client
                    .baseUrl(agentIdentifier)
                    .get()
                    .uri("/.well-known/agent.json")
                    .responseContent()
                    .aggregate()
                    .asString()
                    .map(data -> JsonUtil.fromJson(data, AgentCard.class))
                    .block();
            log.info("Retrieve agent card {} successfully. Info: {}", agentIdentifier, responseCard);
            return responseCard;
        } catch (Exception e) {
            log.error("Error sending task to {}: {}", agentIdentifier, e.getMessage(), e);
            return null;
        }
    }
}
