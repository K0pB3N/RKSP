package org.example;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.example.server.DatabaseConnector;
import org.example.server.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RSocketImplTest {

    private RSocket client;

    @BeforeAll
    static void startServer() {
        new Thread(() -> new Server().start()).start();
    }

    @BeforeEach
    void init() {
        this.client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 8080))
                .start()
                .block();
    }

    @Test
    void fireAndForget() throws SQLException {
        client.fireAndForget(DefaultPayload.create("my_test_data"));
        DatabaseConnector connector = new DatabaseConnector();
        assertTrue(connector.isDataExist("my_test_data"));
        connector.deleteData("my_test_data");
    }

    @Test
    void requestResponse() {
        assertEquals("requestResponse payload", client.requestResponse(DefaultPayload.create("hello")).block().getDataUtf8());
    }

    @Test
    void requestStream() throws InterruptedException {
        List<Integer> a = List.of(1, 2, 3, 4, 5);
        AtomicInteger index = new AtomicInteger();
        client.requestStream(DefaultPayload.create("request-stream")).delayElements(Duration.ofMillis(500)).subscribe(
                payload -> assertEquals(a.get(index.getAndIncrement()), Integer.valueOf(payload.getDataUtf8())),
                e -> System.out.println("error" + e.toString())
        );
        Thread.sleep(2000);
    }

    @Test
    void requestChannel() {
        List<String> a = List.of("o", "n", "e", "t", "w", "o");
        AtomicInteger index = new AtomicInteger();
        client.requestChannel(Flux.just("one", "two").map(DefaultPayload::create)).delayElements(Duration.ofMillis(1000))
                .map(Payload::getDataUtf8)
                .doOnNext(e -> {
                    assertEquals(a.get(index.getAndIncrement()), e);
                })
                .blockLast();
    }
}