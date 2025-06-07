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

package io.github.a2ap.core.client;

import io.github.a2ap.core.model.AgentCard;

/**
 * Interface for resolving AgentCard information. This is used by the client to discover
 * agent capabilities and endpoints.
 */
public interface CardResolver {

    /**
     * Resolves the AgentCard for a given agent identifier. The identifier could be a URL,
     * a DID, or other forms.
     *
     * @param agentIdentifier The identifier of the agent.
     * @return AgentCard info.
     */
    AgentCard resolveCard(String agentIdentifier);

}
