package com.rawan.camelpractice.routes;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.services.BotService;
import com.rawan.camelpractice.services.CampaignService;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.List;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
class TriggerCampaignsRouteTest {
    @MockBean
    private CampaignService campaignService;

    @EndpointInject(value = "mock:direct:botError")
    protected MockEndpoint botErrorMockEndpoint;

    @EndpointInject(value = "mock:seda:chunkCampaigns")
    protected MockEndpoint chunkCampaignsEndPoint;

    @Produce(value = "direct:triggerCampaigns")
    protected ProducerTemplate producerTemplate;
    @Test
    void checkBotIsError() throws InterruptedException {
        List<Campaign> campaignsOdd = Arrays.asList(new Campaign(1,"moo",13),
                new Campaign(1,"moo",11),new Campaign(1,"moo",7));
        Mockito.when(campaignService.getCampaigns()).thenReturn(campaignsOdd);
        producerTemplate.sendBody(campaignsOdd);
        botErrorMockEndpoint.expectedMessageCount(3);
        botErrorMockEndpoint.expectedBodiesReceived(campaignsOdd);
        botErrorMockEndpoint.assertIsSatisfied();
    }
    @Test
    void checkCampaignIsChunked() throws InterruptedException {
        List<Campaign> campaignsEven = Arrays.asList(new Campaign(1,"moo",12),
                new Campaign(1,"moo",8),new Campaign(1,"moo",10));
        Mockito.when(campaignService.getCampaigns()).thenReturn(campaignsEven);
        producerTemplate.sendBody(campaignsEven);
        chunkCampaignsEndPoint.expectedMessageCount(3);
        chunkCampaignsEndPoint.expectedBodiesReceived(campaignsEven);
        chunkCampaignsEndPoint.assertIsSatisfied();
    }
//    @Test
//    @DatabaseSetup(value = "classpath:addCampaigns.xml")
//    void checkCampaignIsChunkedUsingXml() throws InterruptedException {
//        producerTemplate.sendBody("");
//        chunkCampaignsEndPoint.expectedMessageCount(1);
//        chunkCampaignsEndPoint.assertIsSatisfied();
//    }
}