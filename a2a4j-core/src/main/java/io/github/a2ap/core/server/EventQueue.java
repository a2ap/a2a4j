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

import io.github.a2ap.core.model.SendStreamingMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Event queue for A2A responses from agent.
 * Acts as a buffer between the agent's asynchronous execution and the
 * server's response handling (e.g., streaming via SSE). Supports tapping
 * to create child queues that receive the same events.
 * This is the Java equivalent of Python's EventQueue using Reactor's Sinks.Many.
 */
public class EventQueue {

   private static final Logger log = LoggerFactory.getLogger(EventQueue.class); 
    
    private final Sinks.Many<SendStreamingMessageResponse> sink;
    private final List<EventQueue> children = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public EventQueue() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        log.debug("EventQueue initialized.");
    }

    /**
     * Enqueues an event to this queue and all its children.
     *
     * @param event The event object to enqueue.
     */
    public void enqueueEvent(SendStreamingMessageResponse event) {
        if (isClosed.get()) {
            log.warn("Queue is closed. Event will not be enqueued.");
            return;
        }
        
        log.debug("Enqueuing event of type: {}", event.getClass().getSimpleName());

        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure()) {
            log.warn("Failed to enqueue event: {}", result);
        }

        // Propagate to children
        for (EventQueue child : children) {
            child.enqueueEvent(event);
        }
    }

    /**
     * Returns a Flux that emits events from this queue.
     *
     * @return A Flux of events from the queue.
     */
    public Flux<SendStreamingMessageResponse> asFlux() {
        return sink.asFlux();
    }

    /**
     * Taps the event queue to create a new child queue that receives all future events.
     *
     * @return A new EventQueue instance that will receive all events enqueued
     *         to this parent queue from this point forward.
     */
    public EventQueue tap() {
        log.debug("Tapping EventQueue to create a child queue.");
        EventQueue childQueue = new EventQueue();
        children.add(childQueue);
        return childQueue;
    }

    /**
     * Closes the queue for future push events.
     * Once closed, the underlying Flux will complete.
     * Also closes all child queues.
     */
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            log.debug("Closing EventQueue.");
            sink.tryEmitComplete();

            // Close all child queues
            for (EventQueue child : children) {
                child.close();
            }
        }
    }

    /**
     * Checks if the queue is closed.
     *
     * @return true if the queue is closed, false otherwise.
     */
    public boolean isClosed() {
        return isClosed.get();
    }
} 
