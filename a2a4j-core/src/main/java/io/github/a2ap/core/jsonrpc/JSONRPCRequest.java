package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a JSON-RPC request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRPCRequest {

    /**
     * The JSON-RPC version, always "2.0".
     * Required field.
     */
    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";

    /**
     * The method to be invoked.
     * Required field.
     */
    @JsonProperty("method")
    private String method;

    /**
     * The parameters to the method.
     */
    @JsonProperty("params")
    private Object params;

    /**
     * The request identifier.
     * Required field.
     */
    @JsonProperty("id")
    private String id;
}
