package com.rawan.camelpractice.routes;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.language.bean.Bean;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.jndi.JndiContext;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.naming.Context;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TriggerCampaignsRouteTest extends CamelTestSupport {
    @Mock
    private BotService botService;
    @BindToRegistry
    @Mock
    private CampaignService campaignService;

    @EndpointInject(value = "mock:seda:chunkCampaigns")
    protected MockEndpoint chunkCampaignsEndPoint;

    @EndpointInject(value = "mock:direct:botError")
    protected MockEndpoint botErrorMockEndpoint;

    @Produce(value = "direct:triggerCampaigns")
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
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RoutesBuilder[]{ new CheckBotStatusRoute(botService),new TriggerCampaignsRoute(campaignService)
   };
    }


    @Test
    void checkCampaignIsChunked() throws InterruptedException {
        List<Campaign> campaignsEven = Arrays.asList(new Campaign(1,"ahha",12),
                new Campaign(2,"ahha",14),
                new Campaign(13,"ahha",122));

        when(botService.checkBotStatus(anyInt())).thenReturn(true);
        when(campaignService.getCampaigns()).thenReturn(campaignsEven);

        producerTemplate.sendBody(campaignsEven);
        chunkCampaignsEndPoint.expectedMessageCount(campaignsEven.size());
        chunkCampaignsEndPoint.expectedBodiesReceived(campaignsEven);
        assertMockEndpointsSatisfied();
    }
    @Test
    void checkBotIsError() throws InterruptedException {

        List<Campaign> campaignsEven = Arrays.asList(new Campaign(1,"ahha",177),
                new Campaign(2,"ahha",17),
                new Campaign(13,"ahha",127));

        when(botService.checkBotStatus(anyInt())).thenReturn(false);
        when(campaignService.getCampaigns()).thenReturn(campaignsEven);

        producerTemplate.sendBody(campaignsEven);
        botErrorMockEndpoint.expectedMessageCount(campaignsEven.size());
        botErrorMockEndpoint.expectedBodiesReceived(campaignsEven);
        assertMockEndpointsSatisfied();
    }
}
