package fr.volkaert.demos.rsocket.rsocketdemo2020.security.jwt.requester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static picocli.CommandLine.*;

@Slf4j
@SpringBootApplication
public class RequesterApplication {

    public static void main(String... args) {
        SpringApplication.run(RequesterApplication.class, args);
    }

    /**
     * Runs the application.
     */
    @Component
    public class Runner implements CommandLineRunner {

        @Autowired
        private RSocketRequester rSocketRequester;

        @Override
        public void run(String... args) throws Exception {
            CommandLineArguments params = populateCommand(new CommandLineArguments(), args);

            log.debug("token: {}", params.token);
            log.debug("method: {}", params.method);
            log.debug("name: {}", params.name);

            if (StringUtils.isEmpty(params.token)) {
                log.info("Sending message without Bearer Token...");

                String message = rSocketRequester.route(params.method)
                        .data(params.name)
                        .retrieveMono(String.class)
                        .doOnError(throwable -> {
                            log.error(throwable.getMessage(), throwable);
                        })
                        .block();

                log.info("Response: {}", message);
            } else {
                log.info("Sending message with Bearer Token...");

                String message = rSocketRequester.route(params.method)
                        .metadata(params.token, BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                        .data(params.name)
                        .retrieveMono(String.class)
                        .doOnError(throwable -> {
                            log.error(throwable.getMessage(), throwable);
                        })
                        .block();

                log.info("Response: {}", message);
            }
        }
    }

    /**
     * Hello client command line arguments.
     */
    public static class CommandLineArguments {

        /**
         * JWT token for authentication and authorization
         */
        @Option(names = "--token", description = "jwt token")
        public String token;

        /**
         * RSocket method name
         */
        @Parameters(index = "0", arity = "1", description = "the method to call")
        public String method;

        /**
         * "name" argument to send to the method
         */
        @Parameters(index = "1", arity = "1", defaultValue = "name argument for method")
        public String name;
    }
}
