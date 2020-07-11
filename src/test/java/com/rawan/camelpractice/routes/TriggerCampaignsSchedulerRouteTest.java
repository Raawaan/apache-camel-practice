package com.rawan.camelpractice.routes;

import com.rawan.camelpractice.constant.CampaignsDummy;
import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TriggerCampaignsSchedulerRouteTest extends CamelTestSupport {
    @Mock
    private BotService botService;
    @BindToRegistry
    @Mock
    private CampaignService campaignService;

    @EndpointInject(value = "mock:seda:chunkCampaigns")
    protected MockEndpoint chunkCampaignsEndPoint;

    @EndpointInject(value = "mock:direct:botError")
    protected MockEndpoint botErrorMockEndpoint;

    @Produce(value = "direct:quartzScheduler")
    protected ProducerTemplate producerTemplate;


    @BeforeEach
    void setup() throws Exception {
        RouteReifier.adviceWith(context.getRouteDefinitions().get(1), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("seda:chunkCampaigns", "direct:botError");

            }
        });
        RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:quartzScheduler");

            }
        });
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RoutesBuilder[]{new TriggerCampaignsSchedulerRoute(),
                new CheckBotStatusRoute(botService),
                new TriggerCampaignsRoute(campaignService)
        };
    }


    @Test
    void checkCampaignIsChunked() throws InterruptedException {
        List<Campaign> campaignsEven = CampaignsDummy.evenCampaigns;
        when(botService.checkBotStatus(anyInt())).thenReturn(true);
        when(campaignService.getCampaigns()).thenReturn(campaignsEven);
        producerTemplate.sendBody("");
        chunkCampaignsEndPoint.expectedMessageCount(campaignsEven.size());
        chunkCampaignsEndPoint.expectedBodiesReceived(campaignsEven);
        assertMockEndpointsSatisfied();
    }

    @Test
    void checkBotIsError() throws InterruptedException {
        List<Campaign> campaignsOdd = CampaignsDummy.oddCampaigns;
        when(botService.checkBotStatus(anyInt())).thenReturn(false);
        when(campaignService.getCampaigns()).thenReturn(campaignsOdd);
        producerTemplate.sendBody("");
        botErrorMockEndpoint.expectedMessageCount(campaignsOdd.size());
        botErrorMockEndpoint.expectedBodiesReceived(campaignsOdd);
        assertMockEndpointsSatisfied();
    }
}
