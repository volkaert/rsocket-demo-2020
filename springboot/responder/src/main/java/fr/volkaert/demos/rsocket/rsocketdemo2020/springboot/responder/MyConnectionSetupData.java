package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.responder;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
// Optional. Just for demo purpose.
// See https://github.com/gregwhitaker/springboot-rsocketsetup-example
public class MyConnectionSetupData {

    private int someIntData;
    private String someStringData;
}
