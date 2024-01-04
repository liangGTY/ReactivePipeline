package io.github.lianggty.reactive.pipeline.example;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.reactive.pipeline.example.stage.StageA;

import io.github.lianggty.reactive.pipeline.example.stage.StageB;
import io.github.lianggty.reactive.pipeline.example.stage.StageC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.lianggty.pipeline.core.Request;
import io.github.lianggty.pipeline.core.Result;
import io.github.lianggty.pipeline.core.def.DefaultPipeline;

import io.vavr.collection.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Pipeline pipeline =
                new DefaultPipeline("example", new UserRequest(List.ofAll(1L, 2L, 3L)), new LongResult(), true)
                        .addLast(new StageA())
                        .addLast(new StageB())
                        .addLast(new StageC());
        Result block = pipeline.startCalc().block();
        System.out.printf(block.toString());
        pipeline.dependency().draw();
    }

    static class UserRequest implements Request {
        public List<Long> userIdList;

        public UserRequest(List<Long> userIdList) {
            this.userIdList = userIdList;
        }
    }

    static class UserResult implements Result {

    }
}