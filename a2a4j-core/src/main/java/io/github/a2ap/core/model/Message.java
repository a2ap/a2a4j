package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a message exchanged between agents.
 * A message can contain multiple parts of different types (text, file, data).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements SendMessageResponse, SendStreamingMessageResponse {

    /**
     * message id
     */
    @JsonProperty("messageId")
    private String messageId;

    /**
     * task id
     */
    @JsonProperty("taskId")
    private String taskId;
    
    /**
     * context id
     */
    @JsonProperty("contextId")
    private String contextId;
    
    /**
     * The role of the message sender (e.g., "user", "assistant").
     * Required field.
     */
    @JsonProperty("role")
    private String role;

    /**
     * The parts that make up the message content.
     * Each part can be a TextPart, FilePart, or DataPart.
     * Required field.
     */
    @JsonProperty("parts")
    private List<Part> parts;

    /**
     * message metadata
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * kind type
     */
    @JsonProperty("kind")
    private String kind;

}
