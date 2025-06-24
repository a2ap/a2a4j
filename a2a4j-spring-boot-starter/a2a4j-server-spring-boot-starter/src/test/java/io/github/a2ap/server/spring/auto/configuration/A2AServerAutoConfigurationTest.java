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
