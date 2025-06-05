package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

public class MessageSendParams {

    /**
     * message context
     */
    @JsonProperty("message")
    private Message message;

    /**
     * message others configuration
     */
    @JsonProperty("configuration")
    private MessageSendConfiguration configuration;

    /**
     * message metadata
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public MessageSendParams() {
    }

    public MessageSendParams(Message message, MessageSendConfiguration configuration, Map<String, Object> metadata) {
        this.message = message;
        this.configuration = configuration;
        this.metadata = metadata;
    }

    public static MessageSendParamsBuilder builder() {
        return new MessageSendParamsBuilder();
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageSendConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MessageSendConfiguration configuration) {
        this.configuration = configuration;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MessageSendParams that = (MessageSendParams) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(configuration, that.configuration) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, configuration, metadata);
    }

    @Override
    public String toString() {
        return "MessageSendParams{" +
                "message=" + message +
                ", configuration=" + configuration +
                ", metadata=" + metadata +
                '}';
    }

    public static class MessageSendParamsBuilder {
        private Message message;
        private MessageSendConfiguration configuration;
        private Map<String, Object> metadata;

        MessageSendParamsBuilder() {
        }

        public MessageSendParamsBuilder message(Message message) {
            this.message = message;
            return this;
        }

        public MessageSendParamsBuilder configuration(MessageSendConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public MessageSendParamsBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public MessageSendParams build() {
            return new MessageSendParams(message, configuration, metadata);
        }

        @Override
        public String toString() {
            return "MessageSendParams.MessageSendParamsBuilder(message=" + this.message +
                    ", configuration=" + this.configuration +
                    ", metadata=" + this.metadata + ")";
        }
    }
}
