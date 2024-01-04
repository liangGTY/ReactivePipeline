package io.github.lianggty.pipeline.core.def;

import io.github.lianggty.pipeline.core.Stage;

final class DefaultContext extends AbstractContext {

    DefaultContext(String name, DefaultPipeline pipeline, Stage stage) {
        super(name, pipeline, stage);
    }
}
