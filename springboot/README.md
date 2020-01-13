# RSocket with Spring Boot demo

This demo is a simple client/server application using [RSocket](http://rsocket.io/) (open source reactive layer 5/6 networking protocol) 
and [Spring Boot](https://spring.io/projects/spring-boot).

It does NOT use the [low level Java RSocket API](https://github.com/rsocket/rsocket-java) but instead use the 
[higher level Spring RSocket Messaging API](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket)
and [annotations](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket-annot-responders).

Reference documentation about RSocket with Spring and Spring Boot:
>[RSocket section](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket) in the **Spring** Reference Documentation
>
>[RSocket section](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-rsocket) in the **Spring Boot** Reference Documentation 
 
This demo contains 2 modules:
- The server side (often called `responder` using the RSocket terminology), in the `responder` directory.
- The client side (often called `requester` using the RSocket terminology), in the `requester` directory.

## Responder

This is the server side of the application.

The responder exposes 3 RSocket endpoints (in the `CustomerController` class):
- `customers.{id}` which takes as input a customer id (extracted from the path) and returns a `CustomerResponse` object
- `customers` which takes as input a `MultipleCustomersRequest` object and returns a `Flux<CustomerResponse>` object
- `customers-channel`which takes as input a `Flux<CustomerRequest>` object and returns a `Flux<CustomerResponse>` object

## Requester

This is the client side of the application.

> It is the client side of the application but since it is a Spring Web Flux application, in fact it acts as a server 
>from the browser/curl perspective ! 
>
>The actual flow is browser/curl -> requester -> responder.

The requester exposes 3 HTTP REST endpoints (in the `CustomerController` class):
- `customers/{id}` which returns a `Mono<CustomerResponse>` object
- `customers` which returns a `Publisher<CustomerResponse>` object (with media type `text/event-stream`)
- `customers-channel`which returns a `Publisher<CustomerResponse>` object (with media type `text/event-stream`)

The requester adapts and forwards the HTTP REST requests to the RSocket responder endpoint as follows:
- `customers/{id}` -> `customers.{id}`  (pay attention to the replacement of the `/` by a `.` as separator)
- `customers`-> `customers`
- `customers-channel`-> `customers-channel`

## Build
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1:
```
cd springboot/responder
../../mvnw clean package
```

In Terminal 2:
```
cd springboot/requester
../../mvnw clean package
```

## Run 
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1:
```
cd springboot/responder
../../mvnw spring-boot:run
or 
java -jar target/responder-1.0-SNAPSHOT.jar
```

In Terminal 2:
```
cd springboot/requester
../../mvnw spring-boot:run
or 
java -jar target/requester-1.0-SNAPSHOT.jar
```

## Test 

In Terminal 3:
```
curl http://localhost:8080/customers/1
curl http://localhost:8080/customers/2
curl http://localhost:8080/customers
curl http://localhost:8080/customers-channel
```

##### Credit
This demo is derived from a [sample application](https://github.com/b3rnoulli/rsocket-examples) developed by Rafał Kowalski.
Rafał provides more details about RSocket and Spring Boot integration in its [blog](https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-abstraction-over-the-rsocket-66).

This demo also includes parts of Greg Whitaker's sample applications:
- [springboot-rsocketsetup-example](https://github.com/gregwhitaker/springboot-rsocketsetup-example)
- [springboot-rsocketmetadata-example](https://github.com/gregwhitaker/springboot-rsocketmetadata-example)


