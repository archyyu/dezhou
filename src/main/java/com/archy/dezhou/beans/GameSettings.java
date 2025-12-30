package com.archy.dezhou.beans;

import com.archy.dezhou.entity.room.PukerGame;

import lombok.Data;

@Data
public class GameSettings {
    
    private int smallBet;
    private int bigBet;
    private int minBuyIn;
    private int maxBuyIn;
    private int timePerTurn;
    private String gameType; // "Texas Hold'em", etc.
    private String bettingStructure; // "No Limit", "Pot Limit", etc.

    public GameSettings(PukerGame game) {
        this.smallBet = game.getSbet();
        this.bigBet = game.getBbet();
        this.minBuyIn = game.getMinbuy();
        this.maxBuyIn = game.getMaxbuy();
        this.timePerTurn = 30; // Default 30 seconds
        this.gameType = "Texas Hold'em";
        this.bettingStructure = "No Limit";
    }

}
