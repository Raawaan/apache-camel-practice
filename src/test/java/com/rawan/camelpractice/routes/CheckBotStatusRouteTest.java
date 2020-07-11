package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.constant.CampaignsDummy;
import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.routes.CheckBotStatusRoute;
import com.rawan.camelpractice.services.BotService;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.reifier.RouteReifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CheckBotStatusRouteTest extends CamelTestSupport {
    @Mock
    private BotService botService;

    @EndpointInject(value = "mock:seda:chunkCampaigns")
    protected MockEndpoint chunkCampaignsEndPoint;

    @EndpointInject(value = "mock:direct:botError")
    protected MockEndpoint botErrorMockEndpoint;

    @Produce(value = "seda:checkBotStatus")
    protected ProducerTemplate producerTemplate;

    @BeforeEach
    void setup() throws Exception {
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("seda:chunkCampaigns","direct:botError");
            }
        });
    }
    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new CheckBotStatusRoute(botService);
    }

    @Test
    void checkCampaignIsChunked() throws InterruptedException {
        Campaign campaign = CampaignsDummy.evenCampaign;
        when(botService.checkBotStatus(anyInt())).thenReturn(true);
        producerTemplate.sendBody(campaign);
        chunkCampaignsEndPoint.expectedMessageCount(1);
        chunkCampaignsEndPoint.expectedBodiesReceived(campaign);
        assertMockEndpointsSatisfied();
    }
    @Test
    void checkBotIsError() throws InterruptedException {
        Campaign campaign = CampaignsDummy.oddCampaign;
        when(botService.checkBotStatus(anyInt())).thenReturn(false);
        producerTemplate.sendBody(campaign);
        botErrorMockEndpoint.expectedMessageCount(1);
        botErrorMockEndpoint.expectedBodiesReceived(campaign);
        assertMockEndpointsSatisfied();
    }
}