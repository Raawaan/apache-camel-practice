package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckBotStatusRoute extends RouteBuilder {
    private BotService botService;

    @Autowired
    public CheckBotStatusRoute(BotService botService) {
        this.botService = botService;
    }

    @Override
    public void configure() throws Exception {
        from("seda:checkBotStatus?concurrentConsumers=10")
                .choice()
                .when(exchange ->
                        botService.checkBotStatus(((Campaign) exchange.getIn().getBody()).getBotId()))
                .to("seda:chunkCampaigns")
                .otherwise()
                .to("direct:botError");

    }
}
