package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a task exchanged between agents.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

    /**
     * The unique identifier of the task.
     * Required field.
     */
    @JsonProperty("id")
    private String id;

    /**
     * The agent card of the task sender.
     */
    @JsonProperty("sender")
    private AgentCard sender;

    /**
     * The agent card of the task receiver.
     */
    @JsonProperty("receiver")
    private AgentCard receiver;

    /**
     * The input messages for the task.
     * Required field.
     */
    @JsonProperty("input")
    private List<Message> input;

    /**
     * The current status of the task.
     */
    @JsonProperty("status")
    private TaskStatus status;

    /**
     * The authentication information for the task.
     */
    @JsonProperty("authentication")
    private Object authentication;

    /**
     * The metadata associated with the task.
     */
    @JsonProperty("metadata")
    private Object metadata;
}
