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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import java.util.List;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
class TriggerCampaignsRouteAddMockedEndpointsTest {
    @Autowired
    private CampaignService campaignService;

    @EndpointInject(value = "mock:direct:botError")
    protected MockEndpoint botErrorMockEndpoint;

    @EndpointInject(value = "mock:seda:chunkCampaigns")
    protected MockEndpoint chunkCampaignsEndPoint;

    @Produce(value = "direct:triggerCampaigns")
    protected ProducerTemplate producerTemplate;

    @Test
    @DatabaseSetup(value = "classpath:addEvenCampaigns.xml")
    void checkCampaignIsChunked() throws InterruptedException {
        List<Campaign> campaignsEven =  campaignService.getCampaigns();
        producerTemplate.sendBody("");
        Thread.sleep(30000);
        chunkCampaignsEndPoint.expectedMessageCount(campaignsEven.size());
        chunkCampaignsEndPoint.expectedBodiesReceived(campaignsEven);
        chunkCampaignsEndPoint.assertIsSatisfied();
    }
    @Test
    @DatabaseSetup(value = "classpath:addOddCampaigns.xml")
    void checkBotIsError() throws InterruptedException {
        List<Campaign> campaignsEven =  campaignService.getCampaigns();
        producerTemplate.sendBody("");
        botErrorMockEndpoint.expectedMessageCount(campaignsEven.size());
        botErrorMockEndpoint.expectedBodiesReceived(campaignsEven);
        botErrorMockEndpoint.assertIsSatisfied();
    }
}