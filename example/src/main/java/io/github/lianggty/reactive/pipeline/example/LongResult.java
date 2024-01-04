package io.github.lianggty.reactive.pipeline.example;

import io.github.lianggty.pipeline.core.Result;
import lombok.Data;

@Data
public class LongResult implements Result {

    private Long stageARes;
    private Long stageBRes;
    private Long stageCRes;
}
