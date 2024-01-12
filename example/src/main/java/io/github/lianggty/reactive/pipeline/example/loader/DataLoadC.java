package io.github.lianggty.reactive.pipeline.example.loader;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.def.AbstractDataLoader;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class DataLoadC extends AbstractDataLoader<Long, Long> {

    public DataLoadC(Pipeline pipeline) {
        super(pipeline);
    }

    @Override
    public Mono<Long> upstream() {
        return pipeline.getBean(DataLoadB.class)
                .delayElement(Duration.ofMillis(100))
                .map(it -> it + 2L);
    }
}