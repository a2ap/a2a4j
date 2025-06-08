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
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents information about the provider of an agent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentProvider {

    /**
     * The name of the organization providing the agent.
     */
    @NotNull
    private String organization;

    /**
     * An optional URL pointing to the provider's website or information.
     */
    private String url;

    public AgentProvider() {
    }

    public AgentProvider(String organization, String url) {
        this.organization = organization;
        this.url = url;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AgentProvider that = (AgentProvider) o;
        return Objects.equals(organization, that.organization) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organization, url);
    }

    @Override
    public String toString() {
        return "AgentProvider{" + "organization='" + organization + '\'' + ", url='" + url + '\'' + '}';
    }

    /**
     * Builder for creating instances of {@link AgentProvider}.
     */
    public static class Builder {

        private String organization;

        private String url;

        Builder() {
        }

        public Builder organization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public AgentProvider build() {
            return new AgentProvider(organization, url);
        }

        @Override
        public String toString() {
            return "AgentProvider.AgentProviderBuilder(organization=" + this.organization + ", url=" + this.url + ")";
        }

    }

}
