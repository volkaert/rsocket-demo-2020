# RSocket demo

This [RSocket](http://rsocket.io/) (open source reactive layer 5/6 networking protocol) demo is composed of several independent modules:

- a [Spring Boot client/server application](springboot/README.md) which shows the main features of the RSocket protocol and its integration with the Spring framework
- a demo about [how to secure RSocket communications using BasicAuth](security-basicauth/README.md)
- a demo about [how to secure RSocket communications using JWT](security-jwt/README.md)
- a demo about the [resumability](resumability/README.md) feature of RSocket (no loss of data in case of a broken connection, in a full transparent way from the application code)

 
Reference documentation about RSocket with Spring and Spring Boot:
>[RSocket section](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket) in the **Spring** Reference Documentation
>
>[RSocket section](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-rsocket) in the **Spring Boot** Reference Documentation 
 
##### Slides
Slides used as support during developer conferences or quickies can be found [here](https://cutt.ly/volkaert-rsocket).
If you cannot access the link above (for example because your organization does not allow links to Google slides), there is [copy of the slides](slides.pdf) 
in the repository (but this copy may not be as fresh as the [source slides](https://cutt.ly/volkaert-rsocket) hosted in Google slides).
 
##### Credit
Those demos rely heavily on the [examples](https://github.com/b3rnoulli/rsocket-examples) provided by Rafał Kowalski.
Rafał provides more details about RSocket and Spring Boot integration in its [blog](https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-abstraction-over-the-rsocket-66).

Those demos also include large parts of Greg Whitaker's examples:
- [springboot-rsocketsetup-example](https://github.com/gregwhitaker/springboot-rsocketsetup-example)
- [springboot-rsocketmetadata-example](https://github.com/gregwhitaker/springboot-rsocketmetadata-example)
- [springboot-rsocketbasicauth](https://github.com/gregwhitaker/springboot-rsocketbasicauth-example)
- [springboot-rsocketjwt-example](https://github.com/gregwhitaker/springboot-rsocketjwt-example)


