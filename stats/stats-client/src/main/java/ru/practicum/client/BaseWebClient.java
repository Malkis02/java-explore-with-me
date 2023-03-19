package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BaseWebClient {
    protected WebClient webClient;

    public BaseWebClient(WebClient webClient) {
        this.webClient = webClient;
    }


    protected <T> Mono<ResponseEntity<Object>> post(String path, T body) {
        return post(path, null, body);
    }

    protected <T> Mono<ResponseEntity<Object>> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> Mono<ResponseEntity<Object>> makeAndSendRequest(HttpMethod method, String path,
                                                                @Nullable Map<String, Object> parameters, @Nullable T body) {
        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            if (parameters == null) {
                Mono<Object> monoBody = Mono.just(body);
                return webClient
                        .method(method)
                        .uri(path)
                        .headers(defaultHeaders())
                        .body(monoBody, Object.class)
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(response.statusCode(), error))))
                        .toEntity(Object.class)
                        .timeout(Duration.ofMinutes(1));
            } else {
                return webClient
                        .method(method)
                        .uri(path, parameters)
                        .headers(defaultHeaders())
                        .retrieve()
                        .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(response.statusCode(), error))))
                        .toEntity(Object.class)
                        .timeout(Duration.ofMinutes(1));
            }
        }
        if (parameters == null) {
            return webClient
                    .method(method)
                    .uri(path)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new ResponseStatusException(response.statusCode(), error))))
                    .toEntity(Object.class);
        } else {
            return webClient
                    .method(method)
                    .uri(path, parameters)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new ResponseStatusException(response.statusCode(), error))))
                    .toEntity(Object.class);
        }
    }

    private Consumer<HttpHeaders> defaultHeaders() {
        return httpHeaders -> {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        };
    }
}