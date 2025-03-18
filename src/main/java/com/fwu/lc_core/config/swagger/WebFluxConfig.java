package com.fwu.lc_core.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.permanentRedirect;

@Configuration
public class WebFluxConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route()
                .GET("/swagger", request -> permanentRedirect(URI.create("/swagger-ui/index.html")).build())
                .build();
    }
}
