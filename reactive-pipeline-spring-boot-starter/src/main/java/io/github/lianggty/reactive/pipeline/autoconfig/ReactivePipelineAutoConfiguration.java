package io.github.lianggty.reactive.pipeline.autoconfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lianggty.pipeline.core.ExternalBeanContext;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.PipelineContext;
import io.github.lianggty.pipeline.core.PipelineContextFactory;
import io.github.lianggty.pipeline.core.def.GuicePipelineContext;

@Configuration
@EnableConfigurationProperties(ReactivePipelineProperties.class)
public class ReactivePipelineAutoConfiguration {

    @Bean
    public ExternalBeanContext externalBeanContext(ApplicationContext applicationContext) {
        ExternalBeanContext externalBeanContext = new ExternalBeanContext() {
            @Override
            public <T> T get(Class<T> clazz) {
                return applicationContext.getBean(clazz);
            }
        };
        return externalBeanContext;
    }

    @Bean
    public PipelineContextFactory pipelineContextFactory(
            ReactivePipelineProperties reactivePipelineProperties,
            ExternalBeanContext externalBeanContext) {
        PipelineContextFactory pipelineContextFactory = new PipelineContextFactory() {

            @Override
            public PipelineContext _newPipelineContext(Pipeline pipeline) {
                return new GuicePipelineContext(pipeline, externalBeanContext);
            }
        };

        GuicePipelineContext.scan(reactivePipelineProperties.getScanPackage());

        PipelineContextFactory.factory = pipelineContextFactory;

        return pipelineContextFactory;
    }
}
