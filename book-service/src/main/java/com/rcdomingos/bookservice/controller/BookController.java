package com.rcdomingos.bookservice.controller;

import com.rcdomingos.bookservice.model.Book;
import com.rcdomingos.bookservice.repository.BookRepository;
import com.rcdomingos.bookservice.response.Cambio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
@RequestMapping("book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @GetMapping(value = "/{id}/{currency}")
    public Book findBook(@PathVariable("id") Long id,
                         @PathVariable("currency") String currency) {

        var book = repository.findById(id);

        if (book.isEmpty()) throw new IllegalArgumentException("Book Not Found");

        String port = environment.getProperty("local.server.port");

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", book.get().getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);
        var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/{amount}/{from}/{to}", Cambio.class, params);
        var cambio = response.getBody();

        book.get().setEnvironment(port);
        book.get().setPrice(cambio.getConvertedValue());

        return book.get();
    }
}
