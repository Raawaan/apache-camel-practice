package com.rawan.camelpractice.services;

import com.rawan.camelpractice.entities.Campaign;
import com.rawan.camelpractice.repo.CampaignRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CampaignService {
    private List<String> campaignList;
    private Random ran = new Random();
    @Autowired
    CampaignRepo campaignRepository;

    public CampaignService() {
        this.campaignList = new ArrayList<String>();
        campaignList.add("Macchiato");
        campaignList.add("Latte");
        campaignList.add("Flat White");
        campaignList.add("Espresso");
        campaignList.add("Americano");
        campaignList.add("Cappuccino");
    }

    public String saveCampaign() {
        return campaignRepository.save(new Campaign(campaignList.get(ran.nextInt(campaignList.size())))).toString();
    }
    public String saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign).toString();
    }

    public List<Campaign> getCampaigns() {
        return (List<Campaign>) campaignRepository.findAll();
    }
}
