package io.github.lianggty.pipeline.core;

public interface ExternalBeanContext {

    <T> T get(Class<T> clazz);
}
