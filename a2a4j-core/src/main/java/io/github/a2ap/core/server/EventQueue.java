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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import reactor.core.publisher.Flux;

/**
 * Event queue for A2A responses from agent.
 * <p>
 * Acts as a buffer between the agent's asynchronous execution and the
 * server's response handling (e.g., streaming via SSE). Supports tapping
 * to create child queues that receive the same events.
 * <p>
 * This is the Java equivalent of Python's EventQueue using Reactor's Sinks.Many.
 */
public class EventQueue {

    private static final Logger log = LoggerFactory.getLogger(EventQueue.class);

    private final Map<String, BlockingQueue<SendStreamingMessageResponse>> channelMap = new ConcurrentHashMap<>();
    private final List<EventQueue> children = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final BlockingQueue<SendStreamingMessageResponse> eventQueue = new LinkedBlockingQueue<>();

    /**
     * Register a consumer to consume events from a channel.
     * 
     * @param queueName     the channel name
     * @param consumer      the event consumer
     * @param timeoutMillis the timeout in milliseconds, 0 for no timeout
     * @return true if the consumer was successfully registered, false otherwise
     */
    public boolean consumeEvents(String queueName, BiConsumer<SendStreamingMessageResponse, Throwable> consumer,
            long timeoutMillis) {
        if (isClosed.get()) {
            log.warn("Queue is closed. Cannot consume events.");
            return false;
        }

        BlockingQueue<SendStreamingMessageResponse> queue = channelMap.computeIfAbsent(queueName,
                k -> new LinkedBlockingQueue<>());
        log.info("Start consuming events from channel: {}", queueName);

        try {
            long startTime = System.currentTimeMillis();
            while (!isClosed.get()) {
                try {
                    SendStreamingMessageResponse event;
                    if (timeoutMillis > 0) {
                        event = queue.poll(timeoutMillis, TimeUnit.MILLISECONDS);

                        // Check if we've reached the timeout
                        if (event == null && System.currentTimeMillis() - startTime >= timeoutMillis) {
                            log.info("Timeout reached for channel: {}", queueName);
                            return true;
                        }
                    } else {
                        event = queue.take();
                    }

                    if (event != null) {
                        consumer.accept(event, null);
                    }
                } catch (InterruptedException e) {
                    log.warn("Event consumption interrupted for channel: {}", queueName);
                    Thread.currentThread().interrupt();
                    consumer.accept(null, e);
                    return false;
                } catch (Exception e) {
                    log.error("Error consuming event from channel: {}, error: {}", queueName, e.getMessage());
                    consumer.accept(null, e);
                    return false;
                }
            }

            return true;
        } finally {
            // Consider whether to clean up the queue here
            log.info("Stopped consuming events from channel: {}", queueName);
        }
    }

    /**
     * Publish an event to a channel.
     * 
     * @param queueName the channel name
     * @param event     the event to publish
     * @return true if the event was successfully published, false otherwise
     */
    public boolean publishEvent(String queueName, SendStreamingMessageResponse event) {
        if (isClosed.get()) {
            log.warn("Queue is closed. Event will not be published.");
            return false;
        }

        try {
            BlockingQueue<SendStreamingMessageResponse> queue = channelMap.computeIfAbsent(queueName,
                    k -> new LinkedBlockingQueue<>());
            queue.add(event);
            log.info("Published event to channel: {}", queueName);

            // Propagate to children
            for (EventQueue child : children) {
                child.publishEvent(queueName, event);
            }

            return true;
        } catch (Exception e) {
            log.error("Error publishing event to channel: {}, error: {}", queueName, e.getMessage());
            return false;
        }
    }

    /**
     * Close a channel.
     * 
     * @param queueName the channel name
     * @return true if the channel was successfully closed, false otherwise
     */
    public boolean closeChannel(String queueName) {
        try {
            channelMap.remove(queueName);
            log.info("Closed channel: {}", queueName);
            return true;
        } catch (Exception e) {
            log.error("Error closing channel: {}, error: {}", queueName, e.getMessage());
            return false;
        }
    }

    /**
     * Taps the event queue to create a new child queue that receives all future
     * events.
     *
     * @return A new EventQueue instance that will receive all events enqueued
     * to this parent queue from this point forward.
     */
    public EventQueue tap() {
        if (isClosed.get()) {
            log.warn("Queue is closed. Cannot tap.");
            return null;
        }

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

            // Clear all channels
            channelMap.clear();

            // Close all child queues
            for (EventQueue child : children) {
                child.close();
            }

            // Clear the children list
            children.clear();
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

    /**
     * Enqueues an event in the main event queue.
     *
     * @param event The event to enqueue
     * @return true if the event was successfully enqueued, false otherwise
     */
    public boolean enqueueEvent(SendStreamingMessageResponse event) {
        if (isClosed.get()) {
            log.warn("Queue is closed. Event will not be enqueued.");
            return false;
        }

        try {
            eventQueue.add(event);
            log.debug("Enqueued event: {}", event);

            // Propagate to children
            for (EventQueue child : children) {
                child.enqueueEvent(event);
            }

            return true;
        } catch (Exception e) {
            log.error("Error enqueueing event: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns a Flux of events from the main event queue.
     *
     * @return A Flux that emits events from the queue
     */
    public Flux<SendStreamingMessageResponse> asFlux() {
        return Flux.create(sink -> {
            Thread consumer = new Thread(() -> {
                try {
                    while (!isClosed.get() || !eventQueue.isEmpty()) {
                        try {
                            SendStreamingMessageResponse event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                            if (event != null) {
                                sink.next(event);
                            }
                        } catch (InterruptedException e) {
                            log.warn("Event queue flux consumption interrupted");
                            Thread.currentThread().interrupt();
                            sink.error(e);
                            return;
                        }
                    }
                    sink.complete();
                } catch (Exception e) {
                    log.error("Error in event queue flux: {}", e.getMessage());
                    sink.error(e);
                }
            });
            consumer.setDaemon(true);
            consumer.start();

            sink.onDispose(() -> {
                log.debug("Event queue flux disposed");
            });
        });
    }
}
