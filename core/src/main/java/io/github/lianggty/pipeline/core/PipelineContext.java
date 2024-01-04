package io.github.lianggty.pipeline.core;

import io.vavr.collection.List;

public interface PipelineContext {

    <T> T registerDataLoader(Class<T> IOClass);

    List<DataLoader<?>> getAllDataLoader();

    <T> T getBean(Class<T> beanClass);
}
