/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a JSON-RPC 2.0 error object as defined in the JSON-RPC specification.
 * 
 * This class encapsulates error information returned in JSON-RPC responses when
 * a request cannot be processed successfully. It includes both standard JSON-RPC
 * error codes and A2A protocol-specific error codes.
 * 
 * Standard JSON-RPC error codes (as per specification):
 * - PARSE_ERROR (-32700): Invalid JSON was received
 * - INVALID_REQUEST (-32600): The JSON sent is not a valid Request object
 * - METHOD_NOT_FOUND (-32601): The method does not exist or is not available
 * - INVALID_PARAMS (-32602): Invalid method parameter(s)
 * - INTERNAL_ERROR (-32603): Internal JSON-RPC error
 * 
 * A2A protocol-specific error codes:
 * - TASK_NOT_FOUND (-32000): Requested task does not exist
 * - AUTHENTICATION_FAILED (-32001): Authentication credentials are invalid
 * - PUSH_NOTIFICATION_NOT_SUPPORTED (-32002): Push notifications are not supported
 * 
 * The error object may include additional data to provide more context about
 * the specific error condition.
 */

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRPCError {

    /**
     * The error code. Required field.
     */
    @JsonProperty("code")
    private int code;

    /**
     * The error message. Required field.
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

    public static Builder builder() {
        return new Builder();
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
        return code == that.code && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }

    @Override
    public String toString() {
        return "JSONRPCError{" + "code=" + code + ", message='" + message + '\'' + ", data=" + data + '}';
    }

    /**
     * Builder for creating instances of JSONRPCError.
     */
    public static class Builder {

        private int code;

        private String message;

        private Object data;

        Builder() {
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        /**
         * Builds a new JSONRPCError instance with the provided parameters.
         */
        public JSONRPCError build() {
            return new JSONRPCError(code, message, data);
        }

        @Override
        public String toString() {
            return "JSONRPCError.JSONRPCErrorBuilder(code=" + this.code + ", message=" + this.message + ", data="
                    + this.data + ")";
        }

    }

}
