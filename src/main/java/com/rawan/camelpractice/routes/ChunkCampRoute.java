package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.entities.Campaign;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChunkCampRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("seda:chunkCampaigns?concurrentConsumers=10")
                .process(exchange -> {

                    exchange.getIn().setBody("chunking"+exchange.getIn().getBody());
                })
                .to("log:info");
    }
}