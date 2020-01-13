package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.responder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Controller
public class CustomerController {

    private final List<String> RANDOM_NAMES = Arrays.asList("Andrew", "Joe", "Matt", "Rachel", "Robin", "Jack");

    /**
     * The metadata argument is not required in the method's signature but has been added for demo purpose.
     * See the definition of the metadata in the RSocketConfiguration class.
     * The requester argument is not required in the method's signature but has been added for demo purpose.
     * The requester argument is useful if you want to make a callback to the client side.
     */
    @MessageMapping("customers.{id}")
    CustomerResponse getCustomer(@DestinationVariable("id") String customerRequestId, @Headers Map<String, Object> metadata, RSocketRequester requester) {
        log.info("getCustomer({}) called", customerRequestId);
        logMetadata(metadata);
        sendAck(metadata, requester);
        return new CustomerResponse(customerRequestId, getRandomName());
    }

    /**
     * The metadata argument is not required in the method's signature but has been added for demo purpose.
     * See the definition of the metadata in the RSocketConfiguration class.
     * The requester argument is not required in the method's signature but has been added for demo purpose.
     * The requester argument is useful if you want to make a callback to the client side.
     */
    @MessageMapping("customers")
    Flux<CustomerResponse> getCustomers(MultipleCustomersRequest multipleCustomersRequest, @Headers Map<String, Object> metadata, RSocketRequester requester) {
        log.info("getCustomers({}) called", multipleCustomersRequest);
        logMetadata(metadata);
        sendAck(metadata, requester);
        return Flux.range(0, multipleCustomersRequest.getIds().size())
                .delayElements(Duration.ofMillis(500))
                .map(i -> new CustomerResponse(multipleCustomersRequest.getIds().get(i), getRandomName()));
    }

    /**
     * The metadata argument is not required in the method's signature but has been added for demo purpose.
     * See the definition of the metadata in the RSocketConfiguration class.
     * The requester argument is not required in the method's signature but has been added for demo purpose.
     * The requester argument is useful if you want to make a callback to the client side.
     */
    @MessageMapping("customers-channel")
    Flux<CustomerResponse> getCustomersUsingChannel(Flux<CustomerRequest> requests, @Headers Map<String, Object> metadata, RSocketRequester requester) {
        log.info("getCustomersUsingChanned({}) called", requests);
        logMetadata(metadata);
        sendAck(metadata, requester);
        return Flux.from(requests)
                .doOnNext(request -> log.info("Received 'customerChannel' request [{}]", request))
                .map(request -> new CustomerResponse(request.getId(), getRandomName()));
    }

    private String getRandomName() {
        return RANDOM_NAMES.get(new Random().nextInt(RANDOM_NAMES.size() - 1));
    }

    private void logMetadata(Map<String, Object> metadata) {
        log.info("Metadata: traceId={}, spanId={}", metadata.get("traceId"), metadata.get("spanId"));
    }

    private void sendAck(Map<String, Object> metadata, RSocketRequester requester) {
        requester
                .route("customer.ack")
                .data(new MyAck(metadata.get("traceId") + "-" + metadata.get("spanId")))
                .send() // Fire & Forget
                .subscribe();   // do NOT forget to call subscribe() otherwise the ack will not be sent !
    }

//     // See https://github.com/gregwhitaker/springboot-rsocketsetup-example/blob/master/hello-service/src/main/java/example/service/hello/controller/HelloController.java
//    @ConnectMapping("hello.setup")
//    public Mono<Void> setup(Setup setup) {
//        return helloService.isSupportedLocale(new Locale(setup.getLanguage(), setup.getCountry()))
//                .map(isSupported -> {
//                    if (isSupported) {
//                        LOG.info("Configuring service for locale [language: '{}', country: '{}']", setup.getLanguage(), setup.getCountry());
//                        this.locale = new Locale(setup.getLanguage(), setup.getCountry());
//                        return Mono.empty();
//                    } else {
//                        LOG.error("Unsupported locale [language: '{}', country: '{}']", setup.getLanguage(), setup.getCountry());
//                        return Mono.error(new RuntimeException(String.format("Unsupported locale [language: '%s', country: '%s']", setup.getLanguage(), setup.getCountry())));
//                    }
//                })
//                .then();
//    }
}
