package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.responder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
public class RSocketConfiguration {

    @Bean   // See https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket-annot-responders-server
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies());
        return handler;
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                //.encoders(encoders -> encoders.add(new Jackson2JsonEncoder()))
                //.decoders(decoders -> decoders.add(new Jackson2JsonDecoder()))
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder())) // CBOR (Concise Binary Object Representation) instead of JSON
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder())) // CBOR (Concise Binary Object Representation) instead of JSON
                .routeMatcher(new PathPatternRouteMatcher())    // See https://docs.spring.io/spring/docs/5.2.2.RELEASE/spring-framework-reference/web-reactive.html#rsocket-annot-responders
                .metadataExtractorRegistry(registry -> {    // Optional. See https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket-metadata-extractor
                    registry.metadataToExtract(MimeTypeUtils.parseMimeType("message/x.my-rsocket-demo.trace"), String.class, "traceId");
                    registry.metadataToExtract(MimeTypeUtils.parseMimeType("message/x.my-rsocket-demo.span"), String.class, "spanId");

                })
                .build();
    }
}
