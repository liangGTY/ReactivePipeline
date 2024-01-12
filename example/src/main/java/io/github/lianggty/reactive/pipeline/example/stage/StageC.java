package io.github.lianggty.reactive.pipeline.example.stage;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.reactive.pipeline.example.LongResult;
import io.github.lianggty.reactive.pipeline.example.loader.DataLoadC;
import io.github.lianggty.reactive.pipeline.example.loader.DataLoadE;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class StageC implements Stage {

    @Override
    public void pipelineInit(Context ctx) {
        ctx.pipeline().registerCachedIO(DataLoadE.class);
        ctx.fireInit();
    }

    @Override
    public Mono<Result> execute(Context ctx, Request req, Result result) {
        return ctx.getBean(DataLoadE.class).flatMap(it -> {
            log.info("stageC get result: {}", it);

            result.cast(LongResult.class).setStageCRes(it);

            return ctx.fireInvoke(req, result);
        });
    }
}
