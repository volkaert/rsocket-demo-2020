package fr.volkaert.demos.rsocket.rsocketdemo2020.security.jwt.tokengenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Generates an admin token and user token for the demo.
 */
@SpringBootApplication
public class BearerTokenGenerator implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BearerTokenGenerator.class, args);
    }

    @Override
    public void run(String... args) {
        Algorithm algorithm = Algorithm.HMAC256("JAC1O17W1F3QB9E8B4B1MT6QKYOQB36V");

        String adminToken = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer("hello-service-demo")
                .withSubject("admin")
                .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .withAudience("hello-service")
                .withClaim("scope", "ADMIN")
                .sign(algorithm);

        String userToken = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withIssuer("hello-service-demo")
                .withSubject("user")
                .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .withAudience("hello-service")
                .withClaim("scope", "USER")
                .sign(algorithm);

        System.out.println();
        System.out.println("Generated Tokens");
        System.out.println("================");
        System.out.println("Admin: \n" + adminToken);
        System.out.println();
        System.out.println("User: \n" + userToken);
        System.out.println();
    }
}
