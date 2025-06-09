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
import java.util.Objects;

/**
 * Represents authentication information for an agent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentAuthentication {

    /**
     * The authentication schemes supported by the agent. Required field.
     */
    @JsonProperty("schemes")
    private List<String> schemes;

    /**
     * Optional credentials for authentication.
     */
    @JsonProperty("credentials")
    private String credentials;

    public AgentAuthentication() {
    }

    public AgentAuthentication(List<String> schemes, String credentials) {
        this.schemes = schemes;
        this.credentials = credentials;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AgentAuthentication that = (AgentAuthentication) o;
        return Objects.equals(schemes, that.schemes) && Objects.equals(credentials, that.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemes, credentials);
    }

    @Override
    public String toString() {
        return "AgentAuthentication{" + "schemes=" + schemes + ", credentials='" + credentials + '\'' + '}';
    }

    /**
     * Builder class for AgentAuthentication.
     */
    public static class Builder {

        private List<String> schemes;

        private String credentials;

        private Builder() {
        }

        public Builder schemes(List<String> schemes) {
            this.schemes = schemes;
            return this;
        }

        public Builder credentials(String credentials) {
            this.credentials = credentials;
            return this;
        }

        public AgentAuthentication build() {
            return new AgentAuthentication(schemes, credentials);
        }

    }

}
