package fr.volkaert.demos.rsocket.rsocketdemo2020.security.jwt.requester;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class RSocketConfiguration {

    @Value("${helloresponder.host}")
    private String helloResponderHost;

    @Value("${helloresponder.port}")
    private int helloResponderPort;

    @Bean
    public RSocketRequester rsocketRequester() {
        return RSocketRequester.builder()
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .connectTcp(helloResponderHost, helloResponderPort)
                .block();
    }
}
