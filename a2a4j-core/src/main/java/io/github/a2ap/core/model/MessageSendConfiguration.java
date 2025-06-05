package io.github.a2ap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendConfiguration {

    /**
     * accepted output modalities by the client
     */
    private List<String> acceptedOutputModes;
    
    /**
     * number of recent messages to be retrieved
     */
    private Integer historyLength;

    /**
     * where the server should send notifications when disconnected.
     */
    private PushNotificationConfig pushNotificationConfig;

    /**
     * If the server should treat the client as a blocking request
     */
    private Boolean blocking;
}
