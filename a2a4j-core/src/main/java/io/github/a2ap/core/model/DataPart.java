package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a data part of a message, containing structured data.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPart extends Part {

    /**
     * The type of the part, always "data" for DataPart.
     * Required field.
     */
    @JsonProperty("type")
    private final String type = "data";

    /**
     * The structured data content.
     * Required field.
     */
    @JsonProperty("data")
    private Object data;

    /**
     * Optional metadata associated with the part.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
