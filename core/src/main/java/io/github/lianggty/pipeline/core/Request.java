package io.github.lianggty.pipeline.core;

@SuppressWarnings("unchecked")
public interface Request {

    default <T> T cast() {
        return (T) this;
    }

    default <T> T cast(Class<T> clazz) {
        return (T) this;
    }
}
