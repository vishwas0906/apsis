package com.apsis.api;

import com.apsis.service.CounterService;
import io.swagger.annotations.ApiParam;
import io.swagger.api.CounterApi;
import io.swagger.model.Counter;
import io.swagger.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class CounterApiController implements CounterApi {

    private static final Logger log = LoggerFactory.getLogger(CounterApiController.class);

    private List<Counter> counters;
    private CounterService counterService;

    @Autowired
    public CounterApiController(final CounterService counterService, final List<Counter> counters) {
        this.counters = counters;
        this.counterService = counterService;
    }

    @Override
    public ResponseEntity createCounter(@RequestBody final Counter counter) {
        if (StringUtils.isEmpty(counter.getName())) {
            log.error("Invalid or empty name");
            return new ResponseEntity<>(new Error().message("Name can not be empty").code(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        if (counterService.find(counter.getName()).isPresent()) {
            log.error("Name is already taken {}", counter.getName());
            return new ResponseEntity<>(new Error().message("Name already taken " + counter.getName()).code(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        log.info("Counter created {}", counter.getName());
        return new ResponseEntity<>(counterService.save(counter), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Counter>> getAllCounters() {
        return new ResponseEntity<>(counters, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getCounter(@ApiParam(value = "name of counter", required = true) @PathVariable("counterName") String name) {
        Optional<Counter> counter = counterService.increment(name);
        return counter.<ResponseEntity>map(c1 -> new ResponseEntity<>(c1, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new Error().message(name + " Not found").code(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST));
    }
}
