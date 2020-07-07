package com.rawan.camelpractice.services;

import org.springframework.stereotype.Service;

@Service
public class BotService {
    public boolean checkBotStatus(int botId){
        return botId % 2==0;
    }
}
