package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
