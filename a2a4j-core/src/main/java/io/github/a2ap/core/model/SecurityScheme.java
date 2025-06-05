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

import java.util.Objects;

/**
 * Represents a security scheme for agent authentication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityScheme {

    /**
     * The type of security scheme (e.g., "http", "apiKey", "oauth2").
     */
    private String type;

    /**
     * The scheme name for HTTP authentication (e.g., "bearer", "basic").
     */
    private String scheme;

    /**
     * The name of the header, query parameter or cookie for API key authentication.
     */
    private String name;

    /**
     * The location of the API key (e.g., "header", "query", "cookie").
     */
    private String in;

    /**
     * A description of the security scheme.
     */
    private String description;

    /**
     * The bearer format for bearer token authentication.
     */
    private String bearerFormat;

    public SecurityScheme() {
    }

    public SecurityScheme(String type, String scheme, String name, String in,
            String description, String bearerFormat) {
        this.type = type;
        this.scheme = scheme;
        this.name = name;
        this.in = in;
        this.description = description;
        this.bearerFormat = bearerFormat;
    }

    public static SecuritySchemeBuilder builder() {
        return new SecuritySchemeBuilder();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBearerFormat() {
        return bearerFormat;
    }

    public void setBearerFormat(String bearerFormat) {
        this.bearerFormat = bearerFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SecurityScheme that = (SecurityScheme) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(scheme, that.scheme) &&
                Objects.equals(name, that.name) &&
                Objects.equals(in, that.in) &&
                Objects.equals(description, that.description) &&
                Objects.equals(bearerFormat, that.bearerFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, scheme, name, in, description, bearerFormat);
    }

    @Override
    public String toString() {
        return "SecurityScheme{" +
                "type='" + type + '\'' +
                ", scheme='" + scheme + '\'' +
                ", name='" + name + '\'' +
                ", in='" + in + '\'' +
                ", description='" + description + '\'' +
                ", bearerFormat='" + bearerFormat + '\'' +
                '}';
    }

    public static class SecuritySchemeBuilder {
        private String type;
        private String scheme;
        private String name;
        private String in;
        private String description;
        private String bearerFormat;

        SecuritySchemeBuilder() {
        }

        public SecuritySchemeBuilder type(String type) {
            this.type = type;
            return this;
        }

        public SecuritySchemeBuilder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public SecuritySchemeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SecuritySchemeBuilder in(String in) {
            this.in = in;
            return this;
        }

        public SecuritySchemeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SecuritySchemeBuilder bearerFormat(String bearerFormat) {
            this.bearerFormat = bearerFormat;
            return this;
        }

        public SecurityScheme build() {
            return new SecurityScheme(type, scheme, name, in, description, bearerFormat);
        }

        @Override
        public String toString() {
            return "SecurityScheme.SecuritySchemeBuilder(type=" + this.type +
                    ", scheme=" + this.scheme +
                    ", name=" + this.name +
                    ", in=" + this.in +
                    ", description=" + this.description +
                    ", bearerFormat=" + this.bearerFormat + ")";
        }
    }
}
