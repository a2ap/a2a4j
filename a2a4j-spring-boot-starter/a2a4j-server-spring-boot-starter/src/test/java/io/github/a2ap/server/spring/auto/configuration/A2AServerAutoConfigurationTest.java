package io.github.a2ap.server.spring.auto.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Unit tests for {@link A2AServerAutoConfiguration}.
 */
class A2AServerAutoConfigurationTest {

    @Test
    void testAutoConfiguration() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(A2AServerAutoConfiguration.class);
        
        contextRunner.run(context -> {
            assertNotNull(context);
        });
    }

    @Test
    void testWithProperties() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(A2AServerAutoConfiguration.class)
                .withPropertyValues("a2a.server.enabled=true");
        
        contextRunner.run(context -> {
            assertNotNull(context);
        });
    }

    @Test
    void testConstructor() {
        A2AServerAutoConfiguration config = new A2AServerAutoConfiguration();
        assertNotNull(config);
    }
} 