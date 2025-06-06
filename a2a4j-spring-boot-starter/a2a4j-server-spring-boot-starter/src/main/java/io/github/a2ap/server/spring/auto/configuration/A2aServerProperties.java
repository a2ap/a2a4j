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

package io.github.a2ap.server.spring.auto.configuration;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for A2A Server.
 * 
 * This class provides configuration options for setting up an A2A server,
 * including agent metadata, capabilities, and server settings.
 * 
 * <p>Properties can be configured in application.yml or application.properties:
 * <pre>
 * a2a:
 *   server:
 *     enabled: true
 *     name: My A2A Agent
 *     description: A sample A2A agent
 *     version: 1.0.0
 *     url: https://my-agent.example.com
 *     capabilities:
 *       streaming: true
 *       push-notifications: false
 *       state-transition-history: true
 * </pre>
 * 
 * @see io.github.a2ap.core.model.AgentCard
 * @see io.github.a2ap.core.model.AgentCapabilities
 */
@ConfigurationProperties(prefix = "a2a.server")
public class A2aServerProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = -608274692651491547L;

    /**
     * Whether the A2A server is enabled.
     */
    private boolean enabled = true;
    
    /**
     * The name of the agent.
     */
    private String name;
    
    /**
     * A description of what the agent does.
     */
    private String description;
    
    /**
     * The version of the agent.
     */
    private String version;
    
    /**
     * The base URL where the agent can be reached.
     */
    private String url;
    
    /**
     * Agent capabilities configuration.
     */
    private Capabilities capabilities = new Capabilities();

    /**
     * Returns whether the A2A server is enabled.
     * 
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the A2A server is enabled.
     * 
     * @param enabled true to enable the server, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the agent name.
     * 
     * @return the agent name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the agent name.
     * 
     * @param name the agent name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the agent description.
     * 
     * @return the agent description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the agent description.
     * 
     * @param description the agent description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the agent version.
     * 
     * @return the agent version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the agent version.
     * 
     * @param version the agent version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the agent URL.
     * 
     * @return the agent URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the agent URL.
     * 
     * @param url the agent URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the agent capabilities configuration.
     * 
     * @return the capabilities configuration
     */
    public Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the agent capabilities configuration.
     * 
     * @param capabilities the capabilities configuration to set
     */
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Configuration for agent capabilities.
     * 
     * This class defines what features the agent supports,
     * such as streaming responses, push notifications, and state history.
     */
    public static class Capabilities implements Serializable {

        private static final long serialVersionUID = 2371695651871067858L;

        /**
         * Whether the agent supports streaming responses.
         */
        private boolean streaming = true;
        
        /**
         * Whether the agent supports push notifications.
         */
        private boolean pushNotifications = false;
        
        /**
         * Whether the agent maintains state transition history.
         */
        private boolean stateTransitionHistory = true;

        /**
         * Returns whether streaming is supported.
         * 
         * @return true if streaming is supported, false otherwise
         */
        public boolean isStreaming() {
            return streaming;
        }

        /**
         * Sets whether streaming is supported.
         * 
         * @param streaming true to enable streaming support, false to disable
         */
        public void setStreaming(boolean streaming) {
            this.streaming = streaming;
        }

        /**
         * Returns whether push notifications are supported.
         * 
         * @return true if push notifications are supported, false otherwise
         */
        public boolean isPushNotifications() {
            return pushNotifications;
        }

        /**
         * Sets whether push notifications are supported.
         * 
         * @param pushNotifications true to enable push notification support, false to disable
         */
        public void setPushNotifications(boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
        }

        /**
         * Returns whether state transition history is supported.
         * 
         * @return true if state transition history is supported, false otherwise
         */
        public boolean isStateTransitionHistory() {
            return stateTransitionHistory;
        }

        /**
         * Sets whether state transition history is supported.
         * 
         * @param stateTransitionHistory true to enable state transition history, false to disable
         */
        public void setStateTransitionHistory(boolean stateTransitionHistory) {
            this.stateTransitionHistory = stateTransitionHistory;
        }
    }
}
