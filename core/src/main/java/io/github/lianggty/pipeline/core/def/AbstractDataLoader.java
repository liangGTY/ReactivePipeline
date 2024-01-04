package io.github.lianggty.pipeline.core.def;

import io.github.lianggty.pipeline.core.DataLoader;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.exception.IOSubscribedException;

import io.vavr.collection.List;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;

public abstract class AbstractDataLoader<Key, T> extends Mono<T> implements DataLoader<T> {

    private final MonoProcessor<T> processor = MonoProcessor.create();
    private final AtomicBoolean firstSubscribe = new AtomicBoolean(false);

    private List<Publisher<Key>> needLoadKey = List.empty();
    protected final Pipeline pipeline;

    public AbstractDataLoader(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public abstract Mono<T> upstream();

    public String name() {
        return getClass().getSimpleName();
    }

    public Mono<T> fallback(Throwable throwable) {
        return Mono.error(throwable);
    }

    public synchronized void addNeedLoadUserId(Publisher<Key> keyPublisher) {
        if (firstSubscribe.get()) {
            throw new IOSubscribedException("IO subscribed can't change state");
        }
        this.needLoadKey = needLoadKey.prepend(keyPublisher);
    }

    public Flux<Key> getNeedLoadKey() {
        return Flux.merge(needLoadKey);
    }

    @Override
    public T get() {
        if (!processor.isSuccess()) {
            throw new RuntimeException("IO not complete or Error.", processor.getError());
        }
        return processor.block();
    }

    @Override
    public boolean needWait() {
        return false;
    }

    @Override
    public void subscribe(CoreSubscriber<? super T> actual) {
        if (firstSubscribe.compareAndSet(false, true)) {
            upstream()
                    .onErrorResume(this::fallback)
                    .subscribe(processor);
        }
        processor.subscribe(actual);
    }
}
