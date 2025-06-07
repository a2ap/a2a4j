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
 * Interface for resolving and discovering AgentCard information in the A2A protocol ecosystem.
 * 
 * The CardResolver is a critical component that enables agent discovery and capability resolution
 * within the A2A protocol. It abstracts the process of obtaining AgentCard information, which
 * contains essential metadata about an agent's capabilities, endpoints, and communication
 * requirements.
 * 
 * Key responsibilities:
 * - Discover and retrieve AgentCard information from various sources
 * - Parse and validate agent capability metadata
 * - Handle different agent identification schemes (URLs, DIDs, custom identifiers)
 * - Provide caching and optimization for repeated lookups
 * - Support various discovery mechanisms and protocols
 * 
 * Discovery mechanisms may include:
 * - HTTP-based discovery via well-known endpoints (/.well-known/agent.json)
 * - Decentralized Identifier (DID) resolution
 * - Registry-based lookups
 * - Direct configuration or injection
 * - DNS-based service discovery
 * 
 * The AgentCard contains crucial information such as:
 * - Agent capabilities (streaming, push notifications, etc.)
 * - Supported communication endpoints and protocols
 * - Authentication and security requirements
 * - Available skills and services
 * - Version and compatibility information
 * 
 * Implementation considerations:
 * - Error handling for network failures and invalid responses
 * - Caching strategies to minimize repeated network calls
 * - Security validation of retrieved agent information
 * - Support for different agent identifier formats
 * - Graceful degradation when discovery fails
 * 
 * Implementations should be thread-safe and handle concurrent resolution requests
 * efficiently, particularly in high-throughput scenarios.
 */
public interface CardResolver {

    /**
     * Resolves the AgentCard for the configured agent.
     * 
     * This method retrieves and returns the AgentCard containing the agent's
     * capabilities, endpoints, and metadata. The resolution process depends on
     * the specific implementation and the agent identification mechanism used.
     *
     * @return AgentCard containing agent information, or null if resolution fails
     */
    AgentCard resolveCard();

}
