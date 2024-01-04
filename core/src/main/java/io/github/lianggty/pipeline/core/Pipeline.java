package io.github.lianggty.pipeline.core;

import io.github.lianggty.pipeline.core.def.Dependency;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface Pipeline {

    String name();

    Pipeline addLast(Stage stage);

    Pipeline addLast(String name, Stage stage);

    <T> T getBean(Class<T> beanClass);

    <T> T registerCachedIO(Class<T> IOClass);

    Option<Context> context(String name);

    Option<Context> context(Class<? extends Stage> stageClazz);

    Request originalRequest();

    Mono<Result> startCalc();

    void recordDependencies(Object source, Object target);

    List<Stage> stages();

    Dependency dependency();
}
