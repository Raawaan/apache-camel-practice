package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriggerCampaignsRoute extends RouteBuilder {
    @Autowired
    @BeanInject
    CampaignService campaignService;
    @Autowired
    public TriggerCampaignsRoute(CampaignService campaignService) {
        this.campaignService=campaignService;
    }

    @Override
    public void configure() throws Exception {
        from("direct:triggerCampaigns")
                .bean(campaignService, "getCampaigns")
                .split(body())
                .to("seda:checkBotStatus");
    }
}