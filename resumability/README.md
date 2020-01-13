# Resumability of RSocket

This module contains example of resumability mechanism built-in RSocket. 
It consist of `ResumableRequester` and `ResumableResponder` which exchange data using
request-stream method from interaction model. The responder expose server on port `7000` 
while requester connects to the port `7001`
In order to run it please use `socat` with following command:

`socat -d TCP-LISTEN:7001,fork,reuseaddr TCP:localhost:7000`

It creates a mapping between ports `7000` and `7001`, so that the requester can reach the responder. 
If you would like to simulate network connectivity issues please stop and start `socat` while applications are running.
Then, you should see *RESUME* and *RESUME_OK* frames in the logs:

```11:22:06.932 [parallel-6] DEBUG io.rsocket.resume.ClientRSocketSession - Retrying with: ExponentialBackoffResumeStrategy{next=PT8S, firstBackoff=PT1S, maxBackoff=PT16S, factor=2}
   11:22:22.939 [reactor-tcp-nio-2] DEBUG reactor.netty.channel.FluxReceive - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Subscribing inbound receiver [pending: 0, cancelled:false, inboundDone: false]
   11:22:22.939 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ResumableDuplexConnection - client Resumable duplex connection reconnected with connection: io.rsocket.internal.ClientServerInputMultiplexer$InternalDuplexConnection@60ef1f7e
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ResumableDuplexConnection - Switching transport: client
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.UpstreamFramesSubscriber - Upstream subscriber requestN: 128
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ClientRSocketSession - Client ResumableConnection reconnected. Sending RESUME frame with state: [impliedPos: 94, pos: 0]
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.FrameLogger - sending -> 
   Frame => Stream ID: 0 Type: RESUME Flags: 0b0 Length: 44
   Data:
   
   11:22:22.943 [reactor-tcp-nio-2] DEBUG reactor.netty.ReactorNetty - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Added decoder [RSocketLengthCodec] at the end of the user pipeline, full pipeline: [RSocketLengthCodec, reactor.right.reactiveBridge, DefaultChannelPipeline$TailContext#0]
   11:22:22.943 [reactor-tcp-nio-2] DEBUG reactor.netty.resources.PooledConnectionProvider - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Channel connected, now 1 active connections and 0 inactive connections
   11:22:22.968 [reactor-tcp-nio-2] DEBUG io.rsocket.FrameLogger - receiving -> 
   Frame => Stream ID: 0 Type: RESUME_OK Flags: 0b0 Length: 14
   Data:
   
   11:22:22.968 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ClientRSocketSession - ResumeOK FRAME received
```

To find out more about resumability mechanism in RSocket see: https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-load-balancing--resumability-65

## Build
> Prerequisite: [Java 11](https://adoptopenjdk.net/)

In Terminal 1:
```
cd resumability/responder
../../mvnw clean package
```

In Terminal 2:
```
cd resumability/requester
../../mvnw clean package
```

## Run 
> Prerequisite: [Java 11](https://adoptopenjdk.net/) and `socat`

In Terminal 3:
```
socat -d TCP-LISTEN:7001,fork,reuseaddr TCP:localhost:7000
```

In Terminal 1:
```
cd resumability//responder
../../mvnw spring-boot:run
or 
java -jar target/responder-1.0-SNAPSHOT.jar
```

In Terminal 2:
```
cd resumability/requester
../../mvnw spring-boot:run
or 
java -jar target/requester-1.0-SNAPSHOT.jar
```

## Test 

In terminals 1 and 2, look at the data exchanged each second between the requester and the responder.

In terminal 3, stop `socat`. 
The responder continues to send data without errors (by default, frames are buffered in memory; 
you can define your own implementation of a frame store, in a database or in a Redis cache for example).
The requester does NOT receive any data (because `socat` simulates a broken network between the requester and the responder).

Restart `socat`. Wait for few seconds (because there is an exponential backoff so the resume may take time).
You should now see the missing data received by the requester.

The network failure was totally transparent for both the requester and the responder. 
You don't have to handle such network failure handling in your code ! Cool :-) 

>Pay attention to not stop `socat` too long because in this demo the duration of the resumability is defined as 60 seconds.
 

##### Credit
This demo largely derived from a [sample application](https://github.com/b3rnoulli/rsocket-examples) developed by Rafał Kowalski.
Rafał provides more details about RSocket and Spring Boot integration in its [blog](https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-abstraction-over-the-rsocket-66).
