package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.requester;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class CustomerResponse {

    private String id;
    private String name;
}