package fr.volkaert.demos.rsocket.rsocketdemo2020.resumability.requester;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@Slf4j
@SpringBootApplication
public class Requester implements CommandLineRunner {

    @Value("${demo.host:localhost}")
    private String host;

    @Value("${demo.port:7001}")
    private int port;

    @Value("${demo.resume-session-seconds:60}")
    private int resumeSessionSeconds;

    public static void main(String[] args) {
        SpringApplication.run(Requester.class, args);
    }

    @Override
    public void run(String... args) {
        Duration resumeSessionDuration = Duration.ofSeconds(resumeSessionSeconds);

        RSocket socket = RSocketFactory.connect()
                .resume()
                .resumeSessionDuration(resumeSessionDuration)
                .transport(TcpClientTransport.create(host, port))
                .start()
                .block();
        socket.requestStream(DefaultPayload.create("dummy"))
                .map(payload -> {
                    log.info("Received data: [{}]", payload.getDataUtf8());
                    return payload;
                })
                .blockLast();

    }
}
