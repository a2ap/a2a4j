package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a JSON-RPC response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRPCResponse {

    /**
     * The JSON-RPC version, always "2.0".
     * Required field.
     */
    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";

    /**
     * The result of the method invocation.
     * This field is mutually exclusive with error.
     */
    @JsonProperty("result")
    private Object result;

    /**
     * The error object if an error occurred.
     * This field is mutually exclusive with result.
     */
    @JsonProperty("error")
    private JSONRPCError error;

    /**
     * The request identifier.
     * Required field.
     */
    @JsonProperty("id")
    private String id;
}
