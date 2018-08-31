package com.apsis;

import io.swagger.model.Counter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CounterApplication {
    public static void main(String[] args) {
        new SpringApplication(CounterApplication.class).run(args);
    }

    @Bean
    List<Counter> counters() {
        return new ArrayList<>();
    }
}
