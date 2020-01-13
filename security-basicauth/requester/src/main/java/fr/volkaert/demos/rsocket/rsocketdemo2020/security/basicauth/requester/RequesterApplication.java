package fr.volkaert.demos.rsocket.rsocketdemo2020.security.basicauth.requester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
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

        @Qualifier("insecureRSocketRequester")
        @Autowired
        private RSocketRequester insecureRSocketRequester;

        @Qualifier("secureRSocketRequester")
        @Autowired
        private RSocketRequester secureRSocketRequester;

        @Override
        public void run(String... args) throws Exception {
            CommandLineArguments params = populateCommand(new CommandLineArguments(), args);

            log.debug("username: {}", params.username);
            log.debug("password: {}", params.password);
            log.debug("method: {}", params.method);
            log.debug("name: {}", params.name);

            if (!StringUtils.isEmpty(params.username) || !StringUtils.isEmpty(params.password)) {
                log.info("Sending message with Basic Auth metadata...");

                // Sending request to the hello-service
                String message = secureRSocketRequester.route(params.method)
                        .metadata(new UsernamePasswordMetadata(params.username, params.password), UsernamePasswordMetadata.BASIC_AUTHENTICATION_MIME_TYPE)
                        .data(params.name)
                        .retrieveMono(String.class)
                        .doOnError(throwable -> {
                            log.error(throwable.getMessage(), throwable);
                        })
                        .block();

                log.info("Response: {}", message);
            } else {
                log.info("Sending message without Basic Auth metadata...");

                // Sending request to the hello-service
                String message = insecureRSocketRequester.route(params.method)
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
         * Basic auth username
         */
        @Option(names = "--username", description = "basic auth username")
        public String username;

        /**
         * Basic auth password
         */
        @Option(names = "--password", description = "basic auth password")
        public String password;

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
