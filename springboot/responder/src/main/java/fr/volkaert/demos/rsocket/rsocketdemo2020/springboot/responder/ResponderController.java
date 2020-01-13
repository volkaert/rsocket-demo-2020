package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.responder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class ResponderController {

    @ConnectMapping("mysetup")   // route name must match the name provided as argument for RSocketRequester.builder().setupRoute() in the requester code
    public Mono<Void> setup(MyConnectionSetupData setup) {
        log.info("Received connection setup data: someIntData={}, someStringData={}", setup.getSomeIntData(), setup.getSomeStringData());
        return Mono.empty();
    }
}
