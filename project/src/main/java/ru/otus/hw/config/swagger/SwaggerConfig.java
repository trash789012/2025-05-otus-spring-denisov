package ru.otus.hw.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI jamTimeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Jam Time API")
                        .description("API сервиса бронирования репетиций")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Denisov Nikolay")
                                .email("trash789012@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
