package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.requester;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
public class RSocketConfiguration {

    @Value("${responder.host}")
    private String responderHost;

    @Value("${responder.port}")
    private int responderPort;

    /**
     * It is required to define a RSocketMessageHandler in the client side only if there are callbacks made from the server side.
     */
    @Bean   // See https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket-annot-responders-server
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies());
        return handler;
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                //.encoders(encoders -> encoders.add(new Jackson2JsonEncoder()))    // do NOT forget to uncomment .dataMimeType(MimeTypeUtils.APPLICATION_JSON) in rSocketRequester()
                //.decoders(decoders -> decoders.add(new Jackson2JsonDecoder()))    // do NOT forget to uncomment .dataMimeType(MimeTypeUtils.APPLICATION_JSON) in rSocketRequester()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))      // do NOT forget to uncomment .dataMimeType(MimeTypeUtils.parseMimeType("application/cbor")) in rSocketRequester()
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))      // do NOT forget to uncomment .dataMimeType(MimeTypeUtils.parseMimeType("application/cbor")) in rSocketRequester()
                .routeMatcher(new PathPatternRouteMatcher())    // See https://docs.spring.io/spring/docs/5.2.2.RELEASE/spring-framework-reference/web-reactive.html#rsocket-annot-responders
                .metadataExtractorRegistry(registry -> {    // Optional. See https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket-metadata-extractor
                    registry.metadataToExtract(MimeTypeUtils.parseMimeType("message/x.my-rsocket-demo.trace"), String.class, "traceId");
                    registry.metadataToExtract(MimeTypeUtils.parseMimeType("message/x.my-rsocket-demo.span"), String.class, "spanId");
                })
                .build();
    }

    @Bean
    public RSocketRequester rsocketRequester() {
        return RSocketRequester.builder()
                .rsocketFactory(factory -> factory.acceptor(rsocketMessageHandler().responder()))   // to detect @MessageMapping in @Controller annotated classes
                .rsocketStrategies(rsocketStrategies())
                //.dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                //.dataMimeType(MimeTypeUtils.APPLICATION_JSON)                 // do NOT forget to change encoders and decoders in rsocketStrategies()
                .dataMimeType(MimeTypeUtils.parseMimeType("application/cbor"))  // do NOT forget to change encoders and decoders in rsocketStrategies()
                .setupRoute("mysetup")// Optional. See https://github.com/gregwhitaker/springboot-rsocketsetup-example/blob/master/hello-client/src/main/java/example/client/hello/HelloClientApplication.java
                .setupData(new MyConnectionSetupData(1, "xyz"))// Optional. See https://github.com/gregwhitaker/springboot-rsocketsetup-example/blob/master/hello-client/src/main/java/example/client/hello/HelloClientApplication.java
                .connectTcp(responderHost, responderPort)
                .block();
    }
}
