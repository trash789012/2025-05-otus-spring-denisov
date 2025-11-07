package ru.otus.hw.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PathConfig {

    private final String apiBasePath;

    private final String swaggerUiPath;

    private final String openApiDocsPath;

    public PathConfig(
            @Value("${spring.mvc.servlet.path}") String baseApiPath,
            @Value("${springdoc.api-docs.path}") String swaggerUIPath,
            @Value("${springdoc.swagger-ui.path}") String swaggerDocsPath
    ) {
        this.apiBasePath = baseApiPath;
        this.swaggerUiPath = swaggerUIPath;
        this.openApiDocsPath = swaggerDocsPath;
    }
}
