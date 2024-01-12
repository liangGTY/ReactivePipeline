package io.github.lianggty.reactive.pipeline.example.stage;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.reactive.pipeline.example.LongResult;
import io.github.lianggty.reactive.pipeline.example.loader.DataLoadC;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class StageB implements Stage {
    @Override
    public void pipelineInit(Context ctx) {
        ctx.pipeline().registerCachedIO(DataLoadC.class);
        ctx.fireInit();
    }

    @Override
    public Mono<Result> execute(Context ctx, Request req, Result result) {
        return ctx.getBean(DataLoadC.class).flatMap(it -> {
            log.info("stageB get result: {}", it);

            result.cast(LongResult.class).setStageBRes(it);

            return ctx.fireInvoke(req, result);
        });
    }
}
