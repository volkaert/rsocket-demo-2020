package fr.volkaert.demos.rsocket.rsocketdemo2020.springboot.responder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class MultipleCustomersRequest {

    private List<String> ids;
}
