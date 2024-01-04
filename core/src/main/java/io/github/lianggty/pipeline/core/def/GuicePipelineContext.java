package io.github.lianggty.pipeline.core.def;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.github.lianggty.pipeline.core.DataLoader;
import io.github.lianggty.pipeline.core.ExternalBeanContext;
import io.github.lianggty.pipeline.core.Pipeline;
import io.github.lianggty.pipeline.core.PipelineContext;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class GuicePipelineContext implements PipelineContext {

    private final Injector injector;
    private final java.util.Map<Key<?>, Binding<?>> allBindings;
    private final ExternalBeanContext externalBeanContext;
    private Set<Class<?>> dataLoaderList = new HashSet<>();

    public GuicePipelineContext(Pipeline pipeline, ExternalBeanContext externalBeanContext) {
        this.externalBeanContext = externalBeanContext;
        this.injector = Guice.createInjector(new Module(pipeline));
        this.allBindings = this.injector.getAllBindings();
    }

    public <T> T registerDataLoader(Class<T> IOClass) {
        dataLoaderList.add(IOClass);
        return getBean(IOClass);
    }

    static Map<Class<Object>, Function<Pipeline, Provider<Object>>> map;

    public static class Module extends AbstractModule {

        private Pipeline pipeline;

        public Module(Pipeline pipeline) {
            this.pipeline = pipeline;
        }

        @Override
        protected void configure() {
            map.forEach((clazz, matrixPipelineProviderFunction) -> {
                Provider<Object> provider = matrixPipelineProviderFunction.apply(pipeline);
                bind(clazz).toProvider(provider).in(Singleton.class);
            });
        }
    }

    public List<DataLoader<?>> getAllDataLoader() {
        List.ofAll(dataLoaderList).forEach(this::getBean);
        return List.ofAll(dataLoaderList).map(this::getBean)
                .filter(it -> DataLoader.class.isAssignableFrom(it.getClass())).map(it -> (DataLoader<?>) it);
    }

    public <T> T getBean(Class<T> beanClass) {
        if (allBindings.containsKey(Key.get(beanClass))) {
            dataLoaderList.add(beanClass);
            return injector.getInstance(beanClass);
        } else {
            return externalBeanContext.get(beanClass);
        }
    }

    public static void scan(String packagePath) {
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages(packagePath).scan()) {
            ClassInfoList allInterfaces = scanResult.getSubclasses(AbstractDataLoader.class);
            GuicePipelineContext.map = List.ofAll(allInterfaces).toMap(
                    classInfo -> classInfo.loadClass(Object.class),
                    classInfo -> {
                        Constructor<?> constructor;
                        try {
                            constructor = classInfo.loadClass().getConstructor(Pipeline.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                        return pipeline -> (Provider<Object>) () -> {
                            try {
                                return constructor.newInstance(pipeline);
                            } catch (Throwable t) {
                                throw new RuntimeException(t.getMessage(), t);
                            }
                        };
                    }
            );
        }
    }

}
