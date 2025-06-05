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

package io.github.a2ap.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory implementation of QueueManager.
 * 
 * This is the Java equivalent of Python's InMemoryQueueManager.
 */
@Component
public class InMemoryQueueManager implements QueueManager {

    private static final Logger log = LoggerFactory.getLogger(InMemoryQueueManager.class);

    private final ConcurrentMap<String, EventQueue> queues = new ConcurrentHashMap<>();

    @Override
    public EventQueue create(String taskId) {
        log.debug("Creating EventQueue for task: {}", taskId);

        EventQueue newQueue = new EventQueue();
        EventQueue existingQueue = queues.putIfAbsent(taskId, newQueue);

        if (existingQueue != null) {
            log.debug("EventQueue already exists for task: {}", taskId);
        } else {
            log.debug("EventQueue created successfully for task: {}", taskId);
        }

        return newQueue;
    }

    @Override
    public EventQueue tap(String taskId) {
        log.debug("Tapping EventQueue for task: {}", taskId);

        EventQueue mainQueue = queues.get(taskId);
        if (mainQueue == null) {
            log.warn("No EventQueue found for task: {}", taskId);
            return null;
        }

        EventQueue tappedQueue = mainQueue.tap();
        log.debug("Successfully tapped EventQueue for task: {}", taskId);
        return tappedQueue;
    }

    @Override
    public EventQueue get(String taskId) {
        log.debug("Getting EventQueue for task: {}", taskId);
        return queues.get(taskId);
    }

    @Override
    public void remove(String taskId) {
        log.debug("Removing EventQueue for task: {}", taskId);

        EventQueue queue = queues.remove(taskId);
        if (queue != null) {
            queue.close();
            log.debug("EventQueue removed and closed for task: {}", taskId);
        } else {
            log.warn("No EventQueue found to remove for task: {}", taskId);
        }
    }
}
