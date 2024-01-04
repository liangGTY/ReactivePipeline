package io.github.lianggty.pipeline.core;

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
}
