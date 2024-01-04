package io.github.lianggty.pipeline.core;

import reactor.core.publisher.Mono;

public interface Invoker {

    void fireInit();

    Mono<Result> fireInvoke(Request req, Result res);
}
