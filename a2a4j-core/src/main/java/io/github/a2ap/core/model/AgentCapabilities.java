package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * Represents the capabilities supported by an agent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCapabilities {
    /**
     * Indicates if the agent supports streaming responses.
     */
    private boolean streaming = false;

    /**
     * Indicates if the agent supports push notifications.
     */
    private boolean pushNotifications = false;

    /**
     * Indicates if the agent supports state transition history.
     */
    private boolean stateTransitionHistory = false;

    public AgentCapabilities() {
    }

    public AgentCapabilities(boolean streaming, boolean pushNotifications, boolean stateTransitionHistory) {
        this.streaming = streaming;
        this.pushNotifications = pushNotifications;
        this.stateTransitionHistory = stateTransitionHistory;
    }

    public static AgentCapabilitiesBuilder builder() {
        return new AgentCapabilitiesBuilder();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AgentCapabilities that = (AgentCapabilities) o;
        return streaming == that.streaming &&
                pushNotifications == that.pushNotifications &&
                stateTransitionHistory == that.stateTransitionHistory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(streaming, pushNotifications, stateTransitionHistory);
    }

    @Override
    public String toString() {
        return "AgentCapabilities{" +
                "streaming=" + streaming +
                ", pushNotifications=" + pushNotifications +
                ", stateTransitionHistory=" + stateTransitionHistory +
                '}';
    }

    public static class AgentCapabilitiesBuilder {
        private boolean streaming = false;
        private boolean pushNotifications = false;
        private boolean stateTransitionHistory = false;

        AgentCapabilitiesBuilder() {
        }

        public AgentCapabilitiesBuilder streaming(boolean streaming) {
            this.streaming = streaming;
            return this;
        }

        public AgentCapabilitiesBuilder pushNotifications(boolean pushNotifications) {
            this.pushNotifications = pushNotifications;
            return this;
        }

        public AgentCapabilitiesBuilder stateTransitionHistory(boolean stateTransitionHistory) {
            this.stateTransitionHistory = stateTransitionHistory;
            return this;
        }

        public AgentCapabilities build() {
            return new AgentCapabilities(streaming, pushNotifications, stateTransitionHistory);
        }

        public String toString() {
            return "AgentCapabilities.AgentCapabilitiesBuilder(streaming=" + this.streaming +
                    ", pushNotifications=" + this.pushNotifications +
                    ", stateTransitionHistory=" + this.stateTransitionHistory + ")";
        }
    }
}
