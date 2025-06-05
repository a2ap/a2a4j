package io.github.a2ap.core.server.impl;

import io.github.a2ap.core.model.Artifact;
import io.github.a2ap.core.model.RequestContext;
import io.github.a2ap.core.model.TaskStatus;
import io.github.a2ap.core.model.TaskUpdate;
import io.github.a2ap.core.server.TaskHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * demo for task handler
 */
@Component
public class DemoTaskHandler implements TaskHandler {
    @Override
    public Flux<TaskUpdate> handle(RequestContext context) {
        return Flux.just(TaskStatus.CANCELLED, TaskStatus.COMPLETED, Artifact.builder().build());
    }
}
