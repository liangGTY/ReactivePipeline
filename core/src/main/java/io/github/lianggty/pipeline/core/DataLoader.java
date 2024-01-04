package io.github.lianggty.pipeline.core;

import org.reactivestreams.Publisher;

public interface DataLoader<T> extends Publisher<T> {

    T get();

    boolean needWait();
}
