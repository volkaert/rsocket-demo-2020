package fr.volkaert.demos.rsocket.rsocketdemo2020.resumability.requester;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@Slf4j
@SpringBootApplication
public class Requester implements CommandLineRunner {

    private static final int CLIENT_PORT = 7001;
    static final String HOST = "localhost";
    static final Duration RESUME_SESSION_DURATION = Duration.ofSeconds(60);

    public static void main(String[] args) {
        SpringApplication.run(Requester.class, args);
    }

    @Override
    public void run(String... args) {
        RSocket socket = RSocketFactory.connect()
                .resume()
                .resumeSessionDuration(RESUME_SESSION_DURATION)
                .transport(TcpClientTransport.create(HOST, CLIENT_PORT))
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
