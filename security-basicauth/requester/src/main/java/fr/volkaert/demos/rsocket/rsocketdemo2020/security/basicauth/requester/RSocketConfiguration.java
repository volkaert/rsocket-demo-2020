package fr.volkaert.demos.rsocket.rsocketdemo2020.security.basicauth.requester;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.BasicAuthenticationEncoder;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class RSocketConfiguration {

    @Value("${helloresponder.host}")
    private String helloResponderHost;

    @Value("${helloresponder.port}")
    private int helloResponderPort;

    @Bean("insecureRSocketRequester")
    public RSocketRequester insecureRSocketRequester() {
        return RSocketRequester.builder()
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .connectTcp(helloResponderHost, helloResponderPort)
                .block();
    }

    @Bean(name = "secureRSocketRequester")
    public RSocketRequester helloServiceRequester() {
        return RSocketRequester.builder()
                .rsocketStrategies(builder -> {
                    builder.encoder(new BasicAuthenticationEncoder());
                })
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .connectTcp(helloResponderHost, helloResponderPort)
                .block();
    }
}
