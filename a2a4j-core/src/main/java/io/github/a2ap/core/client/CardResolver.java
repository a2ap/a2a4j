package io.github.a2ap.core.client;

import io.github.a2ap.core.model.AgentCard;

/**
 * Interface for resolving AgentCard information.
 * This is used by the client to discover agent capabilities and endpoints.
 */
public interface CardResolver {
    /**
     * Resolves the AgentCard for a given agent identifier.
     * The identifier could be a URL, a DID, or other forms.
     *
     * @param agentIdentifier The identifier of the agent.
     * @return AgentCard info.
     */
    AgentCard resolveCard(String agentIdentifier);
}
