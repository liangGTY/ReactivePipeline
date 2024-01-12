package io.github.lianggty.pipeline.core.def;

import io.github.lianggty.pipeline.core.*;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public class RecordDependenciesPipeline implements Pipeline {

    private final Pipeline delegatePipeline;
    private final Object source;

    public RecordDependenciesPipeline(Object source, Pipeline delegatePipeline) {
        this.source = source;
        this.delegatePipeline = delegatePipeline;
    }

    @Override
    public String name() {
        return delegatePipeline.name();
    }

    @Override
    public Pipeline addLast(Stage stage) {
        return delegatePipeline.addLast(stage);
    }

    @Override
    public Pipeline addLast(String name, Stage stage) {
        return delegatePipeline.addLast(name, stage);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        T bean = delegatePipeline.getBean(beanClass);
        delegatePipeline.recordDependencies(source, bean);
        return bean;
    }

    @Override
    public <T> T registerCachedIO(Class<T> IOClass) {
        T bean = delegatePipeline.registerCachedIO(IOClass);
        delegatePipeline.recordDependencies(source, bean);
        return bean;
    }

    @Override
    public Option<Context> context(String name) {
        return delegatePipeline.context(name);
    }

    @Override
    public Option<Context> context(Class<? extends Stage> stageClazz) {
        return delegatePipeline.context(stageClazz);
    }

    @Override
    public Request originalRequest() {
        return delegatePipeline.originalRequest();
    }

    @Override
    public Mono<Result> startCalc() {
        return delegatePipeline.startCalc();
    }

    @Override
    public void recordDependencies(Object source, Object target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Stage> stages() {
        return delegatePipeline.stages();
    }

    @Override
    public Dependency dependency() {
        return delegatePipeline.dependency();
    }
}
