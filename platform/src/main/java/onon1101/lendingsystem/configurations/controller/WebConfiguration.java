package onon1101.lendingsystem.configurations.controller;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class WebConfiguration {

    @Bean
    RouterFunction<ServerResponse> swaggerUiRedirect() {
        return route(
                GET("/swagger-ui/"),
                request ->
                        ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build());
    }
}
