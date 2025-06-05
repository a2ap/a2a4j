package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a JSON-RPC error.
 */
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

    public JSONRPCError() {
    }

    public JSONRPCError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static JSONRPCErrorBuilder builder() {
        return new JSONRPCErrorBuilder();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JSONRPCError that = (JSONRPCError) o;
        return code == that.code &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }

    @Override
    public String toString() {
        return "JSONRPCError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class JSONRPCErrorBuilder {
        private int code;
        private String message;
        private Object data;

        JSONRPCErrorBuilder() {
        }

        public JSONRPCErrorBuilder code(int code) {
            this.code = code;
            return this;
        }

        public JSONRPCErrorBuilder message(String message) {
            this.message = message;
            return this;
        }

        public JSONRPCErrorBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public JSONRPCError build() {
            return new JSONRPCError(code, message, data);
        }

        @Override
        public String toString() {
            return "JSONRPCError.JSONRPCErrorBuilder(code=" + this.code +
                    ", message=" + this.message +
                    ", data=" + this.data + ")";
        }
    }
}
