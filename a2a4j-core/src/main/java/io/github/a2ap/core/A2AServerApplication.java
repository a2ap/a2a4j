package io.github.a2ap.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Main application class for A2A Server.
 */
@SpringBootApplication
public class A2AServerApplication implements WebFluxConfigurer {
    
    public static void main(String[] args) {
        SpringApplication.run(A2AServerApplication.class, args);
    }
    
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
