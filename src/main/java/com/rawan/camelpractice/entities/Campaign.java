package com.rawan.camelpractice.entities;

import javax.persistence.*;
import java.util.Random;

@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int botId;


    protected Campaign() {
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", botId='" + botId + '\'' +
                "} \n";
    }

    public Campaign(String name) {
        Random ran = new Random();
        this.name = name+ran.nextInt(100);
        this.botId = ran.nextInt(100);
    }
    public Campaign(long id ,String name,int botId) {
        this.id= id;
        this.name = name;
        this.botId = botId;
    }
    public Campaign(String name,int botId) {
        this.name = name;
        this.botId = botId;
    }

    public Long getId() {
        return id;
    }

    public int getBotId() {
        return botId;
    }

    public String getName() {
        return name;
    }
}