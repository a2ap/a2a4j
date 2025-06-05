package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a security scheme for agent authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
} 