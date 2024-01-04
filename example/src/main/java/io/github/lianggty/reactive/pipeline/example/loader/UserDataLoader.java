package io.github.lianggty.reactive.pipeline.example.loader;

import java.time.Duration;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.def.AbstractDataLoader;
import io.github.lianggty.reactive.pipeline.example.loader.UserDataLoader.User;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserDataLoader extends AbstractDataLoader<Long, Map<Long, User>> {

    public UserDataLoader(Pipeline pipeline) {
        super(pipeline);
    }

    @Override
    public Mono<Map<Long, User>> upstream() {
        return getNeedLoadKey()
                .delaySubscription(Duration.ofMillis(50))
                .buffer(20)
                .flatMap(userIds -> {
                    // batch rpc
                    return Flux.fromIterable(userIds)
                            .map(User::new);
                })
                .reduce(HashMap.<Long, User> empty(), (accumulated, user) -> {
                    return accumulated.put(user.userId, user);
                });
    }

    @Override
    public boolean needWait() {
        return true;
    }

    public static class User {
        private Long userId;

        public User(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
