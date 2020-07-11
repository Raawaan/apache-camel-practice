package com.rawan.camelpractice.constant;

import com.rawan.camelpractice.entities.Campaign;

import java.util.Arrays;
import java.util.List;

public class CampaignsDummy {
    public static List<Campaign> evenCampaigns = Arrays.asList(
            new Campaign(1, "ahha", 12),
            new Campaign(2, "ahha", 14),
            new Campaign(13, "ahha", 122));
    public static List<Campaign> oddCampaigns = Arrays.asList(
            new Campaign(1, "ahha", 177),
            new Campaign(2, "ahha", 17),
            new Campaign(13, "ahha", 127));
    public static Campaign evenCampaign = evenCampaigns.get(0);
    public static Campaign oddCampaign = oddCampaigns.get(0);

}
