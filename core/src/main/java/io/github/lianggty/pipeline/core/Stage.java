package io.github.lianggty.pipeline.core;

import reactor.core.publisher.Mono;

public interface Stage {

    void pipelineInit(final Context ctx);

    Mono<Result> execute(Context ctx, Request req, Result result);

    default String name() {
        return this.getClass().getSimpleName();
    }
}
