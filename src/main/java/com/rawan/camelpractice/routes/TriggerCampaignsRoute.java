package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriggerCampaignsRoute extends RouteBuilder {
    @Autowired
    CampaignService campaignService;
    @Autowired
    BotService botService;

    @Override
    public void configure() throws Exception {
        from("direct:triggerCampaigns")
                .process(exchange -> {
                    exchange.getIn().setBody(campaignService.getCampaigns());
                })
                .split(body())
                .to("seda:checkBotStatus")
                .choice()
                .when(exchange -> botService.checkBotStatus(((Campaign) exchange.getIn().getBody()).getBotId()))
                .to("mock:seda:chunkCampaigns")
                .to("seda:chunkCampaigns")
                .otherwise()
                .to("direct:botError")
                .to("mock:direct:botError");
    }
}
