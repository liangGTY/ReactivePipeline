package io.github.lianggty.pipeline.core;

import reactor.core.publisher.Flux;

public class TestArk {

    public static void test() {
        Flux.just(1).blockLast();
    }
}
