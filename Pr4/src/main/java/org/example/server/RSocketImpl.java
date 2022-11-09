package org.example.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;

public class RSocketImpl extends AbstractRSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        try {
            DatabaseConnector connector = new DatabaseConnector();
            connector.addData(payload.getDataUtf8());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Server fireAndForget: " + payload.getDataUtf8());
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        System.out.println("Server requestResponse: " + payload.getDataUtf8());
        return Mono.just(DefaultPayload.create("requestResponse payload"));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return Flux.range(1, 5)  //Просто отдаём назад поток с цифрами от 1 до 5
                .map(i -> DefaultPayload.create("" + i));
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads)
                .flatMap(payload -> Flux.fromStream(payload.getDataUtf8().codePoints().mapToObj(c -> String.valueOf((char) c)) //Разбивваем входные строки на буквы
                        .map(DefaultPayload::create))); //Оборачиваем буквы в пейлоад и отдаём клиенту
    }
}
