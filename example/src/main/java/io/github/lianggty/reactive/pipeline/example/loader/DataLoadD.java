package io.github.lianggty.reactive.pipeline.example.loader;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.def.AbstractDataLoader;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class DataLoadD extends AbstractDataLoader<Long, Long> {

    public DataLoadD(Pipeline pipeline) {
        super(pipeline);
    }

    @Override
    public Mono<Long> upstream() {
        return Mono.delay(Duration.ofMillis(100))
                .thenReturn(10L);
    }
}