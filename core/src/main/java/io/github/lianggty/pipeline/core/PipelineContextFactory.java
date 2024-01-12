package io.github.lianggty.pipeline.core;

public abstract class PipelineContextFactory {

    public static PipelineContextFactory factory;

    public static PipelineContext newPipelineContext(Pipeline pipeline, boolean recordDependencies) {
        return factory._newPipelineContext(pipeline, recordDependencies);
    }

    public abstract PipelineContext _newPipelineContext(Pipeline pipeline, boolean recordDependencies);
}
