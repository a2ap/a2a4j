package io.github.a2ap.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Configuration properties for A2A protocol.
 * This class can be extended to include specific properties as needed.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "a2a.server")
public class A2aServerProperties implements Serializable {

    private static final long serialVersionUID = -608274692651491547L;

    private String name;
    private String description;
    private String version;
    private String url;
    private Capabilities capabilities = new Capabilities();
    
    @Setter
    @Getter
    public static class Capabilities implements Serializable {
        
        private static final long serialVersionUID = 2371695651871067858L;

        private boolean streaming = true;
        private boolean pushNotifications = false;
        private boolean stateTransitionHistory = true;
        
    }
}
