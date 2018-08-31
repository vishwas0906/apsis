package com.apsis.api;

import com.apsis.CounterApplication;
import io.swagger.model.Counter;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CounterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CounterApiControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Before
    public void init() {
        Counter c1 = new Counter().name("C1");
        Counter c2 = new Counter().name("C2");
        Counter c3 = new Counter().name("C3");
        HttpEntity<Counter> entityC1 = new HttpEntity<>(c1, headers);
        HttpEntity<Counter> entityC2 = new HttpEntity<>(c2, headers);
        HttpEntity<Counter> entityC3 = new HttpEntity<>(c3, headers);
        invoke(entityC1, HttpMethod.POST, "/v1/counter");
        invoke(entityC2, HttpMethod.POST, "/v1/counter");
        invoke(entityC3, HttpMethod.POST, "/v1/counter");
    }

    @Test
    public void createCounterWithEmptyName() throws JSONException {
        Counter counter = new Counter();

        HttpEntity<Counter> entity = new HttpEntity<>(counter, headers);
        ResponseEntity<String> response = invoke(entity, HttpMethod.POST, "/v1/counter");

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Name can not be empty"));
    }

    @Test
    public void createDuplicateCounter() throws JSONException {
        Counter counter = new Counter().name("C2");

        HttpEntity<Counter> entity = new HttpEntity<>(counter, headers);

        ResponseEntity<String> response = invoke(entity, HttpMethod.POST, "/v1/counter");

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Name already taken"));
    }

    @Test
    public void getCounter() throws JSONException {

        ResponseEntity<String> get = invoke(null, HttpMethod.GET, "/v1/counter/C3");
        String expectedGet = "{name:C3,count:1}";

        JSONAssert.assertEquals(expectedGet, get.getBody(), false);

    }

    @Test
    public void getAllCounter() throws JSONException {
        ResponseEntity<String> get = invoke(null, HttpMethod.GET, "/v1/counter");

        String expected = "[{name:C1,count:0},{name:C2,count:0},{name:C3,count:1}]";

        JSONAssert.assertEquals(expected, get.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private ResponseEntity<String> invoke(HttpEntity<Counter> entity, HttpMethod Method, String endPoint) {
        return restTemplate.exchange(
                createURLWithPort(endPoint),
                Method, entity, String.class);
    }

}
