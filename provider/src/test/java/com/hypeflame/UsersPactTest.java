package com.hypeflame;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.hypeflame.UsersPactTest.BROKER_URL;

@Provider("users-provider")
@PactBroker(url = BROKER_URL, providerTags = { "dev" })
class UsersPactTest {

    public static final String BROKER_URL = "http://localhost:9292";
    public static final String SERVER_URL = "http://localhost:8081";
    public static final String PATH = "/users/1";
    public static final String BODY = """
        {
          "id":"1",
          "name":"Leanne Graham",
          "username":"leanne",
          "email":"sincere@april.biz"
        }
    """;
    private WireMockServer server;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTestTemplate(final PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    public void before(PactVerificationContext context) throws Exception {
        this.server = new WireMockServer(wireMockConfig().port(8081));
        this.server.stubFor(get(urlEqualTo(PATH))
                .willReturn(aResponse()
                    .withBody(BODY)
                    .withHeader("content-type","application/json")));
        this.server.start();
        context.setTarget(HttpTestTarget.fromUrl(new URL(SERVER_URL)));
    }

    @AfterEach
    public void after() {
        server.stop();
    }

    @State("user-is-leanne")
    public void isLeanne() {
        var template = new RestTemplate();
        template.getForObject(SERVER_URL + PATH, String.class);
    }

}
