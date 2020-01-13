# RSocket Security using BasicAuth

This demo shows how to secure [RSocket](http://rsocket.io/) (open source reactive layer 5/6 networking protocol)
communications between micro-services using BasicAuth authentication.

## Responder 

This is the server side of the application.

The responder exposes 2  RSocket endpoints (in the `ResponderController` class):
- `hello`, that returns a hello message without authentication.
- `hello.secure`, that returns a hello message only for authenticated requests.

At present time, there is only one configured user:
- Username: `admin`
- Password: `password`

## Build
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1
```
cd security-basicauth/responder
../../mvnw clean package
```

In Terminal 2
```
cd security-basicauth/requester
../../mvnw clean package
```

## Run 
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1:
```
cd security-basicauth/responder
../../mvnw spring-boot:run
or 
java -jar target/responder-1.0-SNAPSHOT.jar
```

## Test

In Terminal 2:
```
cd security-basicauth/requester

java -jar target/requester-1.0-SNAPSHOT.jar hello Bob

java -jar target/requester-1.0-SNAPSHOT.jar hello.secure Bob
-> access denied

java -jar target/requester-1.0-SNAPSHOT.jar --username=admin --password=password hello.secure Bob
-> OK
```

##### Credit
This demo relies heavily on the Greg Whitaker's [springboot-rsocketbasicauth](https://github.com/gregwhitaker/springboot-rsocketbasicauth-example) example.
