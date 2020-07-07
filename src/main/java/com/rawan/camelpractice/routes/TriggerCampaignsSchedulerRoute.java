package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriggerCampaignsSchedulerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz://getCamp?cron=0 */2 * ? * *")
                .to("direct:triggerCampaigns");
    }
}