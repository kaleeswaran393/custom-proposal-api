package com.lumen.docgen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "template")
@Data
public class TemplateConfig {
    private String rootPath;
}