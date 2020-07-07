package com.rawan.camelpractice.services;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.repo.CampaignRepo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
class CampaignServiceTest {
    @Autowired
    private CampaignService campaignService;

    @Test
    @ExpectedDatabase(value = "classpath:expectCampaignIsAdded.xml")
    @DatabaseSetup(value = "classpath:addCampaigns.xml")
    void getCampaignsSuccessfully() {
        List<Campaign> campaigns = campaignService.getCampaigns();
        assertNotNull(campaigns);
        assertEquals(campaigns.size(),1);
    }
}