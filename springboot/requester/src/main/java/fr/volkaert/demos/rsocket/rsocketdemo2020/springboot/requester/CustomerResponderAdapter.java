package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.requester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
//@Component
@Controller
public class CustomerResponderAdapter {

    @Autowired
    RSocketRequester rSocketRequester;

    private final int retryNumRetries = 3;
    private final Duration retryFirstBackOff = Duration.ofSeconds(1);
    private final Duration retryMaxBackOff = Duration.ofSeconds(10);

    private Random rand = new Random(System.currentTimeMillis());

    Mono<CustomerResponse> getCustomer(String id) {
        return rSocketRequester
                .route("customers." + id)
                .metadata(metadataSpec -> { // Optional. Added for demo purpose.
                    metadataSpec.metadata(UUID.randomUUID().toString(), MimeType.valueOf("message/x.my-rsocket-demo.trace"));
                    metadataSpec.metadata(Integer.toString(rand.nextInt()), MimeType.valueOf("message/x.my-rsocket-demo.span"));
                })
                //.data(new CustomerRequest(id))
                .retrieveMono(CustomerResponse.class)
                .retryBackoff(retryNumRetries, retryFirstBackOff, retryMaxBackOff) // Optional. Added for demo purpose.
                .doOnNext(customerResponse -> log.info("Received customer as mono [{}]", customerResponse))
                .doOnError(Exception.class, ex -> log.error(ex.getMessage()));
    }

    Flux<CustomerResponse> getCustomers(List<String> ids) {
        return rSocketRequester
                .route("customers")
                .metadata(metadataSpec -> { // Optional. Added for demo purpose.
                    metadataSpec.metadata(UUID.randomUUID().toString(), MimeType.valueOf("message/x.my-rsocket-demo.trace"));
                    metadataSpec.metadata(Integer.toString(rand.nextInt()), MimeType.valueOf("message/x.my-rsocket-demo.span"));
                })
                .data(new MultipleCustomersRequest(ids))
                .retrieveFlux(CustomerResponse.class)
                .retryBackoff(retryNumRetries, retryFirstBackOff, retryMaxBackOff) // Optional. Added for demo purpose.
                .doOnNext(customerResponse -> log.info("Received customer as flux [{}]", customerResponse))
                .doOnError(Exception.class, ex -> log.error(ex.getMessage()));
    }

    Flux<CustomerResponse> getCustomersUsingChannel(Flux<CustomerRequest> customerRequestFlux) {
        return rSocketRequester
                .route("customers-channel")
                .metadata(metadataSpec -> { // Optional. Added for demo purpose.
                    metadataSpec.metadata(UUID.randomUUID().toString(), MimeType.valueOf("message/x.my-rsocket-demo.trace"));
                    metadataSpec.metadata(Integer.toString(rand.nextInt()), MimeType.valueOf("message/x.my-rsocket-demo.span"));
                })
                .data(customerRequestFlux, CustomerRequest.class)
                .retrieveFlux(CustomerResponse.class)
                .retryBackoff(retryNumRetries, retryFirstBackOff, retryMaxBackOff) // Optional. Added for demo purpose.
                .doOnNext(customerResponse -> log.info("Received customer as flux [{}]", customerResponse))
                .doOnError(Exception.class, ex -> log.error(ex.getMessage()));
    }

    @MessageMapping("customer.ack")
    void ack(MyAck ack) {
        System.out.println("**************************");
        log.info("Ack {} received", ack);
    }
}
