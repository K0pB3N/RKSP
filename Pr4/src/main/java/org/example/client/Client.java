package org.example.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Client {

    public void start() throws InterruptedException {
        RSocket client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 8080)) //Указываем порт и IP
                .start() //Запускаем
                .block(); //Блокируемся и ожидаем запросы

        requestStream(client).delayElements(Duration.ofMillis(500)).subscribe( //С задержкой в 500 мс будем выводить полученные данные в консоль
                        payload -> System.out.println(payload.getDataUtf8()),
                        e -> System.out.println("error" + e.toString()),
                        () -> System.out.println("completed")
                ).dispose();

        System.out.println(requestResponse(client));

        fireAndForget(client, "test");

        channel(client).delayElements(Duration.ofMillis(1000))
                .map(Payload::getDataUtf8) //Превращаем поток пэйлоада в поток строк
                .doOnNext(System.out::println) //Выводим строки
                .blockLast();

        Thread.sleep(3000);
        client.dispose();
    }

    private String requestResponse(RSocket client) {
        return client.requestResponse(DefaultPayload.create("hello")).map(Payload::getDataUtf8).block();
    }

    private Flux<Payload> requestStream(RSocket client) {
        return client.requestStream(DefaultPayload.create("request-stream"));
    }

    private Flux<Payload> channel(RSocket client) {
        return client.requestChannel(Flux.just("one", "two") //Эти строки мы будем посылать на сервер
                .map(DefaultPayload::create));//Превращаем поток строк в поток пэйлоадов для отправки на сервер
    }

    private void fireAndForget(RSocket client, String message) {
        client.fireAndForget(DefaultPayload.create(message)).block();
    }

}
