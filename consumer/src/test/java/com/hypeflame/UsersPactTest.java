package com.hypeflame;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "users-provider")
public class UsersPactTest {

    public static final String PATH = "/users/1";
    public static final String BODY = """
        {
          "id":"1",
          "name":"Leanne Graham",
          "username":"leanne",
          "email":"sincere@april.biz"
        }
    """;

    @Pact(provider = "users-provider", consumer = "users-consumer")
    public RequestResponsePact pactWithAllProperties(final PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return builder
            .given("user-is-leanne")
                .uponReceiving("Should return all available properties in response body.")
            .path(PATH)
            .method("GET")
            .willRespondWith().status(200)
            .headers(headers)
            .body(BODY)
            .toPact();
    }

    public static record User(String id, String name, String username, String email){ }

    @Test
    @PactTestFor(pactMethod = "pactWithAllProperties")
    public void runTestPactWithAllProperties(final MockServer server) {
        var template = new RestTemplate();
        var user = template.getForObject(server.getUrl() + PATH, User.class);
        assertNotNull(user);
        assertEquals("1", user.id());
        assertEquals("Leanne Graham", user.name());
        assertEquals("leanne", user.username());
        assertEquals("sincere@april.biz", user.email());
    }

}
