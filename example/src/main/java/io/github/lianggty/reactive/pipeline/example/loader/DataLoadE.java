package io.github.lianggty.reactive.pipeline.example.loader;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.def.AbstractDataLoader;
import reactor.core.publisher.Mono;

public class DataLoadE extends AbstractDataLoader<Long, Long> {

    public DataLoadE(Pipeline pipeline) {
        super(pipeline);
    }

    @Override
    public Mono<Long> upstream() {
        return pipeline.getBean(DataLoadB.class)
                .zipWith(pipeline.getBean(DataLoadD.class), Long::sum);
    }
}