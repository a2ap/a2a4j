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

import java.util.Objects;

/**
 * Configuration for push notifications.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotificationConfig {

    /**
     * The URL to send push notifications to. Required field.
     */
    @JsonProperty("url")
    private String url;

    /**
     * The authentication token for push notifications.
     */
    @JsonProperty("auth_token")
    private String authToken;

    public PushNotificationConfig() {
    }

    public PushNotificationConfig(String url, String authToken) {
        this.url = url;
        this.authToken = authToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PushNotificationConfig that = (PushNotificationConfig) o;
        return Objects.equals(url, that.url) && Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, authToken);
    }

    @Override
    public String toString() {
        return "PushNotificationConfig{" + "url='" + url + '\'' + ", authToken='" + authToken + '\'' + '}';
    }

    /**
     * Builder class for PushNotificationConfig.
     */
    public static class Builder {

        private String url;

        private String authToken;

        Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public PushNotificationConfig build() {
            return new PushNotificationConfig(url, authToken);
        }

        @Override
        public String toString() {
            return "PushNotificationConfig.PushNotificationConfigBuilder(url=" + this.url + ", authToken="
                    + this.authToken + ")";
        }

    }

}
