package io.github.lianggty.pipeline.core.def;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;

import reactor.core.publisher.Mono;

public class AbstractContext implements Context {
    final String name;
    final DefaultPipeline pipeline;
    final Stage stage;

    volatile AbstractContext prev;
    volatile AbstractContext next;

    public AbstractContext(String name, DefaultPipeline pipeline, Stage stage) {
        this.name = name;
        this.pipeline = pipeline;
        this.stage = stage;
    }

    @Override
    public Stage stage() {
        return stage;
    }

    @Override
    public Mono<Result> fireInvoke(Request request, Result response) {
        return next.invokeStage(request, response);
    }

    @Override
    public void fireInit() {
        next.invokeCalcPipelineInit();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
    }

    @Override
    public Context next() {
        return next;
    }

    private Mono<Result> invokeStage(Request req, Result res) {
        Stage stage = stage();
        return stage.execute(this, req, res);
    }

    private void invokeCalcPipelineInit() {
        stage().pipelineInit(this);
    }
}
