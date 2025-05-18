package io.github.a2ap.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.github.a2ap")
public class A2AServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(A2AServerApplication.class, args);
    }
}
