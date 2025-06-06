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

package io.github.a2ap.core.server.config;

import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(A2aServerProperties.class)
public class A2aServerAutoConfiguration {

	@Bean(name = "a2aServerSelfCard")
	public AgentCard a2aServerSelfCard(final A2aServerProperties a2aServerProperties) {
		// Create and return a default AgentCard instance
		return AgentCard.builder()
			.name(a2aServerProperties.getName())
			.url(a2aServerProperties.getUrl())
			.version(a2aServerProperties.getVersion())
			.description(a2aServerProperties.getDescription())
			.capabilities(AgentCapabilities.builder()
				.streaming(a2aServerProperties.getCapabilities().isStreaming())
				.pushNotifications(a2aServerProperties.getCapabilities().isPushNotifications())
				.stateTransitionHistory(a2aServerProperties.getCapabilities().isStateTransitionHistory())
				.build())
			.build();
	}

}
