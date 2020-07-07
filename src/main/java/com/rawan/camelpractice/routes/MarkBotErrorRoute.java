package com.rawan.camelpractice.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MarkBotErrorRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:botError")
                .process(exchange -> {
                    exchange.getIn().setBody("bot error" + exchange.getIn().getBody());
                })
                .to("log:info");
    }
}

