package com.TaiNguyen.SwaggerService.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi authServiceApi(){
        return GroupedOpenApi.builder()
                .group("AuthenticationService")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceApi(){
        return GroupedOpenApi.builder()
                .group("UserManagementService")
                .pathsToMatch("/user/**")
                .build();
    }
}
