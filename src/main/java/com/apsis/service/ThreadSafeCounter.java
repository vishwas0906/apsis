package com.apsis.service;

import io.swagger.model.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThreadSafeCounter implements CounterService {

    private List<Counter> counters;

    @Autowired
    public ThreadSafeCounter(final List<Counter> counters) {
        this.counters = counters;
    }

    @Override
    public synchronized Optional<Counter> increment(final String name) {
        Optional<Counter> resultCounter = find(name);
        if (resultCounter.isPresent()) {
            Counter counter = resultCounter.get();
            Long count = counter.getCount();
            counter.setCount(++count);
        }
        return resultCounter;
    }

    @Override
    public synchronized Optional<Counter> find(final String name) {
        return counters.stream().filter(counter -> name.equals(counter.getName())).findFirst();
    }

    @Override
    public synchronized Counter save(final Counter counter) {
        counters.add(counter.count(0L));
        return counter;
    }
}
