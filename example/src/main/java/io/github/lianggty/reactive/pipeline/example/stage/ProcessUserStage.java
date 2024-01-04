package io.github.lianggty.reactive.pipeline.example.stage;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.reactive.pipeline.example.loader.UserDataLoader;

import io.vavr.collection.Map;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ProcessUserStage implements Stage {

    private UserDataLoader userDataLoader;

    @Override
    public void pipelineInit(Context ctx) {
        this.userDataLoader = ctx.getBean(UserDataLoader.class);
        userDataLoader.addNeedLoadUserId(Flux.just(1L, 2L, 3L, 4L));
        ctx.fireInit();
    }

    @Override
    public Mono<Result> execute(Context ctx, Request req, Result result) {

        Map<Long, UserDataLoader.User> userMap = userDataLoader.get();

        log.info("getUser: {}", userMap.size());

        return ctx.fireInvoke(req, result);
    }
}
