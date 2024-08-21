package ssu.today.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

//        Server server = new Server();
//        server.setUrl("https://api.naoman.site");

        Server local = new Server();
        local.setUrl("http://localhost:8080");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(Arrays.asList(securityRequirement))
//                .info(apiInfo()).servers(List.of(server, local));
                .info(apiInfo()).servers(List.of(local));
    }

    private Info apiInfo() {
        return new Info()
                .title("오늘은 서비스 API")
                .description("그 시절 레트로 감성을 담은 교환일기 서비스, 오늘은입니다.")
                .version("1.0.0");
    }
}
