package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration for push notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushNotificationConfig {

    /**
     * The URL to send push notifications to.
     * Required field.
     */
    @JsonProperty("url")
    private String url;

    /**
     * The authentication token for push notifications.
     */
    @JsonProperty("auth_token")
    private String authToken;
}
