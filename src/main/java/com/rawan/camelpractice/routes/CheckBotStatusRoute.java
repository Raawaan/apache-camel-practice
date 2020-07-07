package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CheckBotStatusRoute extends RouteBuilder {
    @Autowired
    BotService botService;

    @Override
    public void configure() throws Exception {
        from("seda:checkBotStatus?concurrentConsumers=10")
                .process(exchange -> {
                    if (((Campaign) exchange.getIn().getBody()).getId() % 2 == 0) {
                        Thread.sleep(30000);
                    }
                })
                .to("log:info");


    }
}
