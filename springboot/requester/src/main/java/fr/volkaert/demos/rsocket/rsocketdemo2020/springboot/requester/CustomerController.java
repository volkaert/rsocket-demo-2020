package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.requester;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
public class CustomerController {

    private final CustomerResponderAdapter customerResponderAdapter;


    CustomerController(CustomerResponderAdapter customerResponderAdapter) {
        this.customerResponderAdapter = customerResponderAdapter;
    }

    @GetMapping("/customers/{id}")
    Mono<CustomerResponse> getCustomer(@PathVariable String id) {
        return customerResponderAdapter.getCustomer(id);
    }

    @GetMapping(value = "/customers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<CustomerResponse> getCustomers() {
        return customerResponderAdapter.getCustomers(getRandomIds(10));
    }

    @GetMapping(value = "/customers-channel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<CustomerResponse> getCustomersChannel() {
        return customerResponderAdapter.getCustomersUsingChannel(Flux.interval(Duration.ofMillis(1000))
                .map(id -> new CustomerRequest(UUID.randomUUID().toString())));
    }

    private List<String> getRandomIds(int amount) {
        return IntStream.range(0, amount)
                .mapToObj(n -> UUID.randomUUID().toString())
                .collect(toList());
    }

}
