package com.tarasov.tic_tac_toe.webSocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "logging.level.org.springframework.messaging=TRACE",
                "logging.level.org.springframework.web.socket=TRACE"
        })
class WebSocketGreetingTest {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

    @LocalServerPort
    private int port;

    private String register(String username) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"secret123\"}";
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create("http://localhost:" + port + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.statusCode() == 200, "register failed: " + response.body());
        Matcher matcher = TOKEN_PATTERN.matcher(response.body());
        assertTrue(matcher.find(), "no token in: " + response.body());
        return matcher.group(1);
    }

    private WebSocketStompClient stompClient() {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new StringMessageConverter());
        return client;
    }

    private StompHeaders connectHeaders(String token) {
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    @Test
    void authorizedClientReceivesGreeting() throws Exception {
        String username = "ws" + (System.nanoTime() % 1_000_000);
        String token = register(username);

        StompSession session = stompClient().connectAsync(
                        "ws://localhost:" + port + "/ws",
                        new WebSocketHttpHeaders(),
                        connectHeaders(token),
                        new StompSessionHandlerAdapter() { })
                .get(10, TimeUnit.SECONDS);

        CompletableFuture<String> greeting = new CompletableFuture<>();
        session.subscribe("/app/greeting", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                greeting.complete(new String((byte[]) payload, StandardCharsets.UTF_8));
            }
        });

        String payload = greeting.get(10, TimeUnit.SECONDS);
        assertTrue(payload.contains("Hello, " + username), "unexpected payload: " + payload);

        session.disconnect();
    }

    @Test
    void connectionWithoutTokenIsRejected() {
        assertThrows(ExecutionException.class, () ->
                stompClient().connectAsync(
                                "ws://localhost:" + port + "/ws",
                                new WebSocketHttpHeaders(),
                                new StompHeaders(),
                                new StompSessionHandlerAdapter() { })
                        .get(10, TimeUnit.SECONDS));
    }
}
