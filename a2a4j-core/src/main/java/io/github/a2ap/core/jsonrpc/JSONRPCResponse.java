package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a JSON-RPC response.
 */
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
     * result: Message | Task | TaskStatusUpdateEvent | TaskArtifactUpdateEvent;
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

    /**
     * Default constructor
     */
    public JSONRPCResponse() {
    }

    /**
     * Constructor with id
     * 
     * @param id The request identifier
     */
    public JSONRPCResponse(String id) {
        this.id = id;
    }

    /**
     * Constructor with id and result
     * 
     * @param id     The request identifier
     * @param result The result of the method invocation
     */
    public JSONRPCResponse(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    /**
     * Constructor with id and error
     * 
     * @param id    The request identifier
     * @param error The error object
     */
    public JSONRPCResponse(String id, JSONRPCError error) {
        this.id = id;
        this.error = error;
    }

    /**
     * Returns the JSON-RPC version.
     * 
     * @return The JSON-RPC version
     */
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     * Returns the result of the method invocation.
     * 
     * @return The result
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets the result of the method invocation.
     * 
     * @param result The result to set
     */
    public void setResult(Object result) {
        this.result = result;
        this.error = null; // Result and error are mutually exclusive
    }

    /**
     * Returns the error object.
     * 
     * @return The error
     */
    public JSONRPCError getError() {
        return error;
    }

    /**
     * Sets the error object.
     * 
     * @param error The error to set
     */
    public void setError(JSONRPCError error) {
        this.error = error;
        this.result = null; // Result and error are mutually exclusive
    }

    /**
     * Returns the request identifier.
     * 
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the request identifier.
     * 
     * @param id The id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JSONRPCResponse that = (JSONRPCResponse) o;
        return Objects.equals(jsonrpc, that.jsonrpc) &&
                Objects.equals(result, that.result) &&
                Objects.equals(error, that.error) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonrpc, result, error, id);
    }

    @Override
    public String toString() {
        return "JSONRPCResponse{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", result=" + result +
                ", error=" + error +
                ", id='" + id + '\'' +
                '}';
    }
}
