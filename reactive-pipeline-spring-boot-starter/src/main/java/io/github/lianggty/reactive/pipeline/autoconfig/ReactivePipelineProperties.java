package io.github.lianggty.reactive.pipeline.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reactive.pipeline")
public class ReactivePipelineProperties {

    private String scanPackage;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }
}
