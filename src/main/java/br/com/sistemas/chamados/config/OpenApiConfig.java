package br.com.sistemas.chamados.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chamadosOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Sistema de Chamados")
                .description("CRUD de clientes, categorias, técnicos e chamados, com regras de status, comentários, filtros e paginação.")
                .version("1.0"));
    }
}
