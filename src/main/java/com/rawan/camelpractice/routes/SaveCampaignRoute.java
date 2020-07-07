package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.repo.CampaignRepo;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class SaveCampaignRoute extends RouteBuilder {
    @Autowired
    CampaignService campaignService;

    @Override
    public void configure() throws Exception {

        from("quartz://saveCamp?cron=0 * * ? * *")
                .process(exchange -> {
                    exchange.getIn().setBody("Campaign number "+campaignService.saveCampaign()+" is saved");
                })
                .to("log:info");
    }
}
