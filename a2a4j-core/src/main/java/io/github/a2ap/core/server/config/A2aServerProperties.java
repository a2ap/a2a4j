package io.github.a2ap.core.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Configuration properties for A2A protocol.
 * This class can be extended to include specific properties as needed.
 */
@ConfigurationProperties(prefix = "a2a.server")
public class A2aServerProperties implements Serializable {

    private static final long serialVersionUID = -608274692651491547L;

    private String name;
    private String description;
    private String version;
    private String url;
    private Capabilities capabilities = new Capabilities();

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public static class Capabilities implements Serializable {

        private static final long serialVersionUID = 2371695651871067858L;

        private boolean streaming = true;
        private boolean pushNotifications = false;
        private boolean stateTransitionHistory = true;

        public boolean isStreaming() {
            return streaming;
        }

        public void setStreaming(boolean streaming) {
            this.streaming = streaming;
        }

        public boolean isPushNotifications() {
            return pushNotifications;
        }

        public void setPushNotifications(boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
        }

        public boolean isStateTransitionHistory() {
            return stateTransitionHistory;
        }

        public void setStateTransitionHistory(boolean stateTransitionHistory) {
            this.stateTransitionHistory = stateTransitionHistory;
        }
    }
}
