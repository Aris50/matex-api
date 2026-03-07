package com.matex.api.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class MappingDebugConfig {

    @Bean
    ApplicationRunner printMappings(RequestMappingHandlerMapping mapping) {
        return args -> {
            System.out.println("=== REGISTERED SPRING MAPPINGS ===");
            mapping.getHandlerMethods().forEach((info, method) -> {
                System.out.println(info + " -> " + method);
            });
            System.out.println("=== END REGISTERED SPRING MAPPINGS ===");
        };
    }
}