package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChunkCampRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("seda:chunkCampaigns??concurrentConsumers=10")
                .split(body())
                .process(exchange -> {
                    if (((Campaign) exchange.getIn().getBody()).getId()%2==0){
                        Thread.sleep(30000);
                    }
                    exchange.getIn().setBody("chunking"+exchange.getIn().getBody());
                })
                .to("log:info");
    }

}