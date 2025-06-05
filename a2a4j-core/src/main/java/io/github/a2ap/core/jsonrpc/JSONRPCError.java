package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a JSON-RPC error.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRPCError {

    /**
     * The error code.
     * Required field.
     */
    @JsonProperty("code")
    private int code;

    /**
     * The error message.
     * Required field.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Additional data about the error.
     */
    @JsonProperty("data")
    private Object data;

    // Standard JSON-RPC error codes
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    // A2A specific error codes
    public static final int TASK_NOT_FOUND = -32000;
    public static final int AUTHENTICATION_FAILED = -32001;
    public static final int PUSH_NOTIFICATION_NOT_SUPPORTED = -32002;
}
