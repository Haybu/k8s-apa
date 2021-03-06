package io.agilehandy.k8sapp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Random;

/**
 * @Author: Haytham Mohamed
 */

public class MyHandlers {

    Logger log = LoggerFactory.getLogger(MyHandlers.class);

    public static final String HEADER_NAME = "X-PERCENT-FAULT";

    private final String targetURL;
    private final String version;

    public MyHandlers(String targetURL, String version) {
        this.targetURL = targetURL;
        this.version = version;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {

        boolean headerFound = request.headers().asHttpHeaders().containsKey(MyHandlers.HEADER_NAME);
        boolean hangout = false;
        if (headerFound) {
            String percentage = request.headers().header(MyHandlers.HEADER_NAME).get(0);
            int rand = this.randomInRange(0, 100);
            log.info("fault-percentage / random >>> " + percentage + " / " + rand);
            if (rand < Integer.valueOf(percentage)) {
                hangout = true;
            }
        }

        if (hangout) {
            String uri = request.uri().getScheme() + "://" + request.uri().getHost() + ":" + request.uri().getPort();
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .syncBody("{\n\'uri\': " + uri
                            +",\n\'status\': " + HttpStatus.valueOf(500)
                            +",\n\'version\': " + version
                            + "\n}\n"
                    );
        } else {
            Long start = System.currentTimeMillis();

            WebClient.Builder clientBuilder = WebClient
                    .builder()
                    .baseUrl(this.targetURL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    ;

            if (headerFound) {
                clientBuilder.defaultHeader(MyHandlers.HEADER_NAME, request.headers().header(MyHandlers.HEADER_NAME).get(0));
            }

            Mono<String> value = clientBuilder.build()
                    .get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(v -> {
                        Long duration = System.currentTimeMillis() - start;
                        String uri = request.uri().getScheme() + "://" + request.uri().getHost() + ":" + request.uri().getPort();
                        return  "{\'uri\': \'" + uri
                                + "\',\n\'duration\': " + duration
                                + ",\n\'status\': " + HttpStatus.valueOf(200)
                                +",\n\'version\': " + version
                                + ",\n\'content\': " + v + "}\n";
                    })
                    ;
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(value, String.class);
        }
    }

    private int randomInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
