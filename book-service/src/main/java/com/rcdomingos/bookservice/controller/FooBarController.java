package com.rcdomingos.bookservice.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Foo bar")
@RestController
@RequestMapping("book-service")
public class FooBarController {

    private final Logger logger = LoggerFactory.getLogger(FooBarController.class);

    // Retry -> usado para configurar tentativas de conexão quando gera falha
    // fallback -> metodo que sera chamado apos as tentativas e continuar a falha
    @Operation(summary = "foo bar test")
    @GetMapping("/foo-bar")
    @Retry(name = "foo-bar", fallbackMethod = "myFallbackMethod")
    public String fooBar() {
        String urlInvalida = "http://localhost:8080/foo-bar";
        logger.info("Request  to foo-bar is received!");
        ResponseEntity<String> response = new RestTemplate()
                .getForEntity(urlInvalida, String.class);
        //return "Foo-Bar!!";
        return response.getBody();
    }

    // CircuitBreaker -> gerencia as chamadas para evitar excesso de chamadas com erros,
    // evitando sobrecargas no serviço
    @GetMapping("/foo-bar/circuit-breaker")
    @CircuitBreaker(name = "default", fallbackMethod = "myFallbackMethod")
    public String foobarCircuitBreaker() {
        String urlInvalida = "http://localhost:8080/foo-bar";
        logger.info("Request to /foo-bar/circuit-breaker is received!");
        ResponseEntity<String> response = new RestTemplate()
                .getForEntity(urlInvalida, String.class);
        return response.getBody();
    }

    // RateLimiter -> controla a frequencia de determinado evento podendo configurar limites de requisições
    @GetMapping("/foo-bar/rate-limiter")
    @RateLimiter(name = "default")
    public String foobarRateLimiter() {
        logger.info("Request to /foo-bar/rate-limiter is received!");
        return "Foo-Bar!!";
    }

    // Bulkhead -> definir limites de chamadas concorrentes
    @GetMapping("/foo-bar/bulkhead")
    @Bulkhead(name = "default")
    public String foobarBulkhead() {
        logger.info("Request to /foo-bar/bulkhead is received!");
        return "Foo-Bar!!";
    }




    // depois dos retrys sem sucesso, chama o fallbackMethod
    // pode customizar por Exception mais especialista
    public String myFallbackMethod(Exception e) {
        logger.error("Error call fallbackMethod :{}", e.getMessage());
        return "fallbackMethod foo-bar!!!";
    }
}
