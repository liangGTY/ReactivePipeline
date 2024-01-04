package io.github.lianggty.reactive.pipeline.example.stage;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.reactive.pipeline.example.LongResult;
import io.github.lianggty.reactive.pipeline.example.loader.DataLoadA;

import io.vavr.collection.Map;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class StageA implements Stage {

    private DataLoadA dataLoadA;

    @Override
    public void pipelineInit(Context ctx) {
        this.dataLoadA = ctx.getBean(DataLoadA.class);

        ctx.fireInit();
    }

    @Override
    public Mono<Result> execute(Context ctx, Request req, Result result) {
        return dataLoadA.flatMap(it -> {
            log.info("stageA get result: {}", it);

            result.cast(LongResult.class).setStageARes(it);

            return ctx.fireInvoke(req, result);
        });
    }
}
