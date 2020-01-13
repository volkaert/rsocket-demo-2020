package fr.volkaert.demos.rsocket.rsocketdemo2020.security.basicauth.responder;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class ResponderController {

    @MessageMapping("hello")
    public Mono<String> hello(String name) {
        return Mono.just(String.format("Hello, %s! - from unsecured method", name));
    }

    @MessageMapping("hello.secure")
    public Mono<String> helloSecure(String name) {
        return Mono.just(String.format("Hello, %s! - from secured method", name));
    }
}
