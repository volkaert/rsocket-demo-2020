# RSocket Security using JWT (JSON Web Token)

This demo shows how to secure [RSocket](http://rsocket.io/) (open source reactive layer 5/6 networking protocol)
communications between micro-services using [JWT](https://jwt.io) (JSON Web Token) authentication.

This example consists of an RSocket `responder` that returns hello messages based upon the method called and the supplied JWT token from the `requester` application.

The example assumes that you have already retrieved valid JWT tokens from your choice of Authorization Server. To mimic this, a `token-generator`
project has been included to get valid tokens for use with this demo.

## Build
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1
```
cd security-jwt/responder
../../mvnw clean package
```

In Terminal 2
```
cd security-jwt/requester
../../mvnw clean package
```

In Terminal 3
```
cd security-jwt/token-generator
../../mvnw clean package
```

## Run & Test
    
For Terminal 1, go to the `security-jwt/responder` directory.

For Terminal 2, go to the `security-jwt/requester` directory.

For Terminal 3, go to the `security-jwt/token-generator` directory.

  
Follow the steps below to run the example:

1. In Terminal 3, run the following command to generate the admin and user JWT tokens to use for authenticating with the `responder`:

        java -jar target/token-generator-1.0-SNAPSHOT.jar
        
    If successful, you will see the tokens displayed in the console:
        
        Generated Tokens
        ================
        Admin:
        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1ZCI6ImhlbGxvLXNlcnZpY2UiLCJzY29wZSI6IkFETUlOIiwiaXNzIjoiaGVsbG8tc2VydmljZS1kZW1vIiwiZXhwIjoxNTc2ODY4MjE0LCJqdGkiOiIyYjgwOTUwMC0wZWJlLTQ4MDEtOTYwZS1mZjc2MGQ3MjE0ZGUifQ.fzWzcvelcaXooMa5C3w7BI4lJxcruZiA7TwFyPQuH1k
        
        User:
        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXVkIjoiaGVsbG8tc2VydmljZSIsInNjb3BlIjoiVVNFUiIsImlzcyI6ImhlbGxvLXNlcnZpY2UtZGVtbyIsImV4cCI6MTU3Njg2ODIxNCwianRpIjoiOGQzZDE2YWUtZTg5MS00Nzc4LWFjNWEtN2NhY2ExOGEwMTYwIn0.Tlg1WxTcrMliLOBmBRSPR33C3xfbc6KUEkEZit928tE
    
>**Note:** The tokens are valid for `30 minutes`, after which you will need to regenerate new tokens to use the demo.
>        
2. In Terminal 1 (responder), run the following command to start the `responder`:

        java -jar target/responder-1.0-SNAPSHOT.jar
        
    If successful, you will see a message stating the responder has been started in the console.
        
    Now you are ready to start calling the `responder`.
    
3. In Terminal 2 (requester), run the following command to call the unsecured `hello` endpoint:

        java -jar target/requester-1.0-SNAPSHOT.jar hello Bob
        
   Notice that the request was successful and you received a hello response:
   
        2019-12-20 10:37:24.282  INFO 1919 --- [           main] ...    : Response: Hello, Bob! - from unsecured method 
        
4. Next, in Terminal 2 (requester), run the following command to call the `hello.secure` method which requires that the user is authenticated:

        java -jar target/requester-1.0-SNAPSHOT.jar hello.secure Bob
        ../mvnw spring-boot:run -Dspring-boot.run.arguments=hello.secure,Bob
        
    You will receive an `io.rsocket.exceptions.ApplicationErrorException: Access Denied` exception because you have not supplied a valid JWT token.
 
5. Now, in Terminal 2 (requester), run the same command again, but this time supply the `User` JWT token you generated earlier:

        java -jar target/requester-1.0-SNAPSHOT.jar --token {User Token Here} hello.secure Bob

    You will now receive a successful hello message because you have authenticated with a valid JWT token:
    
        2019-12-20 10:42:14.371  INFO 1979 --- [           main] ...    : Response: Hello, Bob! - from secured method
        
6. Next, in Terminal 2 (requester), let's test authorization by calling the `hello.secure.adminonly` endpoint with the `User` token by running the following command:

        java -jar target/requester-1.0-SNAPSHOT.jar --token {User Token Here} hello.secure.adminonly Bob

    You will receive an `io.rsocket.exceptions.ApplicationErrorException: Access Denied` exception because while you are authenticated, you are not authorized to access the method.
    
7. Finally, in Terminal 2 (requester), let's call the `hello.secure.adminonly` endpoint again, but this time use the `Admin` token by running the following command:

        java -jar target/requester-1.0-SNAPSHOT.jar --token {Admin Token Here} hello.secure.adminonly Bob
        
    You will receive a successful hello message because you have supplied a valid JWT token with admin scope:
    
        2019-12-20 10:47:56.047  INFO 2054 --- [           main] ...    : Response: Hello, Bob! - from secured method [admin only]


##### Credit
This demo relies heavily on the Greg Whitaker's [springboot-rsocketjwt](https://github.com/gregwhitaker/springboot-rsocketjwt-example) example.
