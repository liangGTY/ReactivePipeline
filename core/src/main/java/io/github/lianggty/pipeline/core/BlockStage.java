package io.github.lianggty.pipeline.core;

import reactor.core.publisher.Mono;

public interface BlockStage extends Stage {

    Result blockExecute(Context ctx, Request req, Result res);

    default Mono<Result> execute(Context ctx, Request req, Result res) {
        return Mono.just(blockExecute(ctx, req, res));
    }
}
