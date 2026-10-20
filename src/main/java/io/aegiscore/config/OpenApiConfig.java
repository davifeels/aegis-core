package io.aegiscore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aegisCoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AegisCore API")
                        .version("1.0.0")
                        .description("""
                                REST API for cloud asset management.

                                AegisCore provides endpoints to register,
                                retrieve, update and delete cloud assets,
                                with validation and centralized error handling.
                                """)
                        .contact(new Contact()
                                .name("Davi Anderson")
                                .url("https://github.com/davifeels")));
    }
}
