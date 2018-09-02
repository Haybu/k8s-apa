package io.agilehandy.k8sapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * @Author: Haytham Mohamed
 */

@SpringBootApplication
public class K8sAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sAppApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routers(MyHandlers handler) {
        return RouterFunctions
                .route(GET("/"), handler::jsonTest)
                ;
    }

    @Bean
    MyHandlers handler(Environment environment) {
        String target_url = environment.getProperty("TARGET_URL");
        if (target_url == null || target_url.equals("")) {
            target_url = "http://httpbin.org/uuid";
        }
        return new MyHandlers(target_url);
    }

    @Bean
    WebFilter webFilter() {
        return (exchange, chain) ->
        {
            if (exchange.getRequest().getHeaders().containsKey("X-HANG")) {
                exchange.getResponse().getHeaders().add("X-HANG"
                        ,exchange.getRequest().getHeaders().get("X-HANG").get(0));
            }

            return chain.filter(exchange);
        };
    }

}
