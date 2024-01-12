package io.github.lianggty.reactive.pipeline.example.loader;

import java.time.Duration;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.def.AbstractDataLoader;

import reactor.core.publisher.Mono;

public class DataLoadA extends AbstractDataLoader<Long, Long> {

    public DataLoadA(Pipeline pipeline) {
        super(pipeline);
    }

    @Override
    public Mono<Long> upstream() {
        return Mono.delay(Duration.ofMillis(100))
                .thenReturn(1L);
    }
}
