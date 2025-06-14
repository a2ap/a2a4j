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

package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a message exchanged between agents. A message can contain multiple parts of
 * different types (text, file, data).
 */
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
     * "user" | "agent"
     * The role of the message sender (e.g., "user", "assistant"). Required field.
     */
    @JsonProperty("role")
    private String role;

    /**
     * The parts that make up the message content. Each part can be a TextPart, FilePart,
     * or DataPart. Required field.
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
    private String kind = "message";

    public Message() {
    }

    public Message(String messageId, String taskId, String contextId, String role, List<Part> parts,
                   Map<String, Object> metadata, String kind) {
        this.messageId = messageId;
        this.taskId = taskId;
        this.contextId = contextId;
        this.role = role;
        this.parts = parts;
        this.metadata = metadata;
        this.kind = kind;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Message message = (Message) o;
        return Objects.equals(messageId, message.messageId) && Objects.equals(taskId, message.taskId)
                && Objects.equals(contextId, message.contextId) && Objects.equals(role, message.role)
                && Objects.equals(parts, message.parts) && Objects.equals(metadata, message.metadata)
                && Objects.equals(kind, message.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, taskId, contextId, role, parts, metadata, kind);
    }

    @Override
    public String toString() {
        return "Message{" + "messageId='" + messageId + '\'' + ", taskId='" + taskId + '\'' + ", contextId='"
                + contextId + '\'' + ", role='" + role + '\'' + ", parts=" + parts + ", metadata=" + metadata
                + ", kind='" + kind + '\'' + '}';
    }

    /**
     * Builder class for Message.
     */
    public static class Builder {

        private String messageId;

        private String taskId;

        private String contextId;

        private String role;

        private List<Part> parts;

        private Map<String, Object> metadata;

        private String kind = "message";

        private Builder() {
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder contextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder parts(List<Part> parts) {
            this.parts = parts;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder kind(String kind) {
            this.kind = kind == null ? "message" : kind;
            return this;
        }

        public Message build() {
            return new Message(messageId, taskId, contextId, role, parts, metadata, kind);
        }
    }

}
