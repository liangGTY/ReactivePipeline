package io.github.lianggty.pipeline.core.def;

import io.github.lianggty.pipeline.core.Context;
import io.github.lianggty.pipeline.core.PipelineContextFactory;
import io.github.lianggty.pipeline.core.Stage;
import io.github.lianggty.pipeline.core.DataLoader;
import io.github.lianggty.pipeline.core.Invoker;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.PipelineContext;
import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;

public class DefaultPipeline implements Pipeline, Invoker {

    final String name;

    final AbstractContext head;
    final AbstractContext tail;

    final Request originalReq;
    final Result emptyResult;

    final PipelineContext beanContext;


    public DefaultPipeline(String name, Request originalReq, Result emptyResult) {
        this.name = name;
        this.originalReq = originalReq;
        this.emptyResult = emptyResult;

        this.head = new HeadContext(this);
        this.tail = new TailContext(this);

        head.next = tail;
        tail.prev = head;
        this.beanContext = PipelineContextFactory.newPipelineContext(this);
    }

//    public DefaultPipeline(String name, Request originalReq, Result emptyResult, Pipeline parent) {
//        this.name = name;
//        this.originalReq = originalReq;
//        this.emptyResult = emptyResult;
//
//        this.head = new HeadContext(this);
//        this.tail = new TailContext(this);
//
//        head.next = tail;
//        tail.prev = head;
//        this.beanContext = new PipelineContext() {
//            @Override
//            public <T> T registerDataLoader(Class<T> IOClass) {
//                return parent.registerCachedIO(IOClass);
//            }
//
//            @Override
//            public List<DataLoader<?>> getAllDataLoader() {
//                return parent.;
//            }
//
//            @Override
//            public <T> T getBean(Class<T> beanClass) {
//                return null;
//            }
//        };
//    }

    private AbstractContext newContext(String name, Stage stage) {
        return new DefaultContext(name, this, stage);
    }

    private void addLast0(AbstractContext newCtx) {
        AbstractContext prevCtx = tail.prev;
        newCtx.next = tail;
        newCtx.prev = prevCtx;
        tail.prev = newCtx;
        prevCtx.next = newCtx;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Pipeline addLast(Stage stage) {
        return addLast(stage.name(), stage);
    }

    @Override
    public Pipeline addLast(String name, Stage stage) {
        synchronized (this) {
            AbstractContext newCtx = newContext(name, stage);
            addLast0(newCtx);
        }
        return this;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return beanContext.getBean(beanClass);
    }

    @Override
    public <T> T registerCachedIO(Class<T> IOClass) {
        return beanContext.registerDataLoader(IOClass);
    }

    @Override
    public Option<Context> context(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Option<Context> context(Class<? extends Stage> stageClazz) {
        Objects.requireNonNull(stageClazz, "handlerClazz");

        AbstractContext ctx = head.next;

        for (; ; ) {
            if (ctx == null) {
                return Option.none();
            }
            if (stageClazz.isAssignableFrom(ctx.stage().getClass())) {
                return Option.of(ctx);
            }
            ctx = ctx.next;
        }
    }

    @Override
    public Request originalRequest() {
        return originalReq;
    }

    @Override
    public void fireInit() {
        head.fireInit();
    }

    @Override
    public Mono<Result> startCalc() {
        fireInit();

        Map<Boolean, List<DataLoader<?>>> ioMap = beanContext.getAllDataLoader().groupBy(DataLoader::needWait);

        Flux.merge(ioMap.get(false).getOrElse(List.empty())).subscribeOn(Schedulers.boundedElastic()).subscribe();

        Mono<Void> needWaitIOMono = Flux.merge(ioMap.get(true).getOrElse(List.empty())).collectList().then();

        return fireInvoke(originalReq, emptyResult)
                .delaySubscription(needWaitIOMono)
                .timeout(Duration.ofMillis(2000));
    }

    @Override
    public Mono<Result> fireInvoke(Request req, Result res) {
        return Mono.defer(() -> head.fireInvoke(req, res));
    }

    final class HeadContext extends AbstractContext implements Stage {

        public HeadContext(DefaultPipeline pipeline) {
            super("head", pipeline, null);
        }

        @Override
        public Stage stage() {
            return this;
        }

        @Override
        public void pipelineInit(Context ctx) {
            ctx.fireInit();
        }

        @Override
        public Mono<Result> execute(Context ctx, Request request, Result result) {
            return ctx.fireInvoke(request, result);
        }
    }

    final class TailContext extends AbstractContext implements Stage {

        public TailContext(DefaultPipeline pipeline) {
            super("tail", pipeline, null);
        }

        @Override
        public Stage stage() {
            return this;
        }

        @Override
        public void pipelineInit(Context ctx) {
        }

        @Override
        public Mono<Result> execute(Context ctx, Request request, Result result) {
            return Mono.just(result);
        }
    }
}
