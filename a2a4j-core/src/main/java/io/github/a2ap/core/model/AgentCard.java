package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an agent's capabilities and metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCard {
    /**
     * The unique identifier of the agent.
     */
    private String id;

    /**
     * The name of the agent.
     */
    @NotNull
    private String name;

    /**
     * An optional description of the agent.
     */
    private String description;

    /**
     * The base URL endpoint for interacting with the agent.
     */
    @NotNull
    private String url;

    /**
     * Information about the provider of the agent.
     */
    private AgentProvider provider;

    /**
     * The version identifier for the agent or its API.
     */
    @NotNull
    private String version;

    /**
     * An optional URL pointing to the agent's documentation.
     */
    private String documentationUrl;

    /**
     * The capabilities supported by the agent.
     */
    @NotNull
    private AgentCapabilities capabilities;

    /**
     * Authentication details required to interact with the agent.
     */
    private AgentAuthentication authentication;

    /**
     * Security scheme details used for authenticating with this agent.
     */
    private Map<String, SecurityScheme> securitySchemes;

    /**
     * Security requirements for contacting the agent.
     */
    private List<Map<String, List<String>>> security;

    /**
     * Default input modes supported by the agent (e.g., 'text', 'file', 'json').
     * Defaults to ["text"] if not specified.
     */
    private List<String> defaultInputModes = List.of("text");

    /**
     * Default output modes supported by the agent (e.g., 'text', 'file', 'json').
     * Defaults to ["text"] if not specified.
     */
    private List<String> defaultOutputModes = List.of("text");

    /**
     * List of specific skills offered by the agent.
     */
    @NotNull
    private List<AgentSkill> skills;

    public AgentCard() {
    }

    public AgentCard(String id, String name, String description, String url, AgentProvider provider,
            String version, String documentationUrl, AgentCapabilities capabilities,
            AgentAuthentication authentication, Map<String, SecurityScheme> securitySchemes,
            List<Map<String, List<String>>> security, List<String> defaultInputModes,
            List<String> defaultOutputModes, List<AgentSkill> skills) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.provider = provider;
        this.version = version;
        this.documentationUrl = documentationUrl;
        this.capabilities = capabilities;
        this.authentication = authentication;
        this.securitySchemes = securitySchemes;
        this.security = security;
        this.defaultInputModes = defaultInputModes;
        this.defaultOutputModes = defaultOutputModes;
        this.skills = skills;
    }

    public static AgentCardBuilder builder() {
        return new AgentCardBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AgentProvider getProvider() {
        return provider;
    }

    public void setProvider(AgentProvider provider) {
        this.provider = provider;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public AgentCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(AgentCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public AgentAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AgentAuthentication authentication) {
        this.authentication = authentication;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return securitySchemes;
    }

    public void setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }

    public List<Map<String, List<String>>> getSecurity() {
        return security;
    }

    public void setSecurity(List<Map<String, List<String>>> security) {
        this.security = security;
    }

    public List<String> getDefaultInputModes() {
        return defaultInputModes;
    }

    public void setDefaultInputModes(List<String> defaultInputModes) {
        this.defaultInputModes = defaultInputModes;
    }

    public List<String> getDefaultOutputModes() {
        return defaultOutputModes;
    }

    public void setDefaultOutputModes(List<String> defaultOutputModes) {
        this.defaultOutputModes = defaultOutputModes;
    }

    public List<AgentSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<AgentSkill> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AgentCard agentCard = (AgentCard) o;
        return Objects.equals(id, agentCard.id) &&
                Objects.equals(name, agentCard.name) &&
                Objects.equals(description, agentCard.description) &&
                Objects.equals(url, agentCard.url) &&
                Objects.equals(provider, agentCard.provider) &&
                Objects.equals(version, agentCard.version) &&
                Objects.equals(documentationUrl, agentCard.documentationUrl) &&
                Objects.equals(capabilities, agentCard.capabilities) &&
                Objects.equals(authentication, agentCard.authentication) &&
                Objects.equals(securitySchemes, agentCard.securitySchemes) &&
                Objects.equals(security, agentCard.security) &&
                Objects.equals(defaultInputModes, agentCard.defaultInputModes) &&
                Objects.equals(defaultOutputModes, agentCard.defaultOutputModes) &&
                Objects.equals(skills, agentCard.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, provider, version, documentationUrl,
                capabilities, authentication, securitySchemes, security,
                defaultInputModes, defaultOutputModes, skills);
    }

    @Override
    public String toString() {
        return "AgentCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", provider=" + provider +
                ", version='" + version + '\'' +
                ", documentationUrl='" + documentationUrl + '\'' +
                ", capabilities=" + capabilities +
                ", authentication=" + authentication +
                ", securitySchemes=" + securitySchemes +
                ", security=" + security +
                ", defaultInputModes=" + defaultInputModes +
                ", defaultOutputModes=" + defaultOutputModes +
                ", skills=" + skills +
                '}';
    }

    public static class AgentCardBuilder {
        private String id;
        private String name;
        private String description;
        private String url;
        private AgentProvider provider;
        private String version;
        private String documentationUrl;
        private AgentCapabilities capabilities;
        private AgentAuthentication authentication;
        private Map<String, SecurityScheme> securitySchemes;
        private List<Map<String, List<String>>> security;
        private List<String> defaultInputModes = List.of("text");
        private List<String> defaultOutputModes = List.of("text");
        private List<AgentSkill> skills;

        AgentCardBuilder() {
        }

        public AgentCardBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AgentCardBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AgentCardBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AgentCardBuilder url(String url) {
            this.url = url;
            return this;
        }

        public AgentCardBuilder provider(AgentProvider provider) {
            this.provider = provider;
            return this;
        }

        public AgentCardBuilder version(String version) {
            this.version = version;
            return this;
        }

        public AgentCardBuilder documentationUrl(String documentationUrl) {
            this.documentationUrl = documentationUrl;
            return this;
        }

        public AgentCardBuilder capabilities(AgentCapabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public AgentCardBuilder authentication(AgentAuthentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public AgentCardBuilder securitySchemes(Map<String, SecurityScheme> securitySchemes) {
            this.securitySchemes = securitySchemes;
            return this;
        }

        public AgentCardBuilder security(List<Map<String, List<String>>> security) {
            this.security = security;
            return this;
        }

        public AgentCardBuilder defaultInputModes(List<String> defaultInputModes) {
            this.defaultInputModes = defaultInputModes;
            return this;
        }

        public AgentCardBuilder defaultOutputModes(List<String> defaultOutputModes) {
            this.defaultOutputModes = defaultOutputModes;
            return this;
        }

        public AgentCardBuilder skills(List<AgentSkill> skills) {
            this.skills = skills;
            return this;
        }

        public AgentCard build() {
            return new AgentCard(id, name, description, url, provider, version, documentationUrl,
                    capabilities, authentication, securitySchemes, security,
                    defaultInputModes, defaultOutputModes, skills);
        }

        @Override
        public String toString() {
            return "AgentCard.AgentCardBuilder(id=" + this.id + ", name=" + this.name +
                    ", description=" + this.description + ", url=" + this.url +
                    ", provider=" + this.provider + ", version=" + this.version +
                    ", documentationUrl=" + this.documentationUrl + ", capabilities=" + this.capabilities +
                    ", authentication=" + this.authentication + ", securitySchemes=" + this.securitySchemes +
                    ", security=" + this.security + ", defaultInputModes=" + this.defaultInputModes +
                    ", defaultOutputModes=" + this.defaultOutputModes + ", skills=" + this.skills + ")";
        }
    }
}
