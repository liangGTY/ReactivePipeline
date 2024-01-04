package io.github.lianggty.pipeline.core;

public interface Context extends Invoker {

    /**
     * The unique name of the {@link Context}
     */
    String name();

    Stage stage();

    /**
     * this calculator context of pipeline
     */
    Pipeline pipeline();

    Context next();

    default <T> T getBean(Class<T> clazz) {
        return pipeline().getBean(clazz);
    }
}
