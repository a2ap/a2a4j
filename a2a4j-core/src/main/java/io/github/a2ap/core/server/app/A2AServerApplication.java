package io.github.a2ap.core.server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.github.a2ap") // Scan for components in org.a2a.server and its sub-packages
public class A2AServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(A2AServerApplication.class, args);
    }
}
