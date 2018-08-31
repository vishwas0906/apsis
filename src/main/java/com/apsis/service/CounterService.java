package com.apsis.service;

import io.swagger.model.Counter;

import java.util.Optional;

public interface CounterService {
    Optional<Counter> increment(String name);
    Optional<Counter> find(String name);
    Counter save(Counter counter);
}
