package io.github.a2ap.core.model;

import java.util.List;
import java.util.Objects;

public class MessageSendConfiguration {

    /**
     * accepted output modalities by the client
     */
    private List<String> acceptedOutputModes;

    /**
     * number of recent messages to be retrieved
     */
    private Integer historyLength;

    /**
     * where the server should send notifications when disconnected.
     */
    private PushNotificationConfig pushNotificationConfig;

    /**
     * If the server should treat the client as a blocking request
     */
    private Boolean blocking;

    public MessageSendConfiguration() {
    }

    public MessageSendConfiguration(List<String> acceptedOutputModes, Integer historyLength,
            PushNotificationConfig pushNotificationConfig, Boolean blocking) {
        this.acceptedOutputModes = acceptedOutputModes;
        this.historyLength = historyLength;
        this.pushNotificationConfig = pushNotificationConfig;
        this.blocking = blocking;
    }

    public static MessageSendConfigurationBuilder builder() {
        return new MessageSendConfigurationBuilder();
    }

    public List<String> getAcceptedOutputModes() {
        return acceptedOutputModes;
    }

    public void setAcceptedOutputModes(List<String> acceptedOutputModes) {
        this.acceptedOutputModes = acceptedOutputModes;
    }

    public Integer getHistoryLength() {
        return historyLength;
    }

    public void setHistoryLength(Integer historyLength) {
        this.historyLength = historyLength;
    }

    public PushNotificationConfig getPushNotificationConfig() {
        return pushNotificationConfig;
    }

    public void setPushNotificationConfig(PushNotificationConfig pushNotificationConfig) {
        this.pushNotificationConfig = pushNotificationConfig;
    }

    public Boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(Boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MessageSendConfiguration that = (MessageSendConfiguration) o;
        return Objects.equals(acceptedOutputModes, that.acceptedOutputModes) &&
                Objects.equals(historyLength, that.historyLength) &&
                Objects.equals(pushNotificationConfig, that.pushNotificationConfig) &&
                Objects.equals(blocking, that.blocking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acceptedOutputModes, historyLength, pushNotificationConfig, blocking);
    }

    @Override
    public String toString() {
        return "MessageSendConfiguration{" +
                "acceptedOutputModes=" + acceptedOutputModes +
                ", historyLength=" + historyLength +
                ", pushNotificationConfig=" + pushNotificationConfig +
                ", blocking=" + blocking +
                '}';
    }

    public static class MessageSendConfigurationBuilder {
        private List<String> acceptedOutputModes;
        private Integer historyLength;
        private PushNotificationConfig pushNotificationConfig;
        private Boolean blocking;

        MessageSendConfigurationBuilder() {
        }

        public MessageSendConfigurationBuilder acceptedOutputModes(List<String> acceptedOutputModes) {
            this.acceptedOutputModes = acceptedOutputModes;
            return this;
        }

        public MessageSendConfigurationBuilder historyLength(Integer historyLength) {
            this.historyLength = historyLength;
            return this;
        }

        public MessageSendConfigurationBuilder pushNotificationConfig(PushNotificationConfig pushNotificationConfig) {
            this.pushNotificationConfig = pushNotificationConfig;
            return this;
        }

        public MessageSendConfigurationBuilder blocking(Boolean blocking) {
            this.blocking = blocking;
            return this;
        }

        public MessageSendConfiguration build() {
            return new MessageSendConfiguration(acceptedOutputModes, historyLength, pushNotificationConfig, blocking);
        }

        @Override
        public String toString() {
            return "MessageSendConfiguration.MessageSendConfigurationBuilder(" +
                    "acceptedOutputModes=" + this.acceptedOutputModes +
                    ", historyLength=" + this.historyLength +
                    ", pushNotificationConfig=" + this.pushNotificationConfig +
                    ", blocking=" + this.blocking + ")";
        }
    }
}
