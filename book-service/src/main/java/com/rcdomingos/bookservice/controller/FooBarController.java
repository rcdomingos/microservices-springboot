package com.rcdomingos.bookservice.controller;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("book-service")
public class FooBarController {

    private final Logger logger = LoggerFactory.getLogger(FooBarController.class);

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

    // depois dos retrys sem sucesso, chama o fallbackMethod
    // pode customizar por Exception mais especialista
    public String myFallbackMethod(Exception e) {
        logger.error("Error call fallbackMethod :{}", e.getMessage());
        return "fallbackMethod foo-bar!!!";
    }
}
