package io.github.lianggty.pipeline.core;

public abstract class PipelineContextFactory {

    public static PipelineContextFactory factory;

    public static PipelineContext newPipelineContext(Pipeline pipeline) {
        return factory._newPipelineContext(pipeline);
    }

    public abstract PipelineContext _newPipelineContext(Pipeline pipeline);
}
